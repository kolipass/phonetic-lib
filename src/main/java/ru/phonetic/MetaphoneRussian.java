package ru.phonetic;

import org.apache.commons.codec.StringEncoder;

import java.util.Arrays;

/**
 * Реализация русского Metaphone. Версия Петра Каньковски с некоторыми модификациями.
 *
 * @author nikitozzz.pl
 */
public class MetaphoneRussian implements StringEncoder {

	@Override
	public Object encode(Object pObject) {
		return encode(pObject.toString());
	}

	public String encode(String string) {
		if ((string == null) || (string.length() == 0)) return "";

		StringBuilder stringBuilder = normalize(string);
		replaceLastSonant(stringBuilder);

		StringBuilder resultBuilder = new StringBuilder(stringBuilder.length());

		char oldCh = 0;
		for (int i = 0; i < stringBuilder.length(); ++i) {
			char ch = stringBuilder.charAt(i);

			int vowelIndex = vowelPattern.indexOf(ch);
			if (vowelIndex >= 0) {
				if ((oldCh == 'Й' || oldCh == 'И') && (ch == 'О' || ch == 'Е'))
					resultBuilder.setCharAt(resultBuilder.length() - 1, 'И');
				else resultBuilder.append(vowelReplace.charAt(vowelIndex));
			}
			else {
				if (Arrays.binarySearch(sonantsToBreathConsonantsCharArray, ch) >= 0) {
					int sonantIndex = sonants.indexOf(oldCh);
					if (sonantIndex >= 0) {
						oldCh = breathConsonants.charAt(sonantIndex);
						resultBuilder.setCharAt(resultBuilder.length() - 1, oldCh);
					}
				}
				if (oldCh == 'Т' && ch == 'С')
					resultBuilder.setCharAt(resultBuilder.length() - 1, 'Ц');
				else if (ch != oldCh) resultBuilder.append(ch);
			}
			oldCh = ch;
		}
		return resultBuilder.toString();
	}

	private static StringBuilder normalize(String string) {
		StringBuilder stringBuilder = new StringBuilder(string.length());

		char oldCh = 0;
		for (int i = 0; i < string.length(); ++i) {
			char ch = Character.toUpperCase(string.charAt(i));
			if (ch != oldCh) if (Arrays.binarySearch(alphabetCharArray, ch) >= 0) stringBuilder.append(ch);
			oldCh = ch;
		}
		return stringBuilder;
	}

	private static void replaceLastSonant(StringBuilder stringBuilder) {
		if (stringBuilder.length() > 0) {
			int lastSonantIndex = sonants.indexOf(stringBuilder.charAt(stringBuilder.length() - 1));

			if (lastSonantIndex >= 0)
				stringBuilder.setCharAt(stringBuilder.length() - 1, breathConsonants.charAt(lastSonantIndex));
		}
	}

	private static final String alphabet = "ЁАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЫЭЮЯ";
	private static final String sonants = "БЗДВГ";
	private static final String breathConsonants = "ПСТФК";
	private static final String sonantsToBreathConsonants = "ПСТКБВГДЖЗФХЦЧШЩ";
	private static final String vowelPattern = "ОЮЕЭЯЁЫ";
	private static final String vowelReplace = "АУИИАИА";

	private static final char[] alphabetCharArray = alphabet.toCharArray();
	private static final char[] sonantsToBreathConsonantsCharArray = sonantsToBreathConsonants.toCharArray();

	static {
		Arrays.sort(alphabetCharArray);
		Arrays.sort(sonantsToBreathConsonantsCharArray);
	}
}
