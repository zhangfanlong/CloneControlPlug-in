package PreProcess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XML {

	// 创建XML文档
	public static Document Create() {
		Document document = null;
		try {
			// 创建解析工厂
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			// 指定DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();
			// 创建新XML文件
			document = builder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return document;
	}

	// 载入XML文档
	public static Document Load(String filename) {
		Document document = null;
		try {
			// 创建解析工厂
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			// 指定DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new File(filename));
			document.normalize();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return document;
	}
	
	// 保存文件
	public static void Save(Document newXml, String savePath) {

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			DOMSource source = new DOMSource(newXml);
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new File(savePath));
			StreamResult streamResult = new StreamResult(pw);
			transformer.transform(source, streamResult);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}
	
	//从source节点获得CRD节点
	public static Node GetCRDFromSourceNode(Node sourceNode){
		Node crdNode = null;
		NodeList crdNodes = sourceNode.getChildNodes();
		for(int i=0;i<crdNodes.getLength();i++){
			if(crdNodes.item(i).getNodeName().equals("CloneRegionDescriptor")){
				crdNode = crdNodes.item(i);
				break;
			}
		}
		return crdNode;
	}
	
	//获得指定class节点的第index个source子节点
	public static Node GetSpecifiedChildNode(Document doc,Node classNode,int index){
		Node sourceNode = null;
		int temp=0;//计数
		NodeList classChild = classNode.getChildNodes();
		for(int i=0;i<classChild.getLength();i++){
			if(classChild.item(i).getNodeName() == "source"){
					if(temp == index)
						return classChild.item(i);
					++temp;
			}		
		}
		return sourceNode;
	}
	
	
	
	
	// 第index个tagname节点的值
	public static String GetTagValue(Document doc, String tagname, int index) {
		NodeList nodes = doc.getElementsByTagName(tagname);
		Node node = nodes.item(index);
		String value = node.getTextContent();
		return value;
	}

	// 第index个tagname节点的attname属性值
	public static String GetAttriValue(Document doc, String tagname, int index,String attname) {
		String value = "";
		NodeList nodes = doc.getElementsByTagName(tagname);
		Node node = nodes.item(index);
		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			if (attributes.item(i).getNodeName() == attname) {
				value = attributes.item(i).getNodeValue();
				break;
			}
		}
		return value;
	}

	// 第index个父节点tagname的第childno个子节点的attname属性值
	public static String GetChildAttriValue(Document doc, String tagname,
			int index, int childno, String attname) {
		String value = "";
		NodeList nodes = doc.getElementsByTagName(tagname);
		Node node = nodes.item(index);
		NodeList chilenodes = node.getChildNodes();
		// System.out.println(chilenodes.getLength());
		Node child = chilenodes.item(2 * childno + 1);// 避开回车
		NamedNodeMap attributes = child.getAttributes();
		// System.out.println(child.getNodeName());
		for (int i = 0; i < attributes.getLength(); i++) {
			if (attributes.item(i).getNodeName() == attname) {
				value = attributes.item(i).getNodeValue();
				break;
			}
		}
		return value;
	}

	// 通过 uri 值找到所属 document 的 id
	public static String FindDocumentIdByUri(Document doc, String uri,
			int version) {
		String id = "";

		NodeList documentNodes = doc.getElementsByTagName("system")
				.item(version).getLastChild().getChildNodes();
		for (int i = 0; i < documentNodes.getLength(); i++) {
			Node docu = documentNodes.item(i);
			if (docu.hasChildNodes()) {
				// System.out.println(docu.getChildNodes().item(1).getTextContent());
				if (docu.getChildNodes().item(1).getTextContent().equals(uri)) {
					id = docu.getChildNodes().item(0).getTextContent();
					break;
				}
			}
		}
		return id;
	}


}
