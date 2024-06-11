package com.yykj.kepw.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLHandler {
  public static Document loadXMLFile(InputStream inputStream, boolean namespaceAware) throws XmlParseException {
    try {
      Document doc;
      DocumentBuilderFactory dbf = createSecureDocBuilderFactory();
      dbf.setIgnoringComments(true);
      dbf.setNamespaceAware(namespaceAware);
      DocumentBuilder db = dbf.newDocumentBuilder();
      try {
        doc = db.parse(inputStream);
      } finally {
        if (inputStream != null)
          inputStream.close(); 
      } 
      return doc;
    } catch (Exception e) {
      throw new XmlParseException("Error reading information from input stream", e);
    } 
  }
  
  public static Node getSubNode(Node n, String tag) {
    if (n == null)
      return null; 
    NodeList children = n.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node childnode = children.item(i);
      if (childnode.getNodeName().equalsIgnoreCase(tag))
        return childnode; 
    } 
    return null;
  }
  
  public static List<Node> getNodes(Node n, String tag) {
    List<Node> nodes = new ArrayList<>();
    if (n == null)
      return nodes; 
    NodeList children = n.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node childnode = children.item(i);
      if (childnode.getNodeName().equalsIgnoreCase(tag))
        nodes.add(childnode); 
    } 
    return nodes;
  }
  
  public static String getTagAttribute(Node node, String attribute) {
    if (node == null)
      return null; 
    String retval = null;
    NamedNodeMap nnm = node.getAttributes();
    if (nnm != null) {
      Node attr = nnm.getNamedItem(attribute);
      if (attr != null)
        retval = attr.getNodeValue(); 
    } 
    return retval;
  }
  
  public static String getTagValue(Node n, String tag) {
    if (n == null)
      return null; 
    NodeList children = n.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node childnode = children.item(i);
      if (childnode.getNodeName().equalsIgnoreCase(tag) && 
        childnode.getFirstChild() != null)
        return childnode.getFirstChild().getNodeValue(); 
    } 
    return null;
  }
  
  public static DocumentBuilderFactory createSecureDocBuilderFactory() throws ParserConfigurationException {
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    docBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
    docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    return docBuilderFactory;
  }
}
