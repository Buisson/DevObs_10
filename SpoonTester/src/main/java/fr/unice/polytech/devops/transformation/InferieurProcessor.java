package fr.unice.polytech.devops.transformation;

import fr.unice.polytech.devops.selectors.InferieurSelector;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;

/**
 * Created by user on 06/03/16.
 */
public class InferieurProcessor   extends AbstractProcessor<CtElement> {


    private InferieurSelector decider = new InferieurSelector();


    @Override
    public boolean isToBeProcessed(CtElement candidate) {
        return candidate instanceof CtBinaryOperator && decider.decide(candidate);
    }

    @Override
    public void process(CtElement candidate) {
        if (isToBeProcessed(candidate)) {
            CtBinaryOperator op = (CtBinaryOperator)candidate;
            if(op.getKind().equals(BinaryOperatorKind.LT)){
                op.setKind(BinaryOperatorKind.LE);
                System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHhh");
            }

        }
    }
}
