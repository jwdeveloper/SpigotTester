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

package io.github.jwdeveloper.spigot.tester.temp.formatters;

import io.github.jwdeveloper.reflect.api.exceptions.MethodValidationException;
import io.github.jwdeveloper.reflect.api.validators.ValidationResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringJoiner;

public class MethodExceptionFormatter
{
    private final MethodValidationException exception;

    @Data
    @AllArgsConstructor
    public class MappedError {
        private String message;
        private Method method;
    }


    public MethodExceptionFormatter(MethodValidationException exception) {
        this.exception = exception;
    }


    public void show() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + exception.getMessage());
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + exception.getModel().getName()+" "+exception.getModel().getReturnType());

        var reas = new MappedError[exception.getValidationResult().getLogs().size()];
        var i = 0;
        for (var o : exception.getValidationResult().getLogs()) {
            var cast = (ValidationResult<?>) o;
            var method = (Method) cast.getValue();
            reas[i] = new MappedError(cast.getMessage(), method);
            i++;
        }
        Arrays.sort(reas, new UserNameComparator());

        for (var result : reas) {
            if (Modifier.isNative(result.getMethod().getModifiers())) {
                continue;
            }

            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + result.getMessage() + " " + ChatColor.YELLOW + formatMethod(result.getMethod()));
        }
    }


    private String formatMethod(Method method) {


        var sj = new StringBuilder();
        sj.append(ChatColor.BLUE + " ").append(Modifier.toString(method.getModifiers()));
        sj.append(ChatColor.AQUA + " ").append(method.getReturnType().getName());
        sj.append(ChatColor.YELLOW + " ").append(method.getName());
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            sj.append(ChatColor.WHITE + "()");
            return sj.toString();
        }

        sj.append(ChatColor.WHITE + "(");

        StringJoiner argJoiner = new StringJoiner(ChatColor.WHITE + ", ");
        for (Class<?> parameter : method.getParameterTypes()) {
            argJoiner.add(ChatColor.DARK_AQUA + parameter.getName());
        }

        sj.append(argJoiner.toString());
        sj.append(ChatColor.WHITE + ")");
        return sj.toString();
    }

    class UserNameComparator implements Comparator<MappedError> {
        public int compare(MappedError a, MappedError b) {
            return a.getMethod().getName().compareTo(b.getMethod().getName());
        }
    }
}
