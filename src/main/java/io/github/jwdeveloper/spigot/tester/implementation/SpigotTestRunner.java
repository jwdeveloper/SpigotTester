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

package io.github.jwdeveloper.spigot.tester.implementation;



import io.github.jwdeveloper.spigot.tester.api.SpigotTest;
import io.github.jwdeveloper.spigot.tester.api.TestRunner;
import io.github.jwdeveloper.spigot.tester.api.assertions.SpigotAssertion;
import io.github.jwdeveloper.spigot.tester.api.data.TestClassResult;
import io.github.jwdeveloper.spigot.tester.api.data.TestMethodResult;
import io.github.jwdeveloper.spigot.tester.api.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.api.data.TestReport;
import io.github.jwdeveloper.spigot.tester.api.models.TestClassModel;
import io.github.jwdeveloper.spigot.tester.api.models.TestMethodModel;
import io.github.jwdeveloper.spigot.tester.implementation.factory.TestClassModelFactory;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SpigotTestRunner implements TestRunner {
    private final TestClassModelFactory factory;
    private final JarScanner assemblyScanner;
    private final TestOptions options;
    private final Plugin plugin;
    private final EventsHandler eventsHandler;
    private final SpigotEventProxy eventProxy;

    public SpigotTestRunner(Plugin plugin,
                            TestClassModelFactory factory,
                            JarScanner assemblyScanner,
                            TestOptions options,
                            EventsHandler eventsHandler) {
        this.plugin = plugin;
        this.factory = factory;
        this.options = options;
        this.assemblyScanner = assemblyScanner;
        this.eventsHandler = eventsHandler;

        eventProxy = new SpigotEventProxy();
        SpigotAssertion.proxy = eventProxy;
    }

    @Override
    public TestReport run() throws InvocationTargetException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException {

        var classes = assemblyScanner.findByInterface(SpigotTest.class);
        var testClasses = factory.createTestModels(classes);
        var report = new TestReport();

        var classResults = new ArrayList<TestClassResult>();
        for (var testClass : testClasses) {
            var classResult =  performClassTest(testClass);
            eventsHandler.invokeOnTest(classResult);
            classResults.add(classResult);
        }
        report.setPluginName(plugin.getName());
        report.setPluginVersion(plugin.getDescription().getVersion());
        report.setClassResults(classResults);
        eventsHandler.invokeOnFinish(report);
        return report;
    }

    private void waitForAllTest(List<Future<TestClassResult>> futures) throws InterruptedException {
        while (true) {
            Thread.sleep(5);
            var working = futures.stream().filter(e -> !e.isDone()).toList();
            if (working.size() != 0) {
                continue;
            }
            break;
        }
    }

    public TestClassResult performClassTest(TestClassModel model) {
        var result = new TestClassResult();
        result.setPassed(true);
        result.setClassName(model.getName());
        result.setClassPackage(model.getPackageName());

        try {
            model.getSpigotTest().beforeAll();
        } catch (Exception e) {
            result.setPassed(false);
            eventsHandler.invokeOnTest(result);
            return result;
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
            eventsHandler.invokeOnTest(result);
            result.setPassed(false);
            return result;
        }
        result.setTestMethods(methods);

        return result;
    }

    private TestMethodResult performMethodTest(TestMethodModel model, SpigotTest test) {

        var testMethod = new TestMethodResult();
        testMethod.setName(model.getName());
        testMethod.setIgnored(model.isIgnored());
        if(model.isIgnored())
        {
            testMethod.setPassed(true);
            return testMethod;
        }

        try {
            var start = System.nanoTime();

            eventProxy.startSession();
            test.before();
            model.getMethod().invoke(test);
            test.after();
            eventProxy.stopSession();

            var finish = System.nanoTime();
            var result = finish - start;
            var executionTimeInMilis = result / Math.pow(10, 6);
            testMethod.setExecutionTime(executionTimeInMilis);
            testMethod.setPassed(true);
        } catch (Exception e) {
            testMethod.setException(e);
            testMethod.setErrorMessage(e.getMessage());
        }
        return testMethod;
    }
}
