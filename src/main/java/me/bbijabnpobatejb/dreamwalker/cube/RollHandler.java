package me.bbijabnpobatejb.dreamwalker.cube;

import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.packet.ClientActionBarPacket;
import me.bbijabnpobatejb.dreamwalker.packet.ClientConfigPacket;
import me.bbijabnpobatejb.dreamwalker.packet.ClientMessagePacket;
import me.bbijabnpobatejb.dreamwalker.packet.ServerRollPacket;
import me.bbijabnpobatejb.dreamwalker.side.CommonProxy;
import me.bbijabnpobatejb.dreamwalker.util.PlayerUtil;
import me.bbijabnpobatejb.dreamwalker.util.StringUtil;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;

import static me.bbijabnpobatejb.dreamwalker.side.ClientProxy.config;

@UtilityClass
public class RollHandler {

    public boolean handleSubmitChatMessage(String rawMessage, boolean addToChatHistory) {
        String msg = rawMessage.trim();

        String prefix = "";
        for (String p : config.getChannelPrefixes()) {
            if (msg.startsWith(p + config.getRollPrefix())) {
                prefix = p;
                msg = msg.substring(p.length());
                break;
            }
        }

        if (!msg.startsWith(config.getRollPrefix())) return false;

        msg = msg.substring(1).trim();

//        int overrideTotal = -1;
        val mc = Minecraft.getMinecraft();
//        val isAdmin = ClientProxy.clientIsAdmin;
//        if (isAdmin && msg.matches("^\\d+%.*")) {
//            int secondPercentIndex = msg.indexOf("%");
//            if (secondPercentIndex > 0) {
//                try {
//                    overrideTotal = Integer.parseInt(msg.substring(0, secondPercentIndex));
//                    msg = msg.substring(secondPercentIndex + 1).trim();
//                } catch (NumberFormatException ignored) {
//                }
//            }
//        }

        String expr, comment = null;
        if (msg.contains(" ")) {
            expr = msg.substring(0, msg.indexOf(" "));
            comment = msg.substring(msg.indexOf(" ") + 1);
        } else {
            expr = msg;
        }

        if (comment == null) comment = "";
        if (comment.length() > config.getRollCommentMaxChars()) {
            comment = comment.substring(0, config.getRollCommentMaxChars()) + "...";
        }

        val parsed = CubeParser.parse(expr);
//        val total = (overrideTotal >= 0) ? overrideTotal : parsed.total();
        val total = parsed.total();
        val totalText = " = " + total;


        val finalComment = comment;
        val messageRoll = StringUtil.applyPlaceholders(config.getFormatMessageRoll(),
                new HashMap<String, String>() {{
                    put("ordinal", parsed.getOriginal());
                    put("format", parsed.format(config));
                }});
        var resultMessageRoll = StringUtil.applyPlaceholders(config.getFormatResultMessageRoll(),
                new HashMap<String, String>() {{
                    put("result", totalText);
                }});

        if (!finalComment.isEmpty()) {
            val commentMessage = StringUtil.applyPlaceholders(config.getCommentMessageRoll(),
                    new HashMap<String, String>() {{
                        put("comment", finalComment);
                    }});
            resultMessageRoll += commentMessage;
        }


        val message = prefix + messageRoll + resultMessageRoll;


        DreamWalker.getLogger().info("send message: {}", message);
        if (addToChatHistory) mc.ingameGUI.getChatGUI().addToSentMessages(rawMessage);
        mc.thePlayer.sendChatMessage(message);

        if (message.length() > DreamWalker.MAX_CHAT_CHAR) {
            DreamWalker.getLogger().info("send message: {}", resultMessageRoll);
            mc.thePlayer.sendChatMessage(resultMessageRoll);
        }

        DreamWalker.NETWORK.sendToServer(new ServerRollPacket());

        return true;
    }

    public void serverHandlePacket(EntityPlayerMP sender) {
        DreamWalker.getLogger().info("Player {} use roll", sender.getCommandSenderName());

        val simpleConfig = CommonProxy.getConfig();
        val radius = simpleConfig.getRollAnnounceRadius();
        val nearbyPlayers = PlayerUtil.getNearbyPlayers(sender.getEntityWorld(), PlayerUtil.getEntityPos(sender), radius);

        val message = StringUtil.applyPlaceholders(simpleConfig.getRollAnnounceMessage(),
                new HashMap<String, String>() {{
                    put("player", sender.getCommandSenderName());
                }});

        for (EntityPlayer player : nearbyPlayers) {
            if (player instanceof EntityPlayerMP) {
                val playerMP = (EntityPlayerMP) player;
                DreamWalker.NETWORK.sendTo(new ClientActionBarPacket(message), playerMP);
            }
        }
    }

}