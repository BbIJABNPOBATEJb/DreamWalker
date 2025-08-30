package me.bbijabnpobatejb.dreamwalker.config.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleConfig {

    String chatPrefix = "§3❯❯§r ";
    String aliasPrefix = ";";
    String rollPrefix = "%";
    String argsHolder = "{args}";
    String formatMessageRoll = "бросает ⚅ {ordinal}: {format}";
    String formatResultMessageRoll = " {result} (&7{comment}&r)";
    int rollCommentMaxChars = 16;
    String[] channelPrefixes = {"@", "-", "!"};


}
