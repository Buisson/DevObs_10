package fr.unice.polytech.devops.selectors;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtIf;
import spoon.support.reflect.code.CtBinaryOperatorImpl;

/**
 * Created by user on 07/03/16.
 */
public class IfSelector {

  public boolean decide(CtIf candidate){
        if( candidate.getCondition() instanceof CtBinaryOperatorImpl){
            CtBinaryOperatorImpl condition = ((CtBinaryOperatorImpl) candidate.getCondition());
            return ! (condition.getRightHandOperand().toString().equals("null"));
        }
        return true;
    }
}
