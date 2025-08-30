package me.bbijabnpobatejb.dreamwalker.cube;

import lombok.experimental.UtilityClass;
import me.bbijabnpobatejb.dreamwalker.cube.model.CubeRoll;
import me.bbijabnpobatejb.dreamwalker.cube.model.CubeTerm;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CubeParser {
    public CubeRoll parse(String input) {
        String expr = input.replaceAll("\\s", "").toLowerCase().replace("д", "d");
        List<CubeTerm> cubes = new ArrayList<>();
        int bonus = 0;

        StringBuilder originalBuilder = new StringBuilder();
        boolean first = true;

        for (String part : expr.split("\\+")) {
            if (!first) originalBuilder.append("+");
            first = false;

            if (part.contains("d")) {
                String dicePart = part;
                String overrideStr = null;

                if (part.contains("/")) {
                    String[] split = part.split("/", 2);
                    dicePart = split[0];
                    overrideStr = split[1];
                }

                String[] splitDice = dicePart.split("d");
                int count = splitDice[0].isEmpty() ? 1 : Integer.parseInt(splitDice[0]);
                int sides = Integer.parseInt(splitDice[1]);

                List<Integer> overrides = new ArrayList<>();
                if (overrideStr != null) {
                    for (String value : overrideStr.split(",")) {
                        try {
                            overrides.add(Integer.parseInt(value));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                cubes.add(new CubeTerm(count, sides, overrides));
                originalBuilder.append(dicePart); // без "/..."
            } else {
                try {
                    bonus += Integer.parseInt(part);
                } catch (NumberFormatException ignored) {
                }
                originalBuilder.append(part);
            }
        }

        return new CubeRoll(originalBuilder.toString(), cubes, bonus);
    }
}