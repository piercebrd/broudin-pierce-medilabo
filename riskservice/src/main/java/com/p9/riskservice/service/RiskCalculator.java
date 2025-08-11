package com.p9.riskservice.service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RiskCalculator {

    private RiskCalculator() {}

    // Trigger patterns on normalized (lowercased, accents-stripped) text
    private static final Pattern P_HEMOGLOBINE_A1C = Pattern.compile("\\bhemoglobine\\s*a1c\\b");
    private static final Pattern P_MICROALBUMINE   = Pattern.compile("\\bmicroalbumine\\b");
    private static final Pattern P_TAILLE          = Pattern.compile("\\btaille\\b");
    private static final Pattern P_POIDS           = Pattern.compile("\\bpoids\\b");
    private static final Pattern P_SMOKE           = Pattern.compile("\\bfum\\w*\\b");        // fume/fumer/fumeur/fumeuse…
    private static final Pattern P_ANORMAL         = Pattern.compile("\\banormal\\w*\\b");    // anormal/anormale/…
    private static final Pattern P_CHOLES          = Pattern.compile("\\bcholesterol\\b");
    private static final Pattern P_VERTIGE         = Pattern.compile("\\bvertig\\w*\\b");
    private static final Pattern P_RECHUTE         = Pattern.compile("\\brechute\\w*\\b");
    private static final Pattern P_REACTION        = Pattern.compile("\\breaction\\w*\\b");
    private static final Pattern P_ANTICORPS       = Pattern.compile("\\banticorps\\b");

    private static final Pattern[] TRIGGERS = new Pattern[] {
            P_HEMOGLOBINE_A1C, P_MICROALBUMINE, P_TAILLE, P_POIDS,
            P_SMOKE, P_ANORMAL, P_CHOLES, P_VERTIGE, P_RECHUTE, P_REACTION, P_ANTICORPS
    };

    /** Your service uses this. */
    public static int age(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /** Overload if you ever pass ISO date strings. */
    public static int age(String isoBirthDate) {
        if (isoBirthDate == null || isoBirthDate.isBlank()) return 0;
        return age(LocalDate.parse(isoBirthDate.trim()));
    }

    /** Your service uses this. Returns the *label* expected by the UI. */
    public static String riskLevel(int age, String gender, int triggerCountDistinct) {
        if (triggerCountDistinct == 0) return "None";

        boolean over30  = age > 30;
        boolean under30 = age < 30;
        char g = normalizeGender(gender);

        // Borderline: 2..5 and age > 30
        if (over30 && triggerCountDistinct >= 2 && triggerCountDistinct <= 5) return "Borderline";

        if (over30) {
            if (triggerCountDistinct == 6 || triggerCountDistinct == 7) return "In Danger";
            if (triggerCountDistinct >= 8) return "Early onset";
        } else if (under30) {
            if (g == 'm') {
                if (triggerCountDistinct >= 3 && triggerCountDistinct <= 4) return "In Danger";
                if (triggerCountDistinct >= 5)                      return "Early onset";
            } else if (g == 'f') {
                if (triggerCountDistinct >= 4 && triggerCountDistinct <= 6) return "In Danger";
                if (triggerCountDistinct >= 7)                      return "Early onset";
            } else {
                // Unknown gender: conservative adult thresholds
                if (triggerCountDistinct == 6 || triggerCountDistinct == 7) return "In Danger";
                if (triggerCountDistinct >= 8)                      return "Early onset";
            }
        }
        // Age == 30 or any range not matched -> None
        return "None";
    }

    /** 🔁 CHANGED: count *distinct trigger terms*, not occurrences. */
    public static int countTriggers(List<String> noteTexts) {
        String text = mergeAndNormalize(noteTexts);
        if (text.isBlank()) return 0;

        int distinct = 0;
        for (Pattern p : TRIGGERS) {
            Matcher m = p.matcher(text);
            if (m.find()) {           // <-- count each trigger type at most once
                distinct++;
            }
        }
        return distinct;
    }

    /* ---------- helpers ---------- */

    private static String mergeAndNormalize(List<String> notes) {
        if (notes == null || notes.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String n : notes) {
            if (n != null && !n.isBlank()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(n);
            }
        }
        return normalize(sb.toString());
    }

    private static String normalize(String s) {
        String nfd = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return nfd.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private static char normalizeGender(String g) {
        if (g == null) return '?';
        String s = g.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return '?';
        char c = s.charAt(0);
        if (c == 'm') return 'm';
        if (c == 'f') return 'f';
        return '?';
    }
}
