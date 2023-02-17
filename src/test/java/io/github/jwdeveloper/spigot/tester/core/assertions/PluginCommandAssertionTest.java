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


import be.seeseemelk.mockbukkit.MockBukkit;
import io.github.jwdeveloper.spigot.tester.api.assertions.Times;
import io.github.jwdeveloper.spigot.tester.api.models.CommandInvokeModel;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class PluginCommandAssertionTest {

    private Player player;

    @Before
    public void setUp() {
        player = MockBukkit.mock().addPlayer();
    }

    @After
    public void tearDown() {
        MockBukkit.unload();
    }

    @Test
    public void shouldValidateByValid() {
        var assertion1 = new PluginCommandAssertion(getCommandModels(), "teleport");

        assertion1.wasInvoked(Times.once())
                .byPlayer(player)
                .withArguments(player.getName(), "10", "20", "30")
                .withSuccess();
        assertion1.validate();

        var assertion2 = new PluginCommandAssertion(getCommandModels(), "teleport");
        assertion2.wasInvoked(Times.once())
                .byPlayer(player)
                .withArguments(player.getName(), "12", "22", "32")
                .withFail();

        var assertion3 = new PluginCommandAssertion(getCommandModels(), "spawn");
        assertion3.wasInvoked(Times.once())
                .byPlayer(player)
                .withArguments("world")
                .withFail();
    }

    @Test
    public void shouldValidateByName() {
        var assertion = new PluginCommandAssertion(getCommandModels(), "teleport");
        assertion.validate();
    }

    @Test
    public void shouldValidateByArguments() {
        var assertion = new PluginCommandAssertion(getCommandModels(), "teleport");
        assertion.withArguments(player.getName(), "10", "20", "30");
        assertion.validate();
    }

    @Test
    public void shouldValidateBySender() {
        var assertion = new PluginCommandAssertion(getCommandModels(), "teleport");
        assertion.byCommandSender(player);
        assertion.validate();
    }

    @Test
    public void shouldValidateByPlayer() {
        var assertion = new PluginCommandAssertion(getCommandModels(), "teleport");
        assertion.byPlayer(player);
        assertion.validate();
    }

    @Test
    public void shouldValidateByTimesMore() {
        var assertion = new PluginCommandAssertion(getCommandModels(), "teleport");
        assertion.wasInvoked(Times.moreThan(0));
        assertion.validate();
    }

    @Test
    public void shouldValidateByTimesLess() {
        var assertion = new PluginCommandAssertion(getCommandModels(), "teleport");
        assertion.wasInvoked(Times.lessThen(3));
        assertion.validate();
    }

    @Test
    public void shouldValidateByTimesExact() {
        var assertion = new PluginCommandAssertion(getCommandModels(), "teleport");
        assertion.wasInvoked(Times.exact(2));
        assertion.validate();
    }

    @Test
    public void shouldValidateByResultSuccess() {
        var assertion = new PluginCommandAssertion(getCommandModels(), "teleport");
        assertion.withSuccess();
        assertion.validate();
    }

    private List<CommandInvokeModel> getCommandModels() {
        var teleportCmd = new CommandInvokeModel(
                true,
                player,
                null,
                "teleport",
                new String[]{player.getName(), "10", "20", "30"}
                ,""
        );

        var teleportCmdFail = new CommandInvokeModel(
                false,
                player,
                null,
                "teleport",
                new String[]{player.getName(), "12", "22", "32"}
                ,""
        );
        var spawnCmd = new CommandInvokeModel(
                false,
                player,
                null,
                "spawn",
                new String[]{"world"}
                ,""
        );

        return List.of(teleportCmd,teleportCmdFail, spawnCmd);
    }
}