package org.train.other;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.font.effects.Effect;
import org.train.app.Configuration;
import org.train.factory.FontFactory;
import org.train.helper.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ResourceManager {

    private Map<String, String> fonts;
    private FontFactory fontFactory;
    private Map<String, Image> images;
    private Configuration config;

    public ResourceManager(Configuration config, FontFactory fontFactory) {
        this.config = config;
        this.images = new HashMap<String, Image>();
        this.fontFactory = fontFactory;
        this.fonts = new HashMap<String, String>();
        this.loadResources();
    }

    private void loadResources() {
        try {
            Document document = null;
            document = XmlHelper.getDocument(new File(config.get("contentPath") + "resources.xml"));

            NodeList nodeList = document.getElementsByTagName("resource");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                NamedNodeMap nodeMap = node.getAttributes();
                switch (nodeMap.getNamedItem("type").getNodeValue()) {
                    case "image":
                        this.images.put(nodeMap.getNamedItem("id").getNodeValue(),
                                new Image(config.get("contentPath") + node.getTextContent()));
                        break;
                    case "font":
                        this.fonts.put(nodeMap.getNamedItem("id").getNodeValue(),
                                config.get("contentPath") + node.getTextContent());
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Font getFont(String type, int size, Effect effect) throws SlickException {
        return this.fontFactory.getFont(type, size, effect);
    }

    public Image getImage(String name) {
        return this.images.get(name);
    }
}
