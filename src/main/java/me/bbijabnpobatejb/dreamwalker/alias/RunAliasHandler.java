package me.bbijabnpobatejb.dreamwalker.alias;

import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.side.CommonProxy;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.command.CommandBase.getPlayer;

@UtilityClass
public class RunAliasHandler {


    public void handle(EntityPlayerMP sender, String rawMessage) {
        if (rawMessage == null || rawMessage.isEmpty()) {
            Chat.sendChat(sender, "&cОшибка при чтении алиаса");
            return;
        }
        val args = rawMessage.split(" ");

        val argAlias = args[0];
        boolean help = args.length > 1 && args[1].equalsIgnoreCase("?");


        val config = CommonProxy.getConfig();
        if (!AliasHandler.containsAlias(config, argAlias)) {
            Chat.sendChat(sender, "&cИспользуйте " + config.getAliasPrefix() + " перед сообщением");
            return;
        }

        val subStingWithoutAlias = AliasHandler.subStingWithoutChannel(config, argAlias);
        val channel = AliasHandler.subStingChannel(config, argAlias);

        val map = DreamWalker.getInstance().getConfig().getPlayersAliasConfig();
        val globalData = DreamWalker.getInstance().getConfig().getGlobalAliasConfig().getData();
        val globalAlias = globalData.getGlobalAlias();
        val helpAlias = new ArrayList<>(globalAlias);

        EntityPlayerMP target;
        if (help && args.length > 2 && sender.canCommandSenderUseCommand(4, "")) {
            target = getPlayer(sender, args[2]);
        } else {
            target = sender;
        }

        val targetName = target.getCommandSenderName();

        AtomicBoolean foundAlias = new AtomicBoolean(false);

        map.forEach((playerName, jsonFile) -> {
            if (!playerName.equalsIgnoreCase(targetName)) return;
            val playerAlias = jsonFile.getData().getAliases();
            helpAlias.addAll(playerAlias);
            AliasHandler.foundAlias(sender, playerAlias, subStingWithoutAlias, foundAlias, help, target, args, channel);
        });

        if (!foundAlias.get()) {
            AliasHandler.foundAlias(sender, globalAlias, subStingWithoutAlias, foundAlias, help, target, args, channel);
        }

        if (!foundAlias.get()) {
            val listUsageAlias = AliasHandler.listUsageAlias(helpAlias);
            Chat.sendChat(sender, "&cАлиас " + subStingWithoutAlias + " не найден");
            Chat.sendChat(sender, listUsageAlias);
        }

    }

}