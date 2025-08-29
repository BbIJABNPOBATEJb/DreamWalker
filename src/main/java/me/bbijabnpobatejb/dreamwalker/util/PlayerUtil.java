package me.bbijabnpobatejb.dreamwalker.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.UUID;

@UtilityClass
public class PlayerUtil {

    public @Nullable EntityPlayerMP getPlayerFromUUID(UUID uuid) {
        val server = MinecraftServer.getServer();
        if (server == null) return null;

        for (Object o : server.getConfigurationManager().playerEntityList) {
            if (!(o instanceof EntityPlayerMP))continue;
            EntityPlayerMP player = (EntityPlayerMP) o;
            if (player.getUniqueID().equals(uuid)) {
                return player;
            }
        }
        return null;
    }
}
