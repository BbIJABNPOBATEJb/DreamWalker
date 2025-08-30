package me.bbijabnpobatejb.dreamwalker.cube;

import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.util.StringUtil;
import net.minecraft.client.Minecraft;

import java.util.HashMap;

import static me.bbijabnpobatejb.dreamwalker.side.ClientProxy.config;

@UtilityClass
public class RollHandler {

    public boolean handleSubmitChatMessage(String rawMessage) {
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
                    put("format", parsed.format());
                }});
        val resultMessageRoll = StringUtil.applyPlaceholders(config.getFormatResultMessageRoll(),
                new HashMap<String, String>() {{
                    put("result", totalText);
                    put("comment", finalComment);
                }});


        val message = prefix + messageRoll + resultMessageRoll;


        DreamWalker.getLogger().info("send message: {}", message);
        mc.ingameGUI.getChatGUI().addToSentMessages(rawMessage);
        mc.thePlayer.sendChatMessage(message);

        if (message.length() > DreamWalker.MAX_CHAT_CHAR) {

            DreamWalker.getLogger().info("send message: {}", resultMessageRoll);
            mc.thePlayer.sendChatMessage(resultMessageRoll);
        }

        return true;
    }

}