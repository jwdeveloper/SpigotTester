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

package io.github.jwdeveloper.spigot.tester.core.assertions;

import io.github.jwdeveloper.reflect.api.exceptions.ValidationException;
import io.github.jwdeveloper.spigot.tester.api.assertions.EventsAssertions;
import io.github.jwdeveloper.spigot.tester.api.assertions.Times;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PluginEventsAssertions<T extends Event> implements EventsAssertions<T> {

    private final List<Event> events;
    private final ValidationDto validationDto;

    public PluginEventsAssertions(List<Event> events, Class<T> eventClass) {
        this.events = events;
        validationDto = new ValidationDto();
        validationDto.setEventClass(eventClass);
    }


    public EventsAssertions<T> wasInvoked(Times times) {
        validationDto.setTimes(times);
        return this;
    }

    @Override
    public void validate(Function<T, Boolean> onValidation) {
        validationDto.setOnValidation(onValidation);
        validate();
    }


    @Override
    public void validate() {

        var eventsToValidate = (List<T>) events
                .stream()
                .filter(event -> event.getClass().isAssignableFrom(validationDto.getEventClass()) ||
                        event.getClass().equals(validationDto.getEventClass()))
                .collect(Collectors.toList());

        if (validationDto.onValidation != null) {
            eventsToValidate = eventsToValidate.stream().filter(validationDto::getOnValidation).collect(Collectors.toList());
        }

        if (validationDto.times == null) {
            new PluginCommonAssertions(eventsToValidate.size()).shouldNotBe(0);
            return;
        }
        var times = validationDto.getTimes();
        if (!times.validate(eventsToValidate.size())) {
            throw new ValidationException("Events should be " + times.getType().name() + " " + times.getValue() + " but was " + eventsToValidate.size());
        }
    }

    @Data
    private class ValidationDto {
        private Function<T, Boolean> onValidation;
        private Class<T> eventClass;
        private Times times;

        public boolean getOnValidation(T t) {
            return onValidation.apply(t);
        }
    }
}
