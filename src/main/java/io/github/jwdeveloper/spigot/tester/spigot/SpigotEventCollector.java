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

package io.github.jwdeveloper.spigot.tester.spigot;

import io.github.jwdeveloper.spigot.tester.api.collectors.EventCollector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.List;

public class SpigotEventCollector implements Listener, EventCollector
{
    private final List<Event> events;
    private final RegisteredListener listener;

    public SpigotEventCollector(Plugin plugin) {
        events = new ArrayList<>();
        listener = new RegisteredListener(this,
                (listener, event) ->
                {
                    events.add(event);
                },
                EventPriority.MONITOR,
                plugin,
                false);
    }

    public void startCollectingEvents() {
        stopCollectingEvents();
        for (var handler : HandlerList.getHandlerLists()) {
            handler.register(listener);
        }
    }

    public List<Event> getEvents() {
        Bukkit.getConsoleSender().sendMessage("===================");
        for(var e : events)
        {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+ e.getEventName());
        }

        return new ArrayList<Event>(events);
    }

    public void stopCollectingEvents() {
        for (var handler : HandlerList.getHandlerLists()) {
            handler.unregister(listener);
        }
        events.clear();
    }
}
