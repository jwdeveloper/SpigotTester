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
import io.github.jwdeveloper.spigot.tester.api.assertions.CommandAssertions;
import io.github.jwdeveloper.spigot.tester.api.assertions.Times;
import io.github.jwdeveloper.spigot.tester.api.models.CommandInvokeModel;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PluginCommandAssertion implements CommandAssertions {
    private final List<CommandInvokeModel> commands;
    private final CommandValidationModel validationModel;
    private String errorMessage;

    public PluginCommandAssertion(List<CommandInvokeModel> commands, String command) {
        this.commands = commands;
        validationModel = new CommandValidationModel();
        validationModel.setExpectedName(command);


    }

    public PluginCommandAssertion wasInvoked(Times times) {
        validationModel.setExpectedInvokeTimes(times);
        return this;
    }

    public PluginCommandAssertion byPlayer() {
        validationModel.setExpectedInvoker(Invoker.Player);
        return this;
    }

    public PluginCommandAssertion byPlayer(Player player) {
        byPlayer();
        byCommandSender(player);
        return this;
    }

    public PluginCommandAssertion byCommandSender(CommandSender commandSender) {
        validationModel.setExpectedSender(commandSender);
        return this;
    }

    public PluginCommandAssertion byConsole() {
        validationModel.setExpectedInvoker(Invoker.Console);
        validationModel.setExpectedSender(Bukkit.getConsoleSender());
        return this;
    }

    public PluginCommandAssertion withArguments(String... args) {
        validationModel.setExpectedArgs(args);
        return this;
    }

    public PluginCommandAssertion withSuccess() {
        validationModel.setExpectedResult(Result.Success);
        return this;
    }

    public PluginCommandAssertion withFail() {
        validationModel.setExpectedResult(Result.Fail);
        return this;
    }

    @Override
    public void validate() {
        var validCommands = this.commands.stream()
                .filter(c -> c.getCommandName().equals(validationModel.getExpectedName()))
                .collect(Collectors.toList());
        getAssertions(validCommands.size()).shouldNotBe(0);

        if (validationModel.hasCommandSender()) {
            validCommands = validCommands.stream()
                    .filter(c -> c.getCommandSender().equals(validationModel.getExpectedSender()))
                    .collect(Collectors.toList());
        }
        if (validationModel.getExpectedInvoker() != Invoker.Any) {
            validCommands = validCommands.stream()
                    .filter(c ->
                    {
                        switch (validationModel.getExpectedInvoker()) {
                            case Player:
                                return c.getCommandSender() instanceof Player;
                            case Console:
                                return c.getCommandSender() instanceof ConsoleCommandSender;
                        }
                          return false;
                    })
                    .collect(Collectors.toList());
        }
        if (validationModel.getExpectedArgs() != null) {
            validCommands =validCommands.stream()
                    .filter(c ->
                    {
                        var cmdArgs = c.getCommandArgs();
                        var expArgs= validationModel.getExpectedArgs();

                        if(cmdArgs.length != expArgs.length)
                        {
                            return false;
                        }
                        for(var i =0;i<cmdArgs.length;i++)
                        {
                            if(!cmdArgs[i].equals(expArgs[i]))
                            {
                                return false;
                            }
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }
        if (validationModel.getExpectedResult() != Result.Any) {
            validCommands = validCommands.stream()
                    .filter(c ->
                    {
                        var type = validationModel.getExpectedResult();
                        if(type == Result.Any)
                        {
                            return true;
                        }
                        if(type == Result.Fail && !c.isInvokeResult())
                        {
                            return true;
                        }

                        if(type == Result.Success && c.isInvokeResult())
                        {
                            return true;
                        }

                        return false;
                    })
                    .collect(Collectors.toList());
        }
        if(validationModel.getExpectedInvokeTimes() == null)
        {
            getAssertions(validCommands.size()).shouldNotBe(0);
            return;
        }

        var times = validationModel.getExpectedInvokeTimes();
        if(!times.validate(validCommands.size()))
        {
            throw new ValidationException("Commands invoked should be "+times.getType().name()+" "+times.getValue()+" but was "+validCommands.size());
        }
    }

    private PluginCommonAssertions getAssertions(Object target) {
        return new PluginCommonAssertions(target);
    }


    @Data
    private class CommandValidationModel {
        private String expectedName;
        private Times expectedInvokeTimes;
        private Invoker expectedInvoker = Invoker.Any;
        private CommandSender expectedSender;
        private String[] expectedArgs;
        private Result expectedResult = Result.Any;


        private boolean hasCommandSender() {
            return expectedSender != null;
        }
    }

    private enum Invoker {
        Any, Player, Console
    }

    private enum Result {
        Any, Success, Fail
    }
}
