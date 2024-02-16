package org.password;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordCheckerTest {

    @Test
    void checkTheCorrectPasswordIfItHas12Chars() {
        assertTrue( PasswordChecker.checkPasswordComplexity("Password123!") );
    }

    @Test
    void checkTheCorrectPasswordIfItHas8Chars() {
        assertTrue( PasswordChecker.checkPasswordComplexity("Pass456?") );
    }

    @Test
    void checkPasswordIfItHas7CharsWithDigitsAndSpecialChars() {
        assertFalse( PasswordChecker.checkPasswordComplexity("Pas789@") );
    }

    @Test
    void checkPasswordIfItHas12CharsWithoutDigits() {
        assertFalse( PasswordChecker.checkPasswordComplexity("Password?@*!") );
    }

    @Test
    void checkPasswordIfItHas12CharsWithoutSpecialChars() {
        assertFalse( PasswordChecker.checkPasswordComplexity("Password9876") );
    }

    @Test
    void checkPasswordIfItEmpty() {
        assertFalse( PasswordChecker.checkPasswordComplexity("") );
    }

}
