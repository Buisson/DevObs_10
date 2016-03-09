package fr.unice.polytech.devops;

import fr.unice.polytech.devops.utils.NodeHelper;
import fr.unice.polytech.devops.utils.XMLGenerator;
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

    private int getLengthRealElement(NodeList nodes){
        int length = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            if ((nodes.item(i) != null) && (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)) {
                length++;
            }
        }
        return length;
    }

    private void generateRapportHighChart(){
        Document rapportDocXML=null;
        File fXmlFile = new File(project.getBasedir() + "/tmpReport.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        if (fXmlFile.exists()) {
            try {
                rapportDocXML = dbFactory.newDocumentBuilder().parse(fXmlFile);

            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                List<NodeList> nlList = new ArrayList<NodeList>();

                File htmlReport = new File(project.getBasedir() + "/target/mutation-report/htmlReport.html");
                PrintWriter writer = new PrintWriter(htmlReport.getAbsolutePath(), "UTF-8");

                writer.println("<!DOCTYPE html>");
                writer.println("<html>");
                writer.println("<head>");
                writer.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js\"></script>\n" +
                        "<script src=\"https://code.highcharts.com/highcharts.js\"></script>\n" +
                        "<script src=\"https://code.highcharts.com/modules/exporting.js\"></script>\n");
                writer.println("<title>Mutation Report</title>");
                writer.println("<style>");
                writer.println("#tableMutants{border:1px solid;margin:0 auto;}" +
                        "#tableMutants td{border:1px solid;}" +
                        ".aliveMut{background-color:red;}" +
                        ".deadMut{background-color:green;}" +
                        "#titre{"+
                            "text-align: center;\n" +
                            "background-color: rgb(117, 206, 193);\n" +
                            "height: 23px;\n" +
                            "padding: 10px 0px;\n" +
                            "border: 3px solid;\n" +
                            "-webkit-border-top-left-radius: 30px;\n" +
                            "-webkit-border-bottom-right-radius: 30px;\n" +
                            "-moz-border-radius-topleft: 30px;\n" +
                            "-moz-border-radius-bottomright: 30px;\n" +
                            "border-top-left-radius: 30px;\n" +
                            "border-bottom-right-radius: ;" +
                        "}" +
                        ".titreMutant{" +
                            "background-color: rgb(132, 131, 7);\n" +
                            "margin-top: 10px;\n" +
                            "padding-left: 6px;\n" +
                            "padding: 10px;" +
                        "}"+
                        ".titreProcessors{" +
                            "border: 1px solid;"+
                            "margin-top: 5px;" +
                        "}"+
                        ".processor{"+
                            "margin-left: 20px;\n" +
                            "border: 1px solid;"+
                        "}"+
                        ".titreTests{"+
                            "margin-left: 30px;\n" +
                            "border-top: 1px solid;\n" +
                            "border-left: 1px solid;\n" +
                            "border-right: 1px solid;"+
                        "}"+
                        ".titreClass {\n" +
                        "    margin-left: 30px;\n" +
                        "    border: 1px solid;\n" +
                        "}"+
                        ".testFail {\n" +
                        "    margin-left: 45px;\n" +
                        "    border: 1px solid;\n" +
                        "    background-color: lightgreen;\n" +
                        "}"+
                        ".testSuccess {\n" +
                        "    margin-left: 45px;\n" +
                        "    border: 1px solid;\n" +
                        "    background-color: red;\n" +
                        "}"
                );
                writer.println("</style>");
                writer.println("<meta charset=\"UTF-8\">");
                writer.println("</head>");
                writer.println("<body>");
                int mutantVivant = 0;
                int mutantMort = 0;
                int mutantTotal = rapportDocXML.getElementsByTagName("mutant").getLength();
                writer.println("<div id='titre'>Rapport des tests par mutation</div>");
                NodeList nl = rapportDocXML.getElementsByTagName("mutant");
                for(int i = 0 ; i< nl.getLength();i++){
                    writer.println("<div class='titreMutant'>MUTANT "+(i+1)+" : </div>");
                    writer.println("<div class='titreProcessors'>Contient les processors : </div>");
                    NodeList nlChildNodesMutant = nl.item(i).getChildNodes();

                    NodeList nlChildProcessors = nlChildNodesMutant.item(0).getChildNodes();
                    for(int j =0 ; j< nlChildProcessors.getLength() ; j++){
                        writer.println("<div class='processor'>"+nlChildProcessors.item(j).getTextContent()+"</div>");
                    }

                    NodeList nlChildTests = nlChildNodesMutant.item(1).getChildNodes();

                    writer.println("<div class='titreTests'>TESTS : </div>");
                    boolean isAlive = true;
                    for(int ind=0;ind<nlChildTests.getLength();ind++){
                        writer.println("<div class='titreClass'>Dans la classe "+nlChildTests.item(ind).getAttributes().getNamedItem("name")+" : </div>");

                        for(int indj = 0 ; indj<nlChildTests.item(ind).getChildNodes().getLength();indj++){
                            if(nlChildTests.item(ind).getChildNodes().item(indj).hasChildNodes()) {
                                writer.println("<div class='testFail'>[TEST] " + nlChildTests.item(ind).getChildNodes().item(indj).getTextContent() + "[FAIL] dans la methode de test "+nlChildTests.item(ind).getChildNodes().item(indj).getAttributes().getNamedItem("name")+"</div>");
                                isAlive=false;
                            }
                            else{
                                writer.println("<div class='testSuccess'>[TEST] " + nlChildTests.item(ind).getChildNodes().item(indj).getTextContent() + "[SUCESS] dans la methode de test "+nlChildTests.item(ind).getChildNodes().item(indj).getAttributes().getNamedItem("name")+"</div>");
                            }
                        }
                    }
                    if(isAlive){
                        mutantVivant++;
                        writer.println("<div style='background-color:red;'>Mutant"+i+1+" vivant</div>");
                    }
                    else{
                        mutantMort++;
                        writer.println("<div style='background-color:lightgreen'>Mutant"+(i+1)+" tué</div>");
                    }
                }

                float percentageAlive = (mutantVivant*100)/mutantTotal;
                float percentageDead = (mutantMort*100)/mutantTotal;

                writer.println("<div id=\"container\" style=\"min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto\"></div>");
                writer.println("<script>\n" +
                        "$(function () {\n" +
                        "    $('#container').highcharts({\n" +
                        "        chart: {\n" +
                        "            plotBackgroundColor: null,\n" +
                        "            plotBorderWidth: null,\n" +
                        "            plotShadow: false,\n" +
                        "            type: 'pie'\n" +
                        "        },\n" +
                        "        title: {\n" +
                        "            text: 'Pourcentage mutant tué / mutant vivant'\n" +
                        "        },\n" +
                        "        tooltip: {\n" +
                        "            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'\n" +
                        "        },\n" +
                        "        plotOptions: {\n" +
                        "            pie: {\n" +
                        "                allowPointSelect: true,\n" +
                        "                cursor: 'pointer',\n" +
                        "                dataLabels: {\n" +
                        "                    enabled: true,\n" +
                        "                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',\n" +
                        "                    style: {\n" +
                        "                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            }\n" +
                        "        },\n" +
                        "        series: [{\n" +
                        "            name: 'Brands',\n" +
                        "            colorByPoint: true,\n" +
                        "            data: [{\n" +
                        "                name: 'Mutant tué',\n" +
                        "                y: "+percentageDead+"\n" +
                        "            },{\n" +
                        "                name: 'Mutant vivant',\n" +
                        "                y: "+percentageAlive+"\n" +
                        "            }]\n" +
                        "        }]\n" +
                        "    });\n" +
                        "});\n" +
                        "</script>");


                writer.println("</body>");
                writer.println("</html>");
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        fXmlFile.delete();//on supprime le fichier tmpReport.xml
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

        /**Appel generation HTML**/
        XMLGenerator.generateRapportHighChart(project.getBasedir().toString());
        //generateRapportHighChart();
        /**FIN GENERATION HTML**/

    }
}