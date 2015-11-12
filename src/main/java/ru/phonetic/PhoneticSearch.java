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
        Map<String, Encoder> encoders = new LinkedHashMap<String, Encoder>();

        encoders.put("Soundex", new TranslitEncoder(new AlgorithmEncoder(new Soundex())));
        encoders.put("Refined Soundex", new TranslitEncoder(new AlgorithmEncoder(new RefinedSoundex())));
        encoders.put("NYSIIS", new TranslitEncoder(new AlgorithmEncoder(new Nysiis())));
        encoders.put("Daitch-Mokotoff Soundex", new TranslitEncoder(new AlgorithmEncoder(new DMSoundexWrap())));
        encoders.put("Metaphone", new TranslitEncoder(new AlgorithmEncoder(new Metaphone())));
        encoders.put("Double Metaphone", new TranslitEncoder(new AlgorithmEncoder(new DoubleMetaphoneWrap())));
        encoders.put("Russian Metaphone", new AlgorithmEncoder(new MetaphoneRussian()));
        encoders.put("Caverphone", new TranslitEncoder(new AlgorithmEncoder(new Caverphone2())));

        String[] dictionary = loadDictionary("dictionary.txt");

        for (Map.Entry<String, Encoder> entry : encoders.entrySet()) {
            PrintWriter writer = new PrintWriter(entry.getKey() + " Stat.txt");

            Encoder encoder = entry.getValue();

            Map<String, List<String>> stringMap = new TreeMap<String, List<String>>();

            for (String word : dictionary) {
                String[] keys = encoder.encode(word);

                for (String key : keys) {
                    key = key.toUpperCase();
                    List<String> stringList = stringMap.get(key);
                    if (stringList == null) {
                        stringList = new ArrayList<String>();
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
            }
        }
    }

    public static String[] loadDictionary(String filename) throws IOException {
        List<String> lines = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null)
            lines.add(line);

        reader.close();
        return lines.toArray(new String[lines.size()]);
    }

    public static interface Encoder {

        String[] encode(String word) throws EncoderException;
    }

    public static interface StringMultiEncoder extends StringEncoder {

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

    public static class TranslitEncoder implements Encoder {

        private final Encoder encoder;

        public TranslitEncoder(Encoder encoder) {
            this.encoder = encoder;
        }

        public String[] encode(String pString) throws EncoderException {
            return encoder.encode(Translit.toTranslit(pString));
        }
    }

    public static class DMSoundexWrap implements StringMultiEncoder {

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

        public String[] encodeMultiple(String pString) throws EncoderException {
            return encoder.soundexes(pString.toLowerCase());
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
