package ru.meetingbot.util;

import java.util.regex.Matcher;

public class StringMarkdownV2 {

    /**
     * In all other places characters '_'--'*'--'['--']'--'('--')'--'~'--'`'--'>'--'#'--'+'--'-'--'='--'|'--'{'--'}'--'.'--'!' must be escaped with the preceding character '\'.
     *
     * Test string: [ ] \ ^ $ . | ? * + ( ).\n\t \ . | * + _ ~ ` > # - = { } !
     */
    public static String getString(String text) {
        // важно, чтобы сначало заменились одинарные слеши
        text = text.replaceAll("(?<!\\\\)\\\\", "\\\\\\\\");

        text = text.replaceAll("(?<!\\\\)\\.", "\\\\.");
        text = text.replaceAll("(?<!\\\\)\\|", "\\\\|");
        text = text.replaceAll("(?<!\\\\)\\*", "\\\\*");
        text = text.replaceAll("(?<!\\\\)\\+", "\\\\+");

        text = text.replaceAll("(?<!\\\\)_", "\\\\_");
        text = text.replaceAll("(?<!\\\\)~", "\\\\~");
        text = text.replaceAll("(?<!\\\\)`", "\\\\`");
        text = text.replaceAll("(?<!\\\\)>", "\\\\>");
        text = text.replaceAll("(?<!\\\\)#", "\\\\#");
        text = text.replaceAll("(?<!\\\\)-", "\\\\-");
        text = text.replaceAll("(?<!\\\\)=", "\\\\=");
        text = text.replaceAll("(?<!\\\\)!", "\\\\!");

        text = text.replaceAll("(?<!\\\\)\\[", "\\\\[");
        text = text.replaceAll("(?<!\\\\)\\]", "\\\\]");
        text = text.replaceAll("(?<!\\\\)\\(", "\\\\(");
        text = text.replaceAll("(?<!\\\\)\\)", "\\\\)");
        text = text.replaceAll("(?<!\\\\)\\{", "\\\\{");
        text = text.replaceAll("(?<!\\\\)\\}", "\\\\}");

        return text;
    }
}
