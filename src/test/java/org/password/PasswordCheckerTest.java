package org.password;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordCheckerTest {

    @Test
    void checkCorrectPassword() {
        assertTrue( PasswordChecker.checkPasswordComplexity("Password1234=/&¤!") );
        assertTrue( PasswordChecker.checkPasswordComplexity("Üöäõ5_<+,") );
        assertTrue( PasswordChecker.checkPasswordComplexity(" Пас56№?") );
    }

    @Test
    void checkPasswordIfItHasNotEnoughChars() {
        assertFalse( PasswordChecker.checkPasswordComplexity("Pas789@") );
        assertFalse( PasswordChecker.checkPasswordComplexity("ds45 -") );
        assertFalse( PasswordChecker.checkPasswordComplexity(" ") );
        assertFalse( PasswordChecker.checkPasswordComplexity("") );
    }

    @Test
    void checkPasswordIfItHasNoDigits() {
        assertFalse( PasswordChecker.checkPasswordComplexity("Password?@*!") );
    }

    @Test
    void checkPasswordIfItHasNoChars() {
        assertFalse( PasswordChecker.checkPasswordComplexity("Password9876") );
    }

}
