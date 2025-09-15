package me.bbijabnpobatejb.dreamwalker.side;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import me.bbijabnpobatejb.dreamwalker.alias.AliasHandler;
import me.bbijabnpobatejb.dreamwalker.config.model.SimpleConfig;
import me.bbijabnpobatejb.dreamwalker.cube.RollHandler;

public class ClientProxy extends CommonProxy {

    public static boolean clientIsAdmin = false;
    public static SimpleConfig config = new SimpleConfig();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    public static boolean handleChatMessage(String message, boolean addToChatHistory) {
        try {
            if (RollHandler.handleSubmitChatMessage(message, addToChatHistory)) {
                return true;
            }
            if (AliasHandler.handleSubmitChatMessage(message)) {
                return true;
            }
        } catch (Exception ignored) {

        }
        return false;
    }
}