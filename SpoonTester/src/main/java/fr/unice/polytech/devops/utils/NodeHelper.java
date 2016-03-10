package fr.unice.polytech.devops.utils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeHelper {

    public static boolean isElementNode(Node node) {
        return (node.getNodeName() != null) && (node.getNodeType() == Node.ELEMENT_NODE);
    }

    public static int getFirstElementIndex(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            if ((nodes.item(i) != null) && (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)) {
                return i;
            }
        }
        return -1;
    }

    public static int getLastElementIndex(NodeList nodes) {
        for (int i = nodes.getLength() - 1; i >= 0; i--) {
            if ((nodes.item(i) != null) && (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)) {
                return i;
            }
        }
        return -1;
    }

    public static int getLengthRealElement(NodeList nodes){
        int length = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            if ((nodes.item(i) != null) && (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)) {
                length++;
            }
        }
        return length;
    }
}
