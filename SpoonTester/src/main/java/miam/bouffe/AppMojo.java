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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "rapport")
public class AppMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    private int getFirstElementIndex(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            if ((nodes.item(i) != null) && (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)) {
                return i;
            }
        }
        return -1;
    }

    private int getLengthRealElement(NodeList nodes){
        int length = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            if ((nodes.item(i) != null) && (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)) {
                length++;
            }
        }
        return length;
    }

    private boolean isElementNode(Node node) {
        return (node.getNodeName() != null) && (node.getNodeType() == Node.ELEMENT_NODE);
    }

    private void generateHtml(Node processors) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Document doc;
        Document testDoc;

        File dirTarget = new File(project.getBasedir() + "/target/mutation-report");
        if (!dirTarget.exists()) {
            dirTarget.mkdir();
        }

        try {
            List<NodeList> nList = new ArrayList<>();
            File tmpReport  = new File(project.getBasedir() + "/target/mutation-report/tmpReport.xml");

            if (!(tmpReport.exists())) {
                tmpReport.createNewFile();
            }
            doc = dbFactory.newDocumentBuilder().parse(tmpReport);
            Element mutantElement = doc.createElement("mutant");
            Element processorsElement = doc.createElement("processors");

            NodeList processorsChildren = processors.getChildNodes();

            for (int i = 0; i < processorsChildren.getLength(); i++) {
                if (isElementNode(processorsChildren.item(i))) {
                    Element processorElement = doc.createElement("processor");
                    processorElement.setNodeValue(processorsChildren.item(i).getTextContent());
                    processorsElement.appendChild(processorElement);
                }
            }

            if (new File(project.getBasedir() + "/target/surefire-reports").exists()) {
                for (File fXmlFile : new File(project.getBasedir() + "/target/surefire-reports").listFiles()) {
                    if (FilenameUtils.getExtension((fXmlFile.getName())).toLowerCase().equals("xml")) {
                        testDoc = dbFactory.newDocumentBuilder().parse(fXmlFile);
                        nList.add(testDoc.getElementsByTagName("testcase"));
                    }
                }
            }
            mutantElement.appendChild(processorsElement);
            doc.appendChild(mutantElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }

            DOMSource source = new DOMSource(doc);
            System.out.println("\n\n\n\n\nFile\n\n\n\n\n");
            StreamResult result = new StreamResult(System.out);
            try {
                transformer.transform(source, result);
            } catch (TransformerException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

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
        //int tailleProcessors = docProcessor.getElementsByTagName("processors").getLength();
        int tailleProcessors = getLengthRealElement(docProcessor.getElementsByTagName("processors"));

        /**Vide dans le pom.xml ce que la balise processors contient**/
        int longNode = doc.getElementsByTagName("processors").item(0).getChildNodes().getLength();
        System.out.println("LONGEUUUUUR : longTMP : "+longNode);
        for (int i = 0; i < longNode; i++) {
            doc.getElementsByTagName("processors").item(0).getChildNodes().item(i).getParentNode().removeChild(doc.getElementsByTagName("processors").item(0).getChildNodes().item(i));
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
            System.out.println("TAILLE : " + tailleProcessors);
            if (tailleProcessors != 0) {
                System.out.println("################INVOCATION MAVEEEEEENNNNNNNNN##############################");

                int index = getFirstElementIndex(doc.getElementsByTagName("processors"));
                        //doc.getElementsByTagName("processors").item(0).appendChild(temporaryElement);
                //TODO ici enregistrer les fichiers de test.
                generateHtml(doc.getElementsByTagName("processors").item(index));

                String mvnCallString = "mvn";
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    mvnCallString += ".cmd";
                }
                ProcessBuilder pb = new ProcessBuilder(mvnCallString, "package");//TODO remplacer par mvn test ?
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                Process p = pb.start();
                p.waitFor();
                System.out.println("APRES INVOCATIONNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
            } else {
                //Fin des appels recursif
                //tmpProcessorsXML.delete();//On supprimme le fichier temporaire.
            }
            System.out.println("DELETE : " + tmpProcessorsXML.delete());//On supprimme le fichier temporaire.
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

/** TMP liste de processeurs :
 * ###########################
 * <processor>miam.bouffe.CatchProcessor</processor><processor>miam.bouffe.transformation.NotNullCheckAdderProcessor</processor><processor>miam.bouffe.transformation.BinaryOperatorMutator</processor>
 **/

        /**DEBUT GENERATION HTML**/

        if (new File(project.getBasedir() + "/target/surefire-reports").exists()) {
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