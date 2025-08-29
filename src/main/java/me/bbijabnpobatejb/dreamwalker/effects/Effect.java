package me.bbijabnpobatejb.dreamwalker.effects;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.val;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Effect {
    String name;
    String description;

    public Effect(String name, String description) {
        this.name = name.replace("_", " ");
        this.description = description.replace("_", " ");
    }

    @Override
    public String toString() {
        val s = " &b|&r ";
        return "&7 - &f" + name + s
                + description;
    }
}
