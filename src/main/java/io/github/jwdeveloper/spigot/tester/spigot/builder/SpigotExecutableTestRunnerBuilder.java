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


import io.github.jwdeveloper.spigot.tester.api.SpigotTesterSetup;
import io.github.jwdeveloper.spigot.tester.api.builder.ExecutableTestRunnerBuilder;
import io.github.jwdeveloper.spigot.tester.api.data.TestClassResult;
import io.github.jwdeveloper.spigot.tester.api.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.api.data.TestPluginReport;
import io.github.jwdeveloper.spigot.tester.spigot.players.NmsCommunicator;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
import java.util.function.Function;

public class SpigotExecutableTestRunnerBuilder implements ExecutableTestRunnerBuilder {
    private final SpigotTestRunnerBuilder builder;
    private Consumer<Exception> onException = (e) -> {};

    public SpigotExecutableTestRunnerBuilder(Plugin plugin, TestOptions options, NmsCommunicator nmsCommunicator) {
        builder = new SpigotTestRunnerBuilder(plugin, options, nmsCommunicator);
        if(plugin instanceof SpigotTesterSetup)
        {
            var setup = (SpigotTesterSetup)plugin;
            setup.onSpigotTesterSetup(this);
        }
    }

    @Override
    public TestPluginReport run() {
        var runner = builder.build();
        try {
            return runner.run();
        } catch (Exception e) {
            onException.accept(e);
            return null;
        }
    }

    @Override
    public ExecutableTestRunnerBuilder onException(Consumer<Exception> event) {
        onException = event;
        return this;
    }
    @Override
    public ExecutableTestRunnerBuilder onFinish(Consumer<TestPluginReport> event) {
        builder.onFinish(event);
        return this;
    }

    @Override
    public ExecutableTestRunnerBuilder onTest(Consumer<TestClassResult> event) {
        builder.onTest(event);
        return this;
    }

    @Override
    public ExecutableTestRunnerBuilder parameterProvider(Function<Class<?>, Object> provider) {
        builder.parameterProvider(provider);
        return this;
    }

    @Override
    public ExecutableTestRunnerBuilder addParameter(Object parameter) {
        builder.addParameter(parameter);
        return this;
    }

    @Override
    public <T> ExecutableTestRunnerBuilder addParameter(T parameter, Class<T> type) {
        builder.addParameter(parameter, type);
        return this;
    }


    @Override
    public ExecutableTestRunnerBuilder configure(Consumer<TestOptions> options) {
        builder.configure(options);
        return this;
    }
}
