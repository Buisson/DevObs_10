package com.mycompany.app;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;


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
        buildHelper();
        execute();


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
