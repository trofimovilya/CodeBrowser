package ru.ilyatrofimov.codebrowser.utils;

import prettify.PrettifyParser;
import syntaxhighlight.ParseResult;
import syntaxhighlight.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ILYATTR on 30/04/16.
 */
public class PrettifyHighlighter {
    private static final Map<String, String> COLORS = buildColorsMap();

    private static final String FONT_PATTERN = "<font color=\"#%s\">%s</font>";

    private final Parser parser = new PrettifyParser();

    public String highlight(String fileExtension, String sourceCode) {
        StringBuilder highlighted = new StringBuilder();
        List<ParseResult> results = parser.parse(fileExtension, sourceCode);
        for (ParseResult result : results) {
            String type = result.getStyleKeys().get(0);
            String content = sourceCode.substring(result.getOffset(), result.getOffset() + result.getLength());
            highlighted.append(String.format(FONT_PATTERN, getColor(type), content));
        }
        return highlighted.toString();
    }

    private String getColor(String type) {
        if (COLORS.containsKey(type)) {
            return COLORS.get(type);
        } else {
            System.out.println(type);
            return COLORS.get("pln");
        }

    }

    private static Map<String, String> buildColorsMap() {
        Map<String, String> map = new HashMap<>();
        map.put("typ", "ff0000");
        map.put("kwd", "ff7f00");
        map.put("lit", "ffff00");
        map.put("com", "00ff00");
        map.put("str", "0000ff");
        map.put("pun", "4B0082");
        map.put("pln", "000000");
        map.put("dec", "00ffff");
        map.put("tag", "ff00ff");
        map.put("atn", "999999");
        map.put("atv", "663300");

        return map;
    }
}
