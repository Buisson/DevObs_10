package miam.bouffe.transformation;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;

/** a trivial mutation operator that transforms all binary operators to minus ("-") */
public class BinaryOperatorMutator extends AbstractProcessor<CtElement> {
    @Override
    public boolean isToBeProcessed(CtElement candidate) {
        return candidate instanceof CtBinaryOperator;
    }

    public void process(CtElement candidate) {
        if (isToBeProcessed(candidate)) {
            CtBinaryOperator op = (CtBinaryOperator)candidate;
            op.setKind(BinaryOperatorKind.MINUS);
        }
    }
}
