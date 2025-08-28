package me.bbijabnpobatejb.dreamwalker.config.object;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleConfig {

    String aliasPrefix = ";";
    String rollPrefix = "%";
    String argsHolder = "{args}";
    String formatMessageRoll = "бросает ⚅ {ordinal}: {format} (&7{comment}&r)";
    int rollCommentMaxChars = 16;
    String[] channelPrefixes = {"@", "-", "!"};


}
