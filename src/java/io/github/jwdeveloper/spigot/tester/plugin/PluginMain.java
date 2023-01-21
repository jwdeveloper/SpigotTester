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

package io.github.jwdeveloper.spigot.tester.plugin;

import io.github.jwdeveloper.spigot.tester.SpigotTesterAPI;
import io.github.jwdeveloper.spigot.tester.api.data.TestReport;
import io.github.jwdeveloper.spigot.tester.implementation.gson.JsonUtility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PluginMain extends JavaPlugin {

    @Override
    public void onLoad() {
        var plugins = Bukkit.getServer().getPluginManager().getPlugins();
        var config = Config.load(this.getConfig());
        var reports = new ArrayList<TestReport>();

        if (!config.isDisplayLogs())
            getLogger().setLevel(Level.OFF);

        for (var plugin : plugins) {
            if (plugin.equals(this)) {
                continue;
            }
            if (config.getIgnorePlugins().contains(plugin.getName())) {
                continue;
            }
            var report = runTests(plugin);
            reports.add(report);
        }
        var reportJson = saveReport(reports);
        if(config.isOpenWebsite())
        {
            String body = Base64.getEncoder().encodeToString(reportJson.getBytes());
            Browser.open(body);
        }
        if (config.isCloseServerAfterTests())
             System.exit(1);
    }

    private TestReport runTests(Plugin plugin) {
        var testReport = new TestReport();
        try {
            getLogger().info(plugin.getDescription().getName() + " Starting plugin");
            getLogger().info(plugin.getDescription().getName() + " OnLoad");
            plugin.onLoad();
            getLogger().info(plugin.getDescription().getName() + " OnEnable");
            plugin.onEnable();
            getLogger().info(plugin.getDescription().getName() + " Running tests");
            testReport = SpigotTesterAPI.create(plugin).withParameter(plugin, Plugin.class).configure(testOptions ->
            {
                testOptions.setGenerateReport(false);
            }).run();
            getLogger().info(plugin.getDescription().getName() + " OnDisable");
            plugin.onDisable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testReport;
    }


    private String saveReport(List<TestReport> reports)
    {
        var report = new PluginsReport();
        report.setReportId(UUID.randomUUID().toString());
        report.setCreatedAt(OffsetDateTime.now());
        report.setSpigotVersion(Bukkit.getBukkitVersion());
        report.setServerVersion(Bukkit.getVersion());
        report.setSpigotTesterVersion(this.getDescription().getVersion());
        report.setPlugins(reports);

        var json = new JsonUtility();
        try
        {
           return json.save(report, this.getDataFolder().getAbsolutePath(), "report");
        }
        catch (Exception e)
        {
            getLogger().info("Unable to save tests report");
            return "";
        }
    }

}
