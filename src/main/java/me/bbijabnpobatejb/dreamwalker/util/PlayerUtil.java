package me.bbijabnpobatejb.dreamwalker.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class PlayerUtil {

    public @Nullable EntityPlayerMP getPlayerFromUUID(UUID uuid) {
        val server = MinecraftServer.getServer();
        if (server == null) return null;

        for (Object o : server.getConfigurationManager().playerEntityList) {
            if (!(o instanceof EntityPlayerMP)) continue;
            EntityPlayerMP player = (EntityPlayerMP) o;
            if (player.getUniqueID().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

    public List<EntityPlayer> getNearbyPlayers(World world, Vec3 pos, double distance) {
        List<EntityPlayer> list = world.playerEntities;
        List<EntityPlayer> nearby = new ArrayList<>();
        val v = distance * distance;
        for (val player : list) {
            val vec3 = getEntityPos(player);
            val squared = vec3.squareDistanceTo(pos);
            if (squared <= v) {
                nearby.add(player);
            }
        }
        return nearby;
    }

    public Vec3 getEntityPos(Entity player) {
        return Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
    }
}
