package ru.phonetic;

/*
 * Copyright 2004 Joshua Levy This file implements the Daitch Mokotoff Soundex algorithm. You may use this source code
 * or modify it and use the modified version for any project (free, commercial or open source) as long as you do the
 * following two things. Both are required: 1. You must make a good faith effort to notify the author of this code
 * (contact information below) before starting to test whatever product, service, or web site you are developing which
 * includes this code, or code derived from this code. Such notification must include an overview of what is being
 * developed or the web site address of such a description. 2. If I request it, you must provide one copy of the
 * product, or one instance of the service, or one subscription to the web site (if a subscription is required to use
 * it), to the author of this code. If the product is available in several different formats (download and CD, for
 * example) then you may provide any format you wish. For example: 1. If you are using this code in a open source or
 * freely available software project, a single email informing me of this project's web site, containing downloading
 * instructions, would fulfill both requirements. 2. If you are using this code as part of the infrastructure of a web
 * site, and that web site requires paid subscriptions, then one email including the web site's web address and a
 * password, would fulfill both requirements for as long as the password was valid. Contact information: The author is
 * Joshua Levy. Email is joshualevy@yahoo.com Phone is 408-245-5292 Address is 1433 Hawk Ct., Sunnyvale, CA, 94087, USA
 */

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the Daitch Mokotoff Soundex algorithm. More information on this soundex system can be found on
 * these web sites: http://www.avotaynu.com/soundex.html http://www.jewishgen.org/infofiles/soundex.html#DM You can use
 * it as a java class, and encorporate it into your own Java software.
 * <p>
 * To use this class from java code, you need to create a DMSoundex object, and then call one of
 * three methods, as shown below:
 * <p>
 * <PRE>
 * String r1 ;
 * String r2 ;
 * String ra[] ;
 * DMSoundex dms = new DMSoundex() ;
 * <p>
 * r1 = dms.soundexes("daich") ;
 * r2 = dms.sencode("daich") ;
 * ra = dms.soundex("daich") ;
 * <p>
 * r1 will now contain: "350000 340000"
 * r2 will contain "350000"
 * ra will be an array with two entries, "350000" and "340000"
 * </PRE>
 *
 * @version 0.1 (First Beta Release)
 */

public class DMSoundex implements StringEncoder {

    /* The list of all entries */
    static Map<String, Entry> all = new HashMap<String, Entry>();
    public boolean debug = false;
    private String lastMaybe = null;
    private int max = 0;

    public DMSoundex() {
        new Entry("ai", true, "0", "1", "", null);
        new Entry("aj", true, "0", "1", "", null);
        new Entry("ay", true, "0", "1", "", null);
        new Entry("au", true, "0", "7", "", null);
        new Entry("a", true, "0", "", "", null);
        new Entry("b", false, "7", "7", "7", null);
        new Entry("chs", false, "5", "54", "54", null);
        new Entry("ch", false, "5", "5", "5", "tch");
        new Entry("ck", false, "5", "5", "5", "tsk");
        new Entry("c", false, "5", "5", "5", "tz");
        new Entry("cz", false, "4", "4", "4", null);
        new Entry("cs", false, "4", "4", "4", null);
        new Entry("csz", false, "4", "4", "4", null);
        new Entry("czs", false, "4", "4", "4", null);
        new Entry("drz", false, "4", "4", "4", null);
        new Entry("drs", false, "4", "4", "4", null);
        new Entry("ds", false, "4", "4", "4", null);
        new Entry("dsh", false, "4", "4", "4", null);
        new Entry("dsh", false, "4", "4", "4", null);
        new Entry("dz", false, "4", "4", "4", null);
        new Entry("dzh", false, "4", "4", "4", null);
        new Entry("dzs", false, "4", "4", "4", null);
        new Entry("d", false, "3", "3", "3", null);
        new Entry("dt", false, "3", "3", "3", null);
        new Entry("ei", true, "0", "1", "", null);
        new Entry("ey", true, "0", "1", "", null);
        new Entry("ej", true, "0", "1", "", null);
        new Entry("eu", true, "1", "1", "", null);
        new Entry("e", true, "0", "", "", null);
        new Entry("fb", false, "7", "7", "7", null);
        new Entry("f", false, "7", "7", "7", null);
        new Entry("g", false, "5", "5", "5", null);
        new Entry("h", false, "5", "5", "", null);
        new Entry("ia", true, "1", "", "", null);
        new Entry("ie", true, "1", "", "", null);
        new Entry("io", true, "1", "", "", null);
        new Entry("iu", true, "1", "", "", null);
        new Entry("i", true, "0", "", "", null);
        new Entry("j", false, "1", "1", "1", "dzh");
        new Entry("ks", false, "5", "54", "54", null);
        new Entry("kh", false, "5", "5", "5", null);
        new Entry("k", false, "5", "5", "5", null);
        new Entry("l", false, "8", "8", "8", null);
        new Entry("mn", false, "", "66", "66", null);
        new Entry("m", false, "6", "6", "6", null);
        new Entry("nm", false, "", "66", "66", null);
        new Entry("n", false, "6", "6", "6", null);
        new Entry("oi", true, "0", "1", "", null);
        new Entry("oj", true, "0", "1", "", null);
        new Entry("oy", true, "0", "1", "", null);
        new Entry("o", true, "0", "", "", null);
        new Entry("p", false, "7", "7", "7", null);
        new Entry("pf", false, "7", "7", "7", null);
        new Entry("ph", false, "7", "7", "7", null);
        new Entry("q", false, "5", "5", "5", null);
        new Entry("rz", false, "94", "94", "94", null);
        new Entry("rs", false, "94", "94", "94", null);
        new Entry("r", false, "9", "9", "9", null);
        new Entry("schtsch", false, "2", "4", "4", null);
        new Entry("schtsh", false, "2", "4", "4", null);
        new Entry("schtch", false, "2", "4", "4", null);
        new Entry("sch", false, "4", "4", "4", null);
        new Entry("shtch", false, "2", "4", "4", null);
        new Entry("shch", false, "2", "4", "4", null);
        new Entry("shtsh", false, "2", "4", "4", null);
        new Entry("sht", false, "2", "43", "43", null);
        new Entry("scht", false, "2", "43", "43", null);
        new Entry("schd", false, "2", "43", "43", null);
        new Entry("sh", false, "4", "4", "4", null);
        new Entry("stch", false, "2", "4", "4", null);
        new Entry("stsch", false, "2", "4", "4", null);
        new Entry("sc", false, "2", "4", "4", null);
        new Entry("strz", false, "2", "4", "4", null);
        new Entry("strs", false, "2", "4", "4", null);
        new Entry("stsh", false, "2", "4", "4", null);
        new Entry("st", false, "2", "43", "43", null);
        new Entry("szcz", false, "2", "4", "4", null);
        new Entry("szcs", false, "2", "4", "4", null);
        new Entry("szt", false, "2", "43", "43", null);
        new Entry("shd", false, "2", "43", "43", null);
        new Entry("szd", false, "2", "43", "43", null);
        new Entry("sd", false, "2", "43", "43", null);
        new Entry("sz", false, "4", "4", "4", null);
        new Entry("s", false, "4", "4", "4", null);
        new Entry("tch", false, "4", "4", "4", null);
        new Entry("ttch", false, "4", "4", "4", null);
        new Entry("ttsch", false, "4", "4", "4", null);
        new Entry("th", false, "3", "3", "3", null);
        new Entry("trz", false, "4", "4", "4", null);
        new Entry("trs", false, "4", "4", "4", null);
        new Entry("trch", false, "4", "4", "4", null);
        new Entry("tsh", false, "4", "4", "4", null);
        new Entry("ts", false, "4", "4", "4", null);
        new Entry("tts", false, "4", "4", "4", null);
        new Entry("ttsz", false, "4", "4", "4", null);
        new Entry("tc", false, "4", "4", "4", null);
        new Entry("tz", false, "4", "4", "4", null);
        new Entry("ttz", false, "4", "4", "4", null);
        new Entry("tzs", false, "4", "4", "4", null);
        new Entry("tsz", false, "4", "4", "4", null);
        new Entry("t", false, "3", "3", "3", null);
        new Entry("ui", true, "0", "1", "", null);
        new Entry("uj", true, "0", "1", "", null);
        new Entry("uy", true, "0", "1", "", null);
        new Entry("u", true, "0", "", "", null);
        new Entry("ue", true, "0", "", "", null);
        new Entry("v", false, "7", "7", "7", null);
        new Entry("w", false, "7", "7", "7", null);
        new Entry("x", false, "5", "54", "54", null);
        new Entry("y", true, "1", "", "", null);
        new Entry("zh", false, "4", "4", "4", null);
        new Entry("zs", false, "4", "4", "4", null);
        new Entry("zsch", false, "4", "4", "4", null);
        new Entry("zhsh", false, "4", "4", "4", null);
        new Entry("z", false, "4", "4", "4", null);
    }

    static public String getVersion() {
        return "0.1 - first beta release";
    }

    @Override
    public String encode(String source) throws EncoderException {
        return soundex(source);
    }

    @Override
    public Object encode(Object source) throws EncoderException {
        if (!(source instanceof String)) {
            throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
        }
        return soundex((String) source);
    }

    private String[] doubleArray(String[] strings, String firstNew, String secondNew) {
        String[] newStrings = new String[strings.length * 2];
        int ii;
        for (ii = 0; ii < strings.length; ii++) {
            newStrings[ii * 2] = strings[ii] + firstNew;
            newStrings[(ii * 2) + 1] = strings[ii] + secondNew;
        }
        strings = null;
        return newStrings;
    }

    private Entry lookup(String str) {
        Entry e = null;
        int ii = 7;
        if (ii > str.length()) ii = str.length();
        while (ii > 0 && e == null)
            e = (Entry) all.get(str.substring(0, ii--));
        return e;
    }

    private String getRightOne(Entry[] entries, int e, Entry alt) {
        String maybe;
        Entry rightEntry = ((alt == null) ? entries[e] : alt);

        if ((e == max - 1) || (!entries[e + 1].isVowel()))
            maybe = rightEntry.getOther();
        else maybe = rightEntry.getBefore();
        // Check for duplicate numbers
        if (lastMaybe.equals(maybe)) maybe = "";
        return maybe;
    }

    /**
     * Returns the first Daitch Mokotoff Soundex code for the string passed in. For example: dms.sencode("chelm")
     * returns "586000"
     */
    public String sencode(String name) {
        String[] r = soundexes(name);
        return r[0];
    }

    /**
     * Returns all the Daitch Mokotoff Soundex codes for the string passed in, as a single string, with each code
     * seperated by a space. For example: dms.soundex("chelm") returns "586000 486000"
     */
    public String soundex(String name) {
        String result = "";
        String[] r = soundexes(name);
        // System.out.println("number: "+r.length) ;
        int rr;
        for (rr = 0; rr < r.length; rr++) {
            result += r[rr];
            if (rr != (r.length - 1)) result += " ";
        }
        return result;
    }

    /**
     * Returns an array of all the Daitch Molokov Soundex codes for the string passed in. For example:
     * dms.soundex("chelm") returns {"586000","486000"}
     */
    public String[] soundexes(String name) {
        Entry[] entries = new Entry[name.length()];
        int e = 0;
        int rr;
        int loc = 0;

        lastMaybe = null;
        max = 0;

        while (loc < name.length()) {
            entries[e] = lookup(name.substring(loc));
            if (entries[e] == null) {
                if (debug) {
                    throw new RuntimeException("No entry was found for " + name.substring(loc));
                } else continue;
            }
            loc += entries[e].length();
            e += 1;
        }
        max = e;
        // Count up the results we will have

        if (debug) {
            for (int ee = 0; ee < max; ee++)
                System.out.print("entries[" + ee + "]=" + entries[ee].getName() + " ");
            System.out.println("");
        }
        String[] results;
        if (entries[0].getAlternate() == null) {
            results = new String[1];
            results[0] = entries[0].getFirst();
            lastMaybe = entries[0].getFirst();
        } else {
            results = new String[2];
            lastMaybe = results[0] = entries[0].getFirst();
            Entry e2 = lookup(entries[0].getAlternate());
            results[1] = e2.getFirst();
        }
        e = 1;
        while (results[0].length() < 6 && e < max) {
            // System.out.println("e="+e) ;
            String maybe;
            // Check for the vowel stuff
            maybe = getRightOne(entries, e, null);
            if (entries[e].getAlternate() == null)
                for (rr = 0; rr < results.length; rr++)
                    results[rr] += maybe;
            else {
                Entry e2 = lookup(entries[e].getAlternate());
                results = doubleArray(results, maybe, getRightOne(entries, e, e2));
            }
            lastMaybe = maybe;
            e += 1;
        }
        for (rr = 0; rr < results.length; rr++)
            results[rr] += "000000";
        for (rr = 0; rr < results.length; rr++)
            results[rr] = results[rr].substring(0, 6);
        return results;
    }

    private class Entry {

        String name;
        boolean vowel;
        String first;
        String before;
        String other;
        String alternate;

        Entry(String n, boolean v, String f, String b, String o, String a) {
            name = n;
            vowel = v;
            first = f;
            before = b;
            other = o;
            alternate = a;
            all.put(n, this);
        }

        String getName() {
            return name;
        }

        String getFirst() {
            return first;
        }

        boolean isVowel() {
            return vowel;
        }

        String getBefore() {
            return before;
        }

        String getOther() {
            return other;
        }

        String getAlternate() {
            return alternate;
        }

        int length() {
            return name.length();
        }

    }
}
