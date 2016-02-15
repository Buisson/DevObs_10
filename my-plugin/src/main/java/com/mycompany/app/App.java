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


    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("yeaaaaaaaaaaaaaaaaaaaaaaaaaah\nligne2\nligne3\n");
        System.out.println("source :  "+project.toString());

    }
}
