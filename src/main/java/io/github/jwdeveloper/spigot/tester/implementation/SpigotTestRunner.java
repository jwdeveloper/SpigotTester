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
import io.github.jwdeveloper.spigot.tester.api.TestContext;
import io.github.jwdeveloper.spigot.tester.api.players.PlayerFactory;
import io.github.jwdeveloper.spigot.tester.api.TestRunner;
import io.github.jwdeveloper.spigot.tester.api.data.TestClassResult;
import io.github.jwdeveloper.spigot.tester.api.data.TestMethodResult;
import io.github.jwdeveloper.spigot.tester.api.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.api.data.TestPluginReport;
import io.github.jwdeveloper.spigot.tester.api.models.TestClassModel;
import io.github.jwdeveloper.spigot.tester.api.models.TestMethodModel;
import io.github.jwdeveloper.spigot.tester.implementation.factory.TestClassModelFactory;
import io.github.jwdeveloper.spigot.tester.implementation.players.FakePlayerFactoryImpl;
import io.github.jwdeveloper.spigot.tester.implementation.players.NmsCommunicator;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SpigotTestRunner implements TestRunner {
    private final TestClassModelFactory factory;
    private final JarScanner assemblyScanner;
    private final TestOptions options;
    private final Plugin plugin;
    private final EventsHandler eventsHandler;
    private final TestContextImpl testContext;

    public SpigotTestRunner(Plugin plugin,
                            TestClassModelFactory factory,
                            JarScanner assemblyScanner,
                            TestOptions options,
                            EventsHandler eventsHandler,
                            TestContextImpl testContext) {
        this.plugin = plugin;
        this.factory = factory;
        this.options = options;
        this.assemblyScanner = assemblyScanner;
        this.eventsHandler = eventsHandler;
        this.testContext = testContext;
    }

    @Override
    public TestPluginReport run() throws InvocationTargetException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException {
        var classes = assemblyScanner.findBySuperClass(SpigotTest.class);
        var testClasses = factory.createTestModels(classes);
        var report = new TestPluginReport();
        var classResults = new ArrayList<TestClassResult>();
        for (var testClass : testClasses) {
            testContext.start();
            var classResult = performClassTest(testClass);
            testContext.stop();
            eventsHandler.invokeOnTest(classResult);
            classResults.add(classResult);
        }
        report.setPluginName(plugin.getName());
        report.setPluginVersion(plugin.getDescription().getVersion());
        report.setClassResults(classResults);

        for(var tests : report.getClassResults())
        {
            if(!tests.isPassed())
            {
                report.setPassed(false);
                break;
            }
        }

        eventsHandler.invokeOnFinish(report);
        return report;
    }


    public TestClassResult performClassTest(TestClassModel model) {

        var result = new TestClassResult();
        result.setPassed(true);
        result.setClassName(model.getName());
        result.setClassPackage(model.getPackageName());
        try {
            model.getSpigotTest().before();
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
            model.getSpigotTest().after();
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
        if (model.isIgnored()) {
            testMethod.setPassed(true);
            return testMethod;
        }

        try {
            var start = System.nanoTime();
            test.beforeEachTest();
            model.getMethod().invoke(test);
            test.afterEachTest();

            var finish = System.nanoTime();
            var result = finish - start;
            var executionTimeInMilis = result / Math.pow(10, 6);
            testMethod.setExecutionTime(executionTimeInMilis);
            testMethod.setPassed(true);
        }
        catch (InvocationTargetException e)
        {
            testMethod.setException(e.getCause());
            testMethod.setErrorMessage(e.getMessage());
            return testMethod;
        }
        catch (Exception e) {
            testMethod.setException(e);
            testMethod.setErrorMessage(e.getMessage());
            return testMethod;
        }
        return testMethod;
    }
}
