package me.bbijabnpobatejb.dreamwalker.cube.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Cube {
    int sides;
    int result;

    public boolean isMin() {
        return result == 1;
    }

    public boolean isMax() {
        return result == sides;
    }

    @Override
    public String toString() {
        return String.valueOf(result);
    }
}
