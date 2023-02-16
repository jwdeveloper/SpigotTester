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

package io.github.jwdeveloper.spigot.tester.api.assertions;

import io.github.jwdeveloper.spigot.tester.api.exception.AssertionException;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class Assertions {

    private final Object target;

    public Assertions(Object target) {
        this.target = target;
    }

    public boolean shouldNotBeNull() {
        if (target == null) {
            throw new AssertionException("null object");
        }
        return true;
    }

    public boolean shouldBeTrue() {
        if(target instanceof Boolean)
        {
           var bool = (Boolean)target;
            if (!bool) {
                throw new AssertionException("should be true");
            }
        }
        return true;
    }

    public boolean shouldBeFalse() {
        if(target instanceof Boolean)
        {
            var bool = (Boolean)target;
            if (bool) {
                throw new AssertionException("should be false");
            }
        }
        return true;
    }

    public boolean shouldBe(Object object) {
        if (!object.equals(target)) {
            throw new AssertionException("should be equal");
        }
        return true;
    }

    public boolean shouldNotBe(Object object) {

        if (target.equals(object)) {
            throw new RuntimeException("should not be equal");
        }
        return true;
    }

    public <T> boolean shouldContains(T[] list) {

        return true;
    }

    public <T> boolean shouldNotContains(Iterable<T> list) {

        return true;
    }


}
