package com.p9.riskservice.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;

public class RiskCalculator {
    private static final List<String> TRIGGERS = List.of(
            "hémoglobine a1c","hemoglobine a1c","a1c",
            "microalbumine","micro-albumine",
            "taille","poids",
            "fumeur","fumeuse","fumer",
            "anormal","anormale","anormales","anormaux",
            "cholestérol","cholesterol",
            "vertiges","vertige",
            "rechute",
            "réaction","reaction",
            "anticorps"
    );

    public static int countTriggers(List<String> notes) {
        int count = 0;
        for (String note : notes) {
            if (note == null || note.isBlank()) continue;
            String lower = note.toLowerCase(Locale.FRENCH);
            for (String t : TRIGGERS) if (lower.contains(t)) count++;
        }
        return count;
    }

    public static int age(LocalDate dob) {
        if (dob == null) return 0;
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public static String riskLevel(int age, String gender, int triggers) {
        if (triggers == 0) return "None";
        if (age > 30) {
            if (triggers >= 8) return "Early onset";
            if (triggers >= 6) return "In Danger";
            if (triggers >= 2) return "Borderline";
            return "None";
        }
        String g = gender == null ? "" : gender.trim().toUpperCase(Locale.ROOT);
        boolean male = "M".equals(g), female = "F".equals(g);
        if (male)   return triggers >= 5 ? "Early onset" : (triggers >= 3 ? "In Danger" : "None");
        if (female) return triggers >= 7 ? "Early onset" : (triggers >= 4 ? "In Danger" : "None");
        return triggers >= 6 ? "In Danger" : (triggers >= 2 ? "Borderline" : "None");
    }
}
