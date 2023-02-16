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

package io.github.jwdeveloper.spigot.tester.implementation.handlers;

import io.github.jwdeveloper.spigot.tester.api.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.api.data.TestPluginReport;
import io.github.jwdeveloper.spigot.tester.implementation.gson.JsonUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ReportGeneratorHandler {
    private final EventsHandler eventsHandler;
    private final TestOptions options;
    private final JsonUtility json;
    private final Plugin plugin;

    public ReportGeneratorHandler(EventsHandler eventsHandler,
                                  JsonUtility jsonUtility,
                                  TestOptions options,
                                  Plugin plugin) {
        this.eventsHandler = eventsHandler;
        this.options = options;
        this.plugin = plugin;
        this.json = jsonUtility;
        eventsHandler.onFinish(this::onFinish);
    }


    public void onFinish(TestPluginReport report) {
        if (!options.isGenerateReport()) {
            return;
        }

        var outputPath = options.getReportPath();
        if (outputPath == null) {
            outputPath = plugin.getDataFolder().getAbsolutePath();
        }

        var reportName = options.getReportName();
        if (reportName == null) {
            reportName = "report";
        }


        try {
            json.save(report, outputPath, reportName);
            Bukkit.getConsoleSender().sendMessage(" ");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Tests report generated: " + ChatColor.WHITE + outputPath + File.separator + reportName+".json");
            Bukkit.getConsoleSender().sendMessage(" ");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Unable to generate tests report");
            e.printStackTrace();
        }

    }
}
