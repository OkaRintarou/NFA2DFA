import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 自动机类
 */
public class NodeList {
    /**
     * 自动机的状态集
     */
    ArrayList<Node> nodes = new ArrayList<>();
    /**
     * 自动机的输入集
     */
    ArrayList<String> charSet = new ArrayList<>();

    /**
     * 判断一个状态是否在自动机的状态集中出现过
     *
     * @param name  待判断状态
     * @param nodes 状态集
     * @return boolean
     */
    public static boolean notRepetitive(String name, ArrayList<Node> nodes) {
        for (var node : nodes) {
            if (node.getName().equals(name))
                return false;
        }
        return true;
    }

    /**
     * 将含空串的NFA转换为不含空串的NFA<br/>
     * 可以对不含空串的NFA操作，将没有变化
     */
    public void parseNFAWithoutE() {
        //遍历状态集
        for (var node : this.nodes) {
            TreeSet<String> nameSet = new TreeSet<>(node.getNameSet());
            //获取e-closure
            boolean flag;
            Node tmp = node;
            TreeSet<String> tmpSet = new TreeSet<>(tmp.getE());
            do {
                flag = nameSet.addAll(tmpSet);
                tmpSet.clear();
                for (var state : nameSet) {
                    tmp = findNode(state);
                    tmpSet.addAll(tmp.getE());
                }
            } while (flag);
            //遍历输入集
            for (var key : this.charSet) {
                //获取e-closure通过输入可到达的状态
                TreeSet<String> allDestiny = new TreeSet<>();
                for (var name : nameSet) {
                    Node find = findNode(name);
                    String destiny = find.getChangeSet().get(key);
                    if (destiny != null) {
                        String[] destinies = destiny.split(",");
                        allDestiny.addAll(Arrays.asList(destinies));
                    }
                }
                //对可到达状态获取e-closure
                tmpSet.clear();
                TreeSet<String> nameSet_2 = new TreeSet<>();
                for (var name : allDestiny) {
                    Node find = findNode(name);
                    tmpSet.addAll(find.getE());
                }
                do {
                    flag = nameSet_2.addAll(tmpSet);
                    tmpSet.clear();
                    for (var state : nameSet_2) {
                        tmp = findNode(state);
                        tmpSet.addAll(tmp.getE());
                    }
                } while (flag);
                //合并集合，并更新转换函数
                allDestiny.addAll(nameSet_2);
                if (allDestiny.size() != 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (var item : allDestiny) {
                        stringBuilder.append(item).append(",");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    node.addChange(key, stringBuilder.toString());
                }
            }
        }
    }

    /**
     * 从xml文档读入NFA
     *
     * @param file 文件名（需要包含位置）
     * @return 是否运行成功
     */
    public boolean loadFromXml(String file) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(file);
            Element rootElement = document.getRootElement();
            String str_charSet = rootElement.elementTextTrim("charSet");
            String[] strings = str_charSet.split(",");
            this.charSet.addAll(Arrays.asList(strings));
            List<Element> nodeElements = rootElement.elements("node");
            for (var nodeEle : nodeElements) {
                Node node = new Node();
                node.addName(nodeEle.elementTextTrim("name"));
                node.generateName();
                node.setType(nodeEle.elementTextTrim("type"));
                List<Element> changeList = nodeEle.elements("change");
                for (var changeEle : changeList) {
                    String charSet = changeEle.elementTextTrim("charSet");
                    String next = changeEle.elementTextTrim("next");
                    node.addChange(charSet, next);
                }
                String e = nodeEle.elementTextTrim("e");
                if (e != null) {
                    String[] es = e.split(",");
                    for (var item : es) {
                        node.addE(item);
                    }
                }
                nodes.add(node);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 寻找起始状态
     *
     * @return 起始状态
     */
    public Node findSTART() {
        for (var node : nodes) {
            if (node.isSTART()) {
                return node;
            }
        }
        return null;
    }

    /**
     * 添加状态
     *
     * @param node 状态
     */
    public void add(Node node) {
        this.nodes.add(node);
    }

    /**
     * 添加输入集
     *
     * @param charSet 输入集
     */
    public void addCharSet(ArrayList<String> charSet) {
        this.charSet = charSet;
    }

    /**
     * 将NFA转换为DFA<br/>
     * 可对DFA操作，无变化
     *
     * @return DFA
     */
    public NodeList parseDFA() {
        NodeList DFA = new NodeList();
        //添加输入集
        DFA.addCharSet(this.charSet);
        //获取起始状态
        String startName = this.findSTART().getName();
        Node start = new Node();
        start.addName(startName);
        start.generateName();
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(start);
        //该循环中的状态均只有状态名和子状态集合
        //获取状态类型和转换函数集后将从集合中删除
        //转换函数中出现新状态将加入状态集合
        //该状态集合不含元素时退出循环，表示转换完成
        while (!nodes.isEmpty()) {
            Node node = nodes.get(0);
            node.setType(this.findType(node));
            for (var key : this.charSet) {
                Node tmp = new Node();
                for (var name : node.getNameSet()) {
                    Node find = this.findNode(name);
                    String destiny = find.getChangeSet().get(key);
                    if (destiny != null) {
                        String[] destinies = destiny.split(",");
                        for (var des : destinies) {
                            tmp.addName(des);
                        }
                    }
                }
                if (tmp.getNameSet().size() != 0) {
                    tmp.generateName();
                    node.addChange(key, tmp.getName());
                    if (notRepetitive(tmp.getName(), nodes) && notRepetitive(tmp.getName(), DFA.nodes)) {
                        nodes.add(tmp);
                    }
                }
            }
            DFA.add(node);
            nodes.remove(0);
        }
        return DFA;
    }

    /**
     * 查找状态
     *
     * @param name 状态名
     * @return 找到的状态，未找到返回null
     */
    public Node findNode(String name) {
        for (var node : this.nodes) {
            if (node.getName().equals(name))
                return node;
        }
        return null;
    }

    /**
     * 判断状态类型
     *
     * @param node 状态
     * @return 状态类型
     */
    public NodeType findType(Node node) {
        HashSet<NodeType> nodeTypes = new HashSet<>();
        for (var name : node.getNameSet()) {
            NodeType nodeType = this.findType(name);
            switch (nodeType) {
                case START -> nodeTypes.add(NodeType.START);
                case END -> nodeTypes.add(NodeType.END);
                case START_END -> nodeTypes.add(NodeType.START_END);
                case NORMAL -> nodeTypes.add(NodeType.NORMAL);
            }
        }
        if (nodeTypes.size() == 1) {
            return (NodeType) nodeTypes.toArray()[0];
        } else if (nodeTypes.contains(NodeType.END) || nodeTypes.contains(NodeType.START_END))
            return NodeType.END;
        else
            return NodeType.NORMAL;
    }

    /**
     * 获取状态类型，需要该状态已经分配类型
     *
     * @param name 状态名
     * @return 状态类型
     */
    public NodeType findType(String name) {
        Node node = findNode(name);
        return NodeType.valueOf(node.getType());
    }

    /**
     * 将转换后的DFA写入xml文档
     *
     * @param file 文件名（需包含位置）
     * @return 是否运行成功
     */
    public boolean generateXML(String file) {
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("nodes");
            StringBuilder charSet_str = new StringBuilder();
            for (var str : this.charSet) {
                charSet_str.append(str).append(",");
            }
            charSet_str.deleteCharAt(charSet_str.length() - 1);
            root.addElement("charSet").setText(charSet_str.toString());
            for (var node : nodes) {
                Element nodeEle = root.addElement("node");
                nodeEle.addElement("name").setText(node.getName());
                nodeEle.addElement("type").setText(node.getType());
                for (Map.Entry<String, String> change : node.changeSet.entrySet()) {
                    Element changeSet = nodeEle.addElement("change");
                    changeSet.addElement("charSet").setText(change.getKey());
                    changeSet.addElement("next").setText(change.getValue());
                }
            }
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file), outputFormat);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
