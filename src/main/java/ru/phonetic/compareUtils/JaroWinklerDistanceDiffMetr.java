package ru.phonetic.compareUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by kolipass on 19.11.15.
 */
public class JaroWinklerDistanceDiffMetr implements DiffMeter<Double> {
    public Double measureDifference(CharSequence s, CharSequence t) {
        return StringUtils.getJaroWinklerDistance(s, t);
    }
}
