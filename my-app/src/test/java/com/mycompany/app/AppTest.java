package com.mycompany.app;


import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void ApplicationTest(){
        App app = new App();

        assertTrue(app.a()==2);
    }
}
