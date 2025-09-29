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
        boolean allowOverrides = ClientProxy.clientIsAdmin;

        // например: "2d10-5+1" → ["+2d10", "-5", "+1"]
        List<String> parts = getStrings(expr);

        for (String part : parts) {
            originalBuilder.append(part);

            char sign = part.charAt(0);
            String body = part.substring(1);

            if (body.contains("d")) {
                CubeTerm term = getCubeTerm(body, allowOverrides);
                cubes.add(term);

                // если минус перед кубами: вычитаем результат
                if (sign == '-') {
                    bonus -= term.total() * 2;
                }
            } else {
                try {
                    int value = Integer.parseInt(body);
                    bonus += (sign == '-' ? -value : value);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return new CubeRoll(originalBuilder.toString(), cubes, bonus);
    }

    List<String> getStrings(String expr) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        char prevOp = '+'; // считаем что выражение всегда положительное в начале

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '+' || c == '-') {
                if (current.length() > 0) {
                    parts.add(prevOp + current.toString());
                    current.setLength(0);
                }
                prevOp = c;
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            parts.add(prevOp + current.toString());
        }
        return parts;
    }

    CubeTerm getCubeTerm(String body, boolean allowOverrides) {
        String dicePart = body;
        String overrideStr = null;

        if (allowOverrides && body.contains("/")) {
            String[] split = body.split("/", 2);
            dicePart = split[0];
            overrideStr = split[1];
        } else if (!allowOverrides && body.contains("/")) {
            dicePart = body.split("/", 2)[0];
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

        CubeTerm term = new CubeTerm(count, sides, overrides);
        return term;
    }
}