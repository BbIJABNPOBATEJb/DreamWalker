package me.bbijabnpobatejb.dreamwalker.cube;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CubeTerm {
    int count;
    int sides;
    List<Cube> rolled = new ArrayList<>();

    public CubeTerm(int count, int sides) {
        this.count = count;
        this.sides = sides;
        for (int i = 0; i < count; i++) {
            int result = 1 + (int) (Math.random() * sides);
            rolled.add(new Cube(sides, result));
        }
    }

    public int total() {
        return rolled.stream().mapToInt(Cube::getResult).sum();
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%sд%d: ",
                count, sides));

        for (int i = 0; i < rolled.size(); i++) {
            Cube cube = rolled.get(i);
            if (i > 0) sb.append(", ");
            String color = cube.isMin() ? "&c" : cube.isMax() ? "&a" : "";
            sb.append(color).append(cube).append("&r");
        }
        sb.append("]");
        return sb.toString();
    }
}