package me.bbijabnpobatejb.dreamwalker.alias;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Alias {
    String alias;
    String displayName;
    String description;
    List<String> runCommands;

    public Alias(String alias, String displayName, String description, List<String> runCommands) {
        this.alias = alias;
        this.displayName = displayName.replace("_", " ");
        this.description = description.replace("_", " ");
        this.runCommands = runCommands;
    }

    @Override
    public String toString() {
        val s = " &b|&r ";
        return "&7 - &f" + alias + s
                + displayName + s
                + description + s
                + " &6>&r " + String.join(" &6|&r ", runCommands);
    }

    public String playerToString() {
        val s = " &b|&r ";
        return "&7 - &f" + alias + s
                + displayName + s
                + description;
    }

    public static String example() {
        val s = " &b|&r ";
        return "&7 - &f Алиас" + s
                + "Имя" + s
                + "Описание" + s
                + " &6>&r " + String.join(" &6|&r ", new String[]{"команда 1", "команда 2"})
                + " &r&7(Это пример)";
    }
}
