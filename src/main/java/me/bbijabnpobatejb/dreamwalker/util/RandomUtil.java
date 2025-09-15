/*
 *    Copyright 2023 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package me.bbijabnpobatejb.dreamwalker.util;

import lombok.experimental.UtilityClass;
import lombok.val;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.ToDoubleFunction;

/**
 * @author whilein
 */
@UtilityClass
public class RandomUtil {

    private final Random RANDOM = new Random();


    public <T> @Nullable T weightedRandom(
            final Collection<T> items,
            final ToDoubleFunction<T> weightCalculator
    ) {
        val sum = items.stream()
                .mapToDouble(weightCalculator)
                .sum();

        val randomizedSum = RANDOM.nextDouble() * sum;

        double from = 0.0;

        T last = null;

        for (val item : items) {
            val itemWeight = weightCalculator.applyAsDouble(item);

            if (randomizedSum >= from && randomizedSum < from + itemWeight) {
                return item;
            }

            last = item;
            from += itemWeight;
        }

        return last;
    }

    public <T> @Nullable T weightedRandom(
            final Map<T, Double> weights
    ) {
        double sum = weights.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (sum == 0.0) {
            return null;
        }

        double randomizedSum = RANDOM.nextDouble() * sum;

        double from = 0.0;
        T last = null;

        for (Map.Entry<T, Double> entry : weights.entrySet()) {
            double itemWeight = entry.getValue();
            T item = entry.getKey();

            if (randomizedSum >= from && randomizedSum < from + itemWeight) {
                return item;
            }

            last = item;
            from += itemWeight;
        }

        return last;
    }

    public boolean randomChance(double chance) {
        if (chance < 0.0 || chance > 1.0) {
            throw new IllegalArgumentException("Вероятность должна быть от 0.0 до 1.0");
        }
        return RANDOM.nextDouble() < chance;
    }


    public <T> T getRandomElement(final T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    public <T> T getRandomElement(final List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }



}