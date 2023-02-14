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

package io.github.jwdeveloper.spigot.tester.api.data;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class TestClassResult {
    private String className;

    private String classPackage;

    private boolean isIgnored;

    private boolean isPassed = false;
    private List<TestMethodResult> testMethods = new LinkedList<>();

    public long executionTime() {
        long res = 0;
        for (var method : testMethods) {
            res += method.getExecutionTime();
        }
        return res;
    }

    public int testsPassed() {
        int res = 0;
        for (var method : testMethods) {
            res += method.isPassed() ? 1 : 0;
        }
        return res;
    }

    public int testsNotPassed() {
        return testMethods.size() - testsPassed();
    }

    public int totalTests() {
        return testMethods.size() ;
    }
}
