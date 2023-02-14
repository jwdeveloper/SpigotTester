/*
 * MIT License
 *
 * Copyright (c)  2023. jwdeveloper
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

package io.github.jwdeveloper.spigot.exampleplugintotest.tests;

import io.github.jwdeveloper.spigot.tester.api.SpigotTest;
import io.github.jwdeveloper.spigot.tester.api.annotations.Test;
import io.github.jwdeveloper.spigot.tester.api.assertions.SpigotAssertion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ShouldLoadPlugin implements SpigotTest
{
    private Player player;

    @Override
    public void beforeAll() {
        player = Bukkit.getOnlinePlayers().stream().findAny().get();
    }


    @Test
    public void shouldTriggerCommands()
    {
        player.performCommand("plugins");
        Bukkit.getConsoleSender().sendMessage("elo elo elo");
        Bukkit.broadcastMessage("Siema wam");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "plugins");
        SpigotAssertion.shouldInvokeEvent(event ->
        {

        }, PlayerTeleportEvent.class);
        SpigotAssertion.shouldBeTrue(true);
    }

    @Test
    public void shouldTeleport2Times()
    {
        player.teleport(new Location(Bukkit.getWorlds().get(0), 0,100,0));
        player.teleport(new Location(Bukkit.getWorlds().get(0), 0,100,0));
        SpigotAssertion.shouldInvokeEvent(event ->
        {

        }, PlayerTeleportEvent.class);
        SpigotAssertion.shouldBeTrue(true);
    }


}
