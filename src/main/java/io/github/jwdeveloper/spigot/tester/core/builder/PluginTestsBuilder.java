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

package io.github.jwdeveloper.spigot.tester.core.builder;


import io.github.jwdeveloper.spigot.tester.api.TestRunner;
import io.github.jwdeveloper.spigot.tester.api.builder.TestsBuilder;
import io.github.jwdeveloper.spigot.tester.api.PluginTestsSetup;
import io.github.jwdeveloper.spigot.tester.core.data.TestClassResult;
import io.github.jwdeveloper.spigot.tester.core.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.core.data.TestPluginReport;
import io.github.jwdeveloper.spigot.tester.core.context.events.SpigotEventCollector;
import io.github.jwdeveloper.spigot.tester.core.context.PluginsTestsContext;
import io.github.jwdeveloper.spigot.tester.core.PluginsTestsRunner;
import io.github.jwdeveloper.spigot.tester.core.factory.TestClassModelFactory;
import io.github.jwdeveloper.spigot.tester.core.gson.JsonUtility;
import io.github.jwdeveloper.spigot.tester.core.handlers.DisplayTestHandler;
import io.github.jwdeveloper.spigot.tester.core.handlers.EventsHandler;
import io.github.jwdeveloper.spigot.tester.core.handlers.ReportGeneratorHandler;
import io.github.jwdeveloper.spigot.tester.core.context.players.FakePlayerFactory;
import io.github.jwdeveloper.spigot.tester.core.nms.NmsCommunicator;
import io.github.jwdeveloper.spigot.tester.core.JarScanner;
import org.bukkit.plugin.Plugin;

import java.io.InvalidClassException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class PluginTestsBuilder implements TestsBuilder {
    private final Map<Class<?>, Object> parameters;
    private final Plugin plugin;
    private Function<Class<?>, Object> parameterProvider;
    private final TestOptions options;
    private final NmsCommunicator nmsCommunicator;
    private final EventsHandler eventsHandler;

    public PluginTestsBuilder(Plugin plugin, TestOptions options, NmsCommunicator nmsCommunicator)
    {
        this.options = options;
        this.plugin = plugin;
        this.eventsHandler = new EventsHandler();
        this.parameters = new HashMap<>();
        this.parameterProvider = this::defaultDependencyProvider;
        this.nmsCommunicator = nmsCommunicator;
    }

    @Override
    public TestsBuilder onException(Consumer<Exception> event) {
        eventsHandler.onException(event);
        return this;
    }

    @Override
    public PluginTestsBuilder onFinish(Consumer<TestPluginReport> event) {
        eventsHandler.onFinish(event);
        return this;
    }


    @Override
    public PluginTestsBuilder onTest(Consumer<TestClassResult> event) {
        eventsHandler.onTest(event);
        return this;
    }


    @Override
    public PluginTestsBuilder parameterProvider(Function<Class<?>, Object> provider) {
        parameterProvider = provider;
        return this;
    }

    @Override
    public PluginTestsBuilder addParameter(Object parameter) {
        parameters.put(parameter.getClass(), parameter);
        return this;
    }

    @Override
    public <T> PluginTestsBuilder addParameter(T parameter, Class<T> type) {

        if (!parameter.getClass().isAssignableFrom(type)) {
            new InvalidClassException(parameter.getClass().getSimpleName() + "isAssignableFrom " + type.getSimpleName());
        }
        parameters.put(type, parameter);
        return this;
    }

    @Override
    public PluginTestsBuilder configure(Consumer<TestOptions> consumer) {
        consumer.accept(options);
        return this;
    }

    public TestRunner build() {
        if(plugin instanceof PluginTestsSetup)
        {
            var setup = (PluginTestsSetup)plugin;
            setup.onTestsSetup(this);
        }
        var displayTestHandler = new DisplayTestHandler(options);
        eventsHandler.onTest(displayTestHandler::onTest);

        var reportHandler = new ReportGeneratorHandler(
                new JsonUtility(),
                options,
                plugin);
        eventsHandler.onFinish(reportHandler::onFinish);

        var spigotEventCollector = new SpigotEventCollector(plugin);
        var playerFactory = new FakePlayerFactory(nmsCommunicator);
        var testContext = new PluginsTestsContext(plugin, playerFactory, parameterProvider, spigotEventCollector);
        var factory = new TestClassModelFactory(parameterProvider);
        var assemblyScanner = new JarScanner(plugin.getClass());

        return new PluginsTestsRunner(
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
