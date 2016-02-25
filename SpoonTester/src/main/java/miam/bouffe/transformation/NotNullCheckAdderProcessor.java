package miam.bouffe.transformation;

import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.processing.AbstractProcessor;


public class NotNullCheckAdderProcessor extends AbstractProcessor<CtParameter<?>> {

    @Override
    public boolean isToBeProcessed(CtParameter<?> element) {
        return !element.getType().isPrimitive();    // Only for objects
    }

    public void process(CtParameter<?> element) {
        // We declare a new snippet of code to be inserted
        CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();

        // This snipped contains an if check
        final String value = String.format("if (%s == null) "
            + "throw new IllegalArgumentException(\"[Spoon inserted check] null passed as parameter\")",
            element.getSimpleName());
        snippet.setValue(value);

        // We insert the snippet at the beginning of the method body
        if (element.getParent(CtExecutable.class).getBody() != null) {
            element.getParent(CtExecutable.class).getBody().insertBegin(snippet);
        }
    }
}
