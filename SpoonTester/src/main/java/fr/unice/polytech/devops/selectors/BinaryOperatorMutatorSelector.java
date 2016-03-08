package fr.unice.polytech.devops.selectors;

import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtFor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.support.reflect.code.CtForImpl;

/**
 * Created by user on 03/03/16.
 */
public class BinaryOperatorMutatorSelector {


    public boolean decide(CtElement candidate){

        CtBinaryOperator op = (CtBinaryOperator)candidate;
        CtFor loopParent = op.getParent(CtFor.class);
        return !(loopParent == null);






    }
}
