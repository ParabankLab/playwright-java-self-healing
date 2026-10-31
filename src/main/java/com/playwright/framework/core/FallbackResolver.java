package com.playwright.framework.core;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class FallbackResolver {

    private static final long TIME_LIMIT_MS = 200;

    public static String resolve(Page page, String originalSelector) {
        long startTime = System.currentTimeMillis();

        String healed = trySemanticAttributeFallback(page, originalSelector);
        if (healed != null && isWithinTimeLimit(startTime)) {
            return healed;
        }

        return null;
    }

    private static String trySemanticAttributeFallback(Page page, String selector) {
        try {
            // Clean selector to find core keyword
            String clean = selector.toLowerCase().replaceAll("[#\\.\\[\\]\\*\\^\\$]", "").trim();

            // Extract core semantic tokens (e.g. 'username', 'password', 'login')
            String keyword = "";
            if (clean.contains("user")) {
                keyword = "username";
            } else if (clean.contains("pass")) {
                keyword = "password";
            } else if (clean.contains("login") || clean.contains("submit")) {
                keyword = "submit";
            } else {
                // Fallback to last token chunk if no explicit keyword matched
                String[] parts = clean.split("[-_]");
                keyword = parts[parts.length - 1];
            }

            String[] attributeCandidates = {
                    "input[name='" + keyword + "']",
                    "input[id*='" + keyword + "']",
                    "[data-testid*='" + keyword + "']",
                    "input[type='" + (keyword.equals("password") ? "password" : "text") + "']"
            };

            for (String candidate : attributeCandidates) {
                Locator loc = page.locator(candidate);
                if (loc.count() > 0) {
                    return candidate;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static boolean isWithinTimeLimit(long startTime) {
        return (System.currentTimeMillis() - startTime) < TIME_LIMIT_MS;
    }
}