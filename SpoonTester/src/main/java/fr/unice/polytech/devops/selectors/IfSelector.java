package fr.unice.polytech.devops.selectors;

import spoon.reflect.code.CtIf;
import spoon.support.reflect.code.CtBinaryOperatorImpl;

/**
 * Created by user on 07/03/16.
 */
public class IfSelector {





  public boolean decide(CtIf candidate){


        if( candidate.getCondition() instanceof CtBinaryOperatorImpl){

            return ! ((CtBinaryOperatorImpl) candidate.getCondition()).getRightHandOperand().toString().equals("null");
        }

        return true;
    }
}
