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
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.jwdeveloper.spigot.tester.implementation.handlers;

import io.github.jwdeveloper.spigot.tester.api.data.TestClassResult;
import io.github.jwdeveloper.spigot.tester.api.data.TestPluginReport;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Data
public class EventsHandler
{

    private List<Consumer<TestClassResult>> onTestEvent = new ArrayList<>();

    private List<Consumer<TestPluginReport>> onTestFinished = new ArrayList<>();

    public void onTest(Consumer<TestClassResult> event)
    {
        onTestEvent.add(event);
    }

    public void onFinish(Consumer<TestPluginReport> event)
    {
        onTestFinished.add(event);
    }

    public void invokeOnTest(TestClassResult event) {
        onTestEvent.stream().forEach(e -> e.accept(event));
    }

    public void invokeOnFinish(TestPluginReport report) {
        onTestFinished.stream().forEach(e -> e.accept(report));
    }

}
