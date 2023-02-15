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

package io.github.jwdeveloper.spigot.tester.implementation.players;

import io.github.jwdeveloper.reflect.implementation.FluentReflect;
import io.github.jwdeveloper.reflect.implementation.models.JavaConstructorModel;
import io.github.jwdeveloper.reflect.implementation.models.JavaMethodModel;
import io.github.jwdeveloper.spigot.tester.plugin.PluginMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class NmsCommunicator {
    private final Object MINECRAFT_SERVER;
    private final Object PLAYER_LIST;
    private final Object SERVER_WORLD;
    private final JavaConstructorModel CTR_ENTITY_PLAYER;
    private final JavaConstructorModel CTR_CRAFT_PLAYER;
    private final JavaMethodModel M_CONNECT_PLAYER;
    private final JavaMethodModel M_DISCONNECT_PLAYER;
    private final JavaConstructorModel CTR_GAMEPROFILE;
    private final JavaConstructorModel CTR_NETWORKMANAGER;

    private Object PROTOCOL_ENUM_VALUE;

    public NmsCommunicator(FluentReflect fluentReflect) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {


        var C_CRAFT_SERVER = Bukkit.getServer().getClass();
        var F_MINECRAFT_SERVER = fluentReflect.findField(C_CRAFT_SERVER)
                .forAnyVersion(finder ->
                {
                    finder.withProtected();
                    finder.withFinal();
                    finder.withType("net.minecraft.server.dedicated.DedicatedServer");
                }).find();

        MINECRAFT_SERVER = F_MINECRAFT_SERVER.getValue(Bukkit.getServer());


        var F_WORLD = fluentReflect.findField(Bukkit.getWorlds().get(0).getClass())
                .forAnyVersion(finder ->
                {
                    finder.withPrivate();
                    finder.withFinal();
                    finder.withType("net.minecraft.server.level.WorldServer");
                }).find();

        SERVER_WORLD = F_WORLD.getValue(Bukkit.getWorlds().get(0));

        var C_NETWORK_MANAGER = fluentReflect.findClass().forAnyVersion(finder ->
        {
            finder.withName("net.minecraft.network.NetworkManager");
        }).find();

        CTR_NETWORKMANAGER = C_NETWORK_MANAGER.findConstructor().forAnyVersion(finder ->
        {
            finder.withParameter("net.minecraft.network.protocol.EnumProtocolDirection");
        }).find();


        // Entity Player
        var C_ENTITY_PLAYER = fluentReflect.findClass().forAnyVersion(finder ->
        {
            finder.withName("net.minecraft.server.level.EntityPlayer");
        }).find();

        CTR_ENTITY_PLAYER = C_ENTITY_PLAYER.findConstructor().forAnyVersion(finder ->
        {
            finder.withPublic();
            finder.withParameterCount(3);
        }).forVersion("v1_19_R1", finder ->
        {
            finder.withParameterCount(4);
            finder.withParameterMatcher(input -> new Object[]{input[0], input[1], input[2], null});
        }).find();

        //Craft Player
        var C_CRAFT_PLAYER = fluentReflect.findClass().forAnyVersion(finder ->
        {
            finder.withName("org.bukkit.craftbukkit." + PluginMain.getVersion() + ".entity.CraftPlayer");
        }).find();
        CTR_CRAFT_PLAYER = fluentReflect.
                findConstructor(C_CRAFT_PLAYER.getClassType())
                .forAnyVersion(finder ->
                {
                    finder.withPublic();
                    finder.withParameterCount(2);
                }).find();

        //PlayerList
        var F_PLAYER_LIST = fluentReflect.findField(Bukkit.getServer().getClass())
                .forAnyVersion(finder ->
                {
                    finder.withProtected();
                    finder.withFinal();
                    finder.withType("net.minecraft.server.dedicated.DedicatedPlayerList");
                }).find();
        PLAYER_LIST = F_PLAYER_LIST.getValue(Bukkit.getServer());

        M_CONNECT_PLAYER = fluentReflect.findMethod(PLAYER_LIST.getClass()).forAnyVersion(finder ->
        {
            finder.withPublic()
                    .withParameter(C_NETWORK_MANAGER.getClassType())
                    .withParameter(C_ENTITY_PLAYER.getClassType());
        }).find();

        M_DISCONNECT_PLAYER = fluentReflect.findMethod(PLAYER_LIST.getClass()).forAnyVersion(finder ->
        {
            finder.withName("remove");
            finder.withPublic();
            finder.withType(String.class);
            finder.withParameter(C_ENTITY_PLAYER.getClassType());
        }).forVersion("v1_17_R1", finder ->
        {
            finder.withName("disconnect");
        }).find();

        //GameProfile
        CTR_GAMEPROFILE = fluentReflect.findClass().forAnyVersion(finder ->
                {
                    finder.withName("com.mojang.authlib.GameProfile");
                })
                .find()
                .findConstructor()
                .forAnyVersion(finder ->
                {
                    finder.withParameterCount(2);
                }).find();

        var E_PROTOCOL_DIRECTION = fluentReflect.findClass().forAnyVersion(finder ->
        {
            finder.withName("net.minecraft.network.protocol.EnumProtocolDirection");
        }).find().getClassType();
        for (Object obj : E_PROTOCOL_DIRECTION.getEnumConstants()) {
            PROTOCOL_ENUM_VALUE = obj;
        }
    }


    public void connect(Object entityPlayer) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        var networkManager = CTR_NETWORKMANAGER.newInstance(PROTOCOL_ENUM_VALUE);
        M_CONNECT_PLAYER.invoke(PLAYER_LIST, networkManager, entityPlayer);
    }

    public void disconnect(Object entityPlayer) {
        try {
            M_DISCONNECT_PLAYER.invoke(PLAYER_LIST, entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FakePlayer createFakePlayer(UUID uuid, String name) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object gameProfile = CTR_GAMEPROFILE.newInstance(uuid, name);
        Object entityPlayer = CTR_ENTITY_PLAYER.newInstance(MINECRAFT_SERVER, SERVER_WORLD, gameProfile);
        Player player = CTR_CRAFT_PLAYER.newInstance(Bukkit.getServer(), entityPlayer);
        var fakePlayer = new FakePlayer(player, entityPlayer, this);
        return fakePlayer;
    }




    /*
     var server = (CraftServer) Bukkit.getServer();
        var Cserver = server.getClass();
        var field = Cserver.getDeclaredField("console");
        field.setAccessible(true);
        var dedicatedServer = (DedicatedServer) field.get(server);
        var mcServer = (MinecraftServer) dedicatedServer;


        var world = (CraftWorld) Bukkit.getWorlds().get(0);
        var Cworld = world.getClass();
        field = Cworld.getDeclaredField("world");
        field.setAccessible(true);
        var mcWorld = (WorldServer) field.get(world);

        GameProfile profile = new GameProfile(uuid, name);
        EntityPlayer entityPlayer = new EntityPlayer(dedicatedServer, mcWorld, profile);


        var manager = new NetworkManager(EnumProtocolDirection.b);
        var fakePlayer = new FakePlayer(entityPlayer, manager, mcServer, server);
        return fakePlayer;
     */
}
