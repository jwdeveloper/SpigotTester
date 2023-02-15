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

import io.github.jwdeveloper.spigot.tester.api.data.TestClassResult;
import io.github.jwdeveloper.spigot.tester.api.data.TestOptions;
import io.github.jwdeveloper.spigot.tester.implementation.EventsHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class DisplayTestHandler {
    private EventsHandler eventsHandler;
    private TestOptions options;

    public DisplayTestHandler(EventsHandler eventsHandler, TestOptions options) {
        this.eventsHandler = eventsHandler;
        this.options = options;
        eventsHandler.onTest(this::onTest);
    }

    public void onTest(TestClassResult test) {
        if (!options.isDisplayTestsInConsole()) {
            return;
        }
        var message = new MessageBuilder();
        if (test.getTestMethods().size() != 0) {
            message.newLine();
        }
        ChatColor color = ChatColor.WHITE;
        if (test.isPassed()) {
            color = ChatColor.GREEN;
            message.append(withBrackets("V", ChatColor.GREEN, ChatColor.DARK_GREEN));
        } else {
            color = ChatColor.RED;
            message.append(withBrackets("X", ChatColor.RED, ChatColor.DARK_RED));
        }
        message.append(" ").append(ChatColor.WHITE).append(test.getClassName()).append(" ");
        message.append(color)
                .append("[").append(test.testsPassed())
                .append("/").append(test.totalTests())
                .append("] ");

        message.send();

        if (!options.isDisplayTestsInConsole()) {
            return;
        }

        for (var i = 0; i < test.getTestMethods().size(); i++) {
            var child = test.getTestMethods().get(i);
            if (child.isIgnored()) {
                var builder = new MessageBuilder()
                        .append(ChatColor.DARK_GREEN)
                        .append(" > ")
                        .append(ChatColor.GREEN)
                        .append(child.getName())
                        .append(ChatColor.GRAY)
                        .append(" [Ignored]");
                builder.send();
                continue;
            }

            if (child.isPassed()) {
                var builder = new MessageBuilder()
                        .append(ChatColor.DARK_GREEN)
                        .append(" > ")
                        .append(ChatColor.GREEN)
                        .append(child.getName());
                builder.send();
                continue;
            }
            var builder = new MessageBuilder()
                    .append(ChatColor.DARK_RED)
                    .append(" > ")
                    .append(ChatColor.RED)
                    .append(child.getName());
            builder.send();
            //TODO this value should be set in SpigotTestRunner
            var errorMessage = showErrorInfo(child.getException());
            var stackTrace = showStackTrace(child.getException());
            child.setErrorMessage(ChatColor.stripColor(errorMessage));
            child.setStackTrace(ChatColor.stripColor(stackTrace));
        }
    }

    private String withBrackets(String text, ChatColor textColor, ChatColor bracketsColor) {
        return bracketsColor + "[" + textColor + text + bracketsColor + "]" + ChatColor.RESET;
    }

    private String showErrorInfo(Throwable exception) {
        var stackTrace = new MessageBuilder();
        if (exception == null) {
            return "";
        }
        var cause = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
        stackTrace.newLine()
                .space(4)
                .append(ChatColor.AQUA)
                .append("Reason").append(ChatColor.WHITE).space(1).append(cause).append(ChatColor.RESET).newLine()
                .space(4)
                .append(ChatColor.AQUA).append("Exception")
                .append(ChatColor.WHITE).space(1).append(exception.getClass().getSimpleName())
                .append(ChatColor.RESET);
        return stackTrace.send();
    }

    private String showStackTrace(Throwable exception) {

        if (exception == null) {
            return "";
        }
        var offset = 6;
        var stackTraces = "";
        for (var trace : exception.getStackTrace()) {
            var stackTrace = new MessageBuilder();
            offset = 6;
            offset = offset - (trace.getLineNumber() + "").length();
            stackTrace
                    .append(ChatColor.WHITE)
                    .space(6)
                    .append(ChatColor.WHITE, "at line")
                    .append(" ")
                    .append(ChatColor.AQUA, trace.getLineNumber())
                    .space(offset)
                    .append("in", ChatColor.WHITE)
                    .append(" ")
                    .append(ChatColor.GRAY, trace.getClassName())
                    .append(ChatColor.AQUA, "." + trace.getMethodName() + "()")
                    .append(" ")
                    .append(ChatColor.RESET);

            stackTraces += stackTrace.send()+System.lineSeparator();
        }
        new MessageBuilder().newLine().send();
        return stackTraces;
    }

    private class MessageBuilder {
        private StringBuilder builder;

        public MessageBuilder() {
            builder = new StringBuilder();
        }

        public MessageBuilder append(Object... value) {
            for (var v : value) {
                builder.append(v.toString());
            }
            return this;
        }

        public MessageBuilder newLine() {
            builder.append(System.lineSeparator());
            return this;
        }

        public MessageBuilder space(int space) {
            for (var i = 0; i < space; i++) {
                builder.append(" ");
            }
            return this;
        }

        @Override
        public String toString() {
            return builder.toString();
        }

        public String send() {
            var value = builder.toString();
            Bukkit.getConsoleSender().sendMessage(value);
            return value;
        }
    }


}
