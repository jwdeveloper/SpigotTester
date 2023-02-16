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

package io.github.jwdeveloper.spigot.tester.plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.List;

@Getter
@AllArgsConstructor
public class Config  {
    private final boolean closeServerAfterTests;
    private final boolean displayLogs;
    private final boolean openWebsite;
    private final List<String> ignorePlugins;

    public static Config load(Plugin plugin) {
        plugin.saveDefaultConfig();
        var configuration = plugin.getConfig();

        var closeServerAfterTests = configuration.getBoolean("close-server-after-tests");
        var displayLogs = configuration.getBoolean("display-logs");
        var generateWebsite= configuration.getBoolean("open-report-in-website");
        var ignorePlugins = configuration.getStringList("display-logs");
        return new Config(closeServerAfterTests, displayLogs,generateWebsite, ignorePlugins);
    }
}
