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

package io.github.jwdeveloper.spigot.tester.core.context;

import io.github.jwdeveloper.spigot.tester.api.context.TestContext;
import io.github.jwdeveloper.spigot.tester.core.assertions.PluginAssertionFactory;
import io.github.jwdeveloper.spigot.tester.api.players.PlayerFactory;
import io.github.jwdeveloper.spigot.tester.core.context.events.SpigotEventCollector;
import io.github.jwdeveloper.spigot.tester.core.context.commands.SpigotCommandCollector;
import io.github.jwdeveloper.spigot.tester.core.context.players.FakePlayerFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public class PluginsTestsContext implements TestContext {

    private final Plugin plugin;
    private final FakePlayerFactory playerFactory;
    private final Function<Class<?>, Object> parameterProvider;
    private final SpigotEventCollector spigotEventCollector;
    private final SpigotCommandCollector commandCollector;

    public PluginsTestsContext(Plugin plugin,
                               FakePlayerFactory playerFactory,
                               Function<Class<?>, Object> parameterProvider,
                               SpigotEventCollector spigotEventCollector) {
        this.plugin = plugin;
        this.playerFactory = playerFactory;
        this.parameterProvider = parameterProvider;
        this.spigotEventCollector = spigotEventCollector;
        commandCollector = new SpigotCommandCollector();
    }

    public <T> T getParameter(Class<T> tClass) {
        return (T) parameterProvider.apply(tClass);
    }

    @Override
    public void invokeCommand(CommandSender commandSender, String commandName, String... args) {
        commandCollector.invokeCommand(commandSender, commandName, args);
    }

    @Override
    public void invokeCommand(CommandSender commandSender, String commandName) {
        commandCollector.invokeCommand(commandSender, commandName);
    }

    @Override
    public void invokeEvents(Event... events) {
        spigotEventCollector.invokeEvents(events);
    }

    @Override
    public void displayLogs(String title) {


        if(title == null)
        {
            title = "";
        }


        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"] -------- Events ------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"]");
        for(var event : spigotEventCollector.getEvents())
        {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"]  "+event.getEventName());
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"]");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"] -------- Commands ------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"]");
        for(var event : commandCollector.getCommands())
        {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"]  /"+event.getCommandLine());
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"]");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"] -------- Players ------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"]");
        for(var player : playerFactory.getFakePlayers())
        {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"["+title+"]  /"+player.getPlayer().toString());
        }
    }

    @Override
    public PlayerFactory getPlayerFactory() {
        return playerFactory;
    }


    @Override
    public PluginAssertionFactory getAssertionFactory() {
        return new PluginAssertionFactory(spigotEventCollector, commandCollector);
    }


    @Override
    public Plugin plugin() {
        return plugin;
    }


    public void start() {
        commandCollector.start();
        spigotEventCollector.startCollectingEvents();
    }

    public void stop() {
        playerFactory.removeFakePlayers();
        spigotEventCollector.stopCollectingEvents();
        commandCollector.stop();
    }
}
