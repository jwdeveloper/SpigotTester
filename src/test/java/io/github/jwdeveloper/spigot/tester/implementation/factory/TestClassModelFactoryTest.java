/*
 * MIT License
 *
 * Copyright (c)  $originalComment.match("Copyright \(c\) (\d+)", 1, "-", "$today.year")2023. jwdeveloper
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
 *  FITNESS FOR resources.A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.jwdeveloper.spigot.tester.implementation.factory;

import io.github.jwdeveloper.spigot.tester.api.TestContext;
import io.github.jwdeveloper.spigot.tester.api.assertions.Assertions;
import io.github.jwdeveloper.spigot.tester.api.players.PlayerFactory;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.junit.Assert;
import org.junit.Test;
import resources.ExampleTestClass;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TestClassModelFactoryTest {


    @Test
    public void shouldCreateTestClassModels() throws InvocationTargetException, InstantiationException, IllegalAccessException {

        Function<Class<?>, Object> provider = (c) -> new FakeContext();
        var model = new TestClassModelFactory(provider);
        var classes = new ArrayList<Class<?>>();

        classes.add(ExampleTestClass.class);

        var result = model.createTestModels(classes);

        Assert.assertEquals(result.size(),1);

        var testModel = result.get(0);

        Assert.assertEquals(testModel.getTestMethods().size(),2);
    }


    public class FakeContext implements TestContext {

        @Override
        public PlayerFactory getPlayerContext() {
            return null;
        }

        @Override
        public Assertions assertions(Object target) {
            return null;
        }

        @Override
        public Plugin plugin() {
            return null;
        }

        @Override
        public <T> T getParameter(Class<T> clazz) {
            return null;
        }

        @Override
        public List<Event> getInvokedEvents() {
            return null;
        }
    }

}