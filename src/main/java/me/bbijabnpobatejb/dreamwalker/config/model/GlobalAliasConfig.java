package me.bbijabnpobatejb.dreamwalker.config.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.dreamwalker.alias.Alias;

import java.util.Arrays;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GlobalAliasConfig {
    List<Alias> globalAlias = Arrays.asList(
            new Alias("удача", "Удача", "+5 -2", Arrays.asList(
                    "/say Удача",
                    "/say {args}"
            ))
    );




}
