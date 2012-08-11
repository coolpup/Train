package helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import app.Game;

public class XmlHelper {

    public static Document getDocument(File xml) throws ParserConfigurationException, SAXException,
            IOException {

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(xml);
        document.normalizeDocument();

        return document;
    }

    public static void saveDocument(Document document) throws TransformerConfigurationException,
            TransformerFactoryConfigurationError, TransformerFactoryConfigurationError,
            TransformerException, IOException {

        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        DOMSource dom = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(dom, sr);
        String string = sw.toString();
        FileWriter fw = new FileWriter(new File(Game.CONTENT_PATH + "config.xml"));
        fw.write(string);
        fw.close();
    }
}
