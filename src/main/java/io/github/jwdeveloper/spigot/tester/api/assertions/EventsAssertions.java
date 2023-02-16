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

package io.github.jwdeveloper.spigot.tester.api.assertions;

import org.bukkit.event.Event;

import java.util.List;

public class EventsAssertions<T extends Event> {

    private List<T> events;

    public EventsAssertions(List<T> events) {
        this.events = events;
    }

    public EventsAssertions wasInvoked() {
        new CommonAssertions(events.size()).shouldNotBe(0);
        return this;
    }

    public EventsAssertions wasInvoked(Times times) {
        new CommonAssertions(times.getValue()).shouldBe(events.size());
        return this;
    }

    public boolean validate()
    {
        return false;
    }

}
