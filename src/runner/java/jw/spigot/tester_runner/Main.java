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

package jw.spigot.tester_runner;

import jw.spigot.tester.SpigotTester;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Main {
    private static String testsServersPath = "D:\\MC\\tests";
    private static String serverDownloadUrl = "https://download.getbukkit.org/spigot";

    private static String serverDownloadUrl2 = "https://cdn.getbukkit.org/spigot";

    private static String pluginToTest = "D:\\MC\\spigot_1_17_0\\plugins\\HolographicPanels.jar";

    public static void main(String[] args) throws IOException, InterruptedException {
        var version = "1.16.1";
        downloadVersion(version);


    }

    private static void downloadVersion(String version) throws IOException, InterruptedException {
        var downloadUrl = serverDownloadUrl + "/spigot-" + version + ".jar";
        var downloadUrl2 = serverDownloadUrl2 + "/spigot-" + version + ".jar";
        var directory = Paths.get(testsServersPath, version.replace(".", "_"));
        var outputPath = Paths.get(directory.toString(), "spigot.jar");

        if (!Files.exists(outputPath)) {
            System.out.println("Downloading server: " + downloadUrl);
            var firstTru = downloadFile(downloadUrl, outputPath);
            if(firstTru == false)
            {
                System.out.println("Downloading server from alternative link: " + downloadUrl);
                downloadFile(downloadUrl2, outputPath);
            }
            System.out.println("Server downloaded");
        } else {
            System.out.println("Server already downloaded for " + version);
        }
        System.out.println("Creating EULA");
        createEula(directory);


        System.out.println("Copying Plugin to destination");
        copyPluginToTest(directory);

        runServer(directory);
    }


    private static void runServer(Path serverPath) throws IOException, InterruptedException {
        //String javaHome = System.getProperty("java.home")+"\\bin\\java";
        ProcessBuilder pb = new ProcessBuilder( "C:\\Users\\ja\\.jdks\\corretto-16.0.2\\bin\\java","-Xmx2G", "-jar","-DIReallyKnowWhatIAmDoingISwear", "D:\\MC\\tests\\1_17\\spigot.jar");
        pb.directory(new File(serverPath.toString()));
        var proc = pb.start();

        System.out.println("Server is running");
        InputStream in = proc.getInputStream();
        InputStream err = proc.getErrorStream();

        while (true)
        {
            byte b[] = new byte[in.available()];
            in.read(b, 0, b.length);

            var message = new String(b);
            if( !message.equals("") || (message.length() != 0  && message.charAt(0) == '['))
            {
                System.out.println(message);
            }


            byte c[] = new byte[err.available()];
            err.read(c, 0, c.length);
          //  System.out.println(new String(c));
            Thread.sleep(100);
        }
      //  var end =   proc.waitFor();
    }

    private static void createEula(Path path) {
        var content = """
                #By changing the setting below to TRUE you are indicating your agreement to our EULA (https://aka.ms/MinecraftEULA).
                #Fri Jul 29 17:48:03 CEST 2022
                eula=true
                """;

        saveFile(content, Paths.get(path.toString(), "eula.txt"));
    }

    private static void copyPluginToTest(Path serverDirectory) throws IOException {
        var pluginsDirectory = Paths.get(serverDirectory.toString(), "plugins");
        Files.createDirectories(pluginsDirectory);
        var input = Paths.get(pluginToTest);
        var output = Paths.get(pluginsDirectory.toString(), "PluginUnderTest.jar");
        Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
    }

    private static boolean downloadFile(String url, Path output) throws IOException {
        try {
            InputStream in = new URL(url).openStream();
            Files.createDirectories(output);
            Files.copy(in, output, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("unable to download spigot for: "+url+" "+e.getMessage());
            return false;
        }
    }

    static boolean saveFile(String content, Path path) {
        try (FileWriter file = new FileWriter(path.toString())) {
            file.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
