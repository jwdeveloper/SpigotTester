/*
 * MIT License
 *
 * Copyright (c)  2023  jwdeveloper
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

package io.github.jwdeveloper.spigot.tester.core;


import io.github.jwdeveloper.spigot.tester.api.PluginTest;
import io.github.jwdeveloper.spigot.tester.api.TestRunner;
import io.github.jwdeveloper.spigot.tester.core.context.PluginsTestsContext;
import io.github.jwdeveloper.spigot.tester.core.data.TestClassResult;
import io.github.jwdeveloper.spigot.tester.core.data.TestMethodResult;
import io.github.jwdeveloper.spigot.tester.core.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.core.data.TestPluginReport;
import io.github.jwdeveloper.spigot.tester.api.models.TestClassModel;
import io.github.jwdeveloper.spigot.tester.api.models.TestMethodModel;
import io.github.jwdeveloper.spigot.tester.core.factory.TestClassModelFactory;
import io.github.jwdeveloper.spigot.tester.core.handlers.EventsHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class PluginsTestsRunner implements TestRunner {
    private final TestClassModelFactory factory;
    private final JarScanner jarScanner;
    private final TestOptions options;
    private final Plugin plugin;
    private final EventsHandler eventsHandler;
    private final PluginsTestsContext testContext;

    public PluginsTestsRunner(Plugin plugin,
                              TestClassModelFactory factory,
                              JarScanner assemblyScanner,
                              TestOptions options,
                              EventsHandler eventsHandler,
                              PluginsTestsContext testContext) {
        this.plugin = plugin;
        this.factory = factory;
        this.options = options;
        this.jarScanner = assemblyScanner;
        this.eventsHandler = eventsHandler;
        this.testContext = testContext;
    }

    @Override
    public TestPluginReport run()
    {
       try
       {
         return tryRun();
       }
       catch (Exception e)
       {
           eventsHandler.invokeOnException(e);
       }
       return new TestPluginReport();
    }

    public TestPluginReport tryRun() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        var classes = jarScanner.findBySuperClass(PluginTest.class);
        var classResults = new ArrayList<TestClassResult>();
        for (var clazz : classes)
        {
            var testClassModel = factory.createTestModel(clazz, testContext);
            testContext.start();
            var classResult = performClassTest(testClassModel);
            testContext.stop();
            eventsHandler.invokeOnTest(classResult);
            classResults.add(classResult);
        }
        var report = new TestPluginReport();
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


    public TestClassResult performClassTest(TestClassModel classModel) {
        var classResult = new TestClassResult();
        classResult.setPassed(true);
        classResult.setClassName(classModel.getName());
        classResult.setClassPackage(classModel.getPackageName());
        classResult.setTestMethods(new ArrayList<TestMethodResult>());

        var beforeResult = performBefore(classModel);
        if(!beforeResult.isPassed())
        {
            classResult.setPassed(false);
            classResult.getTestMethods().add(beforeResult.getFailInfo());
            return classResult;
        }


        for (var testMethod : classModel.getTestMethods()) {
            var methodResult = performMethodTest(testMethod, classModel.getSpigotTest());
            if (!methodResult.isPassed()) {
                classResult.setPassed(false);
            }
            classResult.getTestMethods().add(methodResult);
        }

        var afterResult = performAfter(classModel);
        if(!afterResult.isPassed())
        {
            classResult.setPassed(false);
            classResult.getTestMethods().add(afterResult.getFailInfo());
            return classResult;
        }

        return classResult;
    }

    private TestMethodResult performMethodTest(TestMethodModel methodModel, PluginTest spigotTest) {

        var methodResult = new TestMethodResult();
        methodResult.setName(methodModel.getName());
        methodResult.setIgnored(methodModel.isIgnored());
        if (methodModel.isIgnored()) {
            methodResult.setPassed(true);
            return methodResult;
        }

        try {
            var start = System.nanoTime();
            testContext.start();
            spigotTest.beforeEachTest();
            methodModel.getMethod().invoke(spigotTest);
            spigotTest.afterEachTest();
            testContext.stop();
            var finish = System.nanoTime();
            var result = finish - start;
            var executionTimeInMiliSec = result / Math.pow(10, 6);
            methodResult.setExecutionTime(executionTimeInMiliSec);
            methodResult.setPassed(true);
            return methodResult;
        }
        catch (InvocationTargetException e)
        {
            methodResult.setException(e.getCause());
            methodResult.setErrorMessage(e.getMessage());
            methodResult.setPassed(false);
            return methodResult;
        }
        catch (Exception e) {
            methodResult.setException(e);
            methodResult.setErrorMessage(e.getMessage());
            methodResult.setPassed(false);
            return methodResult;
        }
    }

    public ResultDto performBefore(TestClassModel testClassModel)
    {
        try {
            testClassModel.getSpigotTest().before();
            if(testContext.getPlayerFactory().getPlayersCount() != 0)
            {
                TestMethodResult res = new TestMethodResult();
                res.setPassed(false);
                res.setErrorMessage("addPlayer() could not be called inside before() method, you can called it in beforeEachTest() or inside test");
                res.setName(testClassModel.getName()+".before()");
                return new ResultDto(false, null);
            }

            return new ResultDto(true, null);
        }
        catch (Exception e)
        {
            TestMethodResult res = new TestMethodResult();
            res.setPassed(false);
            res.setException(e);
            res.setErrorMessage(e.getMessage());
            res.setName(testClassModel.getName()+".before()");
            return new ResultDto(false, null);
        }
    }

    public ResultDto performAfter(TestClassModel testClassModel)
    {
        try {
            testClassModel.getSpigotTest().after();
            return new ResultDto(true, null);
        }
        catch (Exception e)
        {
            TestMethodResult res = new TestMethodResult();
            res.setPassed(false);
            res.setException(e);
            res.setErrorMessage(e.getMessage());
            res.setName(testClassModel.getName()+".after()");
            return new ResultDto(false, null);
        }
    }


    @Getter
    @AllArgsConstructor
    private class ResultDto
    {
        private boolean passed;
        private TestMethodResult failInfo;
    }


}
