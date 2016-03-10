package fr.unice.polytech.devops.utils;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XMLGenerator {

    public static void generateRapportHighChart(String projectPath){
        Document rapportDocXML=null;
        File fXmlFile = new File(projectPath + "/tmpReport.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        if (fXmlFile.exists()) {
            try {
                rapportDocXML = dbFactory.newDocumentBuilder().parse(fXmlFile);

            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                List<NodeList> nlList = new ArrayList<NodeList>();

                File htmlReport = new File(projectPath + "/target/mutation-report/htmlReport.html");
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
                        "}"+
                        "#mutant0{"+
                        "display:none;"+
                        "}"
                );
                writer.println("</style>");
                writer.println("<meta charset=\"UTF-8\">");
                writer.println("</head>");
                writer.println("<body>");
                int mutantVivant = 0;
                int mutantMort = 0;
                int mutantMortNe=0;
                int mutantTotal = rapportDocXML.getElementsByTagName("mutant").getLength();
                writer.println("<div id='titre'>Rapport des tests par mutation</div>");
                NodeList nl = rapportDocXML.getElementsByTagName("mutant");
                for(int i = 0 ; i< nl.getLength();i++){
                    writer.println("<div id='mutant"+i+"'>");
                    writer.println("<div class='titreMutant'>MUTANT " + i + " : </div>");
                    writer.println("<div class='titreProcessors'>Contient les processors : </div>");
                    NodeList nlChildNodesMutant = nl.item(i).getChildNodes();

                    NodeList nlChildProcessors = nlChildNodesMutant.item(0).getChildNodes();
                    for (int j = 0; j < nlChildProcessors.getLength(); j++) {
                        writer.println("<div class='processor'>" + nlChildProcessors.item(j).getTextContent() + "</div>");
                    }
                    if(nl.item(i).getAttributes().getNamedItem("stillborn")==null) {//si on a pas d'attribut stillborn ...

                        NodeList nlChildTests = nlChildNodesMutant.item(1).getChildNodes();

                        writer.println("<div class='titreTests'>TESTS : </div>");
                        boolean isAlive = true;
                        for (int ind = 0; ind < nlChildTests.getLength(); ind++) {
                            writer.println("<div class='titreClass'>Dans la classe " + nlChildTests.item(ind).getAttributes().getNamedItem("name") + " : </div>");

                            for (int indj = 0; indj < nlChildTests.item(ind).getChildNodes().getLength(); indj++) {
                                if (nlChildTests.item(ind).getChildNodes().item(indj).hasChildNodes()) {
                                    writer.println("<div class='testFail'>[TEST] " + nlChildTests.item(ind).getChildNodes().item(indj).getTextContent() + "[FAIL] dans la methode de test " + nlChildTests.item(ind).getChildNodes().item(indj).getAttributes().getNamedItem("name") + "</div>");
                                    isAlive = false;
                                } else {
                                    writer.println("<div class='testSuccess'>[TEST] " + nlChildTests.item(ind).getChildNodes().item(indj).getTextContent() + "[SUCESS] dans la methode de test " + nlChildTests.item(ind).getChildNodes().item(indj).getAttributes().getNamedItem("name") + "</div>");
                                }
                            }
                        }
                        if(i>0) {
                            if (isAlive) {
                                mutantVivant++;
                                writer.println("<div style='background-color:red;'>Mutant" + i + " vivant</div>");
                            } else {
                                mutantMort++;
                                writer.println("<div style='background-color:lightgreen'>Mutant" + i + " tué</div>");
                            }
                        }
                    }
                    else{
                        writer.println("<div style='background-color:yellow'>Mutant" + i + " mort-né</div>");
                        mutantMortNe++;
                    }
                    writer.println("</div>");
                }

                float percentageAlive = (mutantVivant*100)/mutantTotal;
                float percentageDead = (mutantMort*100)/mutantTotal;
                float percentageStillBorn = (mutantMortNe*100)/mutantTotal;

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
                        "            },{\n"+
                        "                 name: 'Mutant Mort-né',\n "+
                        "                 y: "+percentageStillBorn+" "+
                                    "}]\n" +
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

    public static boolean addProcessorsToXml(String projectPath) {
        try {
            File processorsFile = new File(projectPath + "/myProcessor.xml");
            File report = new File(projectPath + "/tmpReport.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document reportDoc;
            Document processorsDoc;
            reportDoc = dbFactory.newDocumentBuilder().parse(report);
            processorsDoc = dbFactory.newDocumentBuilder().parse(processorsFile);

            NodeList mutantList = reportDoc.getElementsByTagName("mutant");
            NodeList processorsList = processorsDoc.getElementsByTagName("processors");

            int processorsCounter = -1;
            List<Node> processors = new ArrayList<>();
            for (int i = 0; i < processorsList.getLength(); i++) {
                if (NodeHelper.isElementNode(processorsList.item(i))) {
                    processors.add(processorsList.item(i));
                }
            }

            for (int i = 0; i < mutantList.getLength(); i++) {
                Node mutant = mutantList.item(i);
                if (NodeHelper.isElementNode(mutant)) {

                    if (processorsCounter >= 0 && processorsCounter < processors.size()) {
                        Element processorsElement = reportDoc.createElement("processors");
                        NodeList procsChildren = processors.get(processorsCounter).getChildNodes();

                        for (int j = 0; j < procsChildren.getLength(); j++) {
                            Node processor = procsChildren.item(j);
                            if (NodeHelper.isElementNode(processor)) {
                                Element processorElement = reportDoc.createElement("processor");
                                processorElement.setTextContent(processor.getTextContent());
                                processorElement.setNodeValue(processor.getTextContent());
                                processorsElement.appendChild(processorElement);
                            }
                        }
                        int firstChildIndex = NodeHelper.getFirstElementIndex(mutant.getChildNodes());
                        mutant.insertBefore(processorsElement,  mutant.getChildNodes().item(firstChildIndex));
                    } else {
                        Element processorsElement = reportDoc.createElement("processors");
                        int firstChildIndex = NodeHelper.getFirstElementIndex(mutant.getChildNodes());
                        mutant.insertBefore(processorsElement,  mutant.getChildNodes().item(firstChildIndex));
                    }
                    processorsCounter++;
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer;

            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(reportDoc);
            StreamResult result = new StreamResult(report);

            transformer.transform(source, result);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean generateXmlFromProcessors(Node processors, String projectPath) {
        File dirTarget = new File(projectPath + "/target/mutation-report");
        if (!dirTarget.exists()) {
            dirTarget.mkdir();
        }

        File report = new File(projectPath + "/tmpReport.xml");
        try {
            if (!report.exists()) {
                report.createNewFile();
                PrintWriter writer = new PrintWriter(report.getAbsolutePath(), "UTF-8");
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                writer.println("<mutants>");
                writer.println("</mutants>");
                writer.close();
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document reportDoc;
            Document testDoc;

            List<NodeList> nList = new ArrayList<>();
            reportDoc = dbFactory.newDocumentBuilder().parse(report);

            NodeList mutantsList = reportDoc.getElementsByTagName("mutants");
            Node mutants = mutantsList.item(NodeHelper.getLastElementIndex(mutantsList));
            Element mutantElement = reportDoc.createElement("mutant");
            //Element processorsElement = reportDoc.createElement("processors");

            NodeList processorsChildren = processors.getChildNodes();
/*
            for (int i = 0; i < processorsChildren.getLength(); i++) {
                if (NodeHelper.isElementNode(processorsChildren.item(i))) {
                    Element processorElement = reportDoc.createElement("processor");
                    processorElement.setTextContent(processorsChildren.item(i).getTextContent());
                    processorElement.setNodeValue(processorsChildren.item(i).getTextContent());
                    processorsElement.appendChild(processorElement);
                }
            }*/
            //mutantElement.appendChild(processorsElement);

            Element testsElement = reportDoc.createElement("tests");

            if (new File(projectPath + "/target/surefire-reports").exists()) {
                File[] fileList = new File(projectPath + "/target/surefire-reports").listFiles();
                if (fileList != null) {
                    for (File fXmlFile : fileList) {
                        if (FilenameUtils.getExtension((fXmlFile.getName())).toLowerCase().equals("xml")) {
                            testDoc = dbFactory.newDocumentBuilder().parse(fXmlFile);
                            Element classElement = reportDoc.createElement("class");
                            int indexFirstTestSuite = NodeHelper.getFirstElementIndex(testDoc.getElementsByTagName("testcase"));
                                if (indexFirstTestSuite != -1) {
                                Element classname = (Element) testDoc.getElementsByTagName("testcase").item(indexFirstTestSuite);
                                classElement.setAttribute("name", classname.getAttribute("classname"));
                            }
                            testsElement.appendChild(classElement);
                            NodeList testsCases = testDoc.getElementsByTagName("testcase");
                            for (int i = 0; i < testsCases.getLength(); i++) {
                                Element testcase = (Element) testsCases.item(i);
                                if (NodeHelper.isElementNode(testcase)) {
                                    Element testElement = reportDoc.createElement("test");
                                    testElement.setAttribute("name", testcase.getAttribute("name"));
                                    NodeList children = testcase.getChildNodes();
                                    if (children.getLength() != 0) {
                                        int firstChildIndex = NodeHelper.getFirstElementIndex(children);
                                        if (firstChildIndex != -1) {
                                            Element firstChild = (Element) children.item(firstChildIndex);
                                            if (firstChild.getTagName().toLowerCase().equals("error")) {
                                                mutantElement.setAttribute("stillborn", "true");
                                                break;
                                            } else {
                                                Element message = reportDoc.createElement("message");
                                                message.setTextContent(firstChild.getAttribute("message"));
                                                testElement.appendChild(message);
                                            }
                                        }
                                    }
                                    classElement.appendChild(testElement);
                                }
                            }
                        }
                    }
                }
            }

            mutantElement.appendChild(testsElement);
            mutants.appendChild(mutantElement);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer;

            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(reportDoc);
            StreamResult result = new StreamResult(report);

            transformer.transform(source, result);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return false;
        } catch (TransformerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
