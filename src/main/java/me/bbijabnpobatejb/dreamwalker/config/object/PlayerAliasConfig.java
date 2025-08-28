package me.bbijabnpobatejb.dreamwalker.config.object;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.dreamwalker.alias.Alias;

import java.util.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerAliasConfig {

    Map<String, List<Alias>> playersAlias = new HashMap<String, List<Alias>>() {
        {
            put("Player123", Arrays.asList(
                    new Alias("сила", "Сила", "+3",
                            Arrays.asList("/roll 1d20+3", "/roll {args}")
                    ),
                    new Alias("ловкость", "Ловкость", "+2",
                            Arrays.asList("/roll 1d20+3", "/roll {args}")
                    )
            ));
        }
    };


}
