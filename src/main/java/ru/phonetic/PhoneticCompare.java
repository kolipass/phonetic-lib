package ru.phonetic;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Caverphone2;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.RefinedSoundex;
import org.apache.commons.codec.language.Soundex;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.phonetic.compareUtils.DiffMeter;
import ru.phonetic.compareUtils.JaroWinklerDistanceDiffMetr;
import ru.phonetic.compareUtils.LevenshteinDistanceDiffMetr;
import ru.phonetic.compareUtils.Preparator;

import static ru.phonetic.PhoneticSearch.AlgorithmEncoder;
import static ru.phonetic.PhoneticSearch.Encoder;
import static ru.phonetic.PhoneticSearch.SplitStringEncoder;
import static ru.phonetic.PhoneticSearch.TranslitEncoder;

/**
 * Created by kolipass on 16.11.15.
 */
public class PhoneticCompare {

    public static final String DEVIDER = ",";

    public static void main(String[] args) throws IOException, EncoderException {
        String comparable = "политика Аристотель собака Ben10";
        String baseString = "В полиции также сообщили, что собака была найдена полицейскими 555";

        Map<String, String> baseStringSet = getPreparatedStringMap(
                getPreperedString(baseString, getPreparators()),
                getPreparedFactory(baseString, comparable));

        Map<String, DiffMeter> diffMeterMap = diffMeterFactory();
        Map<String, Encoder> encoderMap = getStringEncoderMap();

        PrintWriter writer = new PrintWriter("Stat.CSV");
//        PrintWriter writer = new PrintWriter(System.out);
        compare(getPreperedString(comparable, getPreparators()), baseStringSet, encoderMap, diffMeterMap, writer);
        writer.flush();
        writer.close();
    }

    public static ArrayList<Preparator> getPreparators() {
        return new ArrayList<Preparator>() {{
            //Все в нижний регистр
            add(target -> target.toString().toLowerCase());
            //заменить всю пунктуацияю на пробелы, избегая ситуации, когда слова разделяет знак пунктуации: "не синий,а черный"
            add(target -> target.toString().replaceAll("[\\p{Punct},\\s]", " "));
            //пока не решен вопрос с цыфрами
            add(target -> target.toString().replaceAll("\\d+", " "));
            //заменить все повторяющиеся пробелы на одинарный
            add(target -> target.toString().replaceAll("\\s{2,}", " "));
            add(target -> target.toString().trim());
        }};
    }

    /**
     * Подготавливает входную строку с помощью Preparator'ов
     *
     * @param baseString  исходная строка
     * @param preparators базовые преобразования ко всей строке: опустить регист, выкинуть лишнее.Те преобразования, от которых не требуется получить в дальнейшем какие-то сведения
     * @return подготовленную строку
     */
    public static String getPreperedString(String baseString,
                                           List<Preparator> preparators) {
        for (Preparator preparator : preparators) {
            baseString = preparator.prepare(baseString).toString();
        }
        return baseString;
    }

    /**
     * Подготавливает входную строку с помощью Preparator'ов
     *
     * @param baseString      исходная строка
     * @param preparedFactory вариативные Preparator. Ключ - это тег для лога каждому препоратору. Работа каждого из них добавится отдельным пунктом
     * @return Map, который содержит ключ от preparedFactory и преобразованную строку.
     */
    public static Map<String, String> getPreparatedStringMap(String baseString,
                                                             Map<String, Preparator> preparedFactory) {
        final String preparedString = baseString;
        return new HashMap<String, String>() {{
            for (Map.Entry<String, Preparator> entry : preparedFactory.entrySet()) {
                put(entry.getKey(), entry.getValue().prepare(preparedString).toString());
            }
        }};
    }

    public static void compare(String comparable,
                               String baseString,
                               Map<String, PhoneticSearch.Encoder> encoderMap,
                               Map<String, DiffMeter> diffMeterMap,
                               PrintWriter writer) throws EncoderException {
        compare(comparable,
                new HashMap<String, String>() {{
                    put(null, getPreperedString(baseString, getPreparators()));
                }},
                encoderMap,
                diffMeterMap,
                writer);
    }

    public static void compare(String comparable,
                               Map<String, String> baseString,
                               Map<String, PhoneticSearch.Encoder> encoderMap,
                               Map<String, DiffMeter> diffMeterMap,
                               PrintWriter writer) throws EncoderException {

        for (Map.Entry<String, String> entry : baseString.entrySet()) {
            String currentBaseValue = entry.getValue();

            String title = entry.getKey() != null ? wrap(entry.getKey()) + "\n" : null;

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

    public static void printTable(List<String> titles, Map<String, String> calculations, PrintWriter writer) {
        for (String s : titles) {
            if (s != null) {
                writer.println(s);
            }
        }
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
    public static Map<String, DiffMeter> diffMeterFactory() {
        return new HashMap<String, DiffMeter>() {{
            put("JaroWinklerDistance", new JaroWinklerDistanceDiffMetr());
            put("LevenshteinDistance", new LevenshteinDistanceDiffMetr());
        }};
    }

    /**
     * Фабричный метод различных вариантов обработки входящей стрки
     *
     * @param baseString входящая строка для обработки
     * @param comparable строка, с которой будет сравниваться.
     * @return набор из Preparator ов
     */
    public static Map<String, Preparator> getPreparedFactory(final String baseString, final String comparable) {
        final int comparableLength = comparable.length();
        return new HashMap<String, Preparator>() {{
//            put("Full length base String", target -> target);
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
    public static CharSequence getFullWordsString(CharSequence target, int comparableLength) {
        final String SUB = " ";
        int spasePos = target.toString().indexOf(SUB, comparableLength);
        String equalsCountOfWord = target.toString();
        if (spasePos > 0) {
            equalsCountOfWord = target.toString().substring(0, spasePos);
        }
        return equalsCountOfWord;
    }

    public static Map<String, Encoder> getStringEncoderMap() {
        return new LinkedHashMap<String, Encoder>() {{
            put("Soundex", new TranslitEncoder(new AlgorithmEncoder(new Soundex())));
            put("Soundex split by space", new TranslitEncoder(new SplitStringEncoder(new Soundex())));
            put("Refined Soundex", new TranslitEncoder(new AlgorithmEncoder(new RefinedSoundex())));
            put("NYSIIS", new TranslitEncoder(new AlgorithmEncoder(new Nysiis())));
//            put("NYSIIS apache", new TranslitEncoder(new AlgorithmEncoder(new org.apache.commons.codec.language.Nysiis())));
//            put("NYSIIS apache   split by space", new TranslitEncoder(new SplitStringEncoder(new org.apache.commons.codec.language.Nysiis())));
            put("Daitch-Mokotoff Soundex", new TranslitEncoder(new SplitStringEncoder(new PhoneticSearch.DMSoundexWrap())));
            put("Metaphone", new TranslitEncoder(new AlgorithmEncoder(new Metaphone())));
            put("Metaphone   split by space", new TranslitEncoder(new SplitStringEncoder(new Metaphone())));
            put("Double Metaphone Multiple", new TranslitEncoder(new AlgorithmEncoder(new PhoneticSearch.DoubleMetaphoneWrap())));
            put("Double Metaphone", new TranslitEncoder(new AlgorithmEncoder(new DoubleMetaphone())));
            put("Double Metaphone  split by space", new TranslitEncoder(new SplitStringEncoder(new DoubleMetaphone())));
            put("Russian Metaphone", new AlgorithmEncoder(new MetaphoneRussian()));
            put("Caverphone2", new TranslitEncoder(new AlgorithmEncoder(new Caverphone2())));
            put("Caverphone2 split by space", new TranslitEncoder(new SplitStringEncoder(new Caverphone2())));
        }};
    }
}
