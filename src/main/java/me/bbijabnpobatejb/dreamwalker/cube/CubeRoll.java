package me.bbijabnpobatejb.dreamwalker.cube;

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
        return format(total());
    }
    public String format(int total) {
        StringBuilder sb = new StringBuilder();
        for (CubeTerm term : terms) {
            if (sb.length() > 0) sb.append(" + ");
            sb.append(term.format());
        }
        if (bonus != 0) {
            sb.append(" + ").append(bonus);
        }
        sb.append(" = ").append(total);
        return sb.toString();
    }
}