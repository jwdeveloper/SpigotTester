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

package jw.spigot.tester.implementation.builder;


import jw.spigot.tester.api.TestRunner;
import jw.spigot.tester.api.builder.TestRunnerBuilder;
import jw.spigot.tester.api.data.TestClassResult;
import jw.spigot.tester.api.data.TestOptions;
import jw.spigot.tester.api.data.TestReport;
import jw.spigot.tester.implementation.EventsHandler;
import jw.spigot.tester.implementation.JarScanner;
import jw.spigot.tester.implementation.SpigotTestRunner;
import jw.spigot.tester.implementation.factory.TestClassModelFactory;
import jw.spigot.tester.implementation.gson.JsonUtility;
import jw.spigot.tester.implementation.handlers.DisplayTestHandler;
import jw.spigot.tester.implementation.handlers.ReportGeneratorHandler;
import org.bukkit.plugin.Plugin;

import java.io.InvalidClassException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class SpigotTestRunnerBuilder implements TestRunnerBuilder<SpigotTestRunnerBuilder> {
    private final Map<Class<?>, Object> parameters;
    private final Plugin plugin;
    private Function<Class<?>, Object> parameterProvider;
    private final TestOptions options;

    private final EventsHandler eventsHandler;

    public SpigotTestRunnerBuilder(Plugin plugin, TestOptions options) {
        this.options = options;
        this.plugin = plugin;
        this.eventsHandler = new EventsHandler();
        this.parameters = new HashMap<>();
        parameterProvider = this::defaultDependencyProvider;
    }

    @Override
    public SpigotTestRunnerBuilder onFinish(Consumer<TestReport> event) {
        eventsHandler.onFinish(event);
        return this;
    }


    @Override
    public SpigotTestRunnerBuilder onTest(Consumer<TestClassResult> event) {
        eventsHandler.onTest(event);
        return this;
    }


    @Override
    public SpigotTestRunnerBuilder withParameterProvider(Function<Class<?>, Object> provider) {
        parameterProvider = provider;
        return this;
    }

    @Override
    public SpigotTestRunnerBuilder withParameter(Object parameter) {
        parameters.put(parameter.getClass(), parameter);
        return this;
    }

    @Override
    public <T> SpigotTestRunnerBuilder withParameter(T parameter, Class<T> type) {

        if (!parameter.getClass().isAssignableFrom(type)) {
            new InvalidClassException(parameter.getClass().getSimpleName() + "isAssignableFrom " + type.getSimpleName());
        }
        parameters.put(type, parameter);
        return this;
    }

    @Override
    public SpigotTestRunnerBuilder configure(Consumer<TestOptions> consumer) {
        consumer.accept(options);
        return this;
    }


    public TestRunner build() {
        new DisplayTestHandler(eventsHandler, options);
        new ReportGeneratorHandler(
                eventsHandler,
                new JsonUtility(),
                options,
                plugin);

        var factory = new TestClassModelFactory(parameterProvider);
        var assemblyScanner = new JarScanner(plugin.getClass());
        return new SpigotTestRunner(
                plugin,
                factory,
                assemblyScanner,
                options,
                eventsHandler);
    }


    private Object defaultDependencyProvider(Class<?> type) {
        if (!parameters.containsKey(type)) {
            var error = new StringBuilder()
                    .append(type.getSimpleName())
                    .append(" required in test constructor, but it is not provided in as parameter in SpigotTestRunner.run");
            throw new IllegalArgumentException(error.toString());
        }
        return parameters.get(type);
    }
}
