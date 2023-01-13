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

package io.github.jwdeveloper.spigot.tester.implementation.builder;


import io.github.jwdeveloper.spigot.tester.api.builder.ExecutableTestRunnerBuilder;
import io.github.jwdeveloper.spigot.tester.api.data.TestClassResult;
import io.github.jwdeveloper.spigot.tester.api.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.api.data.TestReport;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
import java.util.function.Function;

public class SpigotExecutableTestRunnerBuilder implements ExecutableTestRunnerBuilder {
    private final SpigotTestRunnerBuilder builder;
    private Consumer<Exception> onException = (e) -> {};

    public SpigotExecutableTestRunnerBuilder(Plugin plugin, TestOptions options) {
        builder = new SpigotTestRunnerBuilder(plugin, options);
    }

    @Override
    public TestReport run() {
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
    public ExecutableTestRunnerBuilder onFinish(Consumer<TestReport> event) {
        builder.onFinish(event);
        return this;
    }

    @Override
    public ExecutableTestRunnerBuilder onTest(Consumer<TestClassResult> event) {
        builder.onTest(event);
        return this;
    }

    @Override
    public ExecutableTestRunnerBuilder withParameterProvider(Function<Class<?>, Object> provider) {
        builder.withParameterProvider(provider);
        return this;
    }

    @Override
    public ExecutableTestRunnerBuilder withParameter(Object parameter) {
        builder.withParameter(parameter);
        return this;
    }

    @Override
    public <T> ExecutableTestRunnerBuilder withParameter(T parameter, Class<T> type) {
        builder.withParameter(parameter, type);
        return this;
    }


    @Override
    public ExecutableTestRunnerBuilder configure(Consumer<TestOptions> options) {
        builder.configure(options);
        return this;
    }
}
