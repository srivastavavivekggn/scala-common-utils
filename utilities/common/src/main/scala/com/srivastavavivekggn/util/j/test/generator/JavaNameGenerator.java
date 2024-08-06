package com.srivastavavivekggn.scala.util.j.test.generator;

import com.srivastavavivekggn.scala.util.collection.CollectionUtils;
import com.srivastavavivekggn.scala.util.test.generator.NameGenerator$;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exposes NameGenerator in a Java-friendly way
 */
public class JavaNameGenerator {

    private static NameGenerator$ delegate = NameGenerator$.MODULE$;

    public enum Keys {
        FIRST_NAME(delegate.FIRST_NAME()),
        FIRST_NAME_FEMALE(delegate.FIRST_NAME_FEMALE()),
        FIRST_NAME_MALE(delegate.FIRST_NAME_MALE()),
        LAST_NAME(delegate.LAST_NAME()),
        ANIMAL(delegate.ANIMAL()),
        ADJECTIVE(delegate.ADJECTIVE()),
        VERB_ING(delegate.VERB_ING()),
        ADJECTIVE_OR_VERB(delegate.ADJECTIVE_OR_VERB());

        private String name;

        Keys(String name) {
            this.name = name;
        }


        @Override
        public String toString() {
            return this.name;
        }
    }

    /**
     * Get a random first name
     *
     * @return a random first name
     */
    public static String getFirstName() {
        return delegate.getFirstName();
    }

    /**
     * Get a random female first name
     *
     * @return the female first name
     */
    public static String getFemaleFirstName() {
        return delegate.getFemaleFirstName();
    }

    /**
     * get a random male first name
     *
     * @return a random male first name
     */
    public static String getMaleFirstName() {
        return delegate.getMaleFirstName();
    }

    /**
     * Get a random last name
     *
     * @return the random last name
     */
    public static String getLastName() {
        return delegate.getLastName();
    }

    /**
     * Get a first + last name randomly
     *
     * @return the first and last name as a tuple
     */
    public static String[] getName() {
        Tuple2<String, String> t = delegate.getName();
        return new String[]{t._1(), t._2()};
    }


    /**
     * Get a female first + last name randomly
     *
     * @return the first and last name as a tuple
     */
    public static String[] getFemaleName() {
        Tuple2<String, String> t = delegate.getFemaleName();
        return new String[]{t._1(), t._2()};
    }

    /**
     * Get a male first + last name randomly
     *
     * @return the first and last name as a tuple
     */
    public static String[] getMaleName() {
        Tuple2<String, String> t = delegate.getMaleName();
        return new String[]{t._1(), t._2()};
    }

    /**
     * Fill in placeholders using random values
     *
     * @param format the string format. e.g., "My name is %s %s"
     * @param keys   the data keys to use "firstName", "lastName", etc.
     * @return the formatted string with replaced placeholders
     */
    public static String get(String format, Keys... keys) {
        List<String> keyList = Arrays.stream(keys).map(Keys::toString).collect(Collectors.toList());
        return delegate.get(format, CollectionUtils.asScalaListOrEmpty(keyList));
    }
}
