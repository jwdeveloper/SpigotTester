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

package io.github.jwdeveloper.spigot.tester.api.assertions;

import io.github.jwdeveloper.spigot.tester.api.collectors.CommandsCollector;
import io.github.jwdeveloper.spigot.tester.api.collectors.EventCollector;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;
import java.util.stream.Collectors;

public class AssertionFactory {
    private final EventCollector eventCollector;
    private final CommandsCollector commandsCollector;

    public AssertionFactory(EventCollector collector,
                            CommandsCollector commandsCollector) {
        this.eventCollector = collector;
        this.commandsCollector = commandsCollector;
    }


    public CommonAssertions assertThat(Object target) {
        return new CommonAssertions(target);
    }

    public PlayerAssertions assertThatPlayer(Player player) {
        return new PlayerAssertions(player);
    }

    public <T extends Event> EventsAssertions<T> assertThatEvent(Class<T> eventClass) {
        var events = eventCollector
                .getEvents()
                .stream()
                .filter(event -> event.getClass().isAssignableFrom(eventClass) ||
                        event.getClass().equals(eventClass))
                .collect(Collectors.toList());
        return new EventsAssertions<T>((List<T>) events);
    }

    public CommandAssertion assertThatCommand(String command) {
        return new CommandAssertion(commandsCollector.getCommands(), command);
    }
}
