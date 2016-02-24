package com.mycompany.app;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import spoon.Launcher;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.*;
import java.util.List;


@Mojo( name = "sayhi")
public class App extends AbstractMojo {
    /**
     * permet la récupération d'information sur le project qui utilise notre plugin
     * la variable project est remplie autmatiquement
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = false)
    private MavenProject project;
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("#####################  my_plugin  #####################################\n");
       //s project.add

        String destination = project.getBasedir().toString()+"/target/mutations";
        new File(destination).mkdir();
        cloneFolder(project.getBasedir().toString()+"/src",destination);
       // System.out.println(project.getTestCompileSourceRoots());
     //   System.out.println(project.getCompileSourceRoots().remove(0));
        ///home/user/Desktop/si4/semestre2/DevOps/mutation/DevObs_10/my-app/src/main/java
        //project.addCompileSourceRoot(project.getBasedir().toString()+"/target/mutations/main/java/com/mycompany/app");
        System.out.println("\n#######################################################################\n");
        /*
        TestDriver td = new TestDriver() {
            @Override
            public void test(Object o) {

            }
        };
        BinaryOperatorMutator bm = new BinaryOperatorMutator();
        MutationTester mt = new MutationTester(project.getBasedir().toString()+"/target/mutations/main/java/com/mycompany/app/App.java",td,bm);
        mt.generateMutants();*/

        /*Spoon*/
        Launcher l = new Launcher();
        l.addInputResource(project.getBasedir().toString()+"/src/main/java/com/mycompany/app/App.java");
        System.out.println();
        l.buildModel();
        CtClass origClass = (CtClass) l.getFactory().Package().getRootPackage()
                .getElements(new TypeFilter(CtClass.class)).get(0);
        System.out.println(origClass);

        BinaryOperatorMutator mutator = new BinaryOperatorMutator(); //on cree le binary mutator

        List<CtElement> elementsToBeMutated = origClass.getElements(new Filter<CtElement>() {
            @Override
            public boolean matches(CtElement arg0) {
                return mutator.isToBeProcessed(arg0);
            }
        });

        for (CtElement e : elementsToBeMutated) {
            // this loop is the trickiest part
            // because we want one mutation after the other

            // cloning the AST element
            CtElement op = l.getFactory().Core().clone(e);

            // mutate the element
            mutator.process(op);

            // temporarily replacing the original AST node with the mutated element
            replace(e,op);

            // creating a new class containing the mutating code
            CtClass klass = l.getFactory().Core()
                    .clone(op.getParent(CtClass.class));
            // setting the package
            klass.setParent(origClass.getParent());
            //origClass.
            System.out.println("££££");

            System.out.println("££££");
            System.out.println("####");
            //System.out.println("package "+klass.getPackage().toString()+";");
            System.out.println(klass);
            System.out.println("####");

            String pathMutant = project.getBasedir().toString()+"/target/mutations/main/java/com/mycompany/app/App.java";
            //File tmp = new File(pathMutant);
            new File(pathMutant);
            try {
                PrintWriter writer = new PrintWriter(pathMutant,"UTF-8");
                writer.println("package "+klass.getPackage().toString()+";");
                writer.println(klass.toString());
                writer.close();
            }
            catch (FileNotFoundException e1) {e1.printStackTrace();}
            catch (UnsupportedEncodingException e1) {e1.printStackTrace();}

            // restoring the original code
            //replace(op, e);
        }


        /*Fin Spoon*/


        buildHelper();
        //execute();


    }

    private void replace(CtElement e, CtElement op) {
        if (e instanceof CtStatement && op instanceof CtStatement) {
            ((CtStatement)e).replace((CtStatement) op);
            return;
        }
        if (e instanceof CtExpression && op instanceof CtExpression) {
            ((CtExpression)e).replace((CtExpression) op);
            return;
        }
        throw new IllegalArgumentException(e.getClass()+" "+op.getClass());
    }

    private  void buildHelper() {
        project.getCompileSourceRoots().remove(0);
        project.addCompileSourceRoot(project.getBasedir().toString()+"/target/mutations/main/java");


        /**
        System.out.println("avant   "+project.getBuild().getSourceDirectory());

        project.getBuild().setSourceDirectory(project.getBasedir().toString() +
                "/target/mutations/main/java");
        System.out.println("apres  "+project.getBuild().getSourceDirectory());
**/
    }

    /**
     * merci stackoverflow :)
     * @param source
     * @param target
     */
    public static void cloneFolder(String source, String target) {
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            targetFile.mkdir();
        }
        for (File f : new File(source).listFiles()) {
            if (f.isDirectory()) {
                String append = "/" + f.getName();
                System.out.println("Creating '" + target + append + "': "
                        + new File(target + append).mkdir());
                cloneFolder(source + append, target + append);
            }
        }
    }
}
