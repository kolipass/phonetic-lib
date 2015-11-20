package ru.phonetic;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.language.Caverphone2;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.RefinedSoundex;
import org.apache.commons.codec.language.Soundex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Фонетические алгоритмы - утилита генерации фонетических кодов по словарю фамилий.
 *
 * @author nikitozzz.pl
 */
public class PhoneticSearch {

    public static void main(String[] args) throws IOException, EncoderException {
        Map<String, Encoder> encoders = getStringEncoderMap();

        String[] dictionary = loadDictionary("dictionary.txt");

        for (Map.Entry<String, Encoder> entry : encoders.entrySet()) {
            PrintWriter writer = new PrintWriter(System.out);

            Encoder encoder = entry.getValue();

            Map<String, List<String>> stringMap = new TreeMap<>();

            for (String word : dictionary) {
                String[] keys = encoder.encode(word);

                for (String key : keys) {
                    key = key.toUpperCase();
                    List<String> stringList = stringMap.get(key);
                    if (stringList == null) {
                        stringList = new ArrayList<>();
                        stringMap.put(key, stringList);
                    }
                    stringList.add(word);
                }
            }

            for (Map.Entry<String, List<String>> stringEntry : stringMap.entrySet()) {
                writer.print(stringEntry.getKey());
                writer.print(" ");

                int index = 0;
                for (String value : stringEntry.getValue()) {
                    if (index++ > 0) writer.print(", ");
                    writer.print(value);
                }
                writer.println();

                writer.flush();
            }

            writer.close();
        }
    }

    public static Map<String, Encoder> getStringEncoderMap() {
        return new LinkedHashMap<String, Encoder>() {{
            put("Soundex", new TranslitEncoder(new AlgorithmEncoder(new Soundex())));
            put("Soundex split by space", new TranslitEncoder(new SplitStringEncoder(new Soundex())));
            put("Refined Soundex", new TranslitEncoder(new AlgorithmEncoder(new RefinedSoundex())));
            put("NYSIIS", new TranslitEncoder(new AlgorithmEncoder(new Nysiis())));
            put("NYSIIS apache", new TranslitEncoder(new AlgorithmEncoder(new org.apache.commons.codec.language.Nysiis())));
            put("NYSIIS apache   split by space", new TranslitEncoder(new SplitStringEncoder(new org.apache.commons.codec.language.Nysiis())));
//            put("Daitch-Mokotoff Soundex Multiple", new TranslitEncoder(new AlgorithmEncoder(new DMSoundexWrap())));
            put("Daitch-Mokotoff Soundex", new TranslitEncoder(new SplitStringEncoder(new DMSoundexWrap())));
            put("Metaphone", new TranslitEncoder(new AlgorithmEncoder(new Metaphone())));
            put("Metaphone   split by space", new TranslitEncoder(new SplitStringEncoder(new Metaphone())));
//            put("Double Metaphone Multiple", new TranslitEncoder(new AlgorithmEncoder(new DoubleMetaphoneWrap())));
            put("Double Metaphone", new TranslitEncoder(new AlgorithmEncoder(new DoubleMetaphone())));
            put("Double Metaphone  split by space", new TranslitEncoder(new SplitStringEncoder(new DoubleMetaphone())));
            put("Russian Metaphone", new AlgorithmEncoder(new MetaphoneRussian()));
            put("Caverphone2", new TranslitEncoder(new AlgorithmEncoder(new Caverphone2())));
            put("Caverphone2 split by space", new TranslitEncoder(new SplitStringEncoder(new Caverphone2())));
        }};
    }

    public static String[] loadDictionary(String filename) throws IOException {
        List<String> lines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null)
            lines.add(line);

        reader.close();
        return lines.toArray(new String[lines.size()]);
    }

    public interface Encoder {

        String[] encode(String word) throws EncoderException;
    }

    public interface StringMultiEncoder extends StringEncoder {

        String[] encodeMultiple(String pString) throws EncoderException;
    }

    public static class AlgorithmEncoder implements Encoder {

        private final StringEncoder encoder;

        public AlgorithmEncoder(StringEncoder encoder) {
            this.encoder = encoder;
        }

        public String[] encode(String word) throws EncoderException {
            if (encoder instanceof StringMultiEncoder)
                return ((StringMultiEncoder) encoder).encodeMultiple(word);
            return new String[]{encoder.encode(word)};
        }
    }

    public static class SplitStringEncoder extends AlgorithmEncoder {

        public SplitStringEncoder(StringEncoder encoder) {
            super(encoder);
        }

        public String[] encode(String inputString) throws EncoderException {
            String space = " ";
            if (inputString.lastIndexOf(space) > -1) {
                List<String> result = new ArrayList<>();

                for (String word : inputString.split("[\\p{Punct},\\s]")) {
                    if (word == null || word.isEmpty()) {
                        continue;
                    }
                    result.add(Arrays.toString(super.encode(word)));
                }

                return result.toArray(new String[result.size()]);

            } else {
                return super.encode(inputString);
            }
        }
    }

    public static class TranslitEncoder implements Encoder {

        private final Encoder encoder;

        public TranslitEncoder(Encoder encoder) {
            this.encoder = encoder;
        }

        public String[] encode(String pString) throws EncoderException {
            return encoder.encode(Translit.toTranslit(pString));
        }
    }

    public static class DMSoundexMultiWrap implements StringMultiEncoder {

        private final DMSoundex encoder;

        public DMSoundexMultiWrap() {
            encoder = new DMSoundex();
        }

        public String encode(String pString) throws EncoderException {
            return encoder.sencode(pString.toLowerCase());
        }

        public Object encode(Object pObject) throws EncoderException {
            return encode(pObject.toString());
        }

        public String[] encodeMultiple(String pString) throws EncoderException {
            return encoder.soundexes(pString.toLowerCase());
        }
    }
    public static class DMSoundexWrap implements StringEncoder {

        private final DMSoundex encoder;

        public DMSoundexWrap() {
            encoder = new DMSoundex();
        }

        public String encode(String pString) throws EncoderException {
            return encoder.sencode(pString.toLowerCase());
        }

        public Object encode(Object pObject) throws EncoderException {
            return encode(pObject.toString());
        }

    }

    public static class DoubleMetaphoneWrap implements StringMultiEncoder {

        private final DoubleMetaphone encoder;

        public DoubleMetaphoneWrap() {
            encoder = new DoubleMetaphone();
        }

        public String[] encodeMultiple(String pString) throws EncoderException {
            return new String[]{encoder.doubleMetaphone(pString, false), encoder.doubleMetaphone(pString, true)};
        }

        public String encode(String pString) throws EncoderException {
            return encoder.doubleMetaphone(pString);
        }

        public Object encode(Object pObject) throws EncoderException {
            return encode(pObject.toString());
        }
    }
}
