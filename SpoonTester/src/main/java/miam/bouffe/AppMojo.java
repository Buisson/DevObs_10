package miam.bouffe;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name="rapport")
public class AppMojo extends AbstractMojo{

    @Parameter(defaultValue = "${project}", required=true,readonly = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException,MojoFailureException{
        getLog().info("Hello World !");
        System.out.println("########################");
        System.out.println("########################");
        System.out.println("########################");
        System.out.println(project.getBasedir()+"/target/surefire-reports");
        System.out.println("########################");
        System.out.println("########################");
        System.out.println("########################");

        //XMLParser parserxml = new XMLParser();
    }
}