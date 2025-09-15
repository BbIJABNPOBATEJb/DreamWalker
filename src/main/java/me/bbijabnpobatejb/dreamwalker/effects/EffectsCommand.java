package me.bbijabnpobatejb.dreamwalker.effects;

import lombok.val;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

import java.util.*;

public class EffectsCommand extends CommandBase {

    private static final Map<String, List<Effect>> effects = new HashMap<>();

    @Override
    public String getCommandName() {
        return "effects";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("eff");
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
            showSelfEffects(sender);
            return;
        }


        if (!sender.canCommandSenderUseCommand(4, "")) {
            showSelfEffects(sender);
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
                showPlayerEffects(sender, targetPlayer);
                return;
            case "set":
                if (args.length < 4)
                    throw new WrongUsageException("/effects <ник> set <название через _> <описание через _>");
                setEffect(sender, targetPlayer, args[2], args[3]);
                return;
            case "remove":
                if (args.length < 3)
                    throw new WrongUsageException("/effects <ник> remove <name|all|*>");
                removeEffect(sender, targetPlayer, args[2]);
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

    private void showSelfEffects(ICommandSender player) {
        String name = player.getCommandSenderName();
        List<Effect> list = effects.getOrDefault(name, new ArrayList<>());
        if (list.isEmpty()) {
            Chat.sendChat(player, "&7У вас нету эффектов");
            return;
        }

        Chat.sendChat(player, "&aСписок ваших эффектов:");
        for (Effect effect : list) {
            Chat.sendMessage(player, effect.toString());
        }
    }


    // ========== Admin ==========

    private void showPlayerEffects(ICommandSender sender, String target) {
        List<Effect> list = effects.getOrDefault(target, new ArrayList<>());
        if (list.isEmpty()) {
            Chat.sendChat(sender, "&7У игрока " + target + " нету эффектов");
            return;
        }

        Chat.sendChat(sender, "&aЭффекты игрока " + target + ":");
        for (Effect effect : list) {
            Chat.sendMessage(sender, effect.toString());
        }
    }

    private void setEffect(ICommandSender sender, String player, String effectName, String description) {
        List<Effect> list = effects.computeIfAbsent(player, k -> new ArrayList<>());

        val effectNameSpace = effectName.replace("_", " ");
        description = description.replace("_", " ");

        // Replace if exists
        list.removeIf(e -> e.getName().equalsIgnoreCase(effectNameSpace));
        list.add(new Effect(effectNameSpace, description));

        Chat.sendChat(sender, "&aЭффекты '" + effectNameSpace + "' | '" + description + "' установлен игроку " + player);
    }

    private void removeEffect(ICommandSender sender, String target, String arg) {
        List<Effect> list = effects.get(target);
        if (list == null || list.isEmpty()) {
            Chat.sendChat(sender, "&7У игрока " + target + " нету эффектов");
            return;
        }

        val effectName = arg.replace("_", " ");
        if (effectName.equalsIgnoreCase("all") || effectName.equals("*")) {
            list.clear();
            Chat.sendChat(sender, "&bВсе эффекты удалены с игрока " + target);
        } else {
            boolean removed = list.removeIf(e -> e.getName().equalsIgnoreCase(effectName));
            if (removed) {
                Chat.sendChat(sender, "&bЭффект '" + effectName + "' удалён с игрока " + target);
            } else {
                Chat.sendChat(sender, "&cЭффект '" + effectName + "' не найден на игроке " + target);
            }
        }
    }

    // ========== Utils ==========


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