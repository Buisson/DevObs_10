package packageA;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by user on 10/03/16.
 */
public class ClassBTest {

    private  ClassB b = new ClassB();
    @Test
    public void Test1() {
        assertTrue(b.methode1()==2);
    }

}
