package ru.phonetic.compareUtils;

/**
 * Created by kolipass on 16.11.15.
 */
public interface DiffMeter<T> {
    T measureDifference(CharSequence s, CharSequence t);

}
