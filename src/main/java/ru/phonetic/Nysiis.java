package ru.phonetic;

import org.apache.commons.codec.StringEncoder;

/**
 * This module implements the New York State Identification and Intelligence System (NYSIIS) Phonetic Code.
 * <p>
 * This version contains improvements by David Dossot &lt;david@dossot.net&gt;.
 * 
 * @author David Dossot
 * @author Leo Galambos (implementation of lowercase style)
 */
public class Nysiis implements StringEncoder {

	StringBuilder word = null;

	/**
	 * Description of the Method
	 * 
	 * @param word
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String sencode(String word) {
		Nysiis ny = new Nysiis();
		return ny.encode(word);
	}

	public Object encode(Object object) {
		return encode(object.toString());
	}

	/**
	 * encode - Nysiis phonetic encoding.
	 * 
	 * @param originalWord
	 *            Description of the Parameter
	 * @return String - the encoded word
	 */
	public String encode(String originalWord) {
		word = new StringBuilder(originalWord.toLowerCase());
		char first;

		// strip any trailing S or Zs
		while (word.toString().endsWith("s") || word.toString().endsWith("z"))
			word.deleteCharAt(word.length() - 1);
		// LG
		if (word.length() < 1) return originalWord;

		replaceFront("mac", "mc");
		replaceFront("pf", "f");
		replaceEnd("ix", "ic");
		replaceEnd("ex", "ec");

		replaceEnd("ye", "y");
		replaceEnd("ee", "y");
		replaceEnd("ie", "y");

		replaceEnd("dt", "d");
		replaceEnd("rt", "d");
		replaceEnd("rd", "d");

		replaceEnd("nt", "n");
		replaceEnd("nd", "n");

		// .EV => .EF
		replaceAll("ev", "ef", 1);

		first = word.charAt(0);

		// replace all vowels with 'A'
		// word = replaceAll( word, "A", "A" );
		replaceAll("e", "a");
		replaceAll("i", "a");
		replaceAll("o", "a");
		replaceAll("u", "a");

		// remove any 'W' that follows a vowel
		replaceAll("aw", "a");

		replaceAll("ght", "gt");
		replaceAll("dg", "g");
		replaceAll("ph", "f");

		replaceAll("sch", "s");
		replaceAll("ch", "s");
		// Added by David Dossot
		replaceAll("sh", "s");

		replaceAll("ah", "a", 1);
		replaceAll("ha", "a", 1);

		replaceAll("kn", "n");
		replaceAll("k", "c");

		replaceAll("m", "n", 1);
		replaceAll("q", "g", 1);

		replaceAll("yw", "y");

		replaceAll("y", "a", 1, word.length() - 2);

		replaceAll("wr", "r");

		replaceAll("z", "s", 1);

		replaceEnd("ay", "y");

		while (word.toString().endsWith("a"))
			word.deleteCharAt(word.length() - 1);

		reduceDuplicates();

		if ('a' == first || 'e' == first || 'i' == first || 'o' == first || 'u' == first) {
			if (word.length() > 0) word.deleteCharAt(0);
			word.insert(0, first);
		}

		return word.toString();
	}

	/**
	 * Description of the Method
	 * 
	 * @param word
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public String reduceDuplicates(String word) {
		return reduceDuplicates(new StringBuilder(word)).toString();
	}

	/**
	 * Description of the Method
	 * 
	 * @param word
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public StringBuilder reduceDuplicates(StringBuilder word) {
		char lastChar;
		StringBuilder newWord = new StringBuilder();

		if (0 == word.length()) return word;

		lastChar = word.charAt(0);
		newWord.append(lastChar);
		for (int i = 1; i < word.length(); ++i) {
			if (lastChar != word.charAt(i)) newWord.append(word.charAt(i));
			lastChar = word.charAt(i);
		}

		return newWord;
	}

	/**
	 * Description of the Method
	 */
	private void reduceDuplicates() {
		word = reduceDuplicates(word);
	}

	/**
	 * Description of the Method
	 * 
	 * @param find
	 *            Description of the Parameter
	 * @param repl
	 *            Description of the Parameter
	 */
	private void replaceAll(String find, String repl) {
		replaceAll(find, repl, 0, -1);
	}

	/**
	 * Description of the Method
	 * 
	 * @param find
	 *            Description of the Parameter
	 * @param repl
	 *            Description of the Parameter
	 * @param startPos
	 *            Description of the Parameter
	 */
	private void replaceAll(String find, String repl, int startPos) {
		replaceAll(find, repl, startPos, -1);
	}

	/**
	 * Description of the Method
	 * 
	 * @param find
	 *            Description of the Parameter
	 * @param repl
	 *            Description of the Parameter
	 * @param startPos
	 *            Description of the Parameter
	 * @param endPos
	 *            Description of the Parameter
	 */
	private void replaceAll(String find, String repl, int startPos, int endPos) {
		int pos = word.toString().indexOf(find, startPos);

		if (-1 == endPos) endPos = word.length() - 1;

		while (-1 != pos) {
			if (-1 != endPos && pos > endPos) break;

			word.delete(pos, pos + find.length());

			word.insert(pos, repl);

			pos = word.toString().indexOf(find);
		}

	}

	/**
	 * Description of the Method
	 * 
	 * @param find
	 *            Description of the Parameter
	 * @param repl
	 *            Description of the Parameter
	 */
	private void replaceFront(String find, String repl) {
		if (word.toString().startsWith(find)) {
			word.delete(0, find.length());
			word.insert(0, repl);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param find
	 *            Description of the Parameter
	 * @param repl
	 *            Description of the Parameter
	 */
	private void replaceEnd(String find, String repl) {
		if (word.toString().endsWith(find)) {
			word.delete(word.length() - find.length(), word.length());
			word.append(repl);
		}
	}
}
