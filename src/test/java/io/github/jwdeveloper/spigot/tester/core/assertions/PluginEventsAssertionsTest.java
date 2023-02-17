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
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PluginEventsAssertionsTest {


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
        var assetTeleportEvent = new PluginEventsAssertions(getEvents(), PlayerTeleportEvent.class);

        assetTeleportEvent.wasInvoked(Times.exact(2))
                .validate();

        var assertJoinEvent = new PluginEventsAssertions(getEvents(), PlayerJoinEvent.class);
        assertJoinEvent.wasInvoked(Times.once())
                .validate();
    }


    public List<Event> getEvents() {
        var teleportEvent = new PlayerTeleportEvent(player, player.getLocation(), player.getLocation().add(1, 0, 0));
        teleportEvent.setCancelled(true);

        var teleportEvent2 = new PlayerTeleportEvent(player, player.getLocation(), player.getLocation().add(1, 3, 0));
        teleportEvent2.setCancelled(false);

        var joinEvent = new PlayerJoinEvent(player, "hello");

        return List.of(teleportEvent, teleportEvent2, joinEvent);
    }


}