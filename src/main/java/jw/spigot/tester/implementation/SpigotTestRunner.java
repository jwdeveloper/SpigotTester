/*
 * MIT License
 *
 * Copyright (c)  2023. jwdeveloper
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package jw.spigot.tester.implementation;



import jw.spigot.tester.api.SpigotTest;
import jw.spigot.tester.api.TestRunner;
import jw.spigot.tester.api.data.TestClassResult;
import jw.spigot.tester.api.data.TestMethodResult;
import jw.spigot.tester.api.data.TestOptions;
import jw.spigot.tester.api.data.TestReport;
import jw.spigot.tester.api.models.TestClassModel;
import jw.spigot.tester.api.models.TestMethodModel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class SpigotTestRunner implements TestRunner {
    private final TestClassModelFactory factory;
    private final JarScanner assemblyScanner;
    private final TestOptions options;
    private final Plugin plugin;
    private final Logger logger;
    private ExecutorService executor;

    public SpigotTestRunner(Plugin plugin,
                            TestClassModelFactory factory,
                            JarScanner assemblyScanner,
                            TestOptions options) {
        this.plugin = plugin;
        this.factory = factory;
        this.options = options;
        this.assemblyScanner = assemblyScanner;
        this.logger = plugin.getLogger();
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public TestReport run() throws InvocationTargetException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException {

        var classes = assemblyScanner.findByInterface(SpigotTest.class);
        var testClasses = factory.createTestModels(classes);
        var report = new TestReport();
        List<Future<TestClassResult>> futures = new ArrayList<>();
        for (var testClass : testClasses) {
            futures.add(performClassTest(testClass));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        var classResults = new ArrayList<TestClassResult>();
        for (var future : futures) {
            var classResult = future.get();
            if (!classResult.isPassed()) {
                report.setPassed(false);
            }
            classResults.add(classResult);
        }

        report.setCreatedAt(LocalDateTime.now());
        report.setServerVersion(Bukkit.getVersion());
        report.setPluginVersion(plugin.getDescription().getVersion());
        report.setClassResults(classResults);

        return report;
    }


    public Future<TestClassResult> performClassTest(TestClassModel model) {
        var result = new TestClassResult();
        result.setClassName(model.getName());
        result.setClassPackage(model.getPackageName());
        try {
            model.getSpigotTest().beforeAll();
        } catch (Exception e) {
            return executor.submit(() -> result);
        }

        var methods = new ArrayList<TestMethodResult>();
        for (var testMethod : model.getTestMethods()) {
            var methodResult = performMethodTest(testMethod, model.getSpigotTest());
            if (!methodResult.isPassed()) {
                result.setPassed(false);
            }
            methods.add(methodResult);
        }
        try {
            model.getSpigotTest().afterAll();
        } catch (Exception e) {
            return executor.submit(() -> result);
        }
        result.setPassed(true);
        return executor.submit(() -> result);
    }

    private TestMethodResult performMethodTest(TestMethodModel model, SpigotTest test) {
        var testMethod = new TestMethodResult();
        testMethod.setName(model.getName());
        try {
            var start = System.nanoTime();

            test.before();
            model.getMethod().invoke(test);
            test.after();

            var finish = System.nanoTime();
            var result = finish - start;
            var executionTimeInMilis = result / Math.pow(10, 6);
            testMethod.setExecutionTime(executionTimeInMilis + "");
            testMethod.setPassed(true);
        } catch (Exception e) {
            testMethod.setException(e.getMessage());
        }
        return testMethod;
    }
}
