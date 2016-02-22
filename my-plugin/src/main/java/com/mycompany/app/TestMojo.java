package com.mycompany.app;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.junit.runner.JUnitCore;

/**
 * Created by user on 22/02/16.
 */

@Mojo( name = "test")
public class TestMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;
    public void execute() throws MojoExecutionException, MojoFailureException {
        JUnitCore a  = new JUnitCore();
        //a.run()

    }
}
