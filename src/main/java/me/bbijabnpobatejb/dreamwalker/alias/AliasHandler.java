package me.bbijabnpobatejb.dreamwalker.alias;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.config.object.SimpleConfig;
import me.bbijabnpobatejb.dreamwalker.packet.ClientMessagePacket;
import me.bbijabnpobatejb.dreamwalker.side.CommonProxy;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static me.bbijabnpobatejb.dreamwalker.alias.DreamAliasCommand.DREAMALIAS;
import static me.bbijabnpobatejb.dreamwalker.side.ClientProxy.config;


@UtilityClass
public class AliasHandler {


    @SideOnly(Side.CLIENT)
    public boolean containsAlias(SimpleConfig config, String message) {
        String msg = message.trim();

        for (String p : config.getChannelPrefixes()) {
            if (msg.startsWith(p + config.getAliasPrefix())) {
                msg = msg.substring(p.length());
                break;
            }
        }

        return msg.startsWith(config.getAliasPrefix());
    }

    @SideOnly(Side.SERVER)
    public String subStingWithoutAlias(SimpleConfig config, String message) {
        String prefix = config.getAliasPrefix();
        int index = message.indexOf(prefix);

        if (index == -1) return message;
        return message.substring(index + prefix.length());
    }
    @SideOnly(Side.CLIENT)
    public boolean handleSubmitChatMessage(String rawMessage) {
        val b = containsAlias(config, rawMessage);

        if (b) {
            val command = "/" + DREAMALIAS + " " + rawMessage;
            DreamWalker.getLogger().info("alias command {}", command);
            val mc = Minecraft.getMinecraft();
            mc.ingameGUI.getChatGUI().addToSentMessages(rawMessage);
            mc.thePlayer.sendChatMessage(command);
        }

        return b;
    }

    @SideOnly(Side.SERVER)
    public void sendMessageAtPlayer(EntityPlayerMP player, String message) {
        DreamWalker.NETWORK.sendTo(new ClientMessagePacket(message), player);
    }

    @SideOnly(Side.CLIENT)
    public void handleClientMessagePacket(String message) {
        val mc = Minecraft.getMinecraft();
        DreamWalker.getLogger().info("handlerClientMessagePacket {}", message);
        mc.thePlayer.sendChatMessage(message);
    }

    @SideOnly(Side.SERVER)
    public String listUsageAlias(Map<String, List<Alias>> playersAlias, String targetName, List<Alias> allAlias) {
        val list = playersAlias.getOrDefault(targetName, new ArrayList<>()).stream().map(Alias::getAlias).collect(Collectors.toList());
        val allAliasString = allAlias.stream().map(Alias::getAlias).collect(Collectors.toList());
        list.addAll(allAliasString);
        return "§cСписок доступных вам алиасов: " + String.join(", ", list);
    }

    @SideOnly(Side.SERVER)
    public void foundAlias(ICommandSender sender, List<Alias> aliases, String subStingWithoutAlias, AtomicBoolean foundAlias, boolean help, EntityPlayerMP target, String[] args) {
        for (Alias alias : aliases) {
            if (!alias.getAlias().equalsIgnoreCase(subStingWithoutAlias)) continue;
            foundAlias.set(true);
            runAliasCommand(sender, alias, help, target, args);
            return;
        }
    }

    @SideOnly(Side.SERVER)
    public void runAliasCommand(ICommandSender sender, Alias alias, boolean help, EntityPlayerMP target, String[] args) {
        if (help) {
            val message = alias.getDisplayName() + " " + alias.getDescription() + " " + alias.getAlias();
            Chat.sendChat(sender, message);
        } else {
            String argLine = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "";

            List<String> processedCommands = new ArrayList<>();

            for (String commandTemplate : alias.getRunCommands()) {
                val holder = CommonProxy.getConfig().getArgsHolder();
                if (commandTemplate.contains(holder) && argLine.isEmpty()) continue;
                processedCommands.add(commandTemplate.replace(holder, argLine));
            }

            for (String command : processedCommands) {
                sendMessageAtPlayer(target, command);
            }
        }
    }


}