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

package io.github.jwdeveloper.spigot.tester.temp.formatters;

import io.github.jwdeveloper.reflect.api.exceptions.FieldValidationException;
import io.github.jwdeveloper.reflect.api.validators.ValidationResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;

public class FieldExceptionFormatter {
    private final FieldValidationException exception;

    @Data
    @AllArgsConstructor
    public class MappedError {
        private String message;
        private Field field;
    }

    public FieldExceptionFormatter(FieldValidationException exception) {
        this.exception = exception;
    }


    public void show() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + exception.getMessage());
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Looking for: " + formatModel());
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " ");
        var reas = new MappedError[exception.getValidationResult().getLogs().size()];
        var i = 0;
        for (var o : exception.getValidationResult().getLogs()) {
            var cast = (ValidationResult<?>) o;
            var method = (Field) cast.getValue();
            reas[i] = new MappedError(cast.getMessage(), method);
            i++;
        }
        Arrays.sort(reas, new UserNameComparator());

        for (var result : reas) {
            if (Modifier.isNative(result.getField().getModifiers())) {
                continue;
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + result.getMessage() + " " + ChatColor.YELLOW + formatMethod(result.getField()));
        }
    }

    private String formatModel() {

        var model = exception.getModel();
        var sj = new StringBuilder();
        sj.append(ChatColor.BLUE + " ").append(model.getVisibility().name().toLowerCase());
        if (model.isFinal()) {
            sj.append(ChatColor.BLUE + " ").append("final");
        }
        if (model.isStatic()) {
            sj.append(ChatColor.BLUE + " ").append("static");
        }
        sj.append(ChatColor.AQUA + " ").append(model.getType());

        if (model.getGenerics().size() != 0) {
            sj.append(ChatColor.GOLD + "<");
            for (var i = 0; i < model.getGenerics().size(); i++) {
                sj.append(ChatColor.YELLOW + model.getGenerics().get(i));
                if (i != model.getGenerics().size() - 1) {
                    sj.append(ChatColor.WHITE + ", ");
                }
            }
            sj.append(ChatColor.GOLD + ">");
        }

        sj.append(ChatColor.GREEN + " ").append(model.getName() == null ? "IGNORE_NAME" : model.getName());

        sj.append("     " + ChatColor.YELLOW + " looking in " + exception.getModel().getParentClass() + " class");
        return sj.toString();
    }

    private String formatMethod(Field field) {
        var sj = new StringBuilder();
        sj.append(ChatColor.BLUE + " ").append(Modifier.toString(field.getModifiers()));
        if (field.getType().getName().toLowerCase().equals(exception.getModel().getType().toLowerCase())) {
            sj.append(ChatColor.GREEN + " ").append(field.getType().getName());
        } else {
            sj.append(ChatColor.AQUA + " ").append(field.getType().getName());
        }
        sj.append(getGenericInfo(field));

        sj.append(ChatColor.YELLOW + " ").append(field.getName());
        return sj.toString();
    }

    private String getGenericInfo(Field field) {
        Type genericType = field.getGenericType();
        var sj = new StringBuilder();

        if (!(genericType instanceof ParameterizedType))
            return sj.toString();

        Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
        // Type parameters need to match in sequence
        sj.append(ChatColor.GOLD + "<");
        for (int i = 0; i < types.length; i++) {
            var name = types[i].getTypeName();
            sj.append(ChatColor.YELLOW + name);
            if (i != types.length - 1) {
                sj.append(ChatColor.WHITE + ", ");
            }
        }
        sj.append(ChatColor.GOLD + ">");
        return sj.toString();
    }

    class UserNameComparator implements Comparator<MappedError> {
        public int compare(MappedError a, MappedError b) {
            return a.getField().getType().getName().compareTo(b.getField().getType().getName());
        }
    }
}
