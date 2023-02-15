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

package io.github.jwdeveloper.spigot.tester.implementation;

import io.github.jwdeveloper.spigot.tester.plugin.PluginMain;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.List;

public class SpigotEventProxy implements Listener {
    private final List<Event> events;
    private RegisteredListener listener;

    public SpigotEventProxy() {
        events = new ArrayList<>();
        PluginMain.getPlugin(PluginMain.class).getServer().getPluginManager().registerEvents(this, PluginMain.getPlugin(PluginMain.class));
        listener = new RegisteredListener(this, (listener, event) ->
        {
            events.add(event);
        }, EventPriority.MONITOR, PluginMain.getPlugin(PluginMain.class), false);
    }

    public void startSession() {
        stopSession();
        for (var handler : HandlerList.getHandlerLists()) {
            handler.register(listener);
        }
    }

    public List<Event> getEvents() {
        var result = new ArrayList<Event>();
        result.addAll(events);
        return result;
    }

    public void stopSession() {
        for (var handler : HandlerList.getHandlerLists()) {
            handler.unregister(listener);
        }
        events.clear();
    }
}
