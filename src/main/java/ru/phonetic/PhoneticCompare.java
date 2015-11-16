package ru.phonetic;

import org.apache.commons.codec.EncoderException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.phonetic.compareUtils.DiffMeter;
import ru.phonetic.compareUtils.Preparator;

/**
 * Created by kolipass on 16.11.15.
 */
public class PhoneticCompare {

    public static void main(String[] args) throws IOException, EncoderException {
        String comparable = "волиция также сообщения";
        String baseString = "В полиции также сообщили, что собака была найдена полицейскими 5 августа в частном питомнике в городе Домодедово. На след подозреваемой удалось выйти благодаря показаниям свидетелей и записям камер видеонаблюдения, а ее маршрут был отслежен благодаря информации с турникетов.";

        Map<String, String> baseStringSet = getBase(baseString,
                new ArrayList<Preparator>() {{
                    add(target -> target.toString().toLowerCase());
                    add(target -> target.toString().replaceAll("[\\p{Punct},\\s]", " "));
                    add(target -> target.toString().replaceAll("\\s{2,}", ""));
                }},
                getPreparedFactory(baseString, comparable));

        Map<String, DiffMeter> diffMeterMap = diffMeterFactory();

        compare(comparable, baseStringSet, diffMeterMap);
    }

    private static Map<String, String> getBase(String baseString,
                                               List<Preparator> preparators,
                                               Map<String, Preparator> preparedFactory) {


        for (Preparator preparator : preparators) {
            baseString = preparator.prepare(baseString).toString();
        }

        final String preparedString = baseString;
        return new HashMap<String, String>() {{
            for (Map.Entry<String, Preparator> entry : preparedFactory.entrySet()) {
                put(entry.getKey(), entry.getValue().prepare(preparedString).toString());
            }
        }};
    }

    private static void compare(String comparable,
                                Map<String, String> baseString,
                                Map<String, DiffMeter> diffMeterMap) {

        for (Map.Entry<String, String> entry : baseString.entrySet()) {
            String currentBaseValue = entry.getValue();
            System.out.println(entry.getKey() + ":"
                    + "\nbase: " + currentBaseValue
                    + "\ncomparable: " + comparable);

            for (Map.Entry<String, DiffMeter> diffMeterEntry : diffMeterMap.entrySet()) {
                String meterKey = diffMeterEntry.getKey();
                DiffMeter meter = diffMeterEntry.getValue();
                System.out.println(meterKey + ": " + meter.measureDifference(currentBaseValue, comparable));
            }
        }
    }

    /**
     * Фабричный метод метрик отличий
     *
     * @return вернет две метрики:
     * JaroWinklerDistance
     * LevenshteinDistanceDiffMetr
     */
    private static Map<String, DiffMeter> diffMeterFactory() {
        return new HashMap<String, DiffMeter>() {{
            put("JaroWinklerDistance", new DiffMeter.JaroWinklerDistanceDiffMetr());
            put("LevenshteinDistance", new DiffMeter.LevenshteinDistanceDiffMetr());
        }};
    }

    /**
     * Фабричный метод различных вариантов обработки входящей стрки
     * @param baseString входящая строка для обработки
     * @param comparable строка, с которой будет сравниваться.
     * @return набор из Preparator ов
     */
    private static Map<String, Preparator> getPreparedFactory(final String baseString, final String comparable) {
        final int comparableLength = comparable.length();
        return new HashMap<String, Preparator>() {{
            put("Full length base String", target -> target);
            put("Equals length base String", target -> baseString.substring(0, comparableLength));
            put("Full worlds base string part", target -> getFullWordsString(target, comparableLength));
        }};
    }

    /**
     * ПОлучить строку, не оборванную на последнем слове
     * @param target целевая строка
     * @param comparableLength позиция, где слово могло оборваться
     * @return вернется строка до первого попадания пробела после @comparableLength, в противном случае - вся строка
     */
    private static CharSequence getFullWordsString(CharSequence target, int comparableLength) {
        final String SUB = " ";
        int spasePos = target.toString().indexOf(SUB, comparableLength);
        String equalsCountOfWord = target.toString();
        if (spasePos > 0) {
            equalsCountOfWord = target.toString().substring(0, spasePos);
        }
        return equalsCountOfWord;
    }
}
