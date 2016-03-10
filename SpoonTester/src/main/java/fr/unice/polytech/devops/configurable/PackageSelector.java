package fr.unice.polytech.devops.configurable;

import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;




/**
 * Created by user on 08/03/16.
 */

 public   class PackageSelector {
    // liste conenant les association mutation-package

    private   HashMap<String,String> mutation_package = new HashMap<>();
    private  HashMap<String,String> mutation_methode = new HashMap<>();




//        <processor package="mymain.mayne">fr.unice.polytech.devops.transformation.BinaryOperatorMutator</processor>


    public  void fill(String path){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        File tmpProcessorsXML = new File(path);
        Document docProcessor = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();

            docProcessor = dBuilder.parse(tmpProcessorsXML);
            int longTMP = docProcessor.getElementsByTagName("processors").item(0).getChildNodes().getLength();
            Node tmpnode = docProcessor.getElementsByTagName("processors").item(0);
            for (int i = 0; i < longTMP; i++) {
                Node  sub_Node = tmpnode.getChildNodes().item(i);
                if ((sub_Node.getNodeName() != null) && (sub_Node.getNodeType() == Node.ELEMENT_NODE)) {
                    Node packageName = sub_Node.getAttributes().getNamedItem("package");
                    Node methodeName = sub_Node.getAttributes().getNamedItem("methode");

                    String mutationName =sub_Node.getTextContent().split("transformation.")[1];
                    if(packageName != null){
                        mutation_package.put(mutationName,packageName.getNodeValue());

                    }
                    if(methodeName != null){
                        mutation_methode.put(mutationName,methodeName.getNodeValue());

                    }
                }
            }


            //System.out.println(mutation_methode.size());
            //System.out.println(mutation_package.size());


        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (ParserConfigurationException e) {
        e.printStackTrace();
    }

    }



    public boolean methodeAndPackageChecker(CtElement candidate,String mutationName){

     //   System.out.println(PackageSelector.mutation_methode.size());
       // System.out.println(PackageSelector.mutation_package.size());

        String packageName = mutation_package.get(mutationName);
        String methodeName = mutation_methode.get(mutationName);

        CtMethod parent_methode = candidate.getParent(CtMethod.class);
        CtPackage parent_package = candidate.getParent(CtPackage.class);
/**
        System.out.println("--------------------");
        System.out.println(parent_methode.getSimpleName()+" vs " + methodeName);
        System.out.println(parent_package.getSimpleName()+ " vs "+packageName);
        System.out.println("--------------------");
**/

        if(packageName != null && methodeName != null)
            return packageName.equals(parent_package.getSimpleName()) && methodeName.equals(parent_methode.getSimpleName()) ;
        if(packageName != null && methodeName == null)
            return packageName.equals(parent_package.getSimpleName());
        if(packageName == null && methodeName!= null)
            return methodeName.equals(parent_methode.getSimpleName());


        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        return true;
    }
}
