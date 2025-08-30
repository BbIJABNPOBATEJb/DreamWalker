package me.bbijabnpobatejb.dreamwalker.cube.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CubeRoll {
    String original;
    List<CubeTerm> terms = new ArrayList<>();
    int bonus;

    public CubeRoll(String original, List<CubeTerm> terms, int bonus) {
        this.original = original;
        this.terms.addAll(terms);
        this.bonus = bonus;
    }

    public int total() {
        return terms.stream().mapToInt(CubeTerm::total).sum() + bonus;
    }


    public String format() {
        StringBuilder sb = new StringBuilder();
        for (CubeTerm term : terms) {
            if (sb.length() > 0) sb.append(" + ");
            sb.append(term.format());
        }
        if (bonus != 0) {
            sb.append(" + ").append(bonus);
        }
        return sb.toString();
    }
}