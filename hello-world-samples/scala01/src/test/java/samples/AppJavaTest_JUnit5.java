package samples;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


// To have it picked up by maven surefire you need to add pattern to configuration,
// by default only *Test/Test* are picked up.
class AppJavaTest_JUnit5 {
    @Test
    void testOK() { assertTrue(true); }
}

class AppJavaJUnit5Test {
    @Test
    void testOK() { assertTrue(true); }
}
