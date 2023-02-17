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

package io.github.jwdeveloper.spigot.tester.core.context.commands;


import io.github.jwdeveloper.spigot.tester.api.models.CommandInvokeModel;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Locale;

public class SpigotCommandInvoker {

    public CommandInvokeModel dispatchCommand(CommandSender sender, String commandLine) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(commandLine, "CommandLine cannot be null");
        return dispatch(sender, commandLine);
    }

    private CommandInvokeModel dispatch(CommandSender sender, String commandLine) throws CommandException {
        String[] args = commandLine.split(" ");
        if (args.length == 0) {
            throw new RuntimeException("Command could not be empty");
        }
        String sentCommandLabel = args[0].toLowerCase(Locale.ENGLISH);
        Command target = this.getCommand(sentCommandLabel);
        if (target == null) {
            throw new RuntimeException("Command not found");
        }
        try {
            var commandName = sentCommandLabel;
            var commandArgs = (String[]) Arrays.copyOfRange(args, 1, args.length);
            target.timings.startTiming();
            var result = target.execute(sender, commandName, commandArgs);
            target.timings.stopTiming();
            return new CommandInvokeModel(result, sender, target, commandName, commandArgs, commandLine);
        } catch (CommandException var7) {
            target.timings.stopTiming();
            throw var7;
        } catch (Throwable var8) {
            target.timings.stopTiming();
            throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, var8);
        }
    }

    private Command getCommand(String sentCommandLabel) {
        var server = Bukkit.getServer();
        var clazz = server.getClass();
        try {
            var field = clazz.getDeclaredField("commandMap");
            field.setAccessible(true);
            var commandMap = (CommandMap) field.get(server);
            field.setAccessible(false);
            return commandMap.getCommand(sentCommandLabel);
        } catch (Exception e) {
            throw new RuntimeException("CommandMap not found");
        }
    }
}
