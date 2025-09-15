package me.bbijabnpobatejb.dreamwalker.config.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.dreamwalker.alias.object.Alias;
import me.bbijabnpobatejb.dreamwalker.alias.object.RunCommand;

import java.util.Arrays;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GlobalAliasConfig {
    List<Alias> globalAlias = Arrays.asList(
            new Alias(Arrays.asList("удача", "удч", "уд"), "Удача", false, "+5 -2", Arrays.asList(
                    new RunCommand("/say Удача", 10),
                    new RunCommand("/say {args}", 0)
            ))
    );


}
