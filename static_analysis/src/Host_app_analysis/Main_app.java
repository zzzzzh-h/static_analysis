package Host_app_analysis;

import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.io.*;
import java.util.*;

import static Host_app_analysis.Host_app_Util.*;
import static TPL_analysis.TPL_Util.checkFolder;

public class Main_app {

    public Main_app() throws IOException {
    }

    public static void main(String[] args) throws Exception {
        String app_path="D:\\software\\_download\\TPL_PP\\apk\\apk_ad\\apk";
        String root_save="D:\\software\\_download\\TPL_PP\\apk\\apk_ad\\results";
        String timeFile="D:\\software\\_download\\TPL_PP\\apk\\apk_ad\\results\\timeResult.txt";
        String timeFile_tplToApp="D:\\software\\_download\\TPL_PP\\apk\\apk_ad\\results\\timeResult_tplToApp.txt";

        File timeLog1=new File(timeFile);
        if (timeLog1.exists()) {
            timeLog1.delete();
            timeLog1=new File(timeFile);
        }
        File timeLog2=new File(timeFile_tplToApp);
        if (timeLog2.exists()) {
            timeLog2.delete();
            timeLog2=new File(timeFile_tplToApp);
        }
        File app_list = new File(app_path);
        ArrayList<String> sinks = getSinks("D:\\software\\idea\\ideaProject\\binary_code_analysis\\static_analysis\\src\\SourcesAndSinksRaw.txt");
        ArrayList<String> sources = ReadSources("D:\\software\\idea\\ideaProject\\binary_code_analysis\\static_analysis\\src\\SourceAndSink3.txt");


        for(File app : Objects.requireNonNull(app_list.listFiles())){
            System.out.println("--------------------------------------------------");
            long startTime = System.currentTimeMillis();
            HashMap<String, ArrayList<String>> results_dic = new HashMap<String, ArrayList<String>>();
            HashMap<String, ArrayList<String>> results_dic_tplToApp = new HashMap<String, ArrayList<String>>();
            System.out.println("app: "+app.getName());
            String out_put=root_save+"/"+app.getName();
            checkFolder(out_put);
            String save_path = out_put + "/" + app.getName();
            String save_path_tplToApp=save_path+"_tplToApp";
            String results_file = save_path + ".txt";//分析app到tpl的数据流动结果的文件
            String results_file_tplToApp=save_path_tplToApp+".txt";
            System.out.println("result_file:  "+results_file);
            System.out.println("result_file_tplToApp:  "+results_file_tplToApp);


//            File kz_file = new File(results_file);
//            if (kz_file.exists()) {
//                //continue;
//                kz_file.delete();
//                kz_file=new File(results_file);
//            } else {
//                kz_file.createNewFile();
//            }
//            try {
//                if (app.toString().endsWith(".apk")) {
//                    System.out.println("kz_apk:" + app.toString());
//                    results_dic.clear();
//                    results_dic = (HashMap<String, ArrayList<String>>) getHostAppDataFlowDestination(app.toString(), sinks,sources);
//                    System.out.println("result_dic 1 over");
//                    System.out.println("result_dic size:  "+results_dic.size());
//                }
//            } catch (Exception e) {
//                //System.out.println(results_dic.size());
//                System.out.println(e.toString());
//                e.printStackTrace();
//                System.out.println("error in line 50:" + e.getMessage());
//                continue;
//            }
//            BufferedWriter data_flow_writer = new BufferedWriter(
//                    new FileWriter(results_file));
//            List<String> packageName=getPackageName(app.getPath());
//            String package_name=null;
//            if(packageName.size()==0){
//                String[] strs=package_name.split("\\.");
//
//                package_name=strs[0]+"."+strs[1]+".";
//                data_flow_writer.write(" ### " + "package Name: "+package_name+ " ### "+"\n");
//            }else if(packageName.size()==2){
//                System.out.println("packageName:  "+packageName);
//                System.out.println("begin print");
//
//                data_flow_writer.write(" ### " + "package Name: "+packageName.get(0) + " ### "+"\n");
//                data_flow_writer.write(" ### " + "package Name: "+packageName.get(1)+ " ### "+"\n");
//            }else if(packageName.size()==1){
//                data_flow_writer.write(" ### " + "package Name: "+packageName.get(0) + " ### "+"\n");
//            }
//            data_flow_writer.write(" ### " + "package Name: "+package_name+ " ### "+"\n");
//
//            if (results_dic == null) {
//                data_flow_writer.write("\t" + "null" + "\n");
//                continue;
//            }
//            for (String key : results_dic.keySet()) {
//                ArrayList<String> tmp = results_dic.get(key);
//                if (tmp.size() > 0) {
//                    data_flow_writer.write(key + "\n");
//                    for (String k_type : tmp) {
//                        data_flow_writer.write("\t" + k_type + "\n");
//                    }
//                }
//            }
//            //long endTime = System.currentTimeMillis();
//            //long seconds = (endTime - startTime) / 1000L;
//            //writerow(timeLog,app.getName()+" has run "+seconds);
//            System.out.println("end print");
//            System.out.println("---------------------------------------------------------------------");
//            data_flow_writer.close();
//            System.out.println(app.getName());


//            //进行从app到tpl的数据流分析
//            appTotpl(results_file,app,results_dic,sinks,sources);
//            long endTime1 = System.currentTimeMillis();
//            long seconds1 = (endTime1 - startTime) / 1000L;
//            writerow(timeLog1,app.getName()+" has run "+seconds1);
//            System.out.println("appname1:"+app.getName());
//
//            tplToApp(results_file_tplToApp,app,results_dic_tplToApp,sinks,sources);
//            long endTime2 = System.currentTimeMillis();
//            long seconds2 = (endTime2 - startTime) / 1000L;
//            writerow(timeLog2,app.getName()+" has run "+seconds2);
//
            //合并appTotpl和tplToApp
            writeDataFlow(results_file,results_file_tplToApp,app,results_dic,results_dic_tplToApp,sinks,sources);
        }



    }

    public static void writeDataFlow(String results_file,String results_file_tplToApp,File app,HashMap<String, ArrayList<String>> results_dic,HashMap<String, ArrayList<String>> results_dic_tplToApp,ArrayList<String> sinks,ArrayList<String> sources) throws IOException, XmlPullParserException{
        //获取包名
        List<String> packageName=getPackageName(app.toString());
        String package_name=null;
        if(packageName.size()==1){
            package_name=packageName.get(0);
        }else{
            package_name=packageName.get(1);
        }

        File kz_file = new File(results_file);
        if (kz_file.exists()) {
            kz_file.delete();
            kz_file=new File(results_file);
        } else {
            kz_file.createNewFile();
        }

        try {
            if (app.toString().endsWith(".apk")) {
                System.out.println("kz_apk:" + app.toString());
                results_dic.clear();
                Object[] results_dics= getDataFlow(app.toString(),sinks,sources,package_name);
                results_dic= (HashMap<String, ArrayList<String>>) results_dics[0];
                results_dic_tplToApp= (HashMap<String, ArrayList<String>>) results_dics[1];
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            System.out.println("error in line 50:" + e.getMessage());
            return;
        }
//        //写第一个文件
        BufferedWriter data_flow_writer = new BufferedWriter(
                new FileWriter(results_file));
        if(packageName.size()==0){
            String[] strs=package_name.split("\\.");

            package_name=strs[0]+"."+strs[1]+".";
            data_flow_writer.write(" ### " + "package Name: "+package_name+ " ### "+"\n");
        }else if(packageName.size()==2){
            System.out.println("packageName:  "+packageName);
            System.out.println("begin print");

            data_flow_writer.write(" ### " + "package Name: "+packageName.get(0) + " ### "+"\n");
            data_flow_writer.write(" ### " + "package Name: "+packageName.get(1)+ " ### "+"\n");
        }else if(packageName.size()==1){
            data_flow_writer.write(" ### " + "package Name: "+packageName.get(0) + " ### "+"\n");
        }
        data_flow_writer.write(" ### " + "package Name: "+package_name+ " ### "+"\n");

        if (results_dic == null) {
            data_flow_writer.write("\t" + "null" + "\n");
            return;
        }
        for (String key : results_dic.keySet()) {
            ArrayList<String> tmp = results_dic.get(key);
            if (tmp.size() > 0) {
                data_flow_writer.write(key + "\n");
                for (String k_type : tmp) {
                    data_flow_writer.write("\t" + k_type + "\n");
                }
            }
        }
        data_flow_writer.close();
        //写第二个文件
        BufferedWriter data_flow_writer2 = new BufferedWriter(
                new FileWriter(results_file_tplToApp));
        List<String> packageName2=getPackageName(app.getPath());
        String package_name2=null;
        if(packageName2.size()==0){
            String[] strs=package_name2.split("\\.");

            package_name2=strs[0]+"."+strs[1]+".";
            data_flow_writer2.write(" ### " + "package Name: "+package_name2+ " ### "+"\n");
        }else if(packageName2.size()==2){
            System.out.println("packageName:  "+packageName2);
            System.out.println("begin print");

            data_flow_writer2.write(" ### " + "package Name: "+packageName2.get(0) + " ### "+"\n");
            data_flow_writer2.write(" ### " + "package Name: "+packageName2.get(1)+ " ### "+"\n");
        }else if(packageName2.size()==1){
            data_flow_writer2.write(" ### " + "package Name: "+packageName2.get(0) + " ### "+"\n");
        }
        data_flow_writer2.write(" ### " + "package Name: "+package_name2+ " ### "+"\n");

        if (results_dic_tplToApp == null) {
            data_flow_writer2.write("\t" + "null" + "\n");
            return;
        }
        for (String key : results_dic_tplToApp.keySet()) {
            ArrayList<String> tmp = results_dic_tplToApp.get(key);
            if (tmp.size() > 0) {
                data_flow_writer2.write(key + "\n");
                for (String k_type : tmp) {
                    data_flow_writer2.write("\t" + k_type + "\n");
                }
            }
        }
        System.out.println("end print");
        System.out.println("---------------------------------------------------------------------");
        data_flow_writer2.close();
        System.out.println(app.getName());
    }
    public static void appTotpl(String results_file,File app,HashMap<String, ArrayList<String>> results_dic,ArrayList<String> sinks,ArrayList<String> sources) throws IOException, XmlPullParserException {
        File kz_file = new File(results_file);
        if (kz_file.exists()) {
            //continue;
            kz_file.delete();
            kz_file=new File(results_file);
        } else {
            kz_file.createNewFile();
        }
        try {
            if (app.toString().endsWith(".apk")) {
                System.out.println("kz_apk:" + app.toString());
                results_dic.clear();
                results_dic = (HashMap<String, ArrayList<String>>) getHostAppDataFlowDestination(app.toString(), sinks,sources);
                System.out.println("result_dic 1 over");
                System.out.println("result_dic size:  "+results_dic.size());
            }
        } catch (Exception e) {
            //System.out.println(results_dic.size());
            System.out.println(e.toString());
            e.printStackTrace();
            System.out.println("error in line 50:" + e.getMessage());
            return;
        }
        BufferedWriter data_flow_writer = new BufferedWriter(
                new FileWriter(results_file));
        List<String> packageName=getPackageName(app.getPath());
        String package_name=null;
        if(packageName.size()==0){
            String[] strs=package_name.split("\\.");

            package_name=strs[0]+"."+strs[1]+".";
            data_flow_writer.write(" ### " + "package Name: "+package_name+ " ### "+"\n");
        }else if(packageName.size()==2){
            System.out.println("packageName:  "+packageName);
            System.out.println("begin print");

            data_flow_writer.write(" ### " + "package Name: "+packageName.get(0) + " ### "+"\n");
            data_flow_writer.write(" ### " + "package Name: "+packageName.get(1)+ " ### "+"\n");
        }else if(packageName.size()==1){
            data_flow_writer.write(" ### " + "package Name: "+packageName.get(0) + " ### "+"\n");
        }
        data_flow_writer.write(" ### " + "package Name: "+package_name+ " ### "+"\n");

        if (results_dic == null) {
            data_flow_writer.write("\t" + "null" + "\n");
            return;
        }
        for (String key : results_dic.keySet()) {
            ArrayList<String> tmp = results_dic.get(key);
            if (tmp.size() > 0) {
                data_flow_writer.write(key + "\n");
                for (String k_type : tmp) {
                    data_flow_writer.write("\t" + k_type + "\n");
                }
            }
        }
        System.out.println("end print");
        System.out.println("---------------------------------------------------------------------");
        data_flow_writer.close();
        System.out.println(app.getName());
    }
    public static void tplToApp(String results_file,File app,HashMap<String, ArrayList<String>> results_dic,ArrayList<String> sinks,ArrayList<String> sources) throws IOException, XmlPullParserException {
        System.out.println("appname2:"+app.getName());
        File kz_file = new File(results_file);
        if (kz_file.exists()) {
            //continue;
            kz_file.delete();
            kz_file=new File(results_file);
        } else {
            kz_file.createNewFile();
        }
        try {
            if (app.toString().endsWith(".apk")) {
                System.out.println("kz_apk:" + app.toString());
                results_dic.clear();
                results_dic = (HashMap<String, ArrayList<String>>) getTplDataFlowDestination(app.toString(), sinks,sources);
                System.out.println("result_dic 1 over");
                System.out.println("result_dic size:  "+results_dic.size());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            System.out.println("error in line 50:" + e.getMessage());
            return;
        }
        BufferedWriter data_flow_writer = new BufferedWriter(
                new FileWriter(results_file));
        List<String> packageName=getPackageName(app.getPath());
        String package_name=null;
        if(packageName.size()==0){
            String[] strs=package_name.split("\\.");

            package_name=strs[0]+"."+strs[1]+".";
            data_flow_writer.write(" ### " + "package Name: "+package_name+ " ### "+"\n");
        }else if(packageName.size()==2){
            System.out.println("packageName:  "+packageName);
            System.out.println("begin print");

            data_flow_writer.write(" ### " + "package Name: "+packageName.get(0) + " ### "+"\n");
            data_flow_writer.write(" ### " + "package Name: "+packageName.get(1)+ " ### "+"\n");
        }else if(packageName.size()==1){
            data_flow_writer.write(" ### " + "package Name: "+packageName.get(0) + " ### "+"\n");
        }
        data_flow_writer.write(" ### " + "package Name: "+package_name+ " ### "+"\n");

        if (results_dic == null) {
            data_flow_writer.write("\t" + "null" + "\n");
            return;
        }
        for (String key : results_dic.keySet()) {
            ArrayList<String> tmp = results_dic.get(key);
            if (tmp.size() > 0) {
                data_flow_writer.write(key + "\n");
                for (String k_type : tmp) {
                    data_flow_writer.write("\t" + k_type + "\n");
                }
            }
        }
        System.out.println("end print");
        System.out.println("---------------------------------------------------------------------");
        data_flow_writer.close();
        System.out.println(app.getName());
    }
    private static void writerow (File ofile, String s) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(ofile,true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(s);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
