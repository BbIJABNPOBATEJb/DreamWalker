package me.bbijabnpobatejb.dreamwalker.cube;

import lombok.experimental.UtilityClass;
import me.bbijabnpobatejb.dreamwalker.cube.model.CubeRoll;
import me.bbijabnpobatejb.dreamwalker.cube.model.CubeTerm;
import me.bbijabnpobatejb.dreamwalker.side.ClientProxy;

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

        boolean allowOverrides = ClientProxy.clientIsAdmin;

        for (String part : expr.split("\\+")) {
            if (!first) originalBuilder.append("+");
            first = false;

            if (part.contains("d")) {
                String dicePart = part;
                String overrideStr = null;

                if (allowOverrides && part.contains("/")) {
                    String[] split = part.split("/", 2);
                    dicePart = split[0];
                    overrideStr = split[1];
                } else if (!allowOverrides && part.contains("/")) {
                    dicePart = part.split("/", 2)[0]; // отбрасываем / даже если оно присутствует
                }

                String[] splitDice = dicePart.split("d");
                int count = splitDice[0].isEmpty() ? 1 : Integer.parseInt(splitDice[0]);
                int sides = Integer.parseInt(splitDice[1]);

                List<Integer> overrides = null;

                if (allowOverrides && overrideStr != null) {
                    overrides = new ArrayList<>();
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
                } catch (NumberFormatException ignored) {}
                originalBuilder.append(part);
            }
        }

        return new CubeRoll(originalBuilder.toString(), cubes, bonus);
    }
}