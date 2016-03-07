package miam.bouffe.transformation;

import miam.bouffe.selectors.IfSelector;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.CtLiteralImpl;

/**
 * Created by user on 06/03/16.
 */
public class IfProcessor extends AbstractProcessor<CtIf> {

        private IfSelector decider = new IfSelector();
    @Override
    public boolean isToBeProcessed(CtIf candidate) {


        // pour le moment
        return decider.decide(candidate);
    }


    @Override
    public void process(CtIf ctIf) {
        CtLiteralImpl<Boolean> a = new CtLiteralImpl<>();
        a.setValue(true);
        ctIf.setCondition(a);
    }
}
