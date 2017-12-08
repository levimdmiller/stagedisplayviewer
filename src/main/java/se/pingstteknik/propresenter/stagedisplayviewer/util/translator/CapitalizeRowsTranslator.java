package se.pingstteknik.propresenter.stagedisplayviewer.util.translator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version 1.6
 * @since 1.6
 */
public class CapitalizeRowsTranslator implements Translator {
    private static final Pattern FIRST_CHAR_REGEX = Pattern.compile("(?<=\n|^)([a-zåäö])");

    /**
     * Capitalizes the first word of every line.
     *
     * @param text
     * @return Capitalized text
     */
    public String transform(String text, String notes) {
        Matcher m = FIRST_CHAR_REGEX.matcher(text);
        StringBuffer result = new StringBuffer();
        while (m.find()) { // for each match, replace with capitalized text.
            m.appendReplacement(result, m.group().toUpperCase());
        }
        m.appendTail(result);
        return result.toString();
    }
}
