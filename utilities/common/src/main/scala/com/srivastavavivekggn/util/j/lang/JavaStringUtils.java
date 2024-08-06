package com.srivastavavivekggn.scala.util.j.lang;

import com.srivastavavivekggn.scala.util.collection.CollectionUtils;
import com.srivastavavivekggn.scala.util.lang.StringUtils;
import com.srivastavavivekggn.scala.util.lang.StringUtils$;
import org.apache.commons.text.StringEscapeUtils;
import scala.Option;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavaStringUtils {

    private static StringUtils$ delegate = StringUtils$.MODULE$;
    private static StringUtils.Delimiters$ delimiters = StringUtils.Delimiters$.MODULE$;
    private static StringUtils.EscapedDelimiters$ escapedDelimiters = StringUtils.EscapedDelimiters$.MODULE$;

    public static String EMPTY = "";

    public static String BLANK = " ";

    public static String COMMA = ",";

    public static String COLON = ":";

    public static String SEMI_COLON = ";";

    public static String PIPE = "|";

    public enum Delimiters {
        DOT(delimiters.DOT()),
        AT(delimiters.AT()),
        DASH(delimiters.DASH()),
        UNDERSCORE(delimiters.UNDERSCORE()),
        COMMA(delimiters.COMMA()),
        STAR(delimiters.STAR()),
        VERTICAL_BAR(delimiters.VERTICAL_BAR());

        private final String delimiter;

        Delimiters(String delimiter) {
            this.delimiter = delimiter;
        }


        @Override
        public String toString() {
            return delimiter;
        }
    }

    public enum EscapedDelimiters {

        DOT(escapedDelimiters.DOT()),
        STAR(escapedDelimiters.STAR()),
        VERTICAL_BAR(escapedDelimiters.VERTICAL_BAR());

        private final String delimiter;

        EscapedDelimiters(String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public String toString() {
            return delimiter;
        }
    }

    public static Boolean isEmpty(String str) {
        return delegate.isEmpty(str);
    }

    public static Boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Determine if the given string is empty (optionally trimming)
     *
     * @param str  the string to check
     * @param trim true to trim
     * @return None if the string is empty, or an Option(str) if non-empty
     */
    public static Optional<String> nonEmpty(String str, Boolean trim) {
        Option<String> result = delegate.nonEmpty(str, trim);

        if (result.isDefined()) {
            return Optional.ofNullable(result.get());
        } else {
            return Optional.empty();
        }
    }


    /**
     * Trim the given string
     *
     * @param s the string to trim
     * @return the trimmed string
     */
    public static String trim(String s) {
        return delegate.trim(s);
    }

    public static String capitalize(String str) {
        return delegate.capitalize(str);
    }

    public static String uncapitalize(String str) {
        return delegate.uncapitalize(str);
    }

    public static String[] split(String str, Delimiters delimiter) {
        return split(str, delimiter.toString());
    }

    public static String[] split(String str, String delimiter) {
        return delegate.split(str, delimiter);
    }

    /**
     * Determine if a passed set of String/Option[String] have equal values
     *
     * @param str the strings to check
     * @return true if all passed values are equal
     */
    public static Boolean isEqual(Object... str) {
        return delegate.isEqual(
                CollectionUtils.asScalaListOrEmpty(
                        Arrays.stream(str).collect(Collectors.toList())
                ),
                false
        );
    }

    /**
     * Determine if a passed set of String/Option[String] have equal values to lowercase for comparison
     *
     * @param str the strings to check
     * @return true if all passed values are equal
     */
    public static Boolean isEqualIgnoreCase(Object... str) {
        return delegate.isEqual(
                CollectionUtils.asScalaListOrEmpty(
                        Arrays.stream(str).collect(Collectors.toList())
                ),
                true
        );
    }

    /**
     * Convert camel case word(s) to spaces
     *
     * @param str the string to process
     * @return the converted string
     */
    public static String camelCaseToSpace(String str) {
        return delegate.camelCaseToSpace(str);
    }

    /**
     * Convert camelCase to underscores
     *
     * @param str the string to convert
     * @return the converted string
     */
    public static String camelCaseToUnderscore(String str) {
        return delegate.camelCaseToUnderscore(str);
    }

    /**
     * Convert a camel case string to a string separated by "to"
     *
     * @param str the camel case string
     * @param to  the separator
     * @return the separated string
     */
    public static String camelCaseTo(String str, String to) {
        return delegate.camelCaseTo(str, to);
    }

    public static String toAlphaNumericOnly(String str) {
        return delegate.toAlphaNumericOnly(str);
    }

    /**
     * Determine if the given string is all uppercase
     *
     * @param str the string to check
     * @return true if all uppercase, false otherwise
     */
    public static Boolean isAllUpperCase(String str) {
        return delegate.isAllUpperCase(str);
    }

    /**
     * Convert a character to it's unicode representation
     *
     * @param c the character
     * @return the unicode value
     */
    public static String toUnicode(Character c) {
        return delegate.toUnicode(c);
    }

    /**
     * Unescapes a string containing entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes. Supports HTML 4.0 entities.
     *
     * @param str the string to unescape
     * @return @return a new unescaped { @code String}, { @code null} if null string input
     */
    public static String unescapeHtml(String str) {
        return StringEscapeUtils.unescapeHtml4(str);
    }

    /**
     * Convert a given string to a list of strings using
     * Comma as a default delimiter
     *
     * @param str       the string to convert
     * @param delimiter the delimiter
     * @return list of string values
     */
    public static List<String> delimitedToList(String str, String delimiter) {
        return CollectionUtils.asJavaListOrEmpty(
                delegate.delimitedToList(str, delimiter)
        );
    }

    /**
     * Convert a given string to a list of strings using
     * Comma as a default delimiter
     *
     * @param str       the string to convert
     * @param delimiter the delimiter
     * @return list of string values
     */
    public static List<String> delimitedToList(String str, Delimiters delimiter) {
        return delimitedToList(str, delimiter.toString());
    }

    /**
     * Convert a given CSV string to a list of strings
     *
     * @param str the string
     * @return the list of results
     */
    public static List<String> delimitedToList(String str) {
        return delimitedToList(str, Delimiters.COMMA);
    }
}
