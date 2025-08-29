package me.bbijabnpobatejb.dreamwalker.cube;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.side.ClientProxy;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import me.bbijabnpobatejb.dreamwalker.util.StringUtil;
import net.minecraft.client.Minecraft;

import java.util.HashMap;

import static me.bbijabnpobatejb.dreamwalker.side.ClientProxy.config;

@UtilityClass
public class RollHandler {

    public boolean handleSubmitChatMessage(String rawMessage) {
        String msg = rawMessage.trim();

        // Обработка префикса чата (например -%... или @%...)
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

        int overrideTotal = -1;
        val mc = Minecraft.getMinecraft();
        val isAdmin = ClientProxy.clientIsAdmin;
        if (isAdmin && msg.matches("^\\d+%.*")) {
            int secondPercentIndex = msg.indexOf("%");
            if (secondPercentIndex > 0) {
                try {
                    overrideTotal = Integer.parseInt(msg.substring(0, secondPercentIndex));
                    msg = msg.substring(secondPercentIndex + 1).trim();
                } catch (NumberFormatException ignored) {
                }
            }
        }

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
        val total = (overrideTotal >= 0) ? overrideTotal : parsed.total();


        val finalComment = comment;
        val placeholders = StringUtil.applyPlaceholders(config.getFormatMessageRoll(),
                new HashMap<String, String>() {{
                    put("ordinal", parsed.getOriginal());
                    put("format", parsed.format(total));
                    put("comment", finalComment);
                }});


        val message = prefix + placeholders;

        DreamWalker.getLogger().info("send message: {}", message);
        mc.ingameGUI.getChatGUI().addToSentMessages(rawMessage);
        mc.thePlayer.sendChatMessage(message);
        return true;
    }

}