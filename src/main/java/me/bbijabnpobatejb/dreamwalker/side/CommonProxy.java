package me.bbijabnpobatejb.dreamwalker.side;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.config.model.SimpleConfig;
import me.bbijabnpobatejb.dreamwalker.event.FMLEventListener;
import me.bbijabnpobatejb.dreamwalker.packet.*;
import me.bbijabnpobatejb.dreamwalker.scheduler.Scheduler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import static me.bbijabnpobatejb.dreamwalker.DreamWalker.NETWORK;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK.registerMessage(new ClientMessagePacket.Handler(), ClientMessagePacket.class, 0, Side.CLIENT);
        NETWORK.registerMessage(new ClientConfigPacket.Handler(), ClientConfigPacket.class, 1, Side.CLIENT);
        NETWORK.registerMessage(new ClientActionBarPacket.Handler(), ClientActionBarPacket.class, 2, Side.CLIENT);
        NETWORK.registerMessage(new ServerRollPacket.Handler(), ServerRollPacket.class, 3, Side.SERVER);
        NETWORK.registerMessage(new ServerRunAliasPacket.Handler(), ServerRunAliasPacket.class, 4, Side.SERVER);
        NETWORK.registerMessage(new ServerListEffectPacket.Handler(), ServerListEffectPacket.class, 5, Side.SERVER);

        FMLCommonHandler.instance().bus().register(new FMLEventListener());
        FMLCommonHandler.instance().bus().register(new Scheduler());
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
                player.canCommandSenderUseCommand(4, ""),
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
        for (Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (!(o instanceof EntityPlayerMP)) continue;
            val player = (EntityPlayerMP) o;
            DreamWalker.NETWORK.sendToAll(new ClientConfigPacket(
                    player.canCommandSenderUseCommand(4, ""),
                    config.getAliasPrefix(),
                    config.getArgsHolder(),
                    config.getRollPrefix(),
                    config.getFormatMessageRoll(),
                    config.getRollCommentMaxChars(),
                    config.getChannelPrefixes()
            ));
        }

    }
}
