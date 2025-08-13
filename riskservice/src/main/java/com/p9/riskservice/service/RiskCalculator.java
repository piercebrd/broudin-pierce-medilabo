package com.p9.riskservice.service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe utilitaire finale pour le calcul du risque de diabète.
 * Contient les méthodes de détection des déclencheurs et l’évaluation du niveau de risque.
 */
public final class RiskCalculator {

    // Constructeur privé pour empêcher l’instanciation
    private RiskCalculator() {}

    // Motifs (regex) pour détecter les termes déclencheurs dans les notes
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

    // Tableau regroupant tous les déclencheurs
    private static final Pattern[] TRIGGERS = new Pattern[] {
            P_HEMOGLOBINE_A1C, P_MICROALBUMINE, P_TAILLE, P_POIDS,
            P_SMOKE, P_ANORMAL, P_CHOLES, P_VERTIGE, P_RECHUTE, P_REACTION, P_ANTICORPS
    };

    /**
     * Calcule l’âge en années à partir d’une date de naissance.
     *
     * @param dateOfBirth date de naissance
     * @return âge en années, ou 0 si null
     */
    public static int age(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Calcule l’âge à partir d’une date de naissance au format ISO (yyyy-MM-dd).
     *
     * @param isoBirthDate date de naissance au format ISO
     * @return âge en années, ou 0 si vide ou null
     */
    public static int age(String isoBirthDate) {
        if (isoBirthDate == null || isoBirthDate.isBlank()) return 0;
        return age(LocalDate.parse(isoBirthDate.trim()));
    }

    /**
     * Détermine le niveau de risque en fonction de l’âge, du sexe et du nombre
     * de déclencheurs distincts détectés.
     *
     * @param age âge du patient
     * @param gender sexe du patient ("M" ou "F")
     * @param triggerCountDistinct nombre de déclencheurs distincts
     * @return niveau de risque (None, Borderline, In Danger, Early onset)
     */
    public static String riskLevel(int age, String gender, int triggerCountDistinct) {
        if (triggerCountDistinct == 0) return "None";

        boolean over30  = age > 30;
        boolean under30 = age < 30;
        char g = normalizeGender(gender);

        // Borderline: 2..5 déclencheurs et âge > 30
        if (over30 && triggerCountDistinct >= 2 && triggerCountDistinct <= 5) return "Borderline";

        if (over30) {
            if (triggerCountDistinct == 6 || triggerCountDistinct == 7) return "In Danger";
            if (triggerCountDistinct >= 8) return "Early onset";
        } else if (under30) {
            if (g == 'm') {
                if (triggerCountDistinct >= 3 && triggerCountDistinct <= 4) return "In Danger";
                if (triggerCountDistinct >= 5) return "Early onset";
            } else if (g == 'f') {
                if (triggerCountDistinct >= 4 && triggerCountDistinct <= 6) return "In Danger";
                if (triggerCountDistinct >= 7) return "Early onset";
            } else {
                // Sexe inconnu : on applique les seuils adultes par défaut
                if (triggerCountDistinct == 6 || triggerCountDistinct == 7) return "In Danger";
                if (triggerCountDistinct >= 8) return "Early onset";
            }
        }
        // Cas âge = 30 ou non couvert → Aucun risque
        return "None";
    }

    /**
     * Compte le nombre de déclencheurs distincts trouvés dans une liste de textes de notes.
     *
     * @param noteTexts liste des notes
     * @return nombre de déclencheurs distincts
     */
    public static int countTriggers(List<String> noteTexts) {
        String text = mergeAndNormalize(noteTexts);
        if (text.isBlank()) return 0;

        int distinct = 0;
        for (Pattern p : TRIGGERS) {
            Matcher m = p.matcher(text);
            if (m.find()) { // On compte chaque type de déclencheur au maximum une fois
                distinct++;
            }
        }
        return distinct;
    }

    /* ---------- Méthodes utilitaires ---------- */

    /**
     * Fusionne et normalise une liste de notes en une seule chaîne.
     */
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

    /**
     * Normalise une chaîne :
     * - supprime les accents
     * - convertit en minuscules
     * - réduit les espaces multiples
     */
    private static String normalize(String s) {
        String nfd = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return nfd.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    /**
     * Normalise le sexe pour retourner 'm', 'f' ou '?' si inconnu.
     */
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
