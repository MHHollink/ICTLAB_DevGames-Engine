package nl.devgames.jenkins.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "analyze")
public class DevGamesMojo extends AbstractMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info( "Retrieving data from SonarQube.." );
        getLog().info( "Got the data" );
        getLog().info( "Pushing data to ruleserver.." );
        getLog().info( "Done" );
    }
}
