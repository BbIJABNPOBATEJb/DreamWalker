package me.bbijabnpobatejb.dreamwalker.effects;

import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.packet.ServerListEffectPacket;
import me.bbijabnpobatejb.dreamwalker.packet.ServerRunAliasPacket;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class EffectHandler {

    private static final Map<String, List<Effect>> EFFECTS = new HashMap<>();


    public boolean handleSubmitChatMessage(String rawMessage) {
        if (containsShowEffectCommand(rawMessage)) {
            DreamWalker.NETWORK.sendToServer(new ServerListEffectPacket());
            return true;
        }
        return false;
    }

    public boolean containsShowEffectCommand(String message) {
        List<String> list = new ArrayList<>(EffectsCommand.ALIAS);
        list.add(EffectsCommand.COMMAND);

        for (String s : list) {
            val command = "/" + s;
            if (command.equalsIgnoreCase(message)) return true;
        }
        return false;
    }

    public void showSelfEffects(ICommandSender player) {
        String name = player.getCommandSenderName();
        List<Effect> list = EFFECTS.getOrDefault(name, new ArrayList<>());
        if (list.isEmpty()) {
            Chat.sendChat(player, "&7У вас нету эффектов");
            return;
        }

        Chat.sendChat(player, "&aСписок ваших эффектов:");
        for (Effect effect : list) {
            Chat.sendMessage(player, effect.toString());
        }
    }

    public void showPlayerEffects(ICommandSender sender, String target) {
        List<Effect> list = EFFECTS.getOrDefault(target, new ArrayList<>());
        if (list.isEmpty()) {
            Chat.sendChat(sender, "&7У игрока " + target + " нету эффектов");
            return;
        }

        Chat.sendChat(sender, "&aЭффекты игрока " + target + ":");
        for (Effect effect : list) {
            Chat.sendMessage(sender, effect.toString());
        }
    }

    public void setEffect(ICommandSender sender, String player, String effectName, String description) {
        List<Effect> list = EFFECTS.computeIfAbsent(player, k -> new ArrayList<>());

        val effectNameSpace = effectName.replace("_", " ");
        description = description.replace("_", " ");

        // Replace if exists
        list.removeIf(e -> e.getName().equalsIgnoreCase(effectNameSpace));
        list.add(new Effect(effectNameSpace, description));

        Chat.sendChat(sender, "&aЭффекты '" + effectNameSpace + "' | '" + description + "' установлен игроку " + player);
    }

    public void removeEffect(ICommandSender sender, String target, String arg) {
        List<Effect> list = EFFECTS.get(target);
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

}
