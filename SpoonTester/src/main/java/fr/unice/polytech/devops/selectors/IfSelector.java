package fr.unice.polytech.devops.selectors;

import com.sun.org.apache.xpath.internal.operations.Bool;
import fr.unice.polytech.devops.configurable.PackageSelector;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.support.reflect.code.CtBinaryOperatorImpl;

/**
 * Created by user on 07/03/16.
 */
public class IfSelector {
    PackageSelector selector = new PackageSelector();
    boolean test = false;
    private String MUTATION_NAME ="IfProcessor";

  public boolean decide(CtIf candidate){
      if(! test){
          System.out.println("##########################################");
         // System.out.println(candidate.getPosition().getFile().getAbsolutePath().split("src")[0] + "tmpMyProcessor.xml");
        selector.fill(candidate.getPosition().getFile().getAbsolutePath().split("src")[0] + "myProcessor.xml");
          test = true;
      }
        if( candidate.getCondition() instanceof CtBinaryOperatorImpl){

            CtBinaryOperatorImpl condition = ((CtBinaryOperatorImpl) candidate.getCondition());
            boolean safetyCriteria = ! (condition.getRightHandOperand().toString().equals("null"));
            System.out.println(selector.methodeAndPackageChecker(candidate,MUTATION_NAME));
            System.out.println(safetyCriteria);
            return safetyCriteria && selector.methodeAndPackageChecker(candidate,MUTATION_NAME);
        }
        return false;
    }





}
