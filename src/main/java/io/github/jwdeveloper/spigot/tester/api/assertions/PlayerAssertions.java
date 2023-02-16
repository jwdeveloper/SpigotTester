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

import org.bukkit.entity.Player;

public class PlayerAssertions {
    private final Player player;

    public PlayerAssertions(Player player) {
        this.player = player;
    }

    public PlayerAssertions hasName(String name) {
        getAssertion(player.getName()).shouldBe(name);
        return this;
    }

    public PlayerAssertions hasNotName(String name) {
        getAssertion(player.getName()).shouldNotBe(name);
        return this;
    }

    public PlayerAssertions hasOp() {
        getAssertion(player.isOp()).shouldBeTrue();
        return this;
    }

    public PlayerAssertions hasNotOp() {
        getAssertion(player.isOp()).shouldBeFalse();
        return this;
    }

    public PlayerAssertions hasPassenger() {
        getAssertion(player.getPassengers().size()).shouldNotBe(0);
        return this;
    }

    public PlayerAssertions hasNotPassenger() {
        getAssertion(player.getPassengers().size()).shouldBe(0);
        return this;
    }

    public PlayerAssertions hasPermission(String... permissions) {
        for(var perm : permissions)
        {
            getAssertion(player.hasPermission(perm)).shouldBeTrue();
        }
        return this;
    }

    public PlayerAssertions hasNotPermission(String... permissions) {
        for(var perm : permissions)
        {
            getAssertion(player.hasPermission(perm)).shouldBeFalse();
        }
        return this;
    }

    private CommonAssertions getAssertion(Object target) {
        return new CommonAssertions(target);
    }
}


