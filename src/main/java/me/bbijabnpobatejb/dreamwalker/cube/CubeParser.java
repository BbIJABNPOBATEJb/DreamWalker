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

        List<String> parts = getStrings(expr);

        boolean first = true;
        for (String part : parts) {
            char sign = part.charAt(0);
            String body = part.substring(1);

            if (body.contains("d")) {
                // разбор кубов
                String dicePart = body;
                String overrideStr = null;

                if (allowOverrides && body.contains("/")) {
                    String[] split = body.split("/", 2);
                    dicePart = split[0]; // чистая часть для original
                    overrideStr = split[1];
                } else if (!allowOverrides && body.contains("/")) {
                    dicePart = body.split("/", 2)[0];
                }

                CubeTerm term = buildCubeTerm(dicePart, overrideStr, sign, allowOverrides);
                cubes.add(term);


                // Формируем original: без подкрутки, без лишнего '+'
                if (!first) {
                    originalBuilder.append(sign);
                } else if (sign == '-') {
                    originalBuilder.append('-');
                }
                originalBuilder.append(dicePart);

            } else {
                // бонусы
                int value = 0;
                try {
                    value = Integer.parseInt(body);
                } catch (NumberFormatException ignored) {
                }

                bonus += (sign == '-' ? -value : value);

                if (!first) {
                    originalBuilder.append(sign);
                } else if (sign == '-') {
                    originalBuilder.append('-');
                }
                originalBuilder.append(body);
            }
            first = false;
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

    CubeTerm buildCubeTerm(String dicePart, String overrideStr, char sign, boolean allowOverrides) {
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
        int signValue = sign == '-' ? -1 : 1;
        return new CubeTerm(count, sides, signValue, overrides);
    }
}