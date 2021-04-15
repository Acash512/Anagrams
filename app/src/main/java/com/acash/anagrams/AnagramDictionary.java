package com.acash.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

public class AnagramDictionary {
    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private static int currWordLength = DEFAULT_WORD_LENGTH;
    private static final HashSet<String> wordSet = new HashSet<>();
    private static final HashMap<Integer, HashSet<String>> sizeToWord = new HashMap<>();
    private static final HashMap<String, HashSet<String>> lettersToWord = new HashMap<>();
    private final Random random = new Random();

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while ((line = in.readLine()) != null) {
            String word = line.trim();

            wordSet.add(word);
            HashSet<String> wordsOfCurrSize;

            if (sizeToWord.containsKey(word.length())) {
                wordsOfCurrSize = sizeToWord.get(word.length());
            } else {
                wordsOfCurrSize = new HashSet<>();
            }

            assert wordsOfCurrSize != null;
            wordsOfCurrSize.add(word);
            sizeToWord.put(word.length(), wordsOfCurrSize);

            String sortedLetters = sortString(word);

            if(lettersToWord.containsKey(sortedLetters)){
                Objects.requireNonNull(lettersToWord.get(sortedLetters)).add(word);
            }else{
                HashSet<String> anagrams = new HashSet<>();
                anagrams.add(word);
                lettersToWord.put(sortedLetters,anagrams);
            }
        }
    }

    public HashSet<String> getAnagramsWithOneMoreLetter(String word) {
        HashSet<String> result =  new HashSet<>();
        for (char ch = 97; ch <= 122; ch++) {
            String sortedLetters = sortString(word+ch);

            if(lettersToWord.containsKey(sortedLetters)){
                result.addAll(Objects.requireNonNull(lettersToWord.get(sortedLetters)));
            }

            if(wordSet.contains(ch+word)){
                result.remove(ch+word);
            }

            if(wordSet.contains(word+ch)){
                result.remove(word+ch);
            }
        }

        return result;
    }

    public boolean checkAnagramsWithOneMoreLetter(String word){
        int count = 0;
        for (char ch = 97; ch <= 122; ch++) {
            String sortedLetters = sortString(word+ch);
            if(lettersToWord.containsKey(sortedLetters)){
                count+= Objects.requireNonNull(lettersToWord.get(sortedLetters)).size();

                if(Objects.requireNonNull(lettersToWord.get(sortedLetters)).contains(ch+word))
                    count--;

                if(Objects.requireNonNull(lettersToWord.get(sortedLetters)).contains(word+ch))
                    count--;
            }

            if(count>=MIN_NUM_ANAGRAMS){
                return true;
            }
        }
        return false;
    }

    private String sortString(String targetWord) {
        char[] targetArr = targetWord.toCharArray();
        Arrays.sort(targetArr);
        return new String(targetArr);
    }

    public String pickGoodStarterWord() {
        ArrayList<String> words = new ArrayList<>(Objects.requireNonNull(sizeToWord.get(currWordLength)));

        while (true) {
            int start = random.nextInt(words.size());
            for (int i = start; i < words.size(); i++) {
                if (checkAnagramsWithOneMoreLetter(words.get(i))) {
                    currWordLength++;
                    if (currWordLength > MAX_WORD_LENGTH) {
                        currWordLength = DEFAULT_WORD_LENGTH;
                    }
                    return words.get(i);
                } else {
                    HashSet<String> anagrams = lettersToWord.get(sortString(words.get(i)));
                    assert anagrams != null;
                    for (String anagram:anagrams) {
                        Objects.requireNonNull(sizeToWord.get(currWordLength)).remove(anagram);
                    }
                }
            }
        }
    }
}
