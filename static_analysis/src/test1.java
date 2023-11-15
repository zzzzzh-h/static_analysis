import org.xmlpull.v1.XmlPullParserException;
import soot.Unit;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static Util.xmlHandle.getAndroidName;

public class test1 {
    public static void main(String[] args) throws IOException, XmlPullParserException {
//        File file =new File("D:\\software\\_download\\TPL_PP\\apk\\test\\app\\com.cricbuzz.android.apk");
//        File tempFile=CopyApp(file);
//        //Unit unit="virtualinvoke $r1.<android.os.Bundle: void putBundle(java.lang.String,android.os.Bundle)>(\"KEY_COMPONENT_ACTIVITY_PENDING_RESULT\", $r6)";
//        if (tempFile.delete()) {
//            System.out.println("文件删除成功！");
//        } else {
//            System.out.println("文件删除失败！");
//        }

        //----------------------------------
//        String apk="D:\\software\\_download\\TPL_PP\\apk\\apk_50\\apk\\com.TWCableTV.apk";
//        String apk0="D:\\software\\_download\\TPL_PP\\apk\\apk_50\\apk0\\com.TWCableTV.apk";
//        List<String> packagenames=getPackageName0(apk,apk0);
//        System.out.println("-------------");
//        for(String p:packagenames){
//            System.out.println(p);
//        }

        //-------------------------------------
//        String jar="D:\\software\\_download\\TPL_PP\\tpl\\test\\test\\Alipay.jar";
//        System.out.println(getjarPackageName0(jar));
        //测试方法findLaucher
        String apkPath="D:\\software\\_download\\TPL_PP\\apk\\test\\app\\demo.apk";
        ProcessManifest manifest=new ProcessManifest(apkPath);
        Set<AXmlNode> nodes=manifest.getLaunchableActivities();
        System.out.println("num:"+nodes.size());
        for(AXmlNode node:nodes){
            System.out.println(node.getTag());
            System.out.println(node.getAttribute("name"));
            System.out.println(node.getAttribute("name").getValue());
        }
    }
    public void findLaucher(){

    }
    public static File CopyApp(File sourceFile){
        File parent=new File(sourceFile.getParent());
        System.out.println("file parent:"+parent.toString());
        String target=parent.getParent()+"\\"+sourceFile.getName().substring(0,sourceFile.getName().length()-4)+"_temp.apk";
        System.out.println("target:"+target);
        File targetFile = new File(target);

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile;
    }
    public static List<String> getPackageName0(String apk_path, String temp_path,String temppath) throws IOException, XmlPullParserException {
        List<String> results=new LinkedList<String>();
        ProcessManifest manifest = null;
        manifest = new ProcessManifest(apk_path);
        String package_name = manifest.getPackageName();
        System.out.println("### package name:  "+package_name);
        results.add(package_name);
        System.out.println("app_path:  "+temp_path);
        List<String> androidNames=getAndroidName(temp_path);
        System.out.println("android names");
        System.out.println();
        for(String packageName:androidNames){
            System.out.println("\t"+packageName);
        }
        if(androidNames.size()==0){
            String[] strs=package_name.split("\\.");

            package_name=strs[0]+"."+strs[1]+".";
        }else{
            String[] strs=androidNames.get(0).split("\\.");

            package_name=strs[0]+"."+strs[1]+".";
        }

//        Scanner sc=new Scanner(System.in);
//        System.out.println("please input the package name:");
//        package_name=sc.next();
        System.out.println("package name: "+package_name);
        if(androidNames.size()!=0){
            results.add(androidNames.get(0));
        }

        results.add(package_name);
        return results;
    }
    public static String getjarPackageName0(String jar_path) throws IOException, XmlPullParserException {
        ProcessManifest manifest = null;
        manifest = new ProcessManifest(jar_path);
        String package_name = manifest.getPackageName();
        System.out.println("### package name:  "+package_name);

        return package_name;
    }
}
