package com.futela.api.application.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class SlugGeneratorService {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public String generateSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        slug = WHITESPACE.matcher(slug).replaceAll("-");
        slug = NON_LATIN.matcher(slug).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH);
        slug = slug.replaceAll("-{2,}", "-");
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }
}
