package org.ashot.microservice_starter;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnsiColorParser {

    private static final Pattern ANSI_PATTERN =
            Pattern.compile("\\u001B\\[[0-9;]*m");

    private static final Map<Integer, String> ANSI_COLOR_MAP = Map.ofEntries(
            Map.entry(0, "ansi-reset"),
            Map.entry(1, "ansi-bold"),
            Map.entry(2, "ansi-dim"),
            Map.entry(3, "ansi-italic"),
            Map.entry(4, "ansi-underline"),
            Map.entry(5, "ansi-blink"),
            Map.entry(7, "ansi-inverse"),
            Map.entry(8, "ansi-hidden"),
            Map.entry(9, "ansi-strikethrough"),
            Map.entry(90, "ansi-fg-bright-black"),
            Map.entry(91, "ansi-fg-bright-red"),
            Map.entry(92, "ansi-fg-bright-green"),
            Map.entry(93, "ansi-fg-bright-yellow"),
            Map.entry(94, "ansi-fg-bright-blue"),
            Map.entry(95, "ansi-fg-bright-magenta"),
            Map.entry(96, "ansi-fg-bright-cyan"),
            Map.entry(97, "ansi-fg-bright-white")
    );

    public static StyleSpans<Collection<String>> parse(String text) {
        Matcher matcher = ANSI_PATTERN.matcher(text);
        StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();

        boolean dark = Main.getDarkModeSetting();
        String defaultFg = dark ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
        List<String> currentStyles = new ArrayList<>();
        currentStyles.add(defaultFg);

        int last = 0;
        while (matcher.find()) {
            // Emit text before ANSI code
            if (matcher.start() > last) {
                spans.add(currentStyles, matcher.start() - last);
            }

            // Process ANSI codes
            List<Integer> codes = parseCodes(matcher.group());
            if (codes.contains(0)) {
                currentStyles = new ArrayList<>();
                currentStyles.add(defaultFg);
            } else {
                List<String> styles = new ArrayList<>();
                for (int code : codes) {
                    styles.add(resolveStyle(code, defaultFg));
                }
                currentStyles = styles;
            }

            last = matcher.end();
        }

        // Emit remaining text
        if (last < text.length()) {
            spans.add(currentStyles, text.length() - last);
        }

        return spans.create();
    }

    private static List<Integer> parseCodes(String ansiSequence) {
        // Strip off ESC[ and trailing 'm'
        String clean = ansiSequence.substring(2, ansiSequence.length() - 1);
        if (clean.isEmpty()) {
            return List.of(0); // treat "\u001B[m" as reset
        }
        return Arrays.stream(clean.split(";"))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();
    }

    private static String resolveStyle(int code, String defaultFg) {
        // Direct match (styles or defined colors)
        if (ANSI_COLOR_MAP.containsKey(code)) {
            return ANSI_COLOR_MAP.get(code);
        }

        // Bucket unknown color codes into closest known style
        int base = code % 10;
        return switch (base) {
            case 1 -> ANSI_COLOR_MAP.get(91); // red
            case 2 -> ANSI_COLOR_MAP.get(92); // green
            case 3 -> ANSI_COLOR_MAP.get(93); // yellow
            case 4 -> ANSI_COLOR_MAP.get(94); // blue
            case 5 -> ANSI_COLOR_MAP.get(95); // magenta
            case 6 -> ANSI_COLOR_MAP.get(96); // cyan
            case 7 -> ANSI_COLOR_MAP.get(97); // white
            case 0 -> ANSI_COLOR_MAP.get(97); // black (gray)
            default -> defaultFg;
        };
    }
}


/*
public class AnsiColorParser {

    private static final Pattern ANSI_PATTERN =
            Pattern.compile("\\u001B\\[[0-9;]*m");

    private static final Map<Integer, String> ANSI_COLOR_MAP = Map.ofEntries(
            Map.entry(0, "ansi-reset"),
            Map.entry(1, "ansi-bold"),
            Map.entry(2, "ansi-dim"),
            Map.entry(3, "ansi-italic"),
            Map.entry(4, "ansi-underline"),
            Map.entry(5, "ansi-blink"),
            Map.entry(7, "ansi-inverse"),
            Map.entry(8, "ansi-hidden"),
            Map.entry(9, "ansi-strikethrough"),
            Map.entry(90, "ansi-fg-bright-black"),
            Map.entry(91, "ansi-fg-bright-red"),
            Map.entry(92, "ansi-fg-bright-green"),
            Map.entry(93, "ansi-fg-bright-yellow"),
            Map.entry(94, "ansi-fg-bright-blue"),
            Map.entry(95, "ansi-fg-bright-magenta"),
            Map.entry(96, "ansi-fg-bright-cyan"),
            Map.entry(97, "ansi-fg-bright-white")
    );


    public static StyleSpans<Collection<String>> parse(String text) {
        Matcher matcher = ANSI_PATTERN.matcher(text);
        StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();

        boolean dark = Main.getDarkModeSetting();
        String defaultFg = dark ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
        List<String> currentStyles = new ArrayList<>();
        currentStyles.add(defaultFg);

        int last = 0;
        while (matcher.find()) {
            // 1) emit any text before this ANSI sequence
            if (matcher.start() > last) {
                spans.add(currentStyles, matcher.start() - last);
            }
            // 2) parse codes, then rebuild currentStyles from default + codes
            List<Integer> codes = parseCodes(matcher.group());
            if (codes.contains(0)) {
                currentStyles = new ArrayList<>();
                currentStyles.add(defaultFg);
            } else {
                List<String> styles = new ArrayList<>();
                for (int code : codes) {
          */
/*          String cls = ANSI_COLOR_MAP.get(code);
                    if (cls == null) continue;*//*

                    //todo find solution
                    if (code >= 1 && code <= 9) {
                        styles.add(ANSI_COLOR_MAP.get(code));
                    }
                    else {
                        int lastDigit = code % 10;
                        switch (lastDigit) {
                            case 0 -> styles.add(ANSI_COLOR_MAP.get(97));
                            case 1 -> styles.add(ANSI_COLOR_MAP.get(91));
                            case 2 -> styles.add(ANSI_COLOR_MAP.get(92));
                            case 3 -> styles.add(ANSI_COLOR_MAP.get(93));
                            case 4 -> styles.add(ANSI_COLOR_MAP.get(94));
                            case 5 -> styles.add(ANSI_COLOR_MAP.get(95));
                            case 6 -> styles.add(ANSI_COLOR_MAP.get(96));
                            case 7 -> styles.add(ANSI_COLOR_MAP.get(97));
                        }
                    }
                }
                currentStyles = styles;
            }
            last = matcher.end();
        }
        // 3) emit any remaining text after the last ANSI
        if (last < text.length()) {
            spans.add(currentStyles, text.length() - last);
        }
        return spans.create();
    }

    private static List<Integer> parseCodes(String ansiSequence) {
        // strip off ESC[ and trailing m:
        String clean = ansiSequence.substring(2, ansiSequence.length() - 1);
        if (clean.isEmpty()) {
            return List.of(0); // treat "\u001B[m" as reset
        }
        return Arrays.stream(clean.split(";"))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();
    }
}
*/
