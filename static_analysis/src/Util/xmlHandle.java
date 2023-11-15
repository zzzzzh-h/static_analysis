package Util;

/*
这个是为了找到包含MAIN和LAUNCHER的activity的android name
可以通过调用findLaucherActivity方法获得返回值
即xmlTest.java是xmlHandle.java的完整版
 */
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.LinkedList;
import java.util.List;


public class xmlHandle {
    public static List<String> getAndroidName(String apk_path) throws IOException {
        System.out.println("------apk_path:"+apk_path);
        List<String> packageNames=new LinkedList<>();
        readapkxml r = new readapkxml();
        File xmlfile=r.read(apk_path);
        System.out.println("------apk_path:"+apk_path);
        System.out.println("-------xmlfile:"+xmlfile);
        List<String> ClassNames = new xmlHandle().findLaucherActivity(xmlfile.getPath());
        System.out.println("LAUNCHER and MAIN android name: ");
        for(String Classname:ClassNames){
            System.out.println("\t"+Classname);
        }
        String packageName=null;
        label:
        for (int i = 0; i < ClassNames.size(); i++) {
            packageName = ClassNames.get(i).substring(0, ClassNames.get(i).lastIndexOf("."));
            if (!packageNames.contains(packageName)) {
                for (int j = 0; j < packageNames.size(); j++) {
                    if (packageName.contains(packageNames.get(j))) {
                        break label;
                    } else if (packageNames.get(j).contains(packageName)) {
                        packageNames.remove(j);
                    }
                }
                packageNames.add(packageName);
                //System.out.println(packageName);
            }
        }
        return packageNames;
    }
    public String findPackage(Document doc) {
        Node node = doc.getFirstChild();
        NamedNodeMap attrs = node.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            if (attrs.item(i).getNodeName() == "package") {
                System.out.println("package name:   "+attrs.item(i).getNodeValue());
                return attrs.item(i).getNodeValue();
            }
        }
        return null;
    }
    public String findLaucherActivity_0(String filePath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // 创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();

            //加载xml文件
            Document doc = db.parse(filePath);
            Node tempactivity=null;
            Node activity = null;
            String sTem = "";
            NodeList categoryList = doc.getElementsByTagName("category");
            NodeList actionList=doc.getElementsByTagName("action");
            for(int i=0;i<actionList.getLength();i++){
                Node action=actionList.item(i);
                NamedNodeMap attrs=action.getAttributes();
                for(int j=0;j<attrs.getLength();j++){
                    if(attrs.item(j).getNodeName()=="android:name"){
                        if(attrs.item(j).getNodeValue().equals("android.intent.action.MAIN")){
                            tempactivity=action.getParentNode().getParentNode();
//                        NamedNodeMap acattrs=tempactivity.getAttributes();
//                        for(int k=0;k<acattrs.getLength();k++){
//                            System.out.println(acattrs.item(k));
//                        }
                            break;
                        }
                    }
                }
            }
            //上面是先找到一个action为MAIN的activity，然后把这个activity保存在tempactivity中

            for (int i = 0; i < categoryList.getLength(); i++) {
                Node category = categoryList.item(i);
                NamedNodeMap attrs = category.getAttributes();
                for (int j = 0; j < attrs.getLength(); j++) {
                    //System.out.println(attrs.item(j).toString());
                    if (attrs.item(j).getNodeName() == "android:name") {
                        if (attrs.item(j).getNodeValue().equals("android.intent.category.LAUNCHER")) {
                            //System.out.println(attrs.item(j).getNodeValue());
                            activity = category.getParentNode().getParentNode();
                            //寻找一个包含category为LAUNCHER的activity，保存在activity中
//                        NamedNodeMap acattrs=activity.getAttributes();
//                        for(int k=0;k<acattrs.getLength();k++){
//                            System.out.println(acattrs.item(k));
//                        }
                            if(tempactivity.equals(activity)){
                                //判断tempactivity和activity是否是同一个对象，是的话，就说明MAIN和LAUNCHER同时出现，就是我们要找的那个
                                System.out.println("equal");
                                break;
                            }else{
                                System.out.println("not equal");
                            }


                            //这里再加个循环找一下MAIN
                        }
                    }
                }
            }
            String packageName=findPackage(doc);
            if (activity != null) {
                NamedNodeMap attrs = activity.getAttributes();
                for (int j = 0; j < attrs.getLength(); j++) {
                    if (attrs.item(j).getNodeName() == "android:name") {
                        sTem = attrs.item(j).getNodeValue();
                        //System.out.println("android name:   "+sTem);
                        if(sTem.startsWith(".")){
                            sTem=packageName+sTem;
                        }
                    }
                }
            }
            System.out.println("main and launcher android name:  "+sTem);
            return sTem;
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    public List findLaucherActivity(String filePath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // 创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();

            //加载xml文件
            Document doc = db.parse(filePath);
            Node tempactivity=null;
            Node activity = null;
            List sTems = new LinkedList();//存放所有的既包含MAIN又包含LAUNCHER的 package的Android name
            String sTem="";//是sTems里面的值
            List<Node> MAINactivityList=new LinkedList();
            List<Node> LAUNCHERactivityList=new LinkedList();
            List<Node> MAINandLAUNCHERactivityList=new LinkedList<>();
            NodeList categoryList = doc.getElementsByTagName("category");
            NodeList actionList=doc.getElementsByTagName("action");
            for(int i=0;i<actionList.getLength();i++){
                Node action=actionList.item(i);
                NamedNodeMap actionAttributes=action.getAttributes();
                for(int j=0;j<actionAttributes.getLength();j++){
                    if(actionAttributes.item(j).getNodeName()=="android:name"){
                        if(actionAttributes.item(j).getNodeValue().equals("android.intent.action.MAIN")){
                            tempactivity=action.getParentNode().getParentNode();
                            MAINactivityList.add(tempactivity);
                        }
                    }
                }
            }
            //上面是先找到一个action为MAIN的activity，然后把这个activity保存在tempactivity中
            for (int m = 0; m < categoryList.getLength(); m++) {
                Node category = categoryList.item(m);
                NamedNodeMap categoryAttributes = category.getAttributes();
                for (int n = 0; n < categoryAttributes.getLength(); n++) {
                    //System.out.println(attrs.item(j).toString());
                    if (categoryAttributes.item(n).getNodeName() == "android:name") {
                        if (categoryAttributes.item(n).getNodeValue().equals("android.intent.category.LAUNCHER")) {
                            //System.out.println(attrs.item(j).getNodeValue());
                            activity = category.getParentNode().getParentNode();
                            LAUNCHERactivityList.add(activity);
                        }
                    }
                }
            }
            String packageName=findPackage(doc);

            for (int i = 0; i < MAINactivityList.size(); i++) {
                for (int j = 0; j < LAUNCHERactivityList.size(); j++) {
                    if(MAINactivityList.get(i).equals(LAUNCHERactivityList.get(j))){
                        activity=LAUNCHERactivityList.get(j);
                        if (activity != null) {
                            NamedNodeMap attrs = activity.getAttributes();
                            for (int k = 0; k < attrs.getLength(); k++) {
                                if (attrs.item(k).getNodeName() == "android:name") {
                                    sTem = attrs.item(k).getNodeValue();
                                    //System.out.println("android name:   "+sTem);
                                    if(sTem.startsWith(".")){
                                        sTem=packageName+sTem;
                                    }
                                    sTems.add(sTem);
                                    break;
                                }
                            }
                        }
                        System.out.println("main and launcher name:  "+sTem);
                    }
                }
            }
            return sTems;
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}

