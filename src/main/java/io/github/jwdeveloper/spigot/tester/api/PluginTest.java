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

package io.github.jwdeveloper.spigot.tester.api;

import io.github.jwdeveloper.spigot.tester.api.context.TestContext;
import io.github.jwdeveloper.spigot.tester.api.assertions.CommandAssertions;
import io.github.jwdeveloper.spigot.tester.api.assertions.CommonAssertions;
import io.github.jwdeveloper.spigot.tester.api.assertions.EventsAssertions;
import io.github.jwdeveloper.spigot.tester.api.assertions.PlayerAssertions;
import io.github.jwdeveloper.spigot.tester.api.exception.AssertionException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public abstract class PluginTest  {

    /* Method when class is loaded, can be overridden   */
    public void before() {
    }

    /* Method called before each test, can be overridden  */
    public void beforeEachTest() {
    }

    /* Method called after each test, can be overridden  */
    public void afterEachTest() {
    }

    /* Method called when all tests have been performed, can be overridden  */
    public void after() {
    }

    private TestContext testContext;
    /*
     * Called by test engine when class is ready to perform tests
     */
    private void setTestContext(TestContext testContext) {
        this.testContext = testContext;
    }

    private TestContext getTestContext() {
        if (testContext == null) {
            throw new AssertionException("Using PluginTest methods in constructor is not allowed");
        }
        return testContext;
    }

    /* Assertion utilities  */
    protected final CommonAssertions assertThat(Object target) {
        return getTestContext().getAssertionFactory().assertThat(target);
    }


    /* Assertion utilities  */
    protected final PlayerAssertions assertThatPlayer(Player player) {
        return getTestContext().getAssertionFactory().assertThatPlayer(player);
    }


    /* Assertion utilities  */
    protected final <T extends Event> EventsAssertions<T> assertThatEvent(Class<T> eventClass) {
        return getTestContext().getAssertionFactory().<T>assertThatEvent(eventClass);
    }


    /* Assertion utilities  */
    protected final CommandAssertions assertThatCommand(String commandName) {
        return getTestContext().getAssertionFactory().assertThatCommand(commandName);
    }


    /* Creates fake player  */
    protected final Player addPlayer() {
        var uuid = UUID.randomUUID();
        return getTestContext().getPlayerFactory().createPlayer(uuid, uuid.toString());
    }


    /* Creates fake player  */
    protected final Player addPlayer(String name) {
        return addPlayer(UUID.randomUUID(), name);
    }


    /* Creates fake player  */
    protected final Player addPlayer(UUID uuid, String name) {
        return getTestContext().getPlayerFactory().createPlayer(uuid, name);
    }

    /* Get parameter that have been added in TestRunnerBuilder.addParameter() */
    protected final <T> T getParameter(Class<T> clazz) {
        return getTestContext().getParameter(clazz);
    }


    /* Current tested plugin instance */
    protected final Plugin getPlugin() {
        return getTestContext().plugin();
    }


    /* invokes command, that could be later assert  */
    protected final void invokeCommand(CommandSender commandSender, String commandLine) {
        getTestContext().invokeCommand(commandSender,commandLine);
    }


    /* invokes command, that could be later assert  */
    protected final void invokeCommand(CommandSender commandSender, String commandName, String ... args) {
        getTestContext().invokeCommand(commandSender,commandName, args);
    }


    /* invokes events */
    protected final <T extends Event> void invokeEvent(T ... events) {
        getTestContext().invokeEvents(events);
    }


    /* displays invoked Events / Commands / Players */
    protected final void displayLogs()
    {
        displayLogs("");
    }

    /* displays invoked Events / Commands / Players */
    protected final void displayLogs(String title)
    {
        getTestContext().displayLogs(title);
    }
}
