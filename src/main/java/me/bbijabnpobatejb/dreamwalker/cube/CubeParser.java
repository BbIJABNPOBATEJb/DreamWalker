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

        for (String part : expr.split("\\+")) {
            if (part.contains("d")) {
                String[] split = part.split("d");
                int count = split[0].isEmpty() ? 1 : Integer.parseInt(split[0]);
                int sides = Integer.parseInt(split[1]);
                cubes.add(new CubeTerm(count, sides));
            } else {
                bonus += Integer.parseInt(part);
            }
        }

        return new CubeRoll(input, cubes, bonus);
    }
}