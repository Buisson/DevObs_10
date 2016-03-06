package miam.bouffe.selectors;


import spoon.reflect.code.CtFor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;


/**
 * Created by user on 06/03/16.
 */
public class InferieurSelector {


    public boolean decide(CtElement candidate){
        Filter<CtFor> var1 = new Filter<CtFor>() {
            @Override
            public boolean matches(CtFor ctFor) {
                return true;
            }
        };
        return candidate.getParent().getElements(var1).size()==1;






    }
}
