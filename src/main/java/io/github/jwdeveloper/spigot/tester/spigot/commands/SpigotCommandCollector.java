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

package io.github.jwdeveloper.spigot.tester.spigot.commands;

import io.github.jwdeveloper.spigot.tester.api.collectors.CommandInvokeResult;
import io.github.jwdeveloper.spigot.tester.api.collectors.CommandsCollector;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SpigotCommandCollector implements CommandsCollector {
    private CommandInvoker commandInvoker;
    private List<CommandInvokeResult> invokedCommands;

    public SpigotCommandCollector() {
        commandInvoker = new CommandInvoker();
    }

    public void invokeCommand(CommandSender commandSender, String commandLine) {
        var cmd = commandInvoker.dispatchCommand(commandSender, commandLine);
        invokedCommands.add(cmd);
    }

    public void invokeCommand(CommandSender commandSender, String commandName, String... args) {
        var cmd = commandName;
        for (var arg : args) {
            cmd += " " + arg;
        }
        invokeCommand(commandSender, cmd);
    }

    public void start() {
        invokedCommands = new ArrayList<>();
    }

    public void stop() {
        invokedCommands.clear();
    }

    @Override
    public List<CommandInvokeResult> getCommands() {
        return new ArrayList<>(invokedCommands);
    }
}
