package com.urbanpark.parking.shared.utils;

import java.text.Normalizer;

public class SlugUtils {

    private SlugUtils() {}

    public static String generate(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("[\\s]+", "-")
                .replaceAll("-+", "-");
    }
}