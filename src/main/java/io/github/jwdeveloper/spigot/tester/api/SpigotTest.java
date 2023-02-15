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

package io.github.jwdeveloper.spigot.tester.api;

import io.github.jwdeveloper.spigot.tester.api.assertions.Assertions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public abstract class SpigotTest implements TestMember
{
    private final TestContext testContext;

    public SpigotTest(TestContext testContext)
    {
        this.testContext = testContext;
    }

    @Override
    public void beforeEachTest() {
    }
    @Override
    public void before() {
    }

    @Override
    public void after() {
    }

    @Override
    public void afterEachTest() {
    }

    protected final Assertions assertion(Object object)
    {
        return new Assertions(object);
    }

    protected final Player addPlayer()
    {
        return testContext.playerFactory().createPlayer();
    }
    protected final Player addPlayer(String name)
    {
        return testContext.playerFactory().createPlayer(UUID.randomUUID(), name);
    }
    protected final Player addPlayer(UUID uuid, String name)
    {
        return testContext.playerFactory().createPlayer(uuid, name);
    }


    protected final <T> T getParameter(Class<T> clazz)
    {
          return testContext.getParameter(clazz);
    }

    protected final Plugin getPlugin()
    {
        return testContext.plugin();
    }

}
