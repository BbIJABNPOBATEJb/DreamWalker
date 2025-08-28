package me.bbijabnpobatejb.dreamwalker.alias;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Alias {
    String alias;
    String displayName;
    String description;
    List<String> runCommands;
}
