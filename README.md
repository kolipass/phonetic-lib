# Phonetic-lib
Fork of sample project from article [Phonetic algorithms [RU]](http://habrahabr.ru/post/114947/)

The sample project contains realisation:
- NYSIIS  [phonetic-lib/Nysiis.java at master · kolipass/phonetic-lib](https://github.com/kolipass/phonetic-lib/blob/master/src/main/java/ru/phonetic/Nysiis.java) 
- Russian Metaphone [phonetic-lib/MetaphoneRussian.java at master · kolipass/phonetic-lib](https://github.com/kolipass/phonetic-lib/blob/master/src/main/java/ru/phonetic/MetaphoneRussian.java)

Also the project used Apache Commons Codec:
- NYSIIS [Nysiis (Apache Commons Codec 1.10 API)](https://commons.apache.org/proper/commons-codec/apidocs/index.html) Since: 1.7
- Soundex [Soundex (Apache Commons Codec 1.10 API)](https://commons.apache.org/proper/commons-codec/apidocs/index.html)
- Refined Soundex [RefinedSoundex (Apache Commons Codec 1.10 API)](https://commons.apache.org/proper/commons-codec/apidocs/index.html)
- Daitch-Mokotoff Soundex [DaitchMokotoffSoundex (Apache Commons Codec 1.10 API)](https://commons.apache.org/proper/commons-codec/apidocs/index.html)
- Metaphone [Metaphone (Apache Commons Codec 1.10 API)](https://commons.apache.org/proper/commons-codec/apidocs/index.html)
- Double Metaphone [DoubleMetaphone (Apache Commons Codec 1.10 API)](https://commons.apache.org/proper/commons-codec/apidocs/index.html)
- Caverphone [Caverphone2 (Apache Commons Codec 1.10 API)](https://commons.apache.org/proper/commons-codec/apidocs/index.html)

Usage
====================
Create dictionary.txt (or get it here [252 russian surnames](https://gist.github.com/kolipass/523ea952c52986538d45) ) and run MetaphoneRussian.main(null);



Usage in custom project
====================
 * Normal usage (not work in current moment)
 
Add it to your root build.gradle with:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```
and:

```gradle
dependencies {
	compile 'com.github.kolipass:phonetic-lib:0.0.1'
}
```

or

```gradle
dependencies {
	compile 'com.github.kolipass:phonetic-lib:-SNAPSHOT'
}
```

* Sub project dependency

In your project root folder:

```
$ git submodule add https://github.com/kolipass/phonetic-lib
$ git submodule init
$ git submodule update
$ echo "include ':speachkit:library'" >  settings.gradle
```

* Local usage (prefer in current moment)

If you want to use you own local fork:
You need run ```    gradle install ```and check the local Maven repo folder ```.m2/repository/ru/phonetic/phoneticlib/0.0.1```

Add it to your root build.gradle with:
```gradle
repositories {
        mavenLocal()
}
```
and:

```gradle
dependencies {
     compile 'ru.phonetic.phoneticlib:0.0.1'
}
```

[![Release](https://img.shields.io/github/release/kolipass/phonetic-lib.svg?label=maven)](https://jitpack.io/#kolipass/phonetic-lib)