package utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class RandomDataGenerator {

    public static String generateName() {

        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;

        return RandomStringUtils.random(length, useLetters, useNumbers);

    }

    public static String generatePhone() {

        int length = 10;
        boolean useLetters = false;
        boolean useNumbers = true;

        return RandomStringUtils.random(length, useLetters, useNumbers);

    }

    public static String generateComment() {

        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = true;

        return RandomStringUtils.random(length, useLetters, useNumbers);

    }

    public static String generateValidApiKey() {

        int length = 16;
        boolean useLetters = false;
        boolean useNumbers = true;

        return RandomStringUtils.random(length, useLetters, useNumbers);

    }

    public static int generateValidId() {

        return RandomUtils.nextInt(1, 10);

    }

}
