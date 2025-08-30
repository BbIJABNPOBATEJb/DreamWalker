package me.bbijabnpobatejb.dreamwalker.alias.object;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Alias {
    List<String> alias;
    String displayName;
    String description;
    List<RunCommand> runCommands;

    public Alias(List<String> alias, String displayName, String description, List<RunCommand> runCommands) {
        this.alias = alias;
        this.displayName = displayName.replace("_", " ");
        this.description = description.replace("_", " ");
        this.runCommands = runCommands;
    }

    @Override
    public String toString() {
        val s = " &b|&r ";
        val commands = runCommands.stream().map(RunCommand::toString).collect(Collectors.joining(" &6|&r "));
        return "&7 - &f" + alias + s
                + displayName + s
                + description + s
                 + commands;
    }

    public String playerToString() {
        val s = " &b|&r ";
        return "&7 - &f" + alias + s
                + displayName + s
                + description;
    }

    public static String example() {
        val s = " &b|&r ";
        return "&7 - &f[Алиасы]" + s
                + "Имя" + s
                + "Описание" + s
                + " &6>&r " + String.join(" &6|&r ", new String[]{"команда 1 &7(задержка)&r", "команда 2 &7(задержка)&r"})
                + " &r&7(Это пример)";
    }
}
