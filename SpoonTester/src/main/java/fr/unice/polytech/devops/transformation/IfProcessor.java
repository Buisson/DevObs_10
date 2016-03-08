package fr.unice.polytech.devops.transformation;

import fr.unice.polytech.devops.selectors.IfSelector;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.support.reflect.code.CtLiteralImpl;

/**
 * Created by user on 06/03/16.
 */
public class IfProcessor extends AbstractProcessor<CtIf> {

        private IfSelector decider = new IfSelector();
    @Override
    public boolean isToBeProcessed(CtIf candidate) {
        return decider.decide(candidate);
    }

    @Override
    public void process(CtIf ctIf) {
        CtLiteralImpl<Boolean> a = new CtLiteralImpl<>();
        a.setValue(true);
        ctIf.setCondition(a);
    }
}
