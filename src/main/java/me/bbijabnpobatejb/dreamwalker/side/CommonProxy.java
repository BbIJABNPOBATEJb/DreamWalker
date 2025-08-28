package me.bbijabnpobatejb.dreamwalker.side;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.config.object.SimpleConfig;
import me.bbijabnpobatejb.dreamwalker.event.FMLEventListener;
import me.bbijabnpobatejb.dreamwalker.packet.ClientConfigPacket;
import me.bbijabnpobatejb.dreamwalker.packet.ClientMessagePacket;
import net.minecraft.entity.player.EntityPlayerMP;

import static me.bbijabnpobatejb.dreamwalker.DreamWalker.NETWORK;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK.registerMessage(new ClientMessagePacket.Handler(), ClientMessagePacket.class, 0, Side.CLIENT);
        NETWORK.registerMessage(new ClientConfigPacket.Handler(), ClientConfigPacket.class, 1, Side.CLIENT);

        FMLCommonHandler.instance().bus().register(new FMLEventListener());
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }
    public static SimpleConfig getConfig() {
        return DreamWalker.getInstance().getConfig().getConfig().getData();
    }
    public static void sendConfigToPlayer(EntityPlayerMP player) {
        val config = CommonProxy.getConfig();
        if (config == null) {
            DreamWalker.getLogger().error("Error sendConfigToPlayer config == null");
            return;
        }
        DreamWalker.NETWORK.sendTo(new ClientConfigPacket(
                config.getAliasPrefix(),
                config.getArgsHolder(),
                config.getRollPrefix(),
                config.getFormatMessageRoll(),
                config.getRollCommentMaxChars(),
                config.getChannelPrefixes()
        ), player);
    }
    public static void sendConfigToAllPlayers() {
        val config = CommonProxy.getConfig();
        if (config == null) {
            DreamWalker.getLogger().error("Error sendConfigToAllPlayers config == null");
            return;
        }
        DreamWalker.NETWORK.sendToAll(new ClientConfigPacket(
                config.getAliasPrefix(),
                config.getArgsHolder(),
                config.getRollPrefix(),
                config.getFormatMessageRoll(),
                config.getRollCommentMaxChars(),
                config.getChannelPrefixes()
        ));
    }
}
