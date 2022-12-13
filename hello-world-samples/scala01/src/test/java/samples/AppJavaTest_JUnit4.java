package samples;

import org.junit.*;
import static org.junit.Assert.*;


// !!! class/methods should be 'public' for jUnit4 !!!
// To have it picked up by maven surefire you need to add pattern to configuration,
// by default only *Test/Test* are picked up.
public class AppJavaTest_JUnit4 {
    @Test
    public void testOK() { assertTrue(true); }
}
