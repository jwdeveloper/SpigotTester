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

package io.github.jwdeveloper.spigot.tester.core.factory;


import io.github.jwdeveloper.spigot.tester.api.PluginTest;
import io.github.jwdeveloper.spigot.tester.api.context.TestContext;
import io.github.jwdeveloper.spigot.tester.api.annotations.Test;
import io.github.jwdeveloper.spigot.tester.api.models.TestClassModel;
import io.github.jwdeveloper.spigot.tester.api.models.TestMethodModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestClassModelFactory {

    private final Function<Class<?>, Object> parameterProvider;

    public TestClassModelFactory(Function<Class<?>, Object> parameterProvider) {
        this.parameterProvider = parameterProvider;
    }

    public TestClassModel createTestModel(Class<?> testClass, TestContext testContext) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        var constructors = testClass.getConstructors();
        if (constructors.length == 0) {
            throw new IllegalArgumentException(testClass.getSimpleName() + " need to have one public constructor");
        }
        var instance = (PluginTest) getSpigotTestInstance(constructors[0]);
        setTestContext(instance, testContext);
        var methodModels = getMethodModels(testClass);
        var classModel = new TestClassModel();
        classModel.setName(testClass.getSimpleName());
        classModel.setPackageName(testClass.getPackageName());
        classModel.setSpigotTest(instance);
        classModel.setTestMethods(methodModels);
        return classModel;
    }


    private PluginTest getSpigotTestInstance(Constructor constructor) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        var constructorArguments = new Object[constructor.getParameterCount()];
        var argumentIndex = 0;
        for (var type : constructor.getParameterTypes()) {
            constructorArguments[argumentIndex] = parameterProvider.apply(type);
            argumentIndex++;
        }
        return (PluginTest) constructor.newInstance(constructorArguments);
    }

    private List<TestMethodModel> getMethodModels(Class<?> clazz) {
        var methodModels = new ArrayList<TestMethodModel>();
        var methods = getTestMethods(clazz);

        for (var method : methods) {
            var annotation = method.getAnnotation(Test.class);
            var methodModel = new TestMethodModel();

            methodModel.setIgnored(annotation.ignore());
            methodModel.setName(method.getName() + "()");
            methodModel.setMethod(method);
            if (!annotation.name().equals("")) {
                methodModel.setName(annotation.name());
            }
            methodModels.add(methodModel);
        }
        return methodModels;
    }

    private List<Method> getTestMethods(Class<?> root) {
        var methods = new ArrayList<Method>();
        Class<?> currentClass = root;
        while (currentClass != null && currentClass != Object.class) {
            var temp = Arrays.stream(currentClass.getDeclaredMethods())
                    .filter(c -> c.isAnnotationPresent(Test.class))
                    .collect(Collectors.toList());
            
            methods.addAll(temp);
            currentClass = currentClass.getSuperclass();
        }
        return methods;
    }

    private void setTestContext(PluginTest object, TestContext testContext) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        var methods = PluginTest.class.getDeclaredMethod("setTestContext", TestContext.class);
        methods.setAccessible(true);
        methods.invoke(object, testContext);
        methods.setAccessible(false);
    }

}
