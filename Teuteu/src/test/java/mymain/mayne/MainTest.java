package mymain.mayne;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainTest {

    Main main;

    @Before
    public void setUp() {
        main = new Main();
    }

    @Test
    public void mTest() {
        assertEquals(10, main.m());
    }

    @Test
    public void tTest() {
        assertTrue(true);
    }

    @Test
    public void ttTest() {
        assertTrue(true);
    }

    @Test
    public void retTrue(){assertTrue(main.retTrue());}
}
