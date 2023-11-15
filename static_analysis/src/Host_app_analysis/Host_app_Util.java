package Host_app_analysis;

import Data_flow_analysis.InterProcedureVariableAnalysis;
import org.xmlpull.v1.XmlPullParserException;
import soot.*;
import soot.jimple.*;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.infoflow.cmd.Flowdroid;
import soot.jimple.infoflow.memory.FlowDroidTimeoutWatcher;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.internal.*;
import soot.util.Chain;

import java.io.*;
import java.util.*;

import static TPL_analysis.KzConstant.*;
import static Util.xmlHandle.getAndroidName;

public class Host_app_Util {
    static String androidJars = "D:/software/android/android-sdk/platforms/android-29/android.jar";
    public static ArrayList<String> getSinks(String file_name) throws IOException {
        ArrayList<String> sinks = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file_name));
        String tmp;
        while ((tmp = br.readLine()) != null) {
            if (tmp.endsWith(" -> _SINK_")) {
                if(tmp.startsWith("%")){
                    tmp=tmp.replace("%","");
                }
                String sink = tmp.replace(" -> _SINK_", "");
                sinks.add(sink);
            }
            if (tmp.endsWith(" -> _BOTH_")) {
                if(tmp.startsWith("%")){
                    tmp=tmp.replace("%","");
                }
                String sink = tmp.replace(" -> _BOTH_", "");
                sinks.add(sink);
            }
        }

        return sinks;
    }
    public static ArrayList<String> ReadSources(String file_name) throws IOException {
        ArrayList<String> sources = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file_name));
        String tmp;
        while ((tmp = br.readLine()) != null) {
            if (tmp.startsWith("#")) {
                continue;
            }
            if(!sources.contains(tmp)){
                sources.add(tmp);
            }
        }

        return sources;
    }
    public static List<String> getPackageName(String apk_path) throws IOException, XmlPullParserException {
        List<String> results=new LinkedList<String>();
        ProcessManifest manifest = null;
        manifest = new ProcessManifest(apk_path);
        String package_name = manifest.getPackageName();
        System.out.println("### package name:  "+package_name);

        String app_path=apk_path.replace("apk_ad"+"\\"+"apk","apk_ad"+"\\"+"apk0");

        System.out.println("app_path:  "+app_path);
        List<String> packageNames=getAndroidName(app_path);

        System.out.println("android names");
        for(String packageName:packageNames){
            System.out.println("\t"+packageName);
        }
        if(packageNames.size()==0){
            String[] strs=package_name.split("\\.");

            package_name=strs[0]+"."+strs[1]+".";
        }else{
            String[] strs=packageNames.get(0).split("\\.");

            package_name=strs[0]+"."+strs[1]+".";
        }
//        Scanner sc=new Scanner(System.in);
//        System.out.println("please input the package name:");
//        package_name=sc.next();
        System.out.println("package name: "+package_name);
        if(packageNames.size()!=0){
            results.add(packageNames.get(0));
        }

        results.add(package_name);
        return results;
    }

    public static List<Local> getLocal_def(Unit unit){
        List<Local> def_Local=new LinkedList<Local>();
        try{
            if(unit instanceof IfStmt){
                if(((IfStmt) unit).containsInvokeExpr()){
                    InvokeExpr expr=((InvokeStmt) unit).getInvokeExpr();
                    List<Value> values=expr.getArgs();
                    for(Value value:values){
                        if(value instanceof Local){
                            def_Local.add((Local)value);

                        }
                    }
                }
            }
            else if(unit instanceof IdentityStmt){
                if(((IdentityStmt) unit).containsInvokeExpr()){
                    InvokeExpr expr=((InvokeStmt) unit).getInvokeExpr();
                    List<Value> values=expr.getArgs();
                    for(Value value:values){
                        if(value instanceof Local){
                            def_Local.add((Local)value);
                        }
                    }
                }

            }else if (unit instanceof InvokeStmt) {
                InvokeExpr expr=((InvokeStmt) unit).getInvokeExpr();
                List<Value> values=expr.getArgs();
                for(Value value:values){
                    if(value instanceof Local){
                        def_Local.add((Local)value);
                    }
                }
                //如果是invoke语句，tar_local等于右边
//                for (ValueBox box : unit.getUseBoxes()) {
//                    if (box instanceof JimpleLocalBox) {
//                        def_Local.add((Local) box.getValue());
//                    }
//                }
            }else if(unit instanceof AssignStmt){
                if(((AssignStmt) unit).containsInvokeExpr()){
                    InvokeExpr expr=((AssignStmt) unit).getInvokeExpr();
                    List<Value> values=expr.getArgs();
                    for(Value value:values){
                        if(value instanceof Local){
                            def_Local.add((Local)value);
                        }
                    }
                }else{
                    if (((AssignStmt) unit).getLeftOpBox().getValue() instanceof StaticFieldRef ||
                            ((AssignStmt) unit).getLeftOpBox() instanceof JInstanceFieldRef) {
                        def_Local.add((Local) ((AssignStmt) unit).getRightOpBox().getValue());
                    }

                }
            }else if(unit instanceof JReturnStmt){
                List<ValueBox> valueboxes=unit.getUseBoxes();
                Value value=valueboxes.get(0).getValue();
                def_Local.add((Local)value);
            }else if(unit instanceof JGotoStmt){

            }else{
                def_Local.add((Local) ((JAssignStmt) unit).getRightOpBox().getValue());
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("-------"+e.toString());
            System.out.println(unit);
            System.out.println("######################");
        }finally {
            return def_Local;
        }
    }
    public static Local getLocal0(Unit unit){
        Local tar_local = null;
        if (unit instanceof IfStmt) {
            //continue;
            return null;
        }
        if (unit instanceof IdentityStmt) {
            tar_local = (Local) ((JIdentityStmt) unit).leftBox.getValue();
        } else if (unit instanceof InvokeStmt) {
            //如果是invoke语句，tar_local等于右边
            for (ValueBox box : unit.getUseBoxes()) {
                if (box instanceof JimpleLocalBox) {
                    tar_local = (Local) box.getValue();
                    break;
                }
            }
        } else if (unit instanceof AssignStmt) {
            if (((AssignStmt) unit).getLeftOpBox().getValue() instanceof StaticFieldRef ||
                    ((AssignStmt) unit).getLeftOpBox() instanceof JInstanceFieldRef) {
                tar_local = (Local) ((AssignStmt) unit).getRightOpBox().getValue();
            }
        } else if(unit instanceof ReturnStmt){
            tar_local=(Local)((ReturnStmt)unit).getOpBox().getValue();
        }else {
            tar_local = (Local) ((JAssignStmt) unit).getLeftOpBox().getValue();
        }
        return tar_local;
    }
    public static Local getLocal(Unit unit){
        Local tar_local = null;
        if (unit instanceof IfStmt) {
            //continue;
            return null;
        }
        if (unit instanceof IdentityStmt) {
            tar_local = (Local) ((JIdentityStmt) unit).leftBox.getValue();
        } else if (unit instanceof InvokeStmt) {
            //如果是invoke语句，tar_local等于右边
            for (ValueBox box : unit.getUseBoxes()) {
                if (box instanceof JimpleLocalBox) {
                    tar_local = (Local) box.getValue();
                    break;
                }
            }
        } else if (unit instanceof AssignStmt) {
            if( ((AssignStmt) unit).getLeftOpBox() instanceof JInstanceFieldRef) {
                AssignStmt a=(AssignStmt) unit;
                tar_local=(Local)a.getRightOpBox().getValue();
                //tar_local = (Local) ((AssignStmt) unit).getRightOpBox().getValue();

            }else  if ( ((AssignStmt) unit).getLeftOpBox().getValue() instanceof VariableBox){
                tar_local = (Local) ((AssignStmt) unit).getLeftOpBox().getValue();
            }else  if ( ((AssignStmt) unit).getLeftOpBox() instanceof VariableBox){
                if(((AssignStmt) unit).getLeftOpBox().getValue() instanceof JInstanceFieldRef){
                    return null;
                }else if(((AssignStmt) unit).getLeftOpBox().getValue() instanceof JArrayRef){
                    JArrayRef leftOp=(JArrayRef)((AssignStmt) unit).getLeftOpBox().getValue();
                    tar_local=(Local)leftOp.getBaseBox().getValue();
                    //防止这种情况：
                    //$r2[1] = "AmazonCustomerByEmail"
                }else{
                    tar_local = (Local) ((AssignStmt) unit).getLeftOpBox().getValue();
                }
            }
        } else if(unit instanceof ReturnStmt){
            tar_local=(Local)((ReturnStmt)unit).getOpBox().getValue();
        }else {
            tar_local = (Local) ((JAssignStmt) unit).getLeftOpBox().getValue();
        }
        return tar_local;
    }
    public static Object getHostAppDataFlowDestination(String apk_path, ArrayList<String> sinks, ArrayList<String> sources) throws Exception {
        //从app找到tpl的数据传输路径

        //遍历每个unit，找到包含pi或者tpl的那些语句，提出它的valuebox，找它的usebox，然后把usebox中包含sink的，进行打印
        HashMap<String, ArrayList<String>> results_dic = new HashMap<String, ArrayList<String>>();
        boolean flag_pi = false;
        // init flowdroid
        FlowDroidEnvironment.reset();
        FlowDroidEnvironment.init(apk_path, androidJars);
        //初始化运行的参数
        FlowDroidTimeoutWatcher.timeoutFlag = false;
        int flodroidArgsSize = FlowDroidEnvironment.args.size();
        String[] flowdraoidArgs = new String[flodroidArgsSize];
        FlowDroidEnvironment.args.toArray(flowdraoidArgs);
        ArrayList<InfoflowResults> flowdroidResults = Flowdroid.analyze(flowdraoidArgs);

        if (Flowdroid.exceptionFlag) {
            System.out.println("exceptionFlag");
            return null;
        }
        List<String> name_result=getPackageName(apk_path);
        String package_name=null;
        if(name_result.size()==1){
            package_name=name_result.get(0);
        }else{
            package_name=name_result.get(1);
        }

        List<String> dataList=new LinkedList<String>();
        List<String> data2sinkList=new LinkedList<String>();
        //遍历
        Chain<SootClass> sootClasses=Scene.v().getClasses();
        SootClass sootClass=sootClasses.getFirst();
        int number=0;
        while(sootClass!=sootClasses.getLast()){
            List<SootMethod> sootMethods= sootClass.getMethods();
            for(int j=0;j<sootMethods.size();j++){
                SootMethod sootMethod=sootMethods.get(j);
                Body body;
                try{
                    body=sootMethod.retrieveActiveBody();
                    for(Unit unit:body.getUnits()){
                        flag_pi = false;
                        String stmt_raw = unit.toString();
                        String stmt = stmt_raw.toLowerCase();
                        ArrayList<String> pi = new ArrayList<>();
                        String pi_data=null;
                        String sig=sootMethod.getSignature();
                        for (String pi_pack : sources) {
                            if (stmt.contains(pi_pack.toLowerCase())) {
                                //System.out.println("pi");
                                flag_pi = true;
                                pi_data=pi_pack;
                                if (pi.contains(0+pi_pack + ":" + stmt_raw+" ---- in method: "+sootMethod.getSignature())) {
                                    continue;
                                }
                                pi.add(0+pi_pack + ":" + stmt_raw+" ---- in method: "+sootMethod.getSignature());
                                if(!dataList.contains(pi_pack)){
                                    dataList.add(pi_pack);
                                }
                            }
                        }

                        if (flag_pi) {
                            boolean flag_exist=false;
                            Local tar_local = getLocal(unit);
                            if(tar_local==null){
                                continue;
                            }
                            List<String> results = InterProcedureVariableAnalysis.findUses(
                                    body,
                                    (Stmt) unit,
                                    tar_local);
                            if(sig.startsWith("<"+package_name)||sig.startsWith("<dummyMainClass: "+package_name)){
                                //说明PI是hostapp的，那么找
                                for (String unitValueBoxPair : results) {
                                    for (String sink : sinks) {
                                        if (unitValueBoxPair.contains(sink)) {
                                            String sink_key = unitValueBoxPair;//sink_key就是找到的使用敏感信息的sink语句
                                            if(sink_key.split(" ---- in method: ")[1].startsWith(("<"+package_name))||sink_key.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                continue;
                                                //如果sink是host app内的，就不分析
                                            }
                                            flag_exist=true;
                                            //如果sink是tpl内的，继续
                                            if (results_dic.containsKey(sink_key)) {
                                                StorePIorTPL(results_dic, flag_pi,  pi, sink_key);
                                            } else {
                                                results_dic.put(sink_key, new ArrayList<>());
                                                StorePIorTPL(results_dic, flag_pi,  pi, sink_key);
                                            }
                                            if(!data2sinkList.contains(pi_data)){
                                                data2sinkList.add(pi_data);
                                             }
                                            if(results_dic.containsKey(sink_key)){
                                                results_dic.get(sink_key).add("      ### find one:"+tar_local.toString());
                                                number++;
                                            }else{
                                                results_dic.put(sink_key, new ArrayList<>());

                                                results_dic.get(sink_key).add("      ### find one:"+tar_local.toString());
                                                number++;
                                            }
                                        }
                                    }
                                }
                            }
                            if(flag_exist==true){
                                continue;
                            }else{
                                flag_pi=false;
                            }
                        }
                        for (String pi_pack : target_dataset) {
                            if (stmt.contains(pi_pack.toLowerCase())) {
                                flag_pi = true;
                                pi_data=pi_pack;
                                if (pi.contains(0+pi_pack + ":" + stmt_raw+" ---- in method: "+sootMethod.getSignature())) {
                                    continue;
                                }
                                pi.add(0+pi_pack + ":" + stmt_raw+" ---- in method: "+sootMethod.getSignature());
                                if(!stmt_raw.contains("android.database.Cursor")){
                                    if(!dataList.contains(pi_pack)){
                                        dataList.add(pi_pack);
                                    }
                                }
                            }
                        }
                        //如果包含pi的语句是tpl里的sink，就倒回去找
                        if(flag_pi){
                            int flag_sink;
                            for(String sink:sinks){
                                flag_sink=0;
                                if(stmt.contains(sink.toLowerCase())){
                                    String sink_key=unit.toString()+" ---- in method: "+sig;
                                    if(sig.startsWith("<"+package_name)||sig.startsWith("<dummyMainClass: "+package_name)){
                                        //sig是stmt所在方法的sig
                                        continue;
                                        //如果sink是host app内的，就不分析
                                    }
                                    List<Local> def_Local=getLocal_def(unit);
                                    if(def_Local!=null){
                                        for(Local deflocal:def_Local){
                                            List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                    body,
                                                    (Stmt) unit,
                                                    deflocal);
                                            for(String def:defs) {
                                                if(def.split(" ---- in method: <")[1].startsWith(package_name)||def.split(" ---- in method: ")[1].startsWith("<dummyMainClass: "+package_name)){
                                                    flag_sink=1;
                                                    if(!results_dic.containsKey(sink_key)){
                                                        results_dic.put(sink_key, new ArrayList<>());
                                                    }
                                                    if (!results_dic.get(sink_key).contains("      [def]"+"host app:" + def)){
                                                        results_dic.get(sink_key).add("      [def]"+"host app:" + def);
                                                        if(!data2sinkList.contains(pi_data)){
                                                            data2sinkList.add(pi_data);
                                                        }
                                                        results_dic.get(sink_key).add("      ### find one:"+deflocal.toString());
                                                        number++;
                                                        break;
                                                    }
                                                }
                                            }
                                            if(flag_sink==1){
                                                results_dic.get(sink_key).add("----------------begin");
                                                for(String def:defs) {
                                                    if (!results_dic.get(sink_key).contains("      [def]"+deflocal.toString()+":"+ def)){
                                                        results_dic.get(sink_key).add("      [def]"+deflocal.toString()+":"+ def);
                                                    }
                                                }
                                                results_dic.get(sink_key).add("----------------end");
                                            }
                                        };
                                    }

                                }


                            }
                        }
                        if (flag_pi) {
                            Local tar_local = getLocal(unit);
                            if(tar_local==null){
                                continue;
                            }
                            List<String> results = InterProcedureVariableAnalysis.findUses(
                                    body,
                                    (Stmt) unit,
                                    tar_local);
                            if(sig.startsWith("<"+package_name)||sig.startsWith("<dummyMainClass: "+package_name)){
                                //说明PI是hostapp的，那么找use里面是否含有sink
                                for (String unitValueBoxPair : results) {
                                    for (String sink : sinks) {
                                        if (unitValueBoxPair.contains(sink)) {
                                            String sink_key = unitValueBoxPair;//sink_key就是找到的使用敏感信息的sink语句
                                            if(sink_key.split(" ---- in method: ")[1].startsWith(("<"+package_name))||sink_key.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                continue;
                                                //如果sink是host app内的，就不分析
                                            }
                                            //如果sink是tpl内的，继续
                                            if (results_dic.containsKey(sink_key)) {
                                                StorePIorTPL(results_dic, flag_pi,  pi, sink_key);
                                            } else {
                                                results_dic.put(sink_key, new ArrayList<>());
                                                StorePIorTPL(results_dic, flag_pi,  pi, sink_key);
                                            }
                                            if(!data2sinkList.contains(pi_data)){
                                                data2sinkList.add(pi_data);
                                                //因为是从host app到tpl的，直接加入data2sink
                                            }
                                            if(results_dic.containsKey(sink_key)){
                                                results_dic.get(sink_key).add("      ### find one:"+tar_local.toString());
                                                number++;
                                            }else{
                                                results_dic.put(sink_key, new ArrayList<>());
                                                results_dic.get(sink_key).add("      ### find one:"+tar_local.toString());
                                                number++;
                                            }
                                            List<Local> def_Local=getLocal_def(unit);
                                            if(def_Local!=null){
                                                for(Local deflocal:def_Local){
                                                    List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                            body,
                                                            (Stmt) unit,
                                                            deflocal);
                                                    results_dic.get(sink_key).add("----------------begin");
                                                    for(String def:defs) {
                                                        results_dic.get(sink_key).add("      [def]"+deflocal.toString()+":"+ def);
                                                    }
                                                }
                                            }

                                            results_dic.get(sink_key).add("----------------begin");
                                            for(String use:results) {
                                                results_dic.get(sink_key).add("      [use]"+tar_local.toString()+":"+ use);
                                            }
                                            results_dic.get(sink_key).add("----------------end");

                                        }
                                    }
                                }
                            }else{
                                int flag=0;
                                //pi在tpl里面的情况,找def
                                for (String unitValueBoxPair : results) {
                                    flag=0;
                                    for (String sink : sinks) {
                                        if (unitValueBoxPair.contains(sink)) {
                                            String sink_key = unitValueBoxPair;//sink_key就是找到的使用敏感信息的sink语句
                                            if(sink_key.split(" ---- in method: ")[1].startsWith(("<"+package_name))||sink_key.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                continue;
                                                //如果sink是host app内的，就不分析
                                            }
                                            //如果sink是tpl内的，继续
                                            if (results_dic.containsKey(sink_key)) {
                                                StorePIorTPL(results_dic, flag_pi,  pi, sink_key);
                                            } else {
                                                results_dic.put(sink_key, new ArrayList<>());
                                                StorePIorTPL(results_dic, flag_pi,  pi, sink_key);
                                            }
                                            List<Local> def_Local=getLocal_def(unit);
                                            if(def_Local!=null){
                                                for(Local deflocal:def_Local){
                                                    List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                            body,
                                                            (Stmt) unit,
                                                            deflocal);
                                                    for(String def:defs) {
                                                        if(def.split(" ---- in method: <")[1].startsWith(package_name)||def.split(" ---- in method: ")[1].startsWith("<dummyMainClass: "+package_name)){
                                                            flag=1;
                                                            if(results_dic.containsKey(sink_key)){
                                                                if (!results_dic.get(sink_key).contains("      [def]"+"host app:" + def)){
                                                                    results_dic.get(sink_key).add("      [def]"+"host app:" + def);
                                                                    if(!data2sinkList.contains(pi_data)){
                                                                        data2sinkList.add(pi_data);
                                                                    }

                                                                    results_dic.get(sink_key).add("      ### find one:"+deflocal.toString());
                                                                    number++;
                                                                    break;
                                                                    //只要找到一个host app的就停下来
                                                                }
                                                            }else{
                                                                results_dic.put(sink_key, new ArrayList<>());
                                                                if (!results_dic.get(sink_key).contains("      [def]"+"host app:" + def)){
                                                                    results_dic.get(sink_key).add("      [def]"+"host app:" + def);
                                                                    if(!data2sinkList.contains(pi_data)){
                                                                        data2sinkList.add(pi_data);
                                                                    }

                                                                    results_dic.get(sink_key).add("      ### find one:"+deflocal.toString());
                                                                    number++;
                                                                    break;
                                                                    //只要找到一个host app的就停下来
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if(flag==1){
                                                        results_dic.get(sink_key).add("----------------begin");
                                                        for(String def:defs) {

                                                            results_dic.get(sink_key).add("      [def]"+deflocal.toString()+":"+ def);

                                                        }
                                                        results_dic.get(sink_key).add("----------------end");
                                                        results_dic.get(sink_key).add("----------------begin");
                                                        for(String use:results) {

                                                            results_dic.get(sink_key).add("      [use]"+tar_local.toString()+":"+ use);

                                                        }
                                                        results_dic.get(sink_key).add("----------------end");
                                                    }


                                                };
                                            }

                                        }
                                    }
                                }
                            }


                        }
                    }
                }catch(Exception e){
//                    e.printStackTrace();
//                    System.out.println("in Host_app_Util: getHostAppDataFlowDestination");
                    System.out.println(e.getMessage());
                }
            }
            if(sootClasses.getLast()==sootClass){
                break;
            }else{
                SootClass succ=sootClasses.getSuccOf(sootClass);
                sootClass=succ;
            }
        }
        String key="the data in this apk";
        results_dic.put(key, new ArrayList<>());
        for(String pi:dataList){
            if (!results_dic.get(key).contains("\t"+pi)){
                results_dic.get(key).add("\t"+pi);
            }
        }
        key="the data in this apk and delivered to the sink stmt";
        results_dic.put(key, new ArrayList<>());
        for(String pi:data2sinkList){
            if (!results_dic.get(key).contains("\t"+pi)){
                results_dic.get(key).add("\t"+pi);
            }
        }
        key="test number:";
        results_dic.put(key, new ArrayList<>());
        if (!results_dic.get(key).contains(String.valueOf(number))) {
            results_dic.get(key).add(String.valueOf(number));
        }
        return results_dic;

    }
    /*
    目的是找到从tpl到app有无数据传输路径，寻找方法有两种：
        1.在tpl内定义的PI数据，进行use分析，寻找是否传到了app
        2.在app内使用的PI数据，进行def分析，寻找def是否来自于tpl
     */
    public static Object getTplDataFlowDestination(String apk_path, ArrayList<String> sinks, ArrayList<String> sources) throws Exception{
        //原版是遍历每个unit，找到包含pi或者tpl的那些语句，提出它的valuebox，找它的usebox，然后把usebox中包含sink的，进行打印
        HashMap<String, ArrayList<String>> results_dic = new HashMap<String, ArrayList<String>>();
        boolean flag_pi = false;
        List<String> name_result=getPackageName(apk_path);
        String package_name=null;
        if(name_result.size()==1){
            package_name=name_result.get(0);
        }else{
            package_name=name_result.get(1);
        }

        List<String> dataList=new LinkedList<String>();

        // iterate each statement in each method of each classes
        Chain<SootClass> sootClasses=Scene.v().getClasses();
        SootClass sootClass=sootClasses.getFirst();
        while(sootClass!=sootClasses.getLast()){
            //for(int i=0;i<sootClasses.size();i++){
            List<SootMethod> sootMethods= sootClass.getMethods();
            for(int j=0;j<sootMethods.size();j++){
                SootMethod sootMethod=sootMethods.get(j);
                Body body;
                try{
                    body=sootMethod.retrieveActiveBody();
                    for(Unit unit:body.getUnits()){
                        flag_pi = false;
                        String stmt_raw = unit.toString();
                        String stmt = stmt_raw.toLowerCase();
                        /* analysis pi */
                        String pi_data=null;
                        String sig=sootMethod.getSignature();
//source begin
                        for (String pi_pack : sources) {
                            if (stmt.contains(pi_pack.toLowerCase())) {
                                //System.out.println("pi");
                                flag_pi = true;
                                pi_data=pi_pack;
                            }
                        }

//0-----------------------------------
                        // get the local for analysis
                        if (flag_pi) {
                            Local tar_local = getLocal(unit);
                            boolean flag_exist2=false;
                            if(tar_local==null){
                                continue;
                            }

                            List<String> results = InterProcedureVariableAnalysis.findUses(
                                    body,
                                    (Stmt) unit,
                                    tar_local);
                            if(!sig.startsWith("<"+package_name)&&!sig.startsWith("<dummyMainClass: "+package_name)){
                            //if(sig.startsWith("<"+package_name)||sig.startsWith("<dummyMainClass: "+package_name)){
                                //说明PI在tpl里面，然后进行use分析
                                for (String unitValueBoxPair : results) {
                                    if(unitValueBoxPair.split(" ---- in method: <")[1].startsWith(package_name)||unitValueBoxPair.split(" ---- in method: ")[1].startsWith("<dummyMainClass: "+package_name)){
                                        //unitValue是unit进行use分析后的那些语句，以package_name开头说明是在app内的，说明从tpl内定义的PI有流动到app的
                                        String key=pi_data+":"+stmt_raw+" ---- in method: "+sootMethod.getSignature();
                                        flag_exist2=true;
                                        if(results_dic.containsKey(key)){
                                            //这里把tpl里包含PI的句子当成key，然后把所有use的句子当成value
                                            for(String use:results){
                                                results_dic.get(key).add("[use]"+tar_local.toString()+" :"+use);
                                            }
                                        }else{
                                            results_dic.put(key,new ArrayList<>());
                                            for(String use:results){
                                                results_dic.get(key).add("[use]"+tar_local.toString()+" :"+use);
                                            }
                                        }
                                        if(!dataList.contains(pi_data)){
                                            //然后加入dataList，方便统计
                                            if(!stmt_raw.contains("<android.database.Cursor: ")) {
                                                dataList.add(pi_data);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            //source不像pi列表一样，source只需要看在tpl里的source，然后进行use分析就可以
                            if(flag_exist2==true){
                                continue;
                            }
                        }
//source end
                        for (String pi_pack : target_dataset) {
                            if (stmt.contains(pi_pack.toLowerCase())) {
                                flag_pi = true;
                                pi_data=pi_pack;
                                break;
                            } else{
                                flag_pi=false;
                            }
                        }
                        if (flag_pi) {
                            int flag_same=0;//用来表示def，use和unit是不是一个包下，0代表是一个包，1代表use有例外，2代表def有例外，3代表defuse都有例外
                            int flag_app=0;//用来表示sig是在app内的还是tpl内的，app内的是0，tpl内的是1
                            int flag_sinkInTpl=0;//如果这个unit是tpl内的，而且包含sink语句，那么为1，否则为0
                            Local tar_local = getLocal(unit);
                            if(tar_local==null){
                                continue;
                            }
                            if(sig.startsWith("<"+package_name)||sig.startsWith("<dummyMainClass: "+package_name)){
                                flag_app=0;
                            }else{
                                flag_app=1;
                            }
                            List<String> results = InterProcedureVariableAnalysis.findUses(
                                    body,
                                    (Stmt) unit,
                                    tar_local);
                            //下面先查看use的情况，再查看def的情况
                            if(flag_app==0){
                                //sig是app内的,这种情况不用进行use分析，分析是否会传递到tpl，这是apptotpl的内容
                            }else{
                                //sig是tpl内的
                                int flag_sink=0;//0代表不是sink，1代表是sink
                                for(String sink:sinks){
                                    if(stmt.contains(sink.toLowerCase())){
                                        flag_sink=1;
                                        break;
                                    }
                                }
                                if(flag_sink==0){
                                    //代表这个unit不是sink，如下：
/*
包名：com.callapp.contacts
contact:$r2 = new com.callapp.contacts.CallAppWatchApplication ---- in method: <dummyMainClass: void dummyMainMethod(java.lang.String[])>
	flag_same:1---- strat from tpl
	[use]$r2 :[sameMethod] JimpleLocalBox($r2) in specialinvoke $r2.<com.callapp.contacts.CallAppWatchApplication: void <init>()>() ---- in method: <dummyMainClass: void dummyMainMethod(java.lang.String[])>
	[use]$r2 :JimpleLocalBox(r0) in specialinvoke r0.<android.app.Application: void <init>()>() ---- in method: <com.callapp.contacts.CallAppWatchApplication: void <init>()>
	[use]$r2 :JimpleLocalBox(r0) in r0.<com.callapp.contacts.CallAppWatchApplication: android.os.Handler handler> = $r1 ---- in method: <com.callapp.contacts.CallAppWatchApplication: void <init>()>
	[use]$r2 :[sameMethod] JimpleLocalBox($r2) in virtualinvoke $r2.<com.callapp.contacts.CallAppWatchApplication: void onCreate()>() ---- in method: <dummyMainClass: void dummyMainMethod(java.lang.String[])>
	[use]$r2 :JimpleLocalBox(r0) in specialinvoke r0.<android.app.Application: void onCreate()>() ---- in method: <com.callapp.contacts.CallAppWatchApplication: void onCreate()>
	[use]$r2 :ImmediateBox(r0) in specialinvoke $r1.<java.lang.ref.WeakReference: void <init>(java.lang.Object)>(r0) ---- in method: <com.callapp.contacts.CallAppWatchApplication: void onCreate()>
	[use]$r2 :JimpleLocalBox(r0) in $r2 = virtualinvoke r0.<com.callapp.contacts.CallAppWatchApplication: android.content.res.Resources getResources()>() ---- in method: <com.callapp.contacts.CallAppWatchApplication: void onCreate()>
	[use]$r2 :JimpleLocalBox(r0) in r0.<com.callapp.contacts.CallAppWatchApplication: java.lang.String[] QuickResponseSMS> = $r3 ---- in method: <com.callapp.contacts.CallAppWatchApplication: void onCreate()>
	[use]$r2 :[sameMethod] LinkedRValueBox($r2) in <il.ac.tau.MyApplicationHolder: android.app.Application application> = $r2 ---- in method: <dummyMainClass: void dummyMainMethod(java.lang.String[])>
 */
                                    for(String use:results){
                                        if(use.split(" ---- in method: ")[1].startsWith(("<"+package_name))||use.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                            //说明use有app的，use有例外
                                            flag_same=1;
                                            break;
                                        }
                                        flag_same=0;
                                    }
                                }else{
                                    //这个unit是tpl内的sink
                                    //如果tpl这个包含PI的句子是一个sink，那么，找到这个sink传入的参数，然后进行use分析

                                    List<Local> def_Local_sinkUnit=getLocal_def(unit);
                                    if(def_Local_sinkUnit!=null){
                                        for(Local deflocal:def_Local_sinkUnit){
                                            List<String> defsOfSinkUnit = InterProcedureVariableAnalysis.findUses(
                                                    body,
                                                    (Stmt) unit,
                                                    deflocal);
                                            for(String useOfParam:defsOfSinkUnit){
                                                //sink中参数有被传送到app里面
                                                if(useOfParam.split(" ---- in method: ")[1].startsWith(("<"+package_name))||useOfParam.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                    //说明use有app的，use有例外

                                                    flag_same=1;
                                                    flag_sinkInTpl=1;
                                                    break;
                                                }
                                                flag_same=0;
                                            }
                                            if(flag_same==1){
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            List<Local> def_Local=getLocal_def(unit);
                            if(def_Local!=null){
                                if(flag_app==0){
                                    //sig在app中
                                    //如下：
/*
包名：com.anddoes.launcher
network:$r3 = staticinvoke <com.anddoes.launcher.customscreen.devicescan.g: android.net.NetworkInfo f(android.content.Context)>($r2) ---- in method: <com.anddoes.launcher.customscreen.ui.n: void k()>
	flag_same:2---- strat from app
	[def]$r2 :[sameMethod]$r2 = staticinvoke <com.android.launcher3.LauncherApplication: com.android.launcher3.LauncherApplication getAppContext()>() ---- in method: <com.anddoes.launcher.customscreen.ui.n: void k()>
	[def]$r2 :r0 = <com.android.launcher3.LauncherApplication: com.android.launcher3.LauncherApplication sContext> ---- in method: <com.android.launcher3.LauncherApplication: com.android.launcher3.LauncherApplication getAppContext()>
 */
                                    for(Local deflocal:def_Local){
                                        List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                body,
                                                (Stmt) unit,
                                                deflocal);
                                        for(String def:defs) {
                                            if(!def.split(" ---- in method: ")[1].startsWith(("<"+package_name))&&!def.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                //def是tpl内的
                                                if(flag_same==0){
                                                    flag_same=2;
                                                }
                                                break;
                                            }
                                        }
                                        if(flag_same==2){
                                            break;
                                        }
                                    }
                                }else{
                                    //sig在tpl中
                                    //在tpl中的PI语句只需要看初始语句是否在tpl内，然后再看是否经过app又回到tpl
                                    for(Local deflocal:def_Local){
                                        List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                body,
                                                (Stmt) unit,
                                                deflocal);
                                        for(String def:defs) {
                                            if(def.split(" ---- in method: ")[1].startsWith(("<"+package_name))||def.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                //def是app内的
                                                if(flag_same==0){
                                                    flag_same=2;

                                                }else if(flag_same==1){
                                                    flag_same=3;
                                                }
                                                break;
                                            }
                                        }

                                        if(flag_same==2||flag_same==3){
                                            break;
                                        }
                                    }
                                }
                            }
                            //下面思路是如果def和use始终在app或者在tpl，则不管，如果有任何一个满足交叉，那么输出

                            if(flag_same!=0){
                                if(!dataList.contains(pi_data)){
                                    dataList.add(pi_data);
                                }
                                //flag_same等于0表示unit，def，use都同在app或tpl内
                                String key=pi_data+":"+stmt_raw+" ---- in method: "+sootMethod.getSignature();
                                //先打印所有的use
                                if(!results_dic.containsKey(key)){
                                    results_dic.put(key,new ArrayList<>());
                                }
                                if(flag_app==0){
                                    results_dic.get(key).add("flag_same:"+flag_same+"---- strat from app");
                                }else{
                                    results_dic.get(key).add("flag_same:"+flag_same+"---- strat from tpl");
                                }
                                if(flag_same==1){
                                    if(flag_sinkInTpl==1){
                                        //说明要找sinkintpl的参数的use
                                        List<Local> def_Local_sinkUnit=getLocal_def(unit);
                                        if(def_Local_sinkUnit!=null){
                                            for(Local deflocal:def_Local_sinkUnit){
                                                List<String> defsOfSinkUnit = InterProcedureVariableAnalysis.findUses(
                                                        body,
                                                        (Stmt) unit,
                                                        deflocal);
                                                for(String useOfParam:defsOfSinkUnit){
                                                    results_dic.get(key).add("[useOfParam]"+deflocal.toString()+" :"+useOfParam);
                                                }
                                                results_dic.get(key).add("###next param");
                                            }
                                        }
                                    }
                                    results_dic.get(key).add("-----------------");
                                    for(Local deflocal:def_Local){
                                        List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                body,
                                                (Stmt) unit,
                                                deflocal);
                                        for(String def:defs){
                                            results_dic.get(key).add("[def]"+deflocal.toString()+" :"+def);
                                        }
                                        results_dic.get(key).add("###next deflocal");
                                    }
                                    results_dic.get(key).add("-----------------");
                                    for(String use:results){
                                        results_dic.get(key).add("[use]"+tar_local.toString()+" :"+use);
                                    }
                                }else if(flag_same==2){
                                    if(flag_sinkInTpl==1){
                                        //说明要找sinkintpl的参数的use
                                        List<Local> def_Local_sinkUnit=getLocal_def(unit);
                                        if(def_Local_sinkUnit!=null){
                                            for(Local deflocal:def_Local_sinkUnit){
                                                List<String> defsOfSinkUnit = InterProcedureVariableAnalysis.findUses(
                                                        body,
                                                        (Stmt) unit,
                                                        deflocal);
                                                for(String useOfParam:defsOfSinkUnit){
                                                    results_dic.get(key).add("[useOfParam]"+deflocal.toString()+" :"+useOfParam);
                                                }
                                                results_dic.get(key).add("###next param");
                                            }
                                        }
                                    }
                                    for(Local deflocal:def_Local){
                                        List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                body,
                                                (Stmt) unit,
                                                deflocal);
                                        for(String def:defs){
                                            results_dic.get(key).add("[def]"+deflocal.toString()+" :"+def);
                                        }
                                        results_dic.get(key).add("###next deflocal");
                                    }
                                }else if(flag_same==3){
                                    if(flag_sinkInTpl==1){
                                        //说明要找sinkintpl的参数的use
                                        List<Local> def_Local_sinkUnit=getLocal_def(unit);
                                        if(def_Local_sinkUnit!=null){
                                            for(Local deflocal:def_Local_sinkUnit){
                                                List<String> defsOfSinkUnit = InterProcedureVariableAnalysis.findUses(
                                                        body,
                                                        (Stmt) unit,
                                                        deflocal);
                                                for(String useOfParam:defsOfSinkUnit){
                                                    results_dic.get(key).add("[useOfParam]"+deflocal.toString()+" :"+useOfParam);
                                                }
                                                results_dic.get(key).add("###next param");
                                            }
                                        }
                                    }
                                    //先打印use，再打印def
                                    for(String use:results){
                                        results_dic.get(key).add("[use]"+tar_local.toString()+" :"+use);
                                    }
                                    results_dic.get(key).add("--------------------------------");
                                    for(Local deflocal:def_Local){
                                        List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                body,
                                                (Stmt) unit,
                                                deflocal);
                                        for(String def:defs){
                                            results_dic.get(key).add("[def]"+deflocal.toString()+" :"+def);
                                        }
                                        results_dic.get(key).add("###next deflocal");
                                    }
                                }
                            }
                        }
                    }
                }catch(Exception e){
//                    e.printStackTrace();
//                    System.out.println("in Host_app_Util: getHostAppDataFlowDestination");
                    System.out.println(e.getMessage());
                }
            }
            if(sootClasses.getLast()==sootClass){
                break;
            }else{
                SootClass succ=sootClasses.getSuccOf(sootClass);
                sootClass=succ;
            }
        }

        String key="the data in this apk start from tpl";
        System.out.println("dataList size: "+dataList.size());
        results_dic.put(key, new ArrayList<>());
        for(String pi:dataList){
            if (!results_dic.get(key).contains("\t"+pi)){
                results_dic.get(key).add("\t"+pi);
            }
        }

        return results_dic;
    }
    public static Object[] getDataFlow(String apk_path, ArrayList<String> sinks, ArrayList<String> sources,String package_name) throws Exception{
        Object[] results_dic=new Object[2];
        HashMap<String, ArrayList<String>> results_dic1 = new HashMap<String, ArrayList<String>>();
        HashMap<String, ArrayList<String>> results_dic2 = new HashMap<String, ArrayList<String>>();
        //先对APK进行解析
        FlowDroidEnvironment.reset();
        FlowDroidEnvironment.init(apk_path, androidJars);
        //初始化运行的参数
        FlowDroidTimeoutWatcher.timeoutFlag = false;
        int flodroidArgsSize = FlowDroidEnvironment.args.size();
        String[] flowdraoidArgs = new String[flodroidArgsSize];
        FlowDroidEnvironment.args.toArray(flowdraoidArgs);
        ArrayList<InfoflowResults> flowdroidResults = Flowdroid.analyze(flowdraoidArgs);
        if (Flowdroid.exceptionFlag) {
            System.out.println("exceptionFlag");
            return null;
        }
//        //获取包名，方便后面区分第三方库和host app
//        List<String> name_result=getPackageName(apk_path);
//        String package_name=null;
//        if(name_result.size()==1){
//            package_name=name_result.get(0);
//        }else{
//            package_name=name_result.get(1);
//        }
        List<String> dataList1=new LinkedList<String>();//表示的是收集的host->tpl
        List<String> data2sinkList1=new LinkedList<String>();//表示的是分享的host->tpl
        List<String> dataList2=new LinkedList<String>();//表示的是收集的tpl->host
        // iterate each statement in each method of each classes
        Boolean flag_pi=false;//标记句子是否包含pi
        Chain<SootClass> sootClasses=Scene.v().getClasses();
        SootClass sootClass=sootClasses.getFirst();
        int number=0;
        while(sootClass!=sootClasses.getLast()){
            List<SootMethod> sootMethods= sootClass.getMethods();
            for(int j=0;j<sootMethods.size();j++){
                SootMethod sootMethod=sootMethods.get(j);
                Body body;
                try{
                    body=sootMethod.retrieveActiveBody();
                    for(Unit unit:body.getUnits()){
                        flag_pi = false;
                        String stmt_raw = unit.toString();
                        String stmt = stmt_raw.toLowerCase();
                        /* analysis pi */
                        ArrayList<String> pi = new ArrayList<>();
                        String pi_data=null;
                        String sig=sootMethod.getSignature();
//source begin
                        for (String pi_pack : sources) {
                            if (stmt.contains(pi_pack.toLowerCase())) {
                                flag_pi = true;
                                pi_data=pi_pack;
                                if (pi.contains(0+pi_pack + ":" + stmt_raw+" ---- in method: "+sootMethod.getSignature())) {
                                    continue;
                                }
                                pi.add(0+pi_pack + ":" + stmt_raw+" ---- in method: "+sootMethod.getSignature());
                                if(!dataList1.contains(pi_pack)){
                                    dataList1.add(pi_pack);
                                }
                            }
                        }

                        // get the local for analysis
                        if (flag_pi) {
                            boolean flag_exist=false;//标记是否有sink
                            Local tar_local = getLocal(unit);
                            if(tar_local==null){
                                continue;
                            }
                            List<String> results = InterProcedureVariableAnalysis.findUses(
                                    body,
                                    (Stmt) unit,
                                    tar_local);
                            if(sig.startsWith("<"+package_name)||sig.startsWith("<dummyMainClass: "+package_name)){
                                //说明PI是hostapp的，那么找是否有sink，如果有，加入data2sinklist
                                for (String unitValueBoxPair : results) {
                                    for (String sink : sinks) {
                                        if (unitValueBoxPair.contains(sink)) {
                                            String sink_key = unitValueBoxPair;//sink_key就是找到的使用敏感信息的sink语句
                                            if(sink_key.split(" ---- in method: ")[1].startsWith(("<"+package_name))||sink_key.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                continue;
                                                //如果sink是host app内的，就不分析（因为这样是host->host，没有分析意义）
                                            }
                                            flag_exist=true;
                                            //如果sink是tpl内的，继续
                                            if (results_dic1.containsKey(sink_key)) {
                                                StorePIorTPL(results_dic1, flag_pi,  pi, sink_key);
                                            } else {
                                                results_dic1.put(sink_key, new ArrayList<>());
                                                StorePIorTPL(results_dic1, flag_pi,  pi, sink_key);
                                            }
                                            if(!data2sinkList1.contains(pi_data)){
                                                data2sinkList1.add(pi_data);
                                            }
                                            if(results_dic1.containsKey(sink_key)){
                                                results_dic1.get(sink_key).add("      ### find one:"+tar_local.toString());
                                                number++;
                                            }else{
                                                results_dic1.put(sink_key, new ArrayList<>());

                                                results_dic1.get(sink_key).add("      ### find one:"+tar_local.toString());
                                                number++;
                                            }
                                        }
                                    }
                                }
                            }
                            //下面进行tpl->host的分析-----------------------------------
                            boolean flag_exist2=false;//是表示tpl->host是否发现路径的
                            if(!sig.startsWith("<"+package_name)&&!sig.startsWith("<dummyMainClass: "+package_name)){
                                //if(sig.startsWith("<"+package_name)||sig.startsWith("<dummyMainClass: "+package_name)){
                                //说明PI在tpl里面，然后进行use分析
                                for (String unitValueBoxPair : results) {
                                    if(unitValueBoxPair.split(" ---- in method: <")[1].startsWith(package_name)||unitValueBoxPair.split(" ---- in method: ")[1].startsWith("<dummyMainClass: "+package_name)){
                                        //unitValue是unit进行use分析后的那些语句，以package_name开头说明是在app内的，说明从tpl内定义的PI有流动到app的
                                        String key=pi_data+":"+stmt_raw+" ---- in method: "+sootMethod.getSignature();
                                        flag_exist2=true;
                                        if(results_dic2.containsKey(key)){
                                            //这里把tpl里包含PI的句子当成key，然后把所有use的句子当成value
                                            for(String use:results){
                                                results_dic2.get(key).add("[use]"+tar_local.toString()+" :"+use);
                                            }
                                        }else{
                                            results_dic2.put(key,new ArrayList<>());
                                            for(String use:results){
                                                results_dic2.get(key).add("[use]"+tar_local.toString()+" :"+use);
                                            }
                                        }
                                        if(!dataList2.contains(pi_data)){
                                            //然后加入dataList，方便统计
                                            if(!stmt_raw.contains("<android.database.Cursor: ")) {
                                                dataList2.add(pi_data);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            //source不像pi列表一样，source只需要看在tpl里的source，然后进行use分析就可以
                            //tpl->host的分析结束----------------------------
                            if(flag_exist==true&&flag_exist2==true){
                                continue;
                            }
                            flag_pi=false;
                            //结束，开始进行keywords的分析

                        }


//source end
                        //分析keywords，既有可能是sink，那么就反向分析，也有可能是sourc，那么正向分析
                        for (String pi_pack : target_dataset) {
                            if (stmt.contains(pi_pack.toLowerCase())) {
                                flag_pi = true;
                                pi_data=pi_pack;
                                if (pi.contains(0+pi_pack + ":" + stmt_raw+" ---- in method: "+sootMethod.getSignature())) {
                                    continue;
                                }
                                pi.add(0+pi_pack + ":" + stmt_raw+" ---- in method: "+sootMethod.getSignature());
                                if(!stmt_raw.contains("android.database.Cursor")){
                                    if(!dataList1.contains(pi_pack)){
                                        dataList1.add(pi_pack);
                                    }
                                }
                            }
                        }
                        //如果包含pi的语句是tpl里的sink，就反向分析，即def分析
                        if(flag_pi){
                            int flag1;
                            for(String sink:sinks){
                                flag1=0;
                                if(stmt.contains(sink.toLowerCase())){
                                    String sink_key=unit.toString()+" ---- in method: "+sig;
                                    if(sig.startsWith("<"+package_name)||sig.startsWith("<dummyMainClass: "+package_name)){
                                        //sig是stmt所在方法的sig
                                        continue;
                                        //如果sink是host app内的，就不考虑
                                    }
                                    List<Local> def_Local=getLocal_def(unit);
                                    if(def_Local!=null){
                                        for(Local deflocal:def_Local){
                                            List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                    body,
                                                    (Stmt) unit,
                                                    deflocal);
                                            for(String def:defs) {
                                                if(def.split(" ---- in method: <")[1].startsWith(package_name)||def.split(" ---- in method: ")[1].startsWith("<dummyMainClass: "+package_name)){
                                                    flag1=1;
                                                    if(results_dic1.containsKey(sink_key)){
                                                        //[def]表示是def分析
                                                        if (!results_dic1.get(sink_key).contains("      [def]"+"host app:" + def)){
                                                            results_dic1.get(sink_key).add("      [def]"+"host app:" + def);
                                                            if(!data2sinkList1.contains(pi_data)){
                                                                data2sinkList1.add(pi_data);
                                                            }
                                                            results_dic1.get(sink_key).add("      ### find one:"+deflocal.toString());
                                                            number++;
                                                            break;
                                                            //只要找到一个host app的就停下来
                                                        }
                                                    }else{
                                                        results_dic1.put(sink_key, new ArrayList<>());
                                                        if (!results_dic1.get(sink_key).contains("      [def]"+"host app:" + def)){
                                                            results_dic1.get(sink_key).add("      [def]"+"host app:" + def);
                                                            if(!data2sinkList1.contains(pi_data)){
                                                                data2sinkList1.add(pi_data);
                                                            }

                                                            results_dic1.get(sink_key).add("      ### find one:"+deflocal.toString());
                                                            number++;
                                                            break;
                                                            //只要找到一个host app的就停下来
                                                        }
                                                    }
                                                }
                                            }
                                            if(flag1==1){
                                                //flag1=1表示从sink到source的反向路径找到了一条，这里只是为了方便手工分析，输出了一些额外数据
                                                results_dic1.get(sink_key).add("----------------begin");
                                                for(String def:defs) {
                                                    if (!results_dic1.get(sink_key).contains("      [def]"+deflocal.toString()+":"+ def)){
                                                        results_dic1.get(sink_key).add("      [def]"+deflocal.toString()+":"+ def);
                                                    }
                                                }
                                                results_dic1.get(sink_key).add("----------------end");
                                            }
                                        };
                                    }
                                }
                            }
                        }
//0-----------------------------------
                        // get the local for analysis
                        //下面是正向分析 use分析，因为keywords可能是source语句内的
                        if (flag_pi) {
                            Local tar_local = getLocal(unit);
                            if(tar_local==null){
                                continue;
                            }
                            List<String> results = InterProcedureVariableAnalysis.findUses(
                                    body,
                                    (Stmt) unit,
                                    tar_local);
                            if(sig.startsWith("<"+package_name)||sig.startsWith("<dummyMainClass: "+package_name)){
                                //说明PI是hostapp的，那么找use里面是否含有sink
                                for (String unitValueBoxPair : results) {
                                    for (String sink : sinks) {
                                        if (unitValueBoxPair.contains(sink)) {
                                            String sink_key = unitValueBoxPair;//sink_key就是找到的使用敏感信息的sink语句
                                            if(sink_key.split(" ---- in method: ")[1].startsWith(("<"+package_name))||sink_key.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                continue;
                                                //如果sink是host app内的，就不分析
                                            }
                                            //如果sink是tpl内的，继续
                                            if (results_dic1.containsKey(sink_key)) {
                                                StorePIorTPL(results_dic1, flag_pi,  pi, sink_key);
                                            } else {
                                                results_dic1.put(sink_key, new ArrayList<>());
                                                StorePIorTPL(results_dic1, flag_pi,  pi, sink_key);
                                            }
                                            if(!data2sinkList1.contains(pi_data)){
                                                data2sinkList1.add(pi_data);
                                                //因为是从host app到tpl的，直接加入data2sink
                                            }
                                            if(results_dic1.containsKey(sink_key)){
                                                results_dic1.get(sink_key).add("      ### find one:"+tar_local.toString());
                                                number++;
                                            }else{
                                                results_dic1.put(sink_key, new ArrayList<>());
                                                results_dic1.get(sink_key).add("      ### find one:"+tar_local.toString());
                                                number++;
                                            }
                                            List<Local> def_Local=getLocal_def(unit);
                                            if(def_Local!=null){
                                                for(Local deflocal:def_Local){
                                                    List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                            body,
                                                            (Stmt) unit,
                                                            deflocal);
                                                    results_dic1.get(sink_key).add("----------------begin");
                                                    for(String def:defs) {
                                                        results_dic1.get(sink_key).add("      [def]"+deflocal.toString()+":"+ def);
                                                    }
                                                }
                                            }

                                            results_dic1.get(sink_key).add("----------------begin");
                                            for(String use:results) {
                                                results_dic1.get(sink_key).add("      [use]"+tar_local.toString()+":"+ use);
                                            }
                                            results_dic1.get(sink_key).add("----------------end");

                                        }
                                    }
                                }
                            }else{
                                int flag=0;
                                //pi在tpl里面的情况,如果是一个tpl里包含pi的sink，直接def分析找是否来自host app
                                for (String unitValueBoxPair : results) {
                                    flag=0;
                                    for (String sink : sinks) {
                                        if (unitValueBoxPair.contains(sink)) {
                                            String sink_key = unitValueBoxPair;//sink_key就是找到的使用敏感信息的sink语句
                                            if(sink_key.split(" ---- in method: ")[1].startsWith(("<"+package_name))||sink_key.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                continue;
                                                //如果sink是host app内的，就不分析
                                            }
                                            //如果sink是tpl内的，继续
                                            if (results_dic1.containsKey(sink_key)) {
                                                StorePIorTPL(results_dic1, flag_pi,  pi, sink_key);
                                            } else {
                                                results_dic1.put(sink_key, new ArrayList<>());
                                                StorePIorTPL(results_dic1, flag_pi,  pi, sink_key);
                                            }
                                            List<Local> def_Local=getLocal_def(unit);
                                            if(def_Local!=null){
                                                for(Local deflocal:def_Local){
                                                    List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                            body,
                                                            (Stmt) unit,
                                                            deflocal);
                                                    for(String def:defs) {
                                                        if(def.split(" ---- in method: <")[1].startsWith(package_name)||def.split(" ---- in method: ")[1].startsWith("<dummyMainClass: "+package_name)){
                                                            flag=1;//表示至少一个def来自host
                                                            if(results_dic1.containsKey(sink_key)){
                                                                if (!results_dic1.get(sink_key).contains("      [def]"+"host app:" + def)){
                                                                    results_dic1.get(sink_key).add("      [def]"+"host app:" + def);
                                                                    if(!data2sinkList1.contains(pi_data)){
                                                                        data2sinkList1.add(pi_data);
                                                                    }

                                                                    results_dic1.get(sink_key).add("      ### find one:"+deflocal.toString());
                                                                    number++;
                                                                    break;
                                                                    //只要找到一个host app的就停下来
                                                                }
                                                            }else{
                                                                results_dic1.put(sink_key, new ArrayList<>());
                                                                if (!results_dic1.get(sink_key).contains("      [def]"+"host app:" + def)){
                                                                    results_dic1.get(sink_key).add("      [def]"+"host app:" + def);
                                                                    if(!data2sinkList1.contains(pi_data)){
                                                                        data2sinkList1.add(pi_data);
                                                                    }

                                                                    results_dic1.get(sink_key).add("      ### find one:"+deflocal.toString());
                                                                    number++;
                                                                    break;
                                                                    //只要找到一个host app的就停下来
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if(flag==1){
                                                        //也是为了手工分析额外添加的一些输出
                                                        results_dic1.get(sink_key).add("----------------begin");
                                                        for(String def:defs) {

                                                            results_dic1.get(sink_key).add("      [def]"+deflocal.toString()+":"+ def);

                                                        }
                                                        results_dic1.get(sink_key).add("----------------end");
                                                        results_dic1.get(sink_key).add("----------------begin");
                                                        for(String use:results) {

                                                            results_dic1.get(sink_key).add("      [use]"+tar_local.toString()+":"+ use);

                                                        }
                                                        results_dic1.get(sink_key).add("----------------end");
                                                    }
                                                };
                                            }
                                        }
                                    }
                                }
                            }
                            //tpl->host开始
                            int flag_same=0;//用来表示def，use和unit是不是一个包下，0代表是一个包，1代表use有例外，2代表def有例外，3代表defuse都有例外
                            int flag_app=0;//用来表示sig是在app内的还是tpl内的，app内的是0，tpl内的是1
                            int flag_sinkInTpl=0;//如果这个unit是tpl内的，而且包含sink语句，那么为1，否则为0
                            if(sig.startsWith("<"+package_name)||sig.startsWith("<dummyMainClass: "+package_name)){
                                flag_app=0;
                            }else{
                                flag_app=1;
                            }
                            //下面先查看use的情况，再查看def的情况
                            if(flag_app==0){
                                //sig是app内的,这种情况不用进行use分析，分析是否会传递到tpl，这是apptotpl的内容
                            }else{
                                //sig是tpl内的
                                int flag_sink=0;//0代表不是sink，1代表是sink
                                for(String sink:sinks){
                                    if(stmt.contains(sink.toLowerCase())){
                                        flag_sink=1;
                                        break;
                                    }
                                }
                                if(flag_sink==0){
                                    //代表这个unit不是sink，如下：
/*
包名：com.callapp.contacts
contact:$r2 = new com.callapp.contacts.CallAppWatchApplication ---- in method: <dummyMainClass: void dummyMainMethod(java.lang.String[])>
	flag_same:1---- strat from tpl
	[use]$r2 :[sameMethod] JimpleLocalBox($r2) in specialinvoke $r2.<com.callapp.contacts.CallAppWatchApplication: void <init>()>() ---- in method: <dummyMainClass: void dummyMainMethod(java.lang.String[])>
	[use]$r2 :JimpleLocalBox(r0) in specialinvoke r0.<android.app.Application: void <init>()>() ---- in method: <com.callapp.contacts.CallAppWatchApplication: void <init>()>
	[use]$r2 :JimpleLocalBox(r0) in r0.<com.callapp.contacts.CallAppWatchApplication: android.os.Handler handler> = $r1 ---- in method: <com.callapp.contacts.CallAppWatchApplication: void <init>()>
	[use]$r2 :[sameMethod] JimpleLocalBox($r2) in virtualinvoke $r2.<com.callapp.contacts.CallAppWatchApplication: void onCreate()>() ---- in method: <dummyMainClass: void dummyMainMethod(java.lang.String[])>
	[use]$r2 :JimpleLocalBox(r0) in specialinvoke r0.<android.app.Application: void onCreate()>() ---- in method: <com.callapp.contacts.CallAppWatchApplication: void onCreate()>
	[use]$r2 :ImmediateBox(r0) in specialinvoke $r1.<java.lang.ref.WeakReference: void <init>(java.lang.Object)>(r0) ---- in method: <com.callapp.contacts.CallAppWatchApplication: void onCreate()>
	[use]$r2 :JimpleLocalBox(r0) in $r2 = virtualinvoke r0.<com.callapp.contacts.CallAppWatchApplication: android.content.res.Resources getResources()>() ---- in method: <com.callapp.contacts.CallAppWatchApplication: void onCreate()>
	[use]$r2 :JimpleLocalBox(r0) in r0.<com.callapp.contacts.CallAppWatchApplication: java.lang.String[] QuickResponseSMS> = $r3 ---- in method: <com.callapp.contacts.CallAppWatchApplication: void onCreate()>
	[use]$r2 :[sameMethod] LinkedRValueBox($r2) in <il.ac.tau.MyApplicationHolder: android.app.Application application> = $r2 ---- in method: <dummyMainClass: void dummyMainMethod(java.lang.String[])>
 */
                                    for(String use:results){
                                        if(use.split(" ---- in method: ")[1].startsWith(("<"+package_name))||use.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                            //说明use有app的，use有例外
                                            flag_same=1;
                                            break;
                                        }
                                        flag_same=0;
                                    }
                                }else{
                                    //这个unit是tpl内的sink
                                    //如果tpl这个包含PI的句子是一个sink，那么，找到这个sink传入的参数，然后进行use分析

                                    List<Local> def_Local_sinkUnit=getLocal_def(unit);
                                    if(def_Local_sinkUnit!=null){
                                        for(Local deflocal:def_Local_sinkUnit){
                                            List<String> defsOfSinkUnit = InterProcedureVariableAnalysis.findUses(
                                                    body,
                                                    (Stmt) unit,
                                                    deflocal);
                                            for(String useOfParam:defsOfSinkUnit){
                                                //sink中参数有被传送到app里面
                                                if(useOfParam.split(" ---- in method: ")[1].startsWith(("<"+package_name))||useOfParam.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                    //说明use有app的，use有例外
                                                    flag_same=1;
                                                    flag_sinkInTpl=1;
                                                    break;
                                                }
                                                flag_same=0;
                                            }
                                            if(flag_same==1){
                                                break;
                                            }
                                        }
                                    }
                                }


                            }

                            List<Local> def_Local=getLocal_def(unit);
                            if(def_Local!=null){
                                if(flag_app==0){
                                    //sig在app中
                                    //如下：
/*
包名：com.anddoes.launcher
network:$r3 = staticinvoke <com.anddoes.launcher.customscreen.devicescan.g: android.net.NetworkInfo f(android.content.Context)>($r2) ---- in method: <com.anddoes.launcher.customscreen.ui.n: void k()>
	flag_same:2---- strat from app
	[def]$r2 :[sameMethod]$r2 = staticinvoke <com.android.launcher3.LauncherApplication: com.android.launcher3.LauncherApplication getAppContext()>() ---- in method: <com.anddoes.launcher.customscreen.ui.n: void k()>
	[def]$r2 :r0 = <com.android.launcher3.LauncherApplication: com.android.launcher3.LauncherApplication sContext> ---- in method: <com.android.launcher3.LauncherApplication: com.android.launcher3.LauncherApplication getAppContext()>
 */
                                    for(Local deflocal:def_Local){
                                        List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                body,
                                                (Stmt) unit,
                                                deflocal);
                                        for(String def:defs) {
                                            if(!def.split(" ---- in method: ")[1].startsWith(("<"+package_name))&&!def.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                //def是tpl内的
                                                if(flag_same==0){
                                                    flag_same=2;

                                                }
                                                break;
                                            }
                                        }
                                        if(flag_same==2){
                                            break;
                                        }
                                    }
                                }else{
                                    //sig在tpl中
                                    //在tpl中的PI语句只需要看初始语句是否在tpl内，然后再看是否经过app又回到tpl
                                    for(Local deflocal:def_Local){
                                        List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                body,
                                                (Stmt) unit,
                                                deflocal);
                                        for(String def:defs) {
                                            if(def.split(" ---- in method: ")[1].startsWith(("<"+package_name))||def.split(" ---- in method: ")[1].startsWith(("<dummyMainClass: "+package_name))){
                                                //def是app内的
                                                if(flag_same==0){
                                                    flag_same=2;

                                                }else if(flag_same==1){
                                                    flag_same=3;

                                                }
                                                break;
                                            }
                                        }

                                        if(flag_same==2||flag_same==3){
                                            break;
                                        }
                                    }
                                }
                            }
                            //下面思路是如果def和use始终在app或者在tpl，则不管，如果有任何一个满足交叉，那么输出

                            if(flag_same!=0){
                                if(!dataList2.contains(pi_data)){
                                    dataList2.add(pi_data);
                                }
                                //flag_same等于0表示unit，def，use都同在app或tpl内
                                String key=pi_data+":"+stmt_raw+" ---- in method: "+sootMethod.getSignature();
                                //先打印所有的use
                                if(!results_dic2.containsKey(key)){
                                    results_dic2.put(key,new ArrayList<>());
                                }
                                if(flag_app==0){
                                    results_dic2.get(key).add("flag_same:"+flag_same+"---- strat from app");
                                }else{
                                    results_dic2.get(key).add("flag_same:"+flag_same+"---- strat from tpl");
                                }
                                if(flag_same==1){
                                    if(flag_sinkInTpl==1){
                                        //说明要找sinkintpl的参数的use
                                        List<Local> def_Local_sinkUnit=getLocal_def(unit);
                                        if(def_Local_sinkUnit!=null){
                                            for(Local deflocal:def_Local_sinkUnit){
                                                List<String> defsOfSinkUnit = InterProcedureVariableAnalysis.findUses(
                                                        body,
                                                        (Stmt) unit,
                                                        deflocal);
                                                for(String useOfParam:defsOfSinkUnit){
                                                    results_dic2.get(key).add("[useOfParam]"+deflocal.toString()+" :"+useOfParam);
                                                }
                                                results_dic2.get(key).add("###next param");
                                            }
                                        }
                                    }
                                    results_dic2.get(key).add("-----------------");
                                    for(Local deflocal:def_Local){
                                        List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                body,
                                                (Stmt) unit,
                                                deflocal);
                                        for(String def:defs){
                                            results_dic2.get(key).add("[def]"+deflocal.toString()+" :"+def);
                                        }
                                        results_dic2.get(key).add("###next deflocal");
                                    }
                                    results_dic2.get(key).add("-----------------");
                                    for(String use:results){
                                        results_dic2.get(key).add("[use]"+tar_local.toString()+" :"+use);
                                    }
                                }else if(flag_same==2){
                                    if(flag_sinkInTpl==1){
                                        //说明要找sinkintpl的参数的use
                                        List<Local> def_Local_sinkUnit=getLocal_def(unit);
                                        if(def_Local_sinkUnit!=null){
                                            for(Local deflocal:def_Local_sinkUnit){
                                                List<String> defsOfSinkUnit = InterProcedureVariableAnalysis.findUses(
                                                        body,
                                                        (Stmt) unit,
                                                        deflocal);
                                                for(String useOfParam:defsOfSinkUnit){
                                                    results_dic2.get(key).add("[useOfParam]"+deflocal.toString()+" :"+useOfParam);
                                                }
                                                results_dic2.get(key).add("###next param");
                                            }
                                        }
                                    }
                                    for(Local deflocal:def_Local){
                                        List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                body,
                                                (Stmt) unit,
                                                deflocal);
                                        for(String def:defs){
                                            results_dic2.get(key).add("[def]"+deflocal.toString()+" :"+def);
                                        }
                                        results_dic2.get(key).add("###next deflocal");
                                    }
                                }else if(flag_same==3){
                                    if(flag_sinkInTpl==1){
                                        //说明要找sinkintpl的参数的use
                                        List<Local> def_Local_sinkUnit=getLocal_def(unit);
                                        if(def_Local_sinkUnit!=null){
                                            for(Local deflocal:def_Local_sinkUnit){
                                                List<String> defsOfSinkUnit = InterProcedureVariableAnalysis.findUses(
                                                        body,
                                                        (Stmt) unit,
                                                        deflocal);
                                                for(String useOfParam:defsOfSinkUnit){
                                                    results_dic2.get(key).add("[useOfParam]"+deflocal.toString()+" :"+useOfParam);
                                                }
                                                results_dic2.get(key).add("###next param");
                                            }
                                        }
                                    }
                                    //先打印use，再打印def
                                    for(String use:results){
                                        results_dic2.get(key).add("[use]"+tar_local.toString()+" :"+use);
                                    }
                                    results_dic2.get(key).add("--------------------------------");
                                    for(Local deflocal:def_Local){
                                        List<String> defs = InterProcedureVariableAnalysis.findDefs(
                                                body,
                                                (Stmt) unit,
                                                deflocal);
                                        for(String def:defs){
                                            results_dic2.get(key).add("[def]"+deflocal.toString()+" :"+def);
                                        }
                                        results_dic2.get(key).add("###next deflocal");
                                    }
                                }
                            }
                            //tpl->host结束
                        }
                    }
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
            if(sootClasses.getLast()==sootClass){
                break;
            }else{
                SootClass succ=sootClasses.getSuccOf(sootClass);
                sootClass=succ;
            }
        }
        String key="the data in this apk";
        results_dic1.put(key, new ArrayList<>());
        for(String pi:dataList1){
            if (!results_dic1.get(key).contains("\t"+pi)){
                results_dic1.get(key).add("\t"+pi);
            }
        }
        key="the data in this apk and delivered to the sink stmt";
        results_dic1.put(key, new ArrayList<>());
        for(String pi:data2sinkList1){
            if (!results_dic1.get(key).contains("\t"+pi)){
                results_dic1.get(key).add("\t"+pi);
            }
        }
        key="test number:";
        results_dic1.put(key, new ArrayList<>());
        if (!results_dic1.get(key).contains(String.valueOf(number))) {
            results_dic1.get(key).add(String.valueOf(number));
        }
        //tpl->host
        key="the data in this apk start from tpl";
        System.out.println("dataList size: "+dataList2.size());
        results_dic2.put(key, new ArrayList<>());
        for(String pi:dataList2){
            if (!results_dic2.get(key).contains("\t"+pi)){
                results_dic2.get(key).add("\t"+pi);
            }
        }
        results_dic[0]=results_dic1;
        results_dic[1]=results_dic2;
        return results_dic;
    }
    public static void CheckPI(String stmt, ArrayList<String> pi_list) {
        for (String pi_pack : target_dataset) {
            if (stmt.contains(pi_pack)) {
                pi_list.add(pi_pack + ":" + stmt);
            }
        }

    }

    public static void StorePIorTPL(HashMap<String, ArrayList<String>> results_dic,
                                    boolean flag_pi,
                                    ArrayList<String> pi_list, String sink_sig) {
        for (String pi : pi_list) {
            if (flag_pi && (!results_dic.get(sink_sig).contains("PI:" + pi))) {
                //flag_pi,说明
                results_dic.get(sink_sig).add("PI:" + pi);
            }
        }
    }

    public static boolean containInvokeExpr(Unit unit) {
        if (unit instanceof InvokeStmt) {
            return true;
        } else {
            if (unit instanceof AssignStmt) {
                AssignStmt assignStmt = (AssignStmt)unit;
                Value rValue = assignStmt.getRightOp();
                if (rValue instanceof InvokeExpr) {
                    return true;
                }
            }

            return false;
        }
    }

    public static SootMethod getCalleeMethod(Unit unit) {
        if (unit instanceof InvokeStmt) {
            InvokeStmt invokeStmt = (InvokeStmt)unit;
            return invokeStmt.getInvokeExpr().getMethod();
        } else {
            AssignStmt assignStmt = (AssignStmt)unit;
            Value rValue = assignStmt.getRightOp();
            InvokeExpr invokeExpr = (InvokeExpr)rValue;
            return invokeExpr.getMethod();
        }
    }
}
