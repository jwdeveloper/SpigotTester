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

package io.github.jwdeveloper.spigot.tester.temp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;

public class EventListener implements Listener
{

    public EventListener()
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE+"BLUE MESSAGES ARE TEMPORARY FOR DEBUG REASON");
    }


    @EventHandler
    public void message(AsyncPlayerChatEvent event)
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE+"CHAT EVENT "+event.getPlayer().getName()+" "+event.getPlayer().getName());
    }
    @EventHandler
    public void cmd(PlayerCommandPreprocessEvent  event)
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE+"Player CMD event "+event.getPlayer().getName());
    }
    @EventHandler
    public void serverCmd(ServerCommandEvent event)
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE+"Server CMD event "+event.getSender().getName());
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent event)
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE+"TELEPOERT EVENT "+event.getPlayer().getName());
    }

    @EventHandler
    public void quit(PlayerQuitEvent event)
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE+"quit EVENT "+event.getPlayer().getName());
    }

    @EventHandler
    public void joint(PlayerJoinEvent event)
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE+"joint EVENT "+event.getPlayer().getName());
    }
}
