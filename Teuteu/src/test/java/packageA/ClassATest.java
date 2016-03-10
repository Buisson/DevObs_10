package packageA;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by user on 10/03/16.
 */
public class ClassATest {

    private  ClassA a = new ClassA();
    @Test
    public void Test1() {
        assertTrue(a.methode1()==6);
    }
    @Test
    public void Test2() {
        assertTrue(a.methode3()==33);
        assertTrue(a.methode3()==33);

    }
}
