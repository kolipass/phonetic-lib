package ru.phonetic.compareUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by kolipass on 16.11.15.
 */
public interface DiffMeter<T> {
    T measureDifference(CharSequence s, CharSequence t);

    class LevenshteinDistanceDiffMetr implements DiffMeter<Integer> {
        public Integer measureDifference(CharSequence s, CharSequence t) {
            return StringUtils.getLevenshteinDistance(s, t);
        }
    }

    class JaroWinklerDistanceDiffMetr implements DiffMeter<Double> {
        public Double measureDifference(CharSequence s, CharSequence t) {
            return StringUtils.getJaroWinklerDistance(s, t);
        }
    }
}
