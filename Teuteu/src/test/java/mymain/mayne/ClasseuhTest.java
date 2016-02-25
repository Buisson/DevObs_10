package mymain.mayne;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClasseuhTest {

    Classeuh c;

    @Before
    public void setUp() {
        c = new Classeuh();
    }

    @Test
    public void mTest() {
        assertEquals(10, c.retour());
    }

}
