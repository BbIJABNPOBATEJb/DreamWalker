package me.bbijabnpobatejb.dreamwalker.effects;

import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.*;

public class EffectsCommand extends CommandBase {
    static final Map<String, List<Effect>> effects = new HashMap<>();

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
    public String getCommandUsage(ICommandSender sender) {
        return "/eff [название] " +
                "| /eff <игрок> \"название\" \"описание\" " +
                "| /eff list <игрок>" +
                "| /eff remove <ник> [название|all]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof EntityPlayerMP) {
                val player = (EntityPlayerMP) sender;
                val playerName = player.getCommandSenderName();

                val effect = effects.getOrDefault(playerName, new ArrayList<>());

                if (effect.isEmpty()) {
                    Chat.sendChat(player, "&7У вас нет активных эффектов");
                    return;
                }

                Chat.sendChat(player, "&aСписок ваших эффектов:");

                effect.forEach(s ->
                        player.addChatMessage(new ChatComponentText(" §f- " + s.getName())));
            } else {
                throw new WrongUsageException("Только игрок может использовать эту команду без параметров");
            }
            return;
        }

        val first = args[0];

        if (args.length == 1) {
            effectCommand(sender, first);
            return;
        }
        if (sender.canCommandSenderUseCommand(4, "")) {
            if (adminCommand(sender, args, first)) return;
        }

        throw new WrongUsageException(getCommandUsage(sender));
    }

    void effectCommand(ICommandSender sender, String first) {
        // /effects Порча
        if (sender instanceof EntityPlayerMP) {
            val player = (EntityPlayerMP) sender;
            val playerName = player.getCommandSenderName();
            val effect = effects.getOrDefault(playerName, new ArrayList<>());

            Effect desc = effect.stream().filter(e -> e.getName().equalsIgnoreCase(first)).findFirst().orElse(null);
            if (desc == null) {
                Chat.sendChat(player, "&7У вас нет эффекта \"" + first + "\"");
            } else {
                Chat.sendChat(player, "&a" + desc.getName() + ": &f" + desc.getDescription());
            }
        } else {
            throw new WrongUsageException("Только игрок может использовать эту команду таким образом");
        }
        return;
    }

    boolean adminCommand(ICommandSender sender, String[] args, String first) {
        if (first.equalsIgnoreCase("remove")) {
            return removeEffect(sender, args);
        }

        if (first.equalsIgnoreCase("list")) {
            return listEffect(sender, args);
        }

        if (args.length >= 3) {
            return addEffect(sender, args);
        }
        return false;
    }

    boolean addEffect(ICommandSender sender, String[] args) {
        // /effects <ник> "Название" "Описание"
        String target = args[0];
        String effectName = stripQuotes(args[1]);
        String desc = joinAndStripQuotes(args, 2);

        val effect = effects.getOrDefault(target, new ArrayList<>());
        effect.add(new Effect(effectName, desc));
        Chat.sendChat(sender, "&aЭффект \"" + effectName + "\" добавлен игроку " + target);

        effects.put(target, effect);
        return true;
    }

    boolean listEffect(ICommandSender sender, String[] args) {
        if (args.length < 2) throw new WrongUsageException(getCommandUsage(sender));
        String target = args[1];

        val effect = effects.getOrDefault(target, new ArrayList<>());

        if (effect.isEmpty()) {
            Chat.sendChat(sender, "&7У игрока " + target + " нету эффектов");
        } else {
            for (val v : effect) {
                Chat.sendChat(sender, "&aСписок эффектов " + target + ":");

                effect.forEach(s ->
                        sender.addChatMessage(new ChatComponentText(" §f- " + v.getName())));
            }
        }
        effects.put(target, effect);
        return true;
    }

    boolean removeEffect(ICommandSender sender, String[] args) {
        if (args.length < 3) throw new WrongUsageException(getCommandUsage(sender));
        String target = args[1];
        String key = args[2];

        val effect = effects.getOrDefault(target, new ArrayList<>());
        if (key.equalsIgnoreCase("all")) {
            effect.clear();
            Chat.sendChat(sender, "&cУдалены все эффекты игрока " + target);
        } else {
            val found = effect.removeIf(e -> e.getName().equalsIgnoreCase(key));
            if (found) {
                Chat.sendChat(sender, "&cЭффект \"" + key + "\" удалён у " + target);
            } else {
                Chat.sendChat(sender, "&7У игрока " + target + " нет такого эффекта");
            }
        }
        effects.put(target, effect);
        return true;
    }


    String stripQuotes(String s) {
        if (s == null) return "";
        return s.replaceAll("^\"|\"$", ""); // remove leading/trailing quotes
    }

    String joinAndStripQuotes(String[] args, int start) {
        StringBuilder b = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            b.append(args[i]).append(" ");
        }
        return stripQuotes(b.toString().trim());
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length >= 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}