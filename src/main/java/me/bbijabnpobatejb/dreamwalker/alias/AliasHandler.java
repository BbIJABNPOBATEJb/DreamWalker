package me.bbijabnpobatejb.dreamwalker.alias;

import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.alias.object.Alias;
import me.bbijabnpobatejb.dreamwalker.alias.object.RunCommand;
import me.bbijabnpobatejb.dreamwalker.config.model.SimpleConfig;
import me.bbijabnpobatejb.dreamwalker.packet.ClientMessagePacket;
import me.bbijabnpobatejb.dreamwalker.scheduler.Scheduler;
import me.bbijabnpobatejb.dreamwalker.side.ClientProxy;
import me.bbijabnpobatejb.dreamwalker.side.CommonProxy;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import me.bbijabnpobatejb.dreamwalker.util.PlayerUtil;
import me.bbijabnpobatejb.dreamwalker.util.RandomUtil;
import me.bbijabnpobatejb.dreamwalker.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.bbijabnpobatejb.dreamwalker.alias.RunAliasCommand.DREAM_ALIAS_COMMAND;
import static me.bbijabnpobatejb.dreamwalker.side.ClientProxy.config;


@UtilityClass
public class AliasHandler {


    public boolean containsAlias(SimpleConfig config, String message) {
        return StringUtil.containsPrefix(config, message, config.getAliasPrefix());
    }

    public String subStingWithoutChannel(SimpleConfig config, String message) {
        return StringUtil.subStingWithoutChannel(message, config.getAliasPrefix());
    }

    public String subStingChannel(SimpleConfig config, String message) {
        return StringUtil.subStingChannel(config, message, config.getAliasPrefix());
    }

    public boolean handleSubmitChatMessage(String rawMessage) {
        val containsAlias = containsAlias(config, rawMessage);

        if (containsAlias) {
            val command = "/" + DREAM_ALIAS_COMMAND + " " + rawMessage;
            DreamWalker.getLogger().info("alias command {}", command);
            val mc = Minecraft.getMinecraft();
            mc.ingameGUI.getChatGUI().addToSentMessages(rawMessage);
            mc.thePlayer.sendChatMessage(command);
        }

        return containsAlias;
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
        if (!ClientProxy.handleChatMessage(message, false)) {
            mc.thePlayer.sendChatMessage(message);
        }
    }

    public String listUsageAlias(List<Alias> allAlias) {
        List<String> allAliasString = new ArrayList<>();
        for (Alias alias : allAlias) {
            allAliasString.addAll(alias.getAlias());
        }
        return "§cСписок доступных вам алиасов: " + String.join(", ", allAliasString);
    }

    public void foundAlias(ICommandSender sender, List<Alias> aliases, String subStingWithoutAlias, AtomicBoolean foundAlias, boolean help, EntityPlayerMP target, String[] args, String channel) {
        for (Alias alias : aliases) {
            boolean matched = false;
            for (String s : alias.getAlias()) {
                if (s.equalsIgnoreCase(subStingWithoutAlias)) {
                    matched = true;
                    break;
                }
            }

            if (matched) {
                foundAlias.set(true);
                runAliasCommand(sender, alias, help, target, args, channel);
                return;
            }
        }
    }

    public void runAliasCommand(ICommandSender sender, Alias alias, boolean help, EntityPlayerMP target, String[] args, String channel) {
        if (help) {
            val message = alias.getDisplayName() + " " + alias.getDescription() + " " + alias.getAlias();
            Chat.sendChat(sender, message);
        } else {
            String argLine = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "";

            List<RunCommand> processedCommands = new ArrayList<>();

            val runCommands = alias.getRunCommands();
            if (alias.isRunRandomCommand() && !runCommands.isEmpty()) {
                val runCommand = RandomUtil.getRandomElement(runCommands);
                tryRunCommand(runCommand, argLine, processedCommands);
            } else {
                for (val runCommand : runCommands) {
                    tryRunCommand(runCommand, argLine, processedCommands);
                }
            }

            for (val runCommand : processedCommands) {
                Scheduler.runTask(() ->
                                sendMessageAtPlayer(target.getUniqueID(), channel + runCommand.getCommand()),
                        runCommand.getDelay());
            }
        }
    }

    private static void tryRunCommand(RunCommand runCommand, String argLine, List<RunCommand> processedCommands) {
        val holder = CommonProxy.getConfig().getArgsHolder();
        val string = runCommand.getCommand();
        val replace = string.replace(holder, argLine);
        val delay = runCommand.getDelay();
        processedCommands.add(new RunCommand(replace, delay));
    }


}