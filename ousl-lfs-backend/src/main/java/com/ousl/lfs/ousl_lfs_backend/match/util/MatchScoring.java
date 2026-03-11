package com.ousl.lfs.ousl_lfs_backend.match.util;

import java.time.Duration;
import java.time.Instant;

public class MatchScoring {

    private MatchScoring() {}

    public static int scoreTextStrong(String a, String b) {
        String x = norm(a);
        String y = norm(b);
        if (x.isBlank() || y.isBlank()) return 0;

        if (x.equals(y)) return 60;
        if (x.contains(y) || y.contains(x)) return 45;

        int overlap = tokenOverlap(x, y);
        if (overlap >= 3) return 35;
        if (overlap == 2) return 25;
        if (overlap == 1) return 15;
        return 0;
    }

    public static int scoreCategory(String a, String b) {
        String x = norm(a);
        String y = norm(b);
        if (x.isBlank() || y.isBlank()) return 0;
        return x.equals(y) ? 20 : 0;
    }

    public static int scoreLocation(String a, String b) {
        String x = norm(a);
        String y = norm(b);
        if (x.isBlank() || y.isBlank()) return 0;

        if (x.equals(y)) return 15;
        if (x.contains(y) || y.contains(x)) return 10;
        return 0;
    }

    // ✅ Instant version (matches your lostAt/foundAt types)
    public static int scoreDateClose(Instant a, Instant b) {
        if (a == null || b == null) return 0;

        long days = Math.abs(Duration.between(a, b).toDays());
        if (days <= 1) return 15;
        if (days <= 3) return 12;
        if (days <= 7) return 8;
        if (days <= 14) return 4;
        return 0;
    }

    private static String norm(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase()
                .replaceAll("[^a-z0-9]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static int tokenOverlap(String a, String b) {
        if (a.isBlank() || b.isBlank()) return 0;
        String[] ta = a.split(" ");
        String[] tb = b.split(" ");

        int count = 0;
        for (String x : ta) {
            if (x.length() < 3) continue;
            for (String y : tb) {
                if (x.equals(y)) { count++; break; }
            }
        }
        return count;
    }
}
