package miam.bouffe.transformation;

import miam.bouffe.selectors.BinaryOperatorMutatorSelector;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;

/** a trivial mutation operator that transforms all binary operators to minus ("-") */
public class BinaryOperatorMutator extends AbstractProcessor<CtElement> {
    private BinaryOperatorMutatorSelector decider = new BinaryOperatorMutatorSelector();





    @Override
    public boolean isToBeProcessed(CtElement candidate) {
        return candidate instanceof CtBinaryOperator && decider.decide(candidate);
    }

    public void process(CtElement candidate) {
        if (isToBeProcessed(candidate)) {
            CtBinaryOperator op = (CtBinaryOperator)candidate;
            if(op.getKind().equals(BinaryOperatorKind.PLUS))
                    op.setKind(BinaryOperatorKind.MINUS);
        }
    }
}
