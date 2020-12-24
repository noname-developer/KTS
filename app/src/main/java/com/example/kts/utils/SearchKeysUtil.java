package com.example.kts.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class SearchKeysUtil {

    private static final List<String> searchKeys = new ArrayList<>();

    public static List<String> generateKeys(@NotNull String text) {
        text = text.toLowerCase();
        List<List<String>> wordKeysLists = new ArrayList<>();
        for (String word : text.split("\\s+")) {
            wordKeysLists.add(generateKeysByWord(word));
        }
        permutationWordKeyLists(wordKeysLists, 0);
        return searchKeys;
    }

    private static void permutationWordKeyLists(@NotNull List<List<String>> wordKeysLists, int pos) {
        if (pos == wordKeysLists.size() - 1) {
            printKeys(wordKeysLists);
            return;
        }
        for (int i = pos; i < wordKeysLists.size(); i++) {
            Collections.swap(wordKeysLists, i, pos);
            permutationWordKeyLists(wordKeysLists, pos + 1);
            Collections.swap(wordKeysLists, i, pos);
        }
    }

    private static void printKeys(@NotNull List<List<String>> wordKeysLists) {
        StringBuilder appendedWords = new StringBuilder();
        for (List<String> wordKeys : wordKeysLists) {
            searchKeys.addAll(wordKeys.stream().map(key -> appendedWords.toString() + key).collect(Collectors.toList()));
            appendedWords.append(wordKeys.get(wordKeys.size() - 1));
        }
    }

    @NotNull
    private static List<String> generateKeysByWord(@NotNull String word) {
        List<String> wordKeys = new ArrayList<>();
        StringBuilder str = new StringBuilder();
        for (char c : word.toCharArray()) {
            str.append(c);
            wordKeys.add(str.toString());
        }
        return wordKeys;
    }

    @NotNull
    public static String formatInput(String input) {
        return input.toLowerCase().replaceAll("\\s+", "");
    }
}
