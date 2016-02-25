package monPackage;


import com.mycompany.app.App;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testMutant2(){
        App app = new App();
        System.out.println("TEST");
        System.out.println(app.a());
        assertTrue(app.a()==2);
        //assertTrue(true);
    }

}

