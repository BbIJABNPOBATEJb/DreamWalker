package me.bbijabnpobatejb.dreamwalker.cube.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.dreamwalker.config.model.SimpleConfig;

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

    public String format(SimpleConfig config) {
        StringBuilder sb = new StringBuilder();
        StringBuilder rollsFormatted = new StringBuilder();

        for (int i = 0; i < rolled.size(); i++) {
            if (i > 0) rollsFormatted.append(config.getRollDelimiter());

            Cube cube = rolled.get(i);

            String color = cube.isMin() ? config.getRollColorMin()
                    : cube.isMax() ? config.getRollColorMax()
                    : config.getRollColorNormal();

            rollsFormatted.append(color).append(cube).append(config.getRollColorReset());
        }

        sb.append(String.format(
                config.getRollOutputFormat(),
                count, sides, rollsFormatted.toString()
        ));

        return sb.toString();
    }
}