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

package io.github.jwdeveloper.spigot.tester.spigot.builder;


import io.github.jwdeveloper.spigot.tester.api.TestContext;
import io.github.jwdeveloper.spigot.tester.api.TestRunner;
import io.github.jwdeveloper.spigot.tester.api.builder.TestRunnerBuilder;
import io.github.jwdeveloper.spigot.tester.api.data.TestClassResult;
import io.github.jwdeveloper.spigot.tester.api.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.api.data.TestPluginReport;
import io.github.jwdeveloper.spigot.tester.spigot.SpigotEventCollector;
import io.github.jwdeveloper.spigot.tester.spigot.SpigotTestContext;
import io.github.jwdeveloper.spigot.tester.spigot.SpigotTestRunner;
import io.github.jwdeveloper.spigot.tester.spigot.factory.TestClassModelFactory;
import io.github.jwdeveloper.spigot.tester.spigot.gson.JsonUtility;
import io.github.jwdeveloper.spigot.tester.spigot.handlers.DisplayTestHandler;
import io.github.jwdeveloper.spigot.tester.spigot.handlers.EventsHandler;
import io.github.jwdeveloper.spigot.tester.spigot.handlers.ReportGeneratorHandler;
import io.github.jwdeveloper.spigot.tester.spigot.players.FakePlayerFactoryImpl;
import io.github.jwdeveloper.spigot.tester.spigot.players.NmsCommunicator;
import io.github.jwdeveloper.spigot.tester.spigot.utils.JarScanner;
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
    private final NmsCommunicator nmsCommunicator;
    private final EventsHandler eventsHandler;

    public SpigotTestRunnerBuilder(Plugin plugin, TestOptions options, NmsCommunicator nmsCommunicator)
    {
        this.options = options;
        this.plugin = plugin;
        this.eventsHandler = new EventsHandler();
        this.parameters = new HashMap<>();
        this.parameterProvider = this::defaultDependencyProvider;
        this.nmsCommunicator = nmsCommunicator;
    }

    @Override
    public SpigotTestRunnerBuilder onFinish(Consumer<TestPluginReport> event) {
        eventsHandler.onFinish(event);
        return this;
    }


    @Override
    public SpigotTestRunnerBuilder onTest(Consumer<TestClassResult> event) {
        eventsHandler.onTest(event);
        return this;
    }


    @Override
    public SpigotTestRunnerBuilder parameterProvider(Function<Class<?>, Object> provider) {
        parameterProvider = provider;
        return this;
    }

    @Override
    public SpigotTestRunnerBuilder addParameter(Object parameter) {
        parameters.put(parameter.getClass(), parameter);
        return this;
    }

    @Override
    public <T> SpigotTestRunnerBuilder addParameter(T parameter, Class<T> type) {

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

        var spigotEventCollector = new SpigotEventCollector(plugin);
        var playerFactory = new FakePlayerFactoryImpl(nmsCommunicator);
        var testContext = new SpigotTestContext(plugin, playerFactory, parameterProvider, spigotEventCollector);
        var factory = new TestClassModelFactory(parameterProvider);
        var assemblyScanner = new JarScanner(plugin.getClass());

        parameters.put(TestContext.class, testContext);
        parameters.put(Plugin.class, plugin);
        return new SpigotTestRunner(
                plugin,
                factory,
                assemblyScanner,
                options,
                eventsHandler,
                testContext);
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
