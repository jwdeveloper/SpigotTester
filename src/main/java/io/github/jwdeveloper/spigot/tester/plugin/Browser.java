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


import java.io.IOException;

public class Browser {
    public static void open(String url) {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.indexOf("win") >= 0) {
                windows(url);
            }
            if (os.indexOf("mac") >= 0) {
                mac(url);
            }
            if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
                linux(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void windows(String url) throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
    }

    private static void linux(String url) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] browsers = {"google-chrome", "firefox", "mozilla", "epiphany", "konqueror",
                "netscape", "opera", "links", "lynx"};

        StringBuffer cmd = new StringBuffer();
        for (int i = 0; i < browsers.length; i++)
            if (i == 0)
                cmd.append(String.format("%s \"%s\"", browsers[i], url));
            else
                cmd.append(String.format(" || %s \"%s\"", browsers[i], url));
        // If the first didn't work, try the next browser and so on

        rt.exec(new String[]{"sh", "-c", cmd.toString()});
    }

    private static void mac(String url) throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec("open " + url);
    }

}