package fr.unice.polytech.devops.selectors;

import fr.unice.polytech.devops.configurable.PackageSelector;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtFor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.support.reflect.code.CtForImpl;

/**
 * Created by user on 03/03/16.
 */
public class BinaryOperatorMutatorSelector {

    PackageSelector selector = new PackageSelector();
    boolean test = false;
    private String MUTATION_NAME ="BinaryOperatorMutator";


    public boolean decide(CtElement candidate){

        if(! test){
            // System.out.println(candidate.getPosition().getFile().getAbsolutePath().split("src")[0] + "tmpMyProcessor.xml");
            selector.fill(candidate.getPosition().getFile().getAbsolutePath().split("src")[0] + "myProcessor.xml");
            test = true;
        }

        CtBinaryOperator op = (CtBinaryOperator)candidate;
        CtFor loopParent = op.getParent(CtFor.class);
        return (loopParent == null) && selector.methodeAndPackageChecker(candidate,MUTATION_NAME);






    }
}
