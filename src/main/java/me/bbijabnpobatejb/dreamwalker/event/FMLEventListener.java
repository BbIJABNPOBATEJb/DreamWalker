package me.bbijabnpobatejb.dreamwalker.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;

import static me.bbijabnpobatejb.dreamwalker.side.CommonProxy.sendConfigToPlayer;

public class FMLEventListener {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            sendConfigToPlayer(player);
        }

    }


}

