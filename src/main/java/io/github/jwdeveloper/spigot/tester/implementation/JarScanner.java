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

package io.github.jwdeveloper.spigot.tester.implementation;


import lombok.Getter;

import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class JarScanner {
    @Getter
    private final List<Class<?>> classes;
    private final Map<Class<?>, List<Class<?>>> byInterfaceCache;

    private final Map<Class<?>, List<Class<?>>> byParentCatch;

    public JarScanner(Class<?> clazz) {
        classes = loadPluginClasses(clazz);
        byInterfaceCache = new IdentityHashMap<>();
        byParentCatch = new HashMap<>();
    }


    private static List<Class<?>> loadPluginClasses(final Class<?> clazz) {
        final var source = clazz.getProtectionDomain().getCodeSource();
        if (source == null) return Collections.emptyList();
        final var url = source.getLocation();
        try (final var zip = new ZipInputStream(url.openStream())) {
            final List<Class<?>> classes = new ArrayList<>();
            while (true) {
                final ZipEntry entry = zip.getNextEntry();
                if (entry == null) break;
                if (entry.isDirectory()) continue;
                var name = entry.getName();
                if (!name.endsWith(".class")) continue;
                name = name.replace('/', '.').substring(0, name.length() - 6);
                try {
                    classes.add(Class.forName(name, false, clazz.getClassLoader()));
                } catch (NoClassDefFoundError | ClassNotFoundException e) {
                    System.out.println("Unable to load class:" + name);
                }
            }
            return classes;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Collection<Class<?>> findBySuperClass(Class<?> parentClass) {
        if (byParentCatch.containsKey(parentClass)) {
            return byParentCatch.get(parentClass);
        }
        var result = new ArrayList<Class<?>>();
        for (var _class : classes) {
            if (isClassContainsType(_class, parentClass)) {
                result.add(_class);
            }
        }
        byParentCatch.put(parentClass, result);
        return result;
    }

    private boolean isClassContainsType(Class<?> type, Class<?> searchType) {

        while (true) {
            if (type.isAssignableFrom(searchType)) {
                return true;
            }
            type = type.getSuperclass();

            if (type == null) {
                return false;
            }
            if (type.equals(Object.class)) {
                return false;
            }
        }
    }
}
