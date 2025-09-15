package me.bbijabnpobatejb.dreamwalker.alias;

import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.alias.object.Alias;
import me.bbijabnpobatejb.dreamwalker.alias.object.RunCommand;
import me.bbijabnpobatejb.dreamwalker.config.JsonFile;
import me.bbijabnpobatejb.dreamwalker.config.JsonHandler;
import me.bbijabnpobatejb.dreamwalker.config.model.GlobalAliasConfig;
import me.bbijabnpobatejb.dreamwalker.config.model.PlayerAliasConfig;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AliasCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "alias";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        val isAdmin = sender.canCommandSenderUseCommand(4, "");
        return isAdmin ? "/alias <?/help/player/global> <add/remove/list/player>" : "/alias ?";
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
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            throw new WrongUsageException(getCommandUsage(sender));
        } else {
            String scope = args[0].toLowerCase();
            if (scope.equalsIgnoreCase("?")) {
                handleAliasList(sender);
            } else if (sender.canCommandSenderUseCommand(4, "")) {
                handleAdminSender(sender, args, scope);
            }
        }


    }

    private void handleAdminSender(ICommandSender sender, String[] args, String scope) {
        if (scope.equalsIgnoreCase("help")) {
            handleHelp(sender);
            return;
        }
        if (args.length < 2) throw new WrongUsageException(getCommandUsage(sender));

        JsonHandler config = DreamWalker.getInstance().getConfig();

        if (scope.equalsIgnoreCase("player")) {
            handlePlayerAlias(sender, args, config);
        } else if (scope.equalsIgnoreCase("global")) {
            handleGlobalAlias(sender, args, config);
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    private void handleAliasList(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) {
            Chat.sendChat(sender, "&cOnly for players");
            return;
        }
        val player = (EntityPlayerMP) sender;
        val config = DreamWalker.getInstance().getConfig();

        Chat.sendChat(player, "&bСписок доступных вам алиасов: ");
        val globalAlias = config.getGlobalAliasConfig().getData().getGlobalAlias();
        val map = config.getPlayersAliasConfig();
        val playerName = player.getCommandSenderName();

        sendPlayerAlias(globalAlias, player, " &7Глобальные:");
        if (map.containsKey(playerName)) {
            val playerAlias = map.get(playerName).getData().getAliases();
            sendPlayerAlias(playerAlias, player, " &7Личные:");
        }
    }

    void sendPlayerAlias(List<Alias> list, EntityPlayerMP player, String notEmpty) {
        if (!list.isEmpty()) {
            Chat.sendMessage(player, notEmpty);
        }
        for (Alias alias : list) {
            Chat.sendMessage(player, alias.playerToString());
        }
    }

    private void handleHelp(ICommandSender sender) {
        Chat.sendChat(sender, "&e[Alias System — команды администратора]");
        Chat.sendMessage(sender, "&a/alias ? — список доступных вам алиасов");
        Chat.sendMessage(sender, "&a/alias player <ник> list");
        Chat.sendMessage(sender, "&a/alias player <ник> add <алиасы через ;> <название через _> <описание через _> <выбор случайной команды true/false> <команда; задержка в тиках>");
        Chat.sendMessage(sender, "&a/alias player <ник> remove <алиас>");
        Chat.sendMessage(sender, "&a/alias global list");
        Chat.sendMessage(sender, "&a/alias global add <алиасы через ;> <название через _> <описание через _> <выбор случайной команды true/false> <команда; задержка в тиках>");
        Chat.sendMessage(sender, "&a/alias global remove <алиас>");
    }

    // ---------------- PLAYER ----------------

    private void handlePlayerAlias(ICommandSender sender, String[] args, JsonHandler config) {
        if (args.length < 3) throw new WrongUsageException("/alias player <ник> <add/remove/list> ...");

        String player = args[1];
        String action = args[2].toLowerCase();


        if (action.equalsIgnoreCase("add")) {
            val start = 7;
            if (args.length < start + 1)
                throw new WrongUsageException("/alias player <ник> add <алиасы через ;> <название через _> <описание через _> <выбор случайной команды true/false> <команда; задержка в тиках>");
            val aliasName = args[3];
            val title = args[4];
            val desc = args[5];
            val isRandom = args[6];
            val commandMerged = joinArgs(args, start);
            val commands = parseRunCommand(commandMerged);

            val list = Arrays.asList(aliasName.split(";"));
            val alias = new Alias(list, title, Boolean.parseBoolean(isRandom), desc, commands);
            addAliasToPlayer(config, player, alias);
            Chat.sendChat(sender, "&aАлиас &r'" + alias + "'&a добавлен для игрока " + player);

        } else if (action.equalsIgnoreCase("remove")) {
            if (args.length < 4) throw new WrongUsageException("/alias player <ник> remove <алиас>");
            String alias = args[3];
            removeAliasFromPlayer(config, player, alias, sender);

        } else if (action.equalsIgnoreCase("list")) {
            listAliasesOfPlayer(config, player, sender);

        } else {
            throw new WrongUsageException("/alias player <ник> <add/remove/list>");
        }
    }

    private void addAliasToPlayer(JsonHandler config, String playerName, Alias addAlias) {
        val map = config.getPlayersAliasConfig();
        val playerFile = map.getOrDefault(playerName, config.createPlayerAliasJsonFile(playerName, null));
        val playerAliases = playerFile.getData().getAliases();
        val iterator = playerAliases.iterator();
        while (iterator.hasNext()) {
            Alias existing = iterator.next();
            for (String s1 : existing.getAlias()) {
                for (String s2 : addAlias.getAlias()) {
                    if (s1.equalsIgnoreCase(s2)) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
        playerAliases.add(addAlias);
        playerFile.save();
        config.loadPlayers();
    }

    private void removeAliasFromPlayer(JsonHandler config, String player, String aliasName, ICommandSender sender) {
        JsonFile<PlayerAliasConfig> playerFile = config.getPlayersAliasConfig().get(player);
        if (playerFile == null) {
            Chat.sendChat(sender, "&7У игрока " + player + " нет алиасов");
            return;
        }
        boolean removed = playerFile.getData().getAliases().removeIf(a -> {
            for (String s : a.getAlias()) {
                if (s.equalsIgnoreCase(aliasName)) {
                    return true;
                }
            }
            return false;
        });
        if (removed) {
            playerFile.save();
            Chat.sendChat(sender, "&bАлиас '" + aliasName + "' удалён у игрока " + player);
            config.loadPlayers();
        } else {
            Chat.sendChat(sender, "&cАлиас не найден у игрока " + player);
        }
    }

    private void listAliasesOfPlayer(JsonHandler config, String player, ICommandSender sender) {
        JsonFile<PlayerAliasConfig> file = config.getPlayersAliasConfig().get(player);
        if (file == null || file.getData().getAliases().isEmpty()) {
            Chat.sendChat(sender, "&7У игрока " + player + " нет алиасов");
            return;
        }

        Chat.sendChat(sender, "&aАлиасы игрока " + player + ":");
        Chat.sendMessage(sender, Alias.example());
        for (Alias alias : file.getData().getAliases()) {
            Chat.sendMessage(sender, alias.toString());
        }
    }

    // ---------------- GLOBAL ----------------

    private void handleGlobalAlias(ICommandSender sender, String[] args, JsonHandler config) {
        if (args.length < 2) throw new WrongUsageException("/alias global <add/remove/list> ...");

        String action = args[1].toLowerCase();
        GlobalAliasConfig global = config.getGlobalAliasConfig().getData();

        if (action.equalsIgnoreCase("add")) {
            val start = 6;
            if (args.length < start + 1)
                throw new WrongUsageException("/alias global add <алиасы через ;> <название через _> <описание через _> <выбор случайной команды true/false> <команда; задержка в тиках>");
            val aliasName = args[2];
            val title = args[3];
            val desc = args[4];
            val isRandom = args[5];
            val commandMerged = joinArgs(args, start);
            val commands = parseRunCommand(commandMerged);

            val list = Arrays.asList(aliasName.split(";"));
            Alias alias = new Alias(list, title, Boolean.parseBoolean(isRandom), desc, commands);
            global.getGlobalAlias().removeIf(a -> {
                for (String s : a.getAlias()) {
                    if (s.equalsIgnoreCase(aliasName)) {
                        return true;
                    }
                }
                return false;
            });
            global.getGlobalAlias().add(alias);
            config.getGlobalAliasConfig().save();

            Chat.sendChat(sender, "&aГлобальный алиас добавлен &r'" + alias + "'");

        } else if (action.equalsIgnoreCase("remove")) {
            if (args.length < 3) throw new WrongUsageException("/alias global remove <алиас>");
            String aliasName = args[2];
            boolean removed = global.getGlobalAlias().removeIf(a -> {
                for (String s : a.getAlias()) {
                    if (s.equalsIgnoreCase(aliasName)) {
                        return true;
                    }
                }
                return false;
            });
            if (removed) {
                config.getGlobalAliasConfig().save();
                Chat.sendChat(sender, "&bГлобальный алиас &r'" + aliasName + "' удалён");
            } else {
                Chat.sendChat(sender, "&cГлобальный алиас не найден: " + aliasName);
            }

        } else if (action.equalsIgnoreCase("list")) {
            if (global.getGlobalAlias().isEmpty()) {
                Chat.sendChat(sender, "&7Нет глобальных алиасов");
                return;
            }

            Chat.sendChat(sender, "&aСписок глобальных алиасов:");
            Chat.sendMessage(sender, Alias.example());
            for (Alias alias : global.getGlobalAlias()) {
                Chat.sendMessage(sender, alias.toString());
            }

        } else {
            throw new WrongUsageException("/alias global <add/remove/list>");
        }
    }

    // ---------------- UTILS ----------------

    private String joinArgs(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i]);
            if (i != args.length - 1) builder.append(" ");
        }
        return builder.toString();
    }

    private List<RunCommand> parseRunCommand(String raw) {
        List<RunCommand> list = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) return list;

        String[] parts = raw.split(";");
        for (int i = 0; i < parts.length; i += 2) {
            String cmd = parts[i].trim();
            long delay = 0;

            if (i + 1 < parts.length) {
                try {
                    delay = Long.parseLong(parts[i + 1].trim());
                } catch (NumberFormatException ignored) {
                }
            }

            list.add(new RunCommand(cmd, delay));
        }

        return list;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (!sender.canCommandSenderUseCommand(4, "")) return Collections.emptyList();
        JsonHandler config = DreamWalker.getInstance().getConfig();

        if (args.length == 1) {
            return getListOfStringsFromIterableMatchingLastWord(args, Arrays.asList("player", "global"));
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
            return getListOfStringsFromIterableMatchingLastWord(args, Arrays.asList(MinecraftServer.getServer().getAllUsernames()));
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("player")) {
            return getListOfStringsFromIterableMatchingLastWord(args, Arrays.asList("add", "remove", "list"));
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("player") && args[2].equalsIgnoreCase("remove")) {
            // Подсказываем алиасы игрока
            String targetPlayer = args[1];
            JsonFile<PlayerAliasConfig> file = config.getPlayersAliasConfig().get(targetPlayer);
            if (file != null) {
                List<String> aliases = new ArrayList<>();
                for (Alias alias : file.getData().getAliases()) {
                    aliases.addAll(alias.getAlias());
                }
                return getListOfStringsFromIterableMatchingLastWord(args, aliases);
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("global")) {
            return getListOfStringsFromIterableMatchingLastWord(args, Arrays.asList("add", "remove", "list"));
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("global") && args[1].equalsIgnoreCase("remove")) {
            GlobalAliasConfig global = config.getGlobalAliasConfig().getData();
            List<String> aliases = new ArrayList<>();
            for (Alias alias : global.getGlobalAlias()) {
                aliases.addAll(alias.getAlias());
            }
            return getListOfStringsFromIterableMatchingLastWord(args, aliases);
        }

        return Collections.emptyList();
    }

}