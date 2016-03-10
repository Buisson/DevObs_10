package fr.unice.polytech.devops.transformation;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeSnippetStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ca309567 on 10/03/16.
 */
public class MortNeMutator extends AbstractProcessor<CtCatch> {
    public final List<CtCatch> emptyCatchs = new ArrayList<CtCatch>();

    @Override
    public boolean isToBeProcessed(CtCatch candidate) {
        return candidate.getBody().getStatements().size() == 0;
    }

    public void process(CtCatch element) {
        if (isToBeProcessed(element)) {
            final CtCodeSnippetStatement stmt = getFactory().Code().createCodeSnippetStatement(" TODO : Empty catch clause");
            final CtBlock<?> ctBlock = getFactory().Code().createCtBlock(stmt);
            element.setBody(ctBlock);
        }

    }
}
