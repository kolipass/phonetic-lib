package ru.phonetic;

import org.apache.commons.codec.EncoderException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.phonetic.compareUtils.DiffMeter;
import ru.phonetic.compareUtils.Preparator;

/**
 * Created by kolipass on 16.11.15.
 */
public class PhoneticCompare {

    public static final String DEVIDER = ",";

    public static void main(String[] args) throws IOException, EncoderException {
        String comparable = "волиция также сообщения";
        String baseString = "В полиции также сообщили, что собака была найдена полицейскими ";

        Map<String, String> baseStringSet = getPreparatedStringMap(baseString,
                new ArrayList<Preparator>() {{
                    //Все в нижний регистр
                    add(target -> target.toString().toLowerCase());
                    //заменить всю пунктуацияю на пробелы, избегая ситуации, когда слова разделяет знак пунктуации: "не синий,а черный"
                    add(target -> target.toString().replaceAll("[\\p{Punct},\\s]", " "));
                    //заменить все повторяющиеся пробелы на одинарный
                    add(target -> target.toString().replaceAll("\\s{2,}", " "));
                }},
                getPreparedFactory(baseString, comparable));

        Map<String, DiffMeter> diffMeterMap = diffMeterFactory();
        Map<String, PhoneticSearch.Encoder> encoderMap = PhoneticSearch.getStringEncoderMap();

        PrintWriter writer = new PrintWriter("Stat.CSV");
//        PrintWriter writer = new PrintWriter(System.out);
        compare(comparable, baseStringSet, encoderMap, diffMeterMap, writer);
        writer.flush();
        writer.close();
    }


    /**
     * Подготавливает входную строку с помощью Preparator'ов
     *
     * @param baseString      исходная строка
     * @param preparators     базовые преобразования ко всей строке: опустить регист, выкинуть лишнее.Те преобразования, от которых не требуется получить в дальнейшем какие-то сведения
     * @param preparedFactory вариативные Preparator. Ключ - это тег для лога каждому препоратору. Работа каждого из них добавится отдельным пунктом
     * @return Map, который содержит ключ от preparedFactory и преобразованную строку.
     */
    private static Map<String, String> getPreparatedStringMap(String baseString,
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
                                Map<String, PhoneticSearch.Encoder> encoderMap,
                                Map<String, DiffMeter> diffMeterMap,
                                PrintWriter writer) throws EncoderException {

        for (Map.Entry<String, String> entry : baseString.entrySet()) {
            String currentBaseValue = entry.getValue();

            String title = wrap(entry.getKey()) + "\n";

            String baseLine = "base" + DEVIDER + wrap(currentBaseValue);
            String comparableLine = "comparable" + DEVIDER + wrap(comparable);

            String encodersTitle = DEVIDER + DEVIDER;


            Map<String, String> calculations = new LinkedHashMap<>();
            for (Map.Entry<String, PhoneticSearch.Encoder> encoderEntry : encoderMap.entrySet()) {
                encodersTitle += encoderEntry.getKey() + DEVIDER;

                String encodedBaseString = wrap(Arrays.toString(encoderEntry.getValue()
                        .encode(currentBaseValue)));
                String encodedComparableString = wrap(Arrays.toString(encoderEntry.getValue()
                        .encode(comparable)));

                baseLine += DEVIDER + encodedBaseString;
                comparableLine += DEVIDER + encodedComparableString;


                for (Map.Entry<String, DiffMeter> diffMeterEntry : diffMeterMap.entrySet()) {
                    String meterKey = diffMeterEntry.getKey();
                    DiffMeter meter = diffMeterEntry.getValue();

                    String lastCalculation = calculations.get(meterKey);
                    String calculation = String.valueOf(meter.measureDifference(encodedBaseString, encodedComparableString));

                    calculations.put(meterKey, (
                            lastCalculation == null ? "" : lastCalculation + DEVIDER) + calculation);

                }
            }
            List<String> titles = Arrays.asList(title, encodersTitle, baseLine, comparableLine);
            printTable(titles, calculations, writer);
        }
    }

    private static void printTable(List<String> titles, Map<String, String> calculations, PrintWriter writer) {
        titles.forEach(writer::println);
        for (Map.Entry<String, String> entry : calculations.entrySet()) {
            writer.println(DEVIDER +
                    wrap(entry.getKey()) + DEVIDER + entry.getValue());
        }
    }

    private static String wrap(String s) {
        return "\"" + s + "\"";
    }

    /**
     * Фабричный метод, пораждающий метрики отличий
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
     *
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
     *
     * @param target           целевая строка
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
