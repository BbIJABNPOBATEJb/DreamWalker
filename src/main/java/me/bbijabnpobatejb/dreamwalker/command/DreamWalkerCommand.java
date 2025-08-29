package me.bbijabnpobatejb.dreamwalker.command;

import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.util.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.Arrays;
import java.util.List;

import static me.bbijabnpobatejb.dreamwalker.side.CommonProxy.sendConfigToAllPlayers;

public class DreamWalkerCommand extends CommandBase {

    public static final String TEST_MESSAGE = "§6§l[Сервер] §r§aДобро пожаловать, §eИгрок§f! §7Сегодняшняя дата: §b28.08.2025 §8| §3Время на сервере: §f11:59  \n" +
            "§dСписок доступных команд: §f/help §8| §f/home §8| §f/spawn §8| §f/tpa  \n" +
            "  \n" +
            "§cВнимание! §r§7Сервер скоро перезагрузится через §c5 минут§7. Пожалуйста, сохраните свои ресурсы и выйдите в §aбезопасное место§7.  \n" +
            "  \n" +
            "§b» §r§fВаш уровень: §6[30] §f| Баланс: §e12 345.67₽ §f| Статус: §aONLINE  \n" +
            "§b» §r§fЛокация: §3World §8[§f123, 64, -789§8] §f| Биом: §2Plains  \n" +
            "  \n" +
            "§9Совет: §7Вы можете использовать §e/warp §7для быстрого перемещения между интересными точками!  \n" +
            "§8(Проверьте /warps для списка доступных варпов)";

    @Override
    public String getCommandName() {
        return "dreamwalker";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("dw");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(4, "");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dw <reload | test>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) throw new WrongUsageException(getCommandUsage(sender));
        val arg = args[0];
        if (arg.equalsIgnoreCase("reload")) {
            val config = DreamWalker.getInstance().getConfig();
            config.load();
            sendConfigToAllPlayers();
            Chat.sendChat(sender, "&aКонфиг перезагружен. Загружено алиасов игроков " + config.getPlayersAliasConfig().size());
            return;
        } else if (arg.equalsIgnoreCase("test")) {
            Chat.sendChat(sender, TEST_MESSAGE);
            return;
        }
        throw new WrongUsageException(getCommandUsage(sender));
    }
}