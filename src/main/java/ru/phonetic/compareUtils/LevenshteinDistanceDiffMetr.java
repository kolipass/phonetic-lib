package ru.phonetic.compareUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by kolipass on 19.11.15.
 */
public class LevenshteinDistanceDiffMetr implements DiffMeter<Integer> {
    public Integer measureDifference(CharSequence s, CharSequence t) {
        return StringUtils.getLevenshteinDistance(s, t);
    }
}
