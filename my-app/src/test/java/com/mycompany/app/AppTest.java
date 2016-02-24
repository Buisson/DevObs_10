package com.mycompany.app;


import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void AppTest(){
        App app = new App();

        assertTrue(app.a()==2);
    }
}
