package me.bbijabnpobatejb.dreamwalker.effects;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Effect {
    String name;
    String description;
}
