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

package io.github.jwdeveloper.spigot.tester.implementation.factory;


import io.github.jwdeveloper.spigot.tester.api.SpigotTest;
import io.github.jwdeveloper.spigot.tester.api.annotations.Test;
import io.github.jwdeveloper.spigot.tester.api.models.TestClassModel;
import io.github.jwdeveloper.spigot.tester.api.models.TestMethodModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class TestClassModelFactory {

    private final Function<Class<?>, Object> dependecyProvider;

    public TestClassModelFactory(Function<Class<?>, Object> parameterProvider) {
        this.dependecyProvider = parameterProvider;
    }

    public List<TestClassModel> createTestModels(Collection<Class<?>> testsClasses) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        var result = new ArrayList<TestClassModel>();
        for (var clazz : testsClasses) {
            var constructors = clazz.getConstructors();
            if (constructors.length == 0) {
                throw new IllegalArgumentException(clazz.getSimpleName() + " need to have one public constructor");
            }
            var instance = (SpigotTest) getSpigotTestInstance(constructors[0]);
            var testMethod = getTestMethods(clazz);

            var testModel = new TestClassModel();
            testModel.setName(clazz.getSimpleName());
            testModel.setPackageName(clazz.getPackageName());
            testModel.setSpigotTest(instance);
            testModel.setTestMethods(testMethod);
            result.add(testModel);
        }
        return result;
    }


    private SpigotTest getSpigotTestInstance(Constructor constructor) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        var constructorInput = new Object[constructor.getParameterCount()];
        var currentParamIndex = 0;
        for (var type : constructor.getParameterTypes()) {
            constructorInput[currentParamIndex] = dependecyProvider.apply(type);
            currentParamIndex++;
        }
        return (SpigotTest) constructor.newInstance(constructorInput);
    }

    private List<TestMethodModel> getTestMethods(Class<?> clazz) {
        var result = new ArrayList<TestMethodModel>();
        var methods = Arrays.stream(clazz.getMethods())
                .filter(c -> c.isAnnotationPresent(Test.class))
                .toArray(Method[]::new);


        for (var method : methods) {
            var annotation = method.getAnnotation(Test.class);
            var model = new TestMethodModel();

            model.setIgnored(annotation.ignore());
            model.setName(method.getName() + "()");
            model.setMethod(method);
            if (!annotation.name().equals("")) {
                model.setName(annotation.name());
            }
            result.add(model);
        }
        return result;
    }
}
