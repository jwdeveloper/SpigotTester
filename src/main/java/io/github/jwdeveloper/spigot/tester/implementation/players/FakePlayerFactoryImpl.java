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

package io.github.jwdeveloper.spigot.tester.implementation.players;


import io.github.jwdeveloper.spigot.tester.api.players.PlayerFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakePlayerFactoryImpl implements PlayerFactory {

    private final Map<UUID, FakePlayer> players;
    private final NmsCommunicator nmsCommunicator;

    public FakePlayerFactoryImpl(NmsCommunicator playerNms) {
        players = new HashMap<>();
        this.nmsCommunicator = playerNms;
    }

    public void clear() {
        for (var player : players.values()) {
            player.disconnect();
        }
    }

    @Override
    public Player createPlayer() {
        return createPlayer(UUID.randomUUID());
    }
    public Player createPlayer(UUID uuid) {
        return createPlayer(uuid, uuid.toString());
    }
    public Player createPlayer(UUID uuid, String name) {
        try {
            var fakePlayer = nmsCommunicator.createFakePlayer(uuid, name);
            players.put(uuid, fakePlayer);
            fakePlayer.connect();
            return fakePlayer.getPlayer();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("Unable to create Fake Player, this version of spigot may not be supported");
            e.printStackTrace();
            return null;
        }
    }
}
