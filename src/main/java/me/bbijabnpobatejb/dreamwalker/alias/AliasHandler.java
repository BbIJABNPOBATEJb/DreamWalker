package me.bbijabnpobatejb.dreamwalker.alias;

import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.alias.object.Alias;
import me.bbijabnpobatejb.dreamwalker.alias.object.RunCommand;
import me.bbijabnpobatejb.dreamwalker.config.model.SimpleConfig;
import me.bbijabnpobatejb.dreamwalker.packet.ClientMessagePacket;
import me.bbijabnpobatejb.dreamwalker.scheduler.TickEventListener;
import me.bbijabnpobatejb.dreamwalker.side.CommonProxy;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import me.bbijabnpobatejb.dreamwalker.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static me.bbijabnpobatejb.dreamwalker.alias.RunAliasCommand.DREAM_ALIAS_COMMAND;
import static me.bbijabnpobatejb.dreamwalker.side.ClientProxy.config;


@UtilityClass
public class AliasHandler {


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

    public String subStingWithoutAlias(SimpleConfig config, String message) {
        String prefix = config.getAliasPrefix();
        int index = message.indexOf(prefix);

        if (index == -1) return message;
        return message.substring(index + prefix.length());
    }

    public boolean handleSubmitChatMessage(String rawMessage) {
        val b = containsAlias(config, rawMessage);

        if (b) {
            val command = "/" + DREAM_ALIAS_COMMAND + " " + rawMessage;
            DreamWalker.getLogger().info("alias command {}", command);
            val mc = Minecraft.getMinecraft();
            mc.ingameGUI.getChatGUI().addToSentMessages(rawMessage);
            mc.thePlayer.sendChatMessage(command);
        }

        return b;
    }

    public void sendMessageAtPlayer(UUID playerId, String message) {
        val player = PlayerUtil.getPlayerFromUUID(playerId);
        if (player == null) {
            DreamWalker.getLogger().warn("Cant find player {}, for alias command {}", playerId, message);
            return;
        }
        DreamWalker.NETWORK.sendTo(new ClientMessagePacket(message), player);
    }

    public void handleClientMessagePacket(String message) {
        val mc = Minecraft.getMinecraft();
        DreamWalker.getLogger().info("handlerClientMessagePacket {}", message);
        mc.thePlayer.sendChatMessage(message);
    }

    public String listUsageAlias(List<Alias> allAlias) {
        val allAliasString = allAlias.stream().map(Alias::getAlias).collect(Collectors.toList());
        return "§cСписок доступных вам алиасов: " + String.join(", ", allAliasString);
    }

    public void foundAlias(ICommandSender sender, List<Alias> aliases, String subStingWithoutAlias, AtomicBoolean foundAlias, boolean help, EntityPlayerMP target, String[] args) {
        for (Alias alias : aliases) {
            if (!alias.getAlias().equalsIgnoreCase(subStingWithoutAlias)) continue;
            foundAlias.set(true);
            runAliasCommand(sender, alias, help, target, args);
            return;
        }
    }

    public void runAliasCommand(ICommandSender sender, Alias alias, boolean help, EntityPlayerMP target, String[] args) {
        if (help) {
            val message = alias.getDisplayName() + " " + alias.getDescription() + " " + alias.getAlias();
            Chat.sendChat(sender, message);
        } else {
            String argLine = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "";

            List<RunCommand> processedCommands = new ArrayList<>();

            for (val runCommand : alias.getRunCommands()) {
                val holder = CommonProxy.getConfig().getArgsHolder();
                val string = runCommand.getCommand();
                if (string.contains(holder) && argLine.isEmpty()) continue;
                val replace = string.replace(holder, argLine);
                val delay = runCommand.getDelay();
                processedCommands.add(new RunCommand(replace, delay));
            }

            for (val runCommand : processedCommands) {
                TickEventListener.runTask(() -> {
                    sendMessageAtPlayer(target.getUniqueID(), runCommand.getCommand());
                }, runCommand.getDelay());
            }
        }
    }


}