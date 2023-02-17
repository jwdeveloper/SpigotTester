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

package io.github.jwdeveloper.spigot.tester.api.builder;


import io.github.jwdeveloper.spigot.tester.core.data.TestClassResult;
import io.github.jwdeveloper.spigot.tester.core.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.core.data.TestPluginReport;

import java.util.function.Consumer;
import java.util.function.Function;

public interface TestsBuilder {

    TestsBuilder onException(Consumer<Exception> event);

    /**
     * Invoked when tests are done
     */
    TestsBuilder onFinish(Consumer<TestPluginReport> event);

    /**
     * Invoked when after each test
     */
    TestsBuilder onTest(Consumer<TestClassResult> event);

    /**
     * In case you need to use Dependency Injection container
     */
    TestsBuilder parameterProvider(Function<Class<?>, Object> provider);


    /**
     * Parameter that will be optionally passed to test class constructor
     */
    TestsBuilder addParameter(Object parameter);


    /**
     * Parameter that will be optionally passed to test class constructor
     */
    <T> TestsBuilder addParameter(T parameter, Class<T> type);


    /**
     * Tests configuration
     */
    TestsBuilder configure(Consumer<TestOptions> options);
}
