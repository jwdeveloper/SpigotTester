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

import io.github.jwdeveloper.spigot.tester.api.collectors.CommandInvokeResult;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandAssertion {
    private final String command;
    private final List<CommandInvokeResult> commands;

    public CommandAssertion(List<CommandInvokeResult> commands, String command) {
        this.command = command;
        this.commands = commands;
    }

    public CommandAssertion wasInvoked(Times times) {
        getAssertions(commands.size()).shouldBe(times.getValue());
        return this;
    }

    public CommandAssertion byPlayer() {
        for (var event : commands) {
            getAssertions(event.getCommandSender() instanceof Player).shouldBeTrue();
        }
        return this;
    }

    public CommandAssertion byPlayer(Player player) {
        for (var event : commands) {
            getAssertions(event.getCommandSender() instanceof Player).shouldBeTrue();
            getAssertions(event.getCommandSender()).shouldBe(player);
        }
        return this;
    }

    public CommandAssertion byCommandSender(CommandSender commandSender) {
        for (var event : commands) {
            getAssertions(event.getCommandSender()).shouldBe(commandSender);
        }
        return this;
    }

    public CommandAssertion byConsole() {
        for (var event : commands) {
            getAssertions(event.getCommandSender() instanceof ConsoleCommandSender).shouldBeFalse();
        }
        return this;
    }

    public CommandAssertion withArguments(String... args) {

        boolean found = false;
        for (var cmd : commands) {
            var i = 0;
            for (var param : cmd.getCommandArgs()) {
                if (!param.equals(args[i])) {
                    break;
                }
                i++;
            }
            found = true;
        }
        getAssertions(found).shouldBeTrue();
        return this;
    }

    public CommandAssertion withSuccess()
    {
        for (var event : commands) {
            getAssertions(event.isInvokeResult()).shouldBe(true);
        }
        return this;
    }

    public CommandAssertion withFail()
    {
        for (var event : commands) {
            getAssertions(event.isInvokeResult()).shouldBe(false);
        }
        return this;
    }

    private CommonAssertions getAssertions(Object target) {
        return new CommonAssertions(target);
    }
}
