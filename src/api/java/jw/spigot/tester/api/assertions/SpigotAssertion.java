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

package jw.spigot.tester.api.assertions;

import java.util.Collection;
import java.util.List;

public class SpigotAssertion {
    public static void shouldBeNotNull(Object object)  {
        if (object == null) {
            throw new RuntimeException("null object");
        }
    }

    public static void shouldBeTrue(boolean object) {
        if (!object) {
            throw new RuntimeException("should be true");
        }
    }

    public static void shouldBeEqual(Object o1, Object o2)  {


        if (!o1.equals(o2)) {
            throw new RuntimeException("should be equal");
        }
    }

    public static <T> void shouldContains(List<T> list, T object)  {
        if (!list.contains(object)) {
            throw new RuntimeException("list not contains object");
        }
    }

    public static <T> void shouldNotContains(List<T> list, T object) {
        if (list.contains(object)) {
            throw new RuntimeException("list should not contains object");
        }
    }

    public static void shouldBeFalse(boolean object) {
        if (object == true) {
            throw new RuntimeException("should be false");
        }
    }

    public static <T> void shouldBeNotEmpty(Collection<T> object) {
        shouldBeNotNull(object);
        if (object.size() == 0) {
            throw new RuntimeException("should be false");
        }
    }
    public static void shouldNotBeEqual(Object o1, Object o2) {

        if (o1.equals(o2))
        {
            throw new RuntimeException("should not be equal");
        }
    }
}
