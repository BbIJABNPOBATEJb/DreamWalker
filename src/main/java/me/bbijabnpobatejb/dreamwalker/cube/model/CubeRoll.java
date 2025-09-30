package me.bbijabnpobatejb.dreamwalker.cube.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.config.model.SimpleConfig;

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


    public String format(SimpleConfig config) {
        StringBuilder sb = new StringBuilder();
        val plus = config.getRollPlus();
        boolean first = true;
        for (CubeTerm term : terms) {
            if (!first) {
                sb.append(term.getSign() == 1 ? " + " : " - ");
            } else if (term.getSign() == -1) {
                sb.append("-");
            }
            sb.append(term.format(config));
            first = false;
        }

        if (bonus != 0) {
            sb.append(bonus > 0 ? " + " : " - ").append(Math.abs(bonus));
        }
        return sb.toString();
    }
}