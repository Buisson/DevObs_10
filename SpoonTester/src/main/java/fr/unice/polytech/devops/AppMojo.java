package fr.unice.polytech.devops;

import fr.unice.polytech.devops.utils.NodeHelper;
import fr.unice.polytech.devops.utils.XMLGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Mojo(name = "rapport")
public class AppMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Debut du Plugin Maven de Mutation");

        /**MODIFICATION DU POM**/
        //TODO Voir le probleme des child (surement faire un trim quelque part ...)
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document doc = null;
        Document docProcessor = null;

        File pomXML = new File(project.getBasedir() + "/pom.xml"); //Fichier pom.xml
        File tmpProcessorsXML = new File(project.getBasedir() + "/tmpMyProcessor.xml"); //Fichier temporaire myprocessor

        try {
            if (!tmpProcessorsXML.exists()) { //Si le fichier temporaire n'existe pas le creer.
                Files.copy(Paths.get(project.getBasedir() + "/myProcessor.xml"), Paths.get(project.getBasedir() + "/tmpMyProcessor.xml"), StandardCopyOption.REPLACE_EXISTING);
            }
            /**Parseur xml stuff**/
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(pomXML);
            docProcessor = dBuilder.parse(tmpProcessorsXML);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**Sauvegarde de la taille avant suppression pour condition d'arret maven**/
        int tailleProcessors = NodeHelper.getLengthRealElement(docProcessor.getElementsByTagName("processors"));

        /**Vide dans le pom.xml ce que la balise processors contient**/
        int firstItemIndex = NodeHelper.getFirstElementIndex(doc.getElementsByTagName("processors"));
        int longNode = doc.getElementsByTagName("processors").item(firstItemIndex).getChildNodes().getLength();
        if (firstItemIndex != -1) {
            for (int i = 0; i < longNode; i++) {
                if (doc.getElementsByTagName("processors").item(firstItemIndex).getChildNodes().item(i) != null) {
                    doc.getElementsByTagName("processors").item(firstItemIndex).getChildNodes().item(i).getParentNode().removeChild(doc.getElementsByTagName("processors").item(firstItemIndex).getChildNodes().item(i));
                }
            }
        }

        if (tailleProcessors != 0) {
            /**Ajout des processors dans le pom**/
            int longTMP = docProcessor.getElementsByTagName("processors").item(0).getChildNodes().getLength();
            //int longTMP = getLengthRealElement(docProcessor.getElementsByTagName("processors").item(0).getChildNodes());
            Node tmpnode = docProcessor.getElementsByTagName("processors").item(0);
            for (int i = 0; i < longTMP; i++) {
                Element temporaryElement = doc.createElement("processor");
                if ((tmpnode.getChildNodes().item(i).getNodeName() != null) && (tmpnode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE)) {
                    temporaryElement.appendChild(doc.createTextNode(tmpnode.getChildNodes().item(i).getTextContent()));
                    doc.getElementsByTagName("processors").item(0).appendChild(temporaryElement);
                }
            }


            /**Suppression du premier element processors du fichier temporaire**/

            docProcessor.getElementsByTagName("processors").item(0).getParentNode().removeChild(docProcessor.getElementsByTagName("processors").item(0));
        }

        /**Mise a jour des fichier xml (pom + tmpprocessor)**/
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        DOMSource sourceProcessors = new DOMSource(docProcessor);
        StreamResult result = new StreamResult(new File(project.getBasedir() + "/pom.xml"));
        StreamResult resultProcessors = new StreamResult(tmpProcessorsXML);
        // Output to console for testing
        //StreamResult result = new StreamResult(System.out);
        try {
            transformer.transform(source, result);//modifie le pom.xml
            transformer.transform(sourceProcessors, resultProcessors); // modifie le myProcessor.xml
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        /**APPELLE RECURSIF MAVEN**/
        try {
            if (tailleProcessors != 0) {
                //System.out.println("################INVOCATION MAVEEEEEENNNNNNNNN##############################");
                int index = NodeHelper.getFirstElementIndex(doc.getElementsByTagName("processors"));
                XMLGenerator.generateXmlFromProcessors(doc.getElementsByTagName("processors").item(index), project.getBasedir().toString());
                String mvnCallString = "mvn";
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    mvnCallString += ".cmd";
                }
                ProcessBuilder pb = new ProcessBuilder(mvnCallString, "package");//TODO remplacer par mvn test ?
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                Process p = pb.start();
                p.waitFor();
                //System.out.println("APRES INVOCATIONNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
            } else {
                //Fin des appels recursif

                int index = NodeHelper.getFirstElementIndex(doc.getElementsByTagName("processors"));
                XMLGenerator.generateXmlFromProcessors(doc.getElementsByTagName("processors").item(index), project.getBasedir().toString());
                XMLGenerator.addProcessorsToXml(project.getBasedir().toString());
                /**Appel generation HTML**/
                XMLGenerator.generateRapportHighChart(project.getBasedir().toString());
                /**FIN GENERATION HTML**/

                firstItemIndex = NodeHelper.getFirstElementIndex(doc.getElementsByTagName("processors"));
                longNode = doc.getElementsByTagName("processors").item(firstItemIndex).getChildNodes().getLength();
                for (int i = longNode - 1; i >= 0; i--) {
                    if (doc.getElementsByTagName("processors").item(firstItemIndex).getChildNodes().item(i) != null) {
                        doc.getElementsByTagName("processors").item(firstItemIndex).getChildNodes().item(i).getParentNode().removeChild(doc.getElementsByTagName("processors").item(firstItemIndex).getChildNodes().item(i));
                    }
                }
                source = new DOMSource(doc);
                result = new StreamResult(new File(project.getBasedir() + "/pom.xml"));
                transformer.transform(source, result);
            }
            tmpProcessorsXML.delete();//On supprimme le fichier temporaire.
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}