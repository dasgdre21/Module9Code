import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

//modified to count number of movies released by decade
public class DOMDVDReader {
	public void read(String filepath) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(true);

		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
			db.setErrorHandler(new ErrorHandler() {
				public void error(SAXParseException spe) {
					System.err.println(spe);
				}

				public void fatalError(SAXParseException spe) {
					System.err.println(spe);
				}

				public void warning(SAXParseException spe) {
					System.out.println(spe);
				}
			});
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			System.exit(1);
		}

		Document doc = null;
		try {
			doc = db.parse(new File(filepath));
		} catch (SAXException se) {
			System.err.println(se);
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
		NodeList nodeList = doc.getDocumentElement().getChildNodes();

		// key = decade of release_year, value = number of dvds
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		
		//calls method to count number of movies released by decade
		countDecades(nodeList, hm); 
		System.out.println(printMap(hm));
	}
	
	//sorts map and returns String with formatted output
	public String printMap(HashMap<String, Integer> unsortedMap) {
		
		// TreeMap to sort/store values of HashMap
		TreeMap<String, Integer> sortedMap = new TreeMap<>();

		// puts data from HashMap into TreeMap to sort 
		sortedMap.putAll(unsortedMap);

		String s = ""; //body of string to be returned
		String head = "  <count decade=\"";
		String extension = "\">";
		String tail = "</count>\n";

		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			String key = (String) entry.getKey(); // key
			Integer value = (Integer) entry.getValue(); // value

			s += head + key.toString() + extension + value.toString() + tail;
		}
		
		String header = "<DVD>\n" + " <summary>\n";
	    String trailer = " </summary>\n" + "</DVD>";
		
		return header + s + trailer; //XML formatted String
	} 

	//I'm not using this method, but I used parts of it in my countDecades method
	public void EchoNodes(NodeList nodeList) {
		if (nodeList == null)
			return;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child_node = nodeList.item(i);
			if (child_node.getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element) child_node;
				System.out.println("Tag Name:" + el.getTagName());
				NamedNodeMap attributes = child_node.getAttributes();
				Node attribute = attributes.getNamedItem("id");
				if (attribute != null)
					System.out.println("Attr:  " + attribute.getNodeName() + " = " + attribute.getNodeValue());
			} else if (child_node.getNodeType() == Node.TEXT_NODE) {
				Text tn = (Text) child_node;
				String text = tn.getWholeText().trim();
				if (text.length() > 0)
					System.out.println("Text:  " + text);
			}
			EchoNodes(child_node.getChildNodes());
		}
	}

	//DOM XML parser that counts number of movies released by decade
	public void countDecades(NodeList nodeList, HashMap<String, Integer> hm) {

		if (nodeList == null)
			return;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child_node = nodeList.item(i);
			if (child_node.getNodeType() == Node.TEXT_NODE) {
				Text tn = (Text) child_node;
				String text = tn.getWholeText().trim();
				if (text.length() > 0) {
					
					//finds release_year for each dvd
					if (child_node.getParentNode().getNodeName().equals("release_year")) {
						
						//key in HashMap is the decade of release_year
						String key = text.substring(0, 3) + "0";
						
						//if key is not in HashMap yet, it is put in
						//value = 1
						if (!hm.containsKey(key)) {
							hm.put(key, 1);
						} 
						//if key is already in map
						//value is incremented then put back in
						else {
							Integer value = hm.get(key) + 1;
							hm.put(key, value);
						}
					}
				}
			}
			countDecades(child_node.getChildNodes(), hm); //method calls itself again
		}
	}

	public static void main(String[] args) {
		DOMDVDReader domDVDReader = new DOMDVDReader();
		domDVDReader.read("dvd.xml");
	}
}
