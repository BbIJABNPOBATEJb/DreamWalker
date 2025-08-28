package me.bbijabnpobatejb.dreamwalker.alias;

import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.side.CommonProxy;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DreamAliasCommand extends CommandBase {

    public static final String DREAMALIAS = "dreamalias";

    @Override
    public String getCommandName() {
        return DREAMALIAS;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("da");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/da <alias>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        DreamWalker.getLogger().info("alias {}", String.join(" ", args));
        if (!(sender instanceof EntityPlayerMP)) {
            Chat.sendChat(sender, "&cOnly for players");
            return;
        }
        val player = (EntityPlayerMP) sender;
        if (args.length < 1) throw new CommandException(getCommandUsage(sender));
        val argAlias = args[0];
        boolean help = args.length > 1 && args[1].equalsIgnoreCase("?");


        val config = CommonProxy.getConfig();
        if (!AliasHandler.containsAlias(config, argAlias)) {
            Chat.sendChat(sender, "&cИспользуйте " + config.getAliasPrefix() + " перед сообщением");
            return;
        }

        val subStingWithoutAlias = AliasHandler.subStingWithoutAlias(config, argAlias);

        val playerData = DreamWalker.getInstance().getConfig().getPlayerAliasConfig().getData();
        val globalData = DreamWalker.getInstance().getConfig().getGlobalAliasConfig().getData();
        val playersAlias = playerData.getPlayersAlias();
        val allAlias = globalData.getGlobalAlias();

        EntityPlayerMP target;
        if (args.length > 2 && sender.canCommandSenderUseCommand(4, "")) {
            target = getPlayer(sender, args[2]);
        } else {
            target = player;
        }

        val targetName = target.getCommandSenderName();

        AtomicBoolean foundAlias = new AtomicBoolean(false);

        playersAlias.forEach((playerName, aliases) -> {
            if (!playerName.equalsIgnoreCase(targetName)) return;
            AliasHandler.foundAlias(sender, aliases, subStingWithoutAlias, foundAlias, help, target, args);
        });

        if (!foundAlias.get()) {
            AliasHandler.foundAlias(sender, allAlias, subStingWithoutAlias, foundAlias, help, target, args);
        }

        if (!foundAlias.get()) {
            val listUsageAlias = AliasHandler.listUsageAlias(playersAlias, targetName, allAlias);
            Chat.sendChat(sender, "&cАлиас " + subStingWithoutAlias + " не найден");
            Chat.sendChat(sender, listUsageAlias);
        }

    }


}