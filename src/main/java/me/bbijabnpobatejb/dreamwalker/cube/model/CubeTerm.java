package me.bbijabnpobatejb.dreamwalker.cube.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
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
        this(count, sides, null);
    }

    public CubeTerm(int count, int sides, @Nullable List<Integer> overrides) {
        this.count = count;
        this.sides = sides;

        for (int i = 0; i < count; i++) {
            int value;
            if (overrides != null && !overrides.isEmpty()) {
                int override;
                if (overrides.size() == 1) {
                    override = overrides.get(0); // единичное значение → всем
                } else if (i < overrides.size()) {
                    override = overrides.get(i);
                } else {
                    override = -1; // превышение длины → рандом
                }

                if (override > 0) {
                    value = Math.max(1, Math.min(sides, override)); // clamp
                } else {
                    value = 1 + (int) (Math.random() * sides);
                }
            } else {
                value = 1 + (int) (Math.random() * sides);
            }

            rolled.add(new Cube(sides, value));
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