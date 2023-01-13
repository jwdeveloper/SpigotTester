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

package jw.spigot;

import jw.spigot.tester.SpigotTester;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        SpigotTester.create(this)
                //
                //will parameter will be passed to test constructor
                .withParameter(this, JavaPlugin.class)
                .configure(options ->
                {
                    //tests will be running in order
                    options.setRunInParallel(false);

                    //tests will be running in parallel
                    options.setRunInParallel(true);

                    //output report file path
                    options.setReportPath("...");
                })
                .onTest(result ->
                {
                    Bukkit.getLogger().info("Tests info: "+result.toString());
                })
                .onFinish(report ->
                {
                    Bukkit.getLogger().info("Tests finished report: "+report.toString());
                })
                .onException(e ->
                {
                    e.printStackTrace();
                })
                .run();
    }
}
