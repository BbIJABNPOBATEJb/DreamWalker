package me.bbijabnpobatejb.dreamwalker.config.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.dreamwalker.alias.object.Alias;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerAliasConfig {

    List<Alias> aliases = new ArrayList<>();
// new Alias("сила", "Сила", "+3",
//           Arrays.asList("/roll 1d20+3", "/roll {args}")
//                    ),
//                            new Alias("ловкость", "Ловкость", "+2",
//                                      Arrays.asList("/roll 1d20+3", "/roll {args}")
//                    )

}
