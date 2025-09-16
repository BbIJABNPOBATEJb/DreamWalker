package me.bbijabnpobatejb.dreamwalker.effects;

import me.bbijabnpobatejb.dreamwalker.util.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

import java.util.*;

public class EffectsCommand extends CommandBase {


    public static final String COMMAND = "effects";
    public static final List<String> ALIAS = Collections.singletonList("eff");

    @Override
    public String getCommandName() {
        return COMMAND;
    }

    @Override
    public List<String> getCommandAliases() {
        return ALIAS;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        if (sender.canCommandSenderUseCommand(4, "")) {
            return "/effects | /effects help | " +
                    "/effects <ник> list | " +
                    "/effects <ник> set <название через _> <описание через _> | " +
                    "/effects <ник> remove <name|all|*>";
        }
        return "/effects | /effects help";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            EffectHandler.showSelfEffects(sender);
            return;
        }


        if (!sender.canCommandSenderUseCommand(4, "")) {
            EffectHandler.showSelfEffects(sender);
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            showHelp(sender);
            return;
        }

        if (args.length < 2) throw new WrongUsageException(getCommandUsage(sender));


        String targetPlayer = args[0];
        String action = args[1].toLowerCase();

        switch (action) {
            case "list":
                EffectHandler.showPlayerEffects(sender, targetPlayer);
                return;
            case "set":
                if (args.length < 4)
                    throw new WrongUsageException("/effects <ник> set <название через _> <описание через _>");
                EffectHandler.setEffect(sender, targetPlayer, args[2], args[3]);
                return;
            case "remove":
                if (args.length < 3)
                    throw new WrongUsageException("/effects <ник> remove <name|all|*>");
                EffectHandler.removeEffect(sender, targetPlayer, args[2]);
                return;
        }

        throw new WrongUsageException(getCommandUsage(sender));
    }

    private void showHelp(ICommandSender sender) {
        Chat.sendChat(sender, "&e[Effects — команды администратора]");
        Chat.sendMessage(sender, "&a/effects — посмотреть свои эффекты");
        Chat.sendMessage(sender, "&a/effects <ник> list — посмотреть эффекты игрока");
        Chat.sendMessage(sender, "&a/effects <ник> set <название> <описание> — установить/заменить эффект игроку");
        Chat.sendMessage(sender, "&a/effects <ник> remove <название|all|*> — удалить эффект у игрока");
        Chat.sendMessage(sender, "&a/effects help — показать это сообщение");
    }
    // ========= For Players ==========


    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }

        if (args.length == 2 && sender.canCommandSenderUseCommand(4, "")) {
            return getListOfStringsFromIterableMatchingLastWord(args, Arrays.asList("list", "set", "remove"));
        }

        return Collections.emptyList();
    }
}