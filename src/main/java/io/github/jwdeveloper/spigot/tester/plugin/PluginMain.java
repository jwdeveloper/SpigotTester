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

import io.github.jwdeveloper.reflect.api.exceptions.ValidationException;
import io.github.jwdeveloper.reflect.implementation.FluentReflect;
import io.github.jwdeveloper.spigot.tester.SpigotTesterAPI;
import io.github.jwdeveloper.spigot.tester.api.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.api.data.TestPluginReport;
import io.github.jwdeveloper.spigot.tester.api.data.TestsReport;
import io.github.jwdeveloper.spigot.tester.implementation.gson.JsonUtility;
import io.github.jwdeveloper.spigot.tester.implementation.players.NmsCommunicator;
import io.github.jwdeveloper.spigot.tester.temp.ValidationExceptionDisplay;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PluginMain extends JavaPlugin {

    @Override
    public void onEnable() {
        waitForAllPluginToBeEnabled(plugins ->
        {
            getLogger().info("Server version: " + PluginMain.getVersion());
            var optional = createNmsCommunicator();
            if (optional.isEmpty()) {
                getLogger().warning("SpigotTester will not working for this minecraft version, try different");
                return;
            }
            var nmsCommunicator = optional.get();

            getLogger().info("Loading config");
            var config = Config.load(this);
            var reports = new ArrayList<TestPluginReport>();

            if (!config.isDisplayLogs())
                getLogger().setLevel(Level.OFF);

            getLogger().info("Preparing to start tests");
            for (var plugin : plugins) {
                if (config.getIgnorePlugins().contains(plugin.getName())) {
                    continue;
                }
                var report = runTests(plugin, nmsCommunicator);
                reports.add(report);
            }

            getLogger().info("Saving report");
            var reportJson = saveReport(reports);

            if (config.isOpenWebsite()) {
                String body = Base64.getEncoder().encodeToString(reportJson.getBytes());
                Browser.open(body);
            }
            getLogger().info("Tests done");
            if (config.isCloseServerAfterTests())
                System.exit(1);
        });

    }

    private Optional<NmsCommunicator> createNmsCommunicator() {
        try {
            var fluentReflect = new FluentReflect(getVersion());
            var nmsCommunicator = new NmsCommunicator(fluentReflect);
            return Optional.of(nmsCommunicator);
        } catch (ValidationException e) {
            ValidationExceptionDisplay.showError(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void waitForAllPluginToBeEnabled(Consumer<List<Plugin>> onEnable) {
        var plugins = Bukkit.getPluginManager().getPlugins();
        Bukkit.getScheduler().runTaskTimer(this, (task) ->
        {
            for (var plugin : plugins) {
                if (!plugin.isEnabled())
                    return;
            }
            var pluginsToTest = Arrays
                    .stream(plugins)
                    .filter(plugin -> !plugin.equals(this))
                    .collect(Collectors.toList());

            onEnable.accept(pluginsToTest);
            task.cancel();
        }, 10, 10);
    }

    private TestPluginReport runTests(Plugin plugin, NmsCommunicator nmsCommunicator) {
        getLogger().info("Running tests for plugin: " + plugin.getDescription().getName());
        return SpigotTesterAPI.create(plugin, new TestOptions(), nmsCommunicator)
                .configure(testOptions ->
                {
                    //To avoid generating report file for each plugin
                    testOptions.setGenerateReport(false);
                })
                .onException(Throwable::printStackTrace)
                .run();
    }


    private String saveReport(List<TestPluginReport> reports) {
        var report = new TestsReport();
        report.setReportId(UUID.randomUUID().toString());
        report.setCreatedAt(OffsetDateTime.now());
        report.setSpigotVersion(Bukkit.getBukkitVersion());
        report.setServerVersion(Bukkit.getVersion());
        report.setSpigotTesterVersion(this.getDescription().getVersion());
        report.setPlugins(reports);
        for (var pluginReport : reports) {
            if (!pluginReport.isPassed()) {
                report.setPassed(false);
                break;
            }
        }

        var json = new JsonUtility();
        try {
            return json.save(report, this.getDataFolder().getAbsolutePath(), "report");
        } catch (Exception e) {
            getLogger().info("Unable to save tests report");
            return "";
        }
    }

    public static String getVersion() {
        var packageName = Bukkit.getServer().getClass().getPackageName();
        var index = packageName.lastIndexOf('.');
        return packageName.substring(index + 1);
    }
}
