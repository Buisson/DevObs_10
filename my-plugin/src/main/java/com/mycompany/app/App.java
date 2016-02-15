package com.mycompany.app;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;


/**
 * Hello world!
 *
 */
@Mojo( name = "sayhi")
public class App extends AbstractMojo
{
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("yeaaaaaaaaaaaaaaaaaaaaaaaaaah\nligne2\nligne3\n");

    }
}
