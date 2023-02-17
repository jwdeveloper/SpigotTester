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

package io.github.jwdeveloper.spigot.tester.api.assertions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Times {
    @Getter
    private int value;

    @Getter
    private TimesType type;

    public boolean validate(int input) {
        switch (type) {
            case Less:
                return input < value;

            case More:
                return input > value;

            case Exact:
                return value == input;
        }
        return false;
    }


    public static Times never() {
        return exact(0);
    }

    public static Times once() {
        return exact(1);
    }

    public static Times exact(int times) {
        return new Times(times, TimesType.Exact);
    }

    public static Times moreThan(int times) {
        return new Times(times, TimesType.More);
    }

    public static Times lessThen(int times) {
        return new Times(times, TimesType.Less);
    }

    public enum TimesType {
        Exact, More, Less
    }
}
