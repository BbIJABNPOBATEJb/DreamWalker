package me.bbijabnpobatejb.dreamwalker.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

@UtilityClass
public class Chat {

    public final String PREFIX = "§3❯❯§r ";

    public void sendChat(ICommandSender target, String message) {
        message = PREFIX + message;
        target.addChatMessage(buildMessage(message));
    }

    public ChatComponentText buildMessage(String message) {
        return new ChatComponentText(message.replace("&", "§"));
    }

    public void sendChat(String message) {
        val server = MinecraftServer.getServer();
        if (server == null) {
            DreamWalker.getLogger().error("Cant send chat, because server is null");
            return;
        }
        server.getConfigurationManager().sendChatMsg(buildMessage(message));
    }

}
