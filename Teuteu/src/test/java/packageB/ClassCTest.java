package packageB;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by user on 10/03/16.
 */
public class ClassCTest {

    private  ClassC c = new ClassC();
    @Test
    public void Test1() {
        assertTrue(c.methode1()==6);
    }

    @Test
    public void Test2() {
        assertTrue(c.methode2()==10);
    }

}
