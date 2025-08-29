package me.bbijabnpobatejb.dreamwalker.alias.object;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RunCommand {
    String command;
    long delay;

    @Override
    public String toString() {
        return "&6>&r " + command + " &7(" + delay + ")&r";
    }
}
