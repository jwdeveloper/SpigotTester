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

package io.github.jwdeveloper.spigot.exampleplugintotest;


import io.github.jwdeveloper.spigot.tester.api.PluginTest;
import io.github.jwdeveloper.spigot.tester.api.annotations.Test;
import io.github.jwdeveloper.spigot.tester.api.assertions.Times;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionAttachment;

public class ExampleTests extends PluginTest {


    @Test(name = "crafting permission test")
    public void shouldUseCrafting() {
        //Arrange
        Player player = addPlayer("mike");
        CraftingManager craftingManager = getParameter(CraftingManager.class);
        PermissionAttachment attachment = player.addAttachment(getPlugin());
        attachment.setPermission("crating", true);

        //Act
        boolean result = craftingManager.canPlayerUseCrating(player);

        //Assert
        assertThat(result).shouldBeTrue();

        assertThatPlayer(player)
                .hasName("mike")
                .hasPermission("crating");
    }

    @Test(name = "teleport only player with op")
    public void shouldBeTeleported() {
        //Arrange
        Player playerJoe = addPlayer("joe");
        Player playerMike = addPlayer("mike");

        //Act
        invokeCommand(playerJoe, "teleport " + playerJoe.getName() + " 1 3 3");

        playerMike.setOp(true);
        invokeCommand(playerMike, "teleport " + playerMike.getName() + " 1 2 3");

        //Assert
        assertThatEvent(PlayerTeleportEvent.class)
                .wasInvoked(Times.once())
                .validate();

        assertThatCommand("teleport")
                .wasInvoked(Times.once())
                .byPlayer(playerJoe)
                .validate();

        assertThatCommand("teleport")
                .wasInvoked(Times.once())
                .byPlayer(playerMike)
                .validate();
    }

}
