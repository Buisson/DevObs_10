package mymain.mayne;

import org.junit.runner.JUnitCore;

public class Main {
    public static void main(String[] args) {
        JUnitCore jc;
        try {
            System.out.println("A");
        } catch(Exception e){}
    }

    public int m() {
        return 2 + 3;
    }

    public boolean retTrue(){
        if(false){
            return false;
        }
        return true;
    }
}
