
package fedaemon.pruebas.util;


import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Mike
 */


public class XMLCreator {

private String parent;
private String child;
private Integer ctr = 0;
private HashMap<Integer, String> xml;
private String root;
private HashMap<Integer, String> roots;
private StringBuilder sB;

private HashMap<Integer, String> getRoot() {
    this.roots.put(1, "<" + root + ">");
    this.roots.put(2, "</" + root + ">");
    return roots;
}

public void setRoot(String root) {
    this.root = root;
}

public XMLCreator() {
    if (this.xml == null) {
        this.xml = new HashMap<Integer, String>();
    }
    if (this.roots == null) {
        this.roots = new HashMap<Integer, String>();
    }
    if (this.sB == null) {
        this.sB = new StringBuilder();
    }
}

/*
 * private void startParentField(String parent) { this.parent = "<" + parent
 * + ">"; this.ctr++; addToXML(getParentField()); }
 * 
 * private void endParentField(String parent) { this.parent = "</" + parent
 * + ">"; this.ctr++; addToXML(getParentField()); }
 * 
 * public void setParent(String parent) { startParentField(parent);
 * endParentField(parent); }
 */

public void addChildField(String child) {
    this.child = "<" + child + ">" + "</" + child + ">";
    this.ctr++;
    addToXML(getChildField());

}

public void addChildField(String child, String value) {
    this.child = "<" + child + ">" + value + "</" + child + ">";
    this.ctr++;
    addToXML(getChildField());
}

private String getParentField() {
    return parent;
}

private String getChildField() {
    return child;
}

private void addToXML(String element) {
    xml.put(ctr, element);
}

public void buildXMLPreview() {

    // Set<Integer> keys = xml.keySet();
    getRoot();
    System.out.println(roots.get(1));
    sB.append(roots.get(1));
    for (int x = 1; x <= xml.size(); x++) {
        sB.append(xml.get(x));
    }
    sB.append(roots.get(2));
    System.out.println(sB.toString());
    sB.delete(0, sB.length());
}

public void buildXMLToFile() throws IOException {

    FileWriter writer = new FileWriter("try.xml");
    getRoot();
    writer.append(roots.get(1));
    for (int x = 1; x <= xml.size(); x++) {
        writer.append(xml.get(x));
    }
    writer.append(roots.get(2));
    writer.flush();
    writer.close();
}

}
