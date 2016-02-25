package miam.bouffe;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
        //File fXmlFile = new File(project.getBasedir()+"/target/surefire-reports/TEST-mymain.mayne.MainTest.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;


        if(new File(project.getBasedir()+"/target/surefire-reports").exists()) {
            File dirTarget = new File(project.getBasedir() + "/target/mutation-report");
            if (!dirTarget.exists()) {
                dirTarget.mkdir();
            }

            try {
                List<NodeList> nlList = new ArrayList<NodeList>();
                File htmlReport = new File(project.getBasedir() + "/target/mutation-report/htmlReport.html");
                for (File fXmlFile : new File(project.getBasedir() + "/target/surefire-reports").listFiles()) {
                    String extension = FilenameUtils.getExtension(fXmlFile.getName());
                    //System.out.println("EXTENSION : " +extension);
                    if (extension.equals("xml")) {
                        //System.out.println("EXTENSION : " +extension);
                        //System.out.println("DANS LE IF XMLLLLLLLLLL");
                        if (!htmlReport.exists()) {
                            htmlReport.createNewFile();
                        }
                        dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(fXmlFile);
                        System.out.println("ROOT : " + doc.getDocumentElement().getNodeName());

                        NodeList nl = doc.getElementsByTagName("testcase");
                        nlList.add(nl);
                    }
                }
                PrintWriter writer = new PrintWriter(htmlReport.getAbsolutePath(), "UTF-8");

                writer.println("<!DOCTYPE html>");
                writer.println("<html>");
                writer.println("<head>");
                writer.println("<title>Mutation Report</title>");
                writer.println("<style>");
                writer.println("#tableMutants{border:1px solid;margin:0 auto;}" +
                        "#tableMutants td{border:1px solid;}" +
                        ".aliveMut{background-color:red;}" +
                        ".deadMut{background-color:green;}");
                writer.println("</style>");
                writer.println("</head>");
                writer.println("<body>");
                int mutantVivant = 0;
                int mutantMort = 0;
                for (NodeList nl : nlList) {
                    System.out.println("########DANS NLIST!##########");
                    String className = "";
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element elem = (Element) nl.item(i);
                        if (className.isEmpty()) {
                            className = elem.getAttribute("classname");
                            writer.println("<div style='border: 1px solid;background-color: #EEE'>Dans la classe : " + className + "</div>");
                            writer.println("<table id='tableMutants'><tr><td>Mutant Vivant</td><td>Mutant Tué</td></tr>");
                        }
                        if (elem.getChildNodes().getLength() == 0) {
                            writer.println("<tr><td class='aliveMut'>Dans la methode " + elem.getAttribute("name") + "</td><td></td></tr>");
                            mutantVivant++;
                        } else {
                            writer.println("<tr><td></td><td class='deadMut'>Dans la methode " + elem.getAttribute("name") + "</td></tr>");
                            mutantMort++;
                        }
                    }
                    writer.println("</table>");
                    writer.println("<div style='text-align: center;'>Nombre de Mutants vivant : " + mutantVivant + "</div>");
                    writer.println("<div style='text-align: center;'>Nombre de Mutants mort : " + mutantMort + "</div>");
                    float porcentageDeadMut = ((float) mutantMort / ((float) mutantMort + (float) mutantVivant)) * 100;
                    float porcentageAliveMut = (((float) mutantVivant / ((float) mutantMort + (float) mutantVivant)) * 100);
                    writer.println("<div style='text-align: center;'>% de Mutants mort : " + porcentageDeadMut + "% </div>");
                    writer.println("<div style='text-align: center;'>% de Mutants vivant : " + porcentageAliveMut + "% </div>");
                }
                writer.println("</body>");
                writer.println("</html>");
                writer.close();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

        }
        //XMLParser parserxml = new XMLParser();
    }
}