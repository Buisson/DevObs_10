package miam.bouffe;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Mojo(name="rapport")
public class AppMojo extends AbstractMojo{

    @Parameter(defaultValue = "${project}", required=true,readonly = true)
    private MavenProject project;


    private Xpp3Dom generateConfiguration() {
        Xpp3Dom config = new Xpp3Dom("configuration");
        Xpp3Dom processors = new Xpp3Dom("processors");
        Xpp3Dom catchProc = new Xpp3Dom("processor");
        catchProc.setValue("miam.bouffe.CatchProcessor");
        Xpp3Dom notNullCheckAdderProc = new Xpp3Dom("processor");
        notNullCheckAdderProc.setValue("NotNullCheckAdderProcessor");
        Xpp3Dom binaryOperatorProc = new Xpp3Dom("processor");
        binaryOperatorProc.setValue("BinaryOperatorMutator");

        processors.addChild(catchProc);
        processors.addChild(notNullCheckAdderProc);
        //processors.addChild(binaryOperatorProc);
        config.addChild(processors);

        return config;
    }


    public void execute() throws MojoExecutionException,MojoFailureException{
        getLog().info("Debut du Plugin Maven de Mutation");

        /*
        System.out.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤plugins : ");
        int ind=0;int realInd=0;
        for (Object p :project.getBuildPlugins()){
            System.out.println(p.toString());
            Plugin pTemp = (Plugin) p;
            if(pTemp.toString().equals("Plugin [fr.inria.gforge.spoon:spoon-maven-plugin]")){
                System.out.println("DANS LE IF!!");
                System.out.println(pTemp.getConfiguration().toString());
                System.out.println("FIN IF");
                ((Plugin) p).setConfiguration(generateConfiguration());
                System.out.println(((Plugin)p).getConfiguration().toString());
                realInd=ind;
            }
            ind++;
        }
        System.out.println("ééééééééééééééééééééééééé");
        System.out.println(((Plugin)project.getBuildPlugins().get(realInd)).getConfiguration().toString());
        System.out.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
        System.out.println(project.getBasedir());
        System.out.println("FIN PROJECT");*/



        /**MODIFICATION DU POM**/
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document doc = null;

        File pomXML = new File(project.getBasedir()+"/pom.xml");

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(pomXML);
        } catch (ParserConfigurationException e) {e.printStackTrace();}
        catch (SAXException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}

        System.out.println("ROOT : " + doc.getDocumentElement().getNodeName());
        NodeList nlPOM = doc.getDocumentElement().getElementsByTagName("processors");

        //removeChilds(nlPOM.item(0));
        nlPOM.item(0).removeChild(nlPOM.item(0).getFirstChild());

        if(doc.getDocumentElement().getElementsByTagName("processor").getLength()==1) {
            Document document = dBuilder.newDocument();
            Element elemProc = document.createElement("processor");
            elemProc.setTextContent("miam.bouffe.CatchProcessor");
            nlPOM.item(0).appendChild(elemProc);
        }

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer=null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(project.getBasedir()+"/pom.xml"));

        // Output to console for testing
        //StreamResult result = new StreamResult(System.out);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }


        try {
            System.out.println("################INVOCATION MAVEEEEEENNNNNNNNN##############################");

            if(doc.getDocumentElement().getElementsByTagName("processor").getLength()>=1) {
                Runtime.getRuntime().exec("mvn package");
            }
            System.out.println("APRES LE EXEC()");
        } catch (IOException e) {
            System.out.println("################EXCEPTION MAVEEEEEENNNNNNNNN##############################");
            e.printStackTrace();
        }
/**
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( project.getBasedir()+"/pom.xml" ) );
        request.setGoals( Arrays.asList( "package" ) );

        Invoker invoker = new DefaultInvoker();
        try {
            System.out.println("################INVOCATION MAVEEEEEENNNNNNNNN##############################");
            //System.out.println(request.);
            invoker.execute( request );
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }**/

        /**
                 <processor>miam.bouffe.CatchProcessor</processor>
                 <processor>miam.bouffe.transformation.NotNullCheckAdderProcessor</processor>
                 <processor>miam.bouffe.transformation.BinaryOperatorMutator</processor>
                 **/

        /**DEBUT GENERATION HTML**/
        //DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        //DocumentBuilder dBuilder = null;

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
                    if (extension.equals("xml")) {
                        if (!htmlReport.exists()) {
                            htmlReport.createNewFile();
                        }
                        dBuilder = dbFactory.newDocumentBuilder();
                        doc = dBuilder.parse(fXmlFile);
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
                writer.println("<meta charset=\"UTF-8\">");
                writer.println("</head>");
                writer.println("<body>");
                int mutantVivant = 0;
                int mutantMort = 0;
                for (NodeList nl : nlList) {
                    //System.out.println("########DANS NLIST!##########");
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

        /**FIN GENERATION HTML**/

    }
}