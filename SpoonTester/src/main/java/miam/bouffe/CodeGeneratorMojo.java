package miam.bouffe;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name="hello")
public class CodeGeneratorMojo extends AbstractMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Hello World !");
        for (int i = 0; i < 20; i++) {
            getLog().info("Hello " + i + " !");
        }
    }
}
