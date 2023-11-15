package TPL_analysis;

import Data_flow_analysis.InterProcedureVariableAnalysis;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static Host_app_analysis.Host_app_Util.*;
import static TPL_analysis.KzConstant.target_dataset;
import static TPL_analysis.TPL_Util.checkFolder;
import static TPL_analysis.TPL_Util.transAar2Jar;

public class Main_TPL {
    private static String root_results_folder="D:\\software\\_download\\TPL_PP\\tpl\\tpl_test\\tpl_result";
    private static String tmp_folder="D:\\software\\_download\\TPL_PP\\tpl\\tpl_test\\tpl_temp";

    ArrayList<String> sinks=new ArrayList<String>();
    ArrayList<String> sources=new ArrayList<>();
    public static void main(String[] args) throws IOException {
        Main_TPL m=new Main_TPL();

        m.sinks = getSinks("D:\\software\\idea\\ideaProject\\binary_code_analysis\\static_analysis\\src\\SourcesAndSinksRaw.txt");
        m.sources=ReadSources("D:\\software\\idea\\ideaProject\\binary_code_analysis\\static_analysis\\src\\SourceAndSink2.txt");
        String root_tpl_folder="D:\\software\\_download\\TPL_PP\\tpl\\tpl_test\\tpl_data";
        File tpl_file=new File(root_tpl_folder);
        File[] files=tpl_file.listFiles();
        for(File tpl:files){
            System.out.println("--------------------------------------");
            System.out.println("tpl: "+tpl.toString());
            //D:\software\_download\TPL_PP\tpl\tpl_ad\tpl_data\xxx.aar
            String output_path = root_results_folder + "/" + tpl.getName();
            checkFolder(output_path);
            String entrypoint_file = output_path + "/entrypoints.txt";
            String data_flow_file = output_path + "/data_flow.txt";

            System.out.println(entrypoint_file);
            System.out.println(data_flow_file);
            System.out.println(1);
            try {
                System.out.println(2);
                m.dealSingleTPL(tpl, output_path, entrypoint_file, data_flow_file);
            } catch (Exception e) {
                System.out.println(3);
                System.out.println(e.toString());
                e.printStackTrace();
                continue;
             }
        }

    }

    private void dealSingleTPL(File ver, String output_path, String entrypoint_file, String data_flow_file) throws Exception {
        System.out.println(4);
        String[] tmp1 = ver.toString().split("\\\\");
        String TPL_name = tmp1[tmp1.length - 2];
        String results_file = root_results_folder + "/" + TPL_name + ".txt";
        System.out.println("----result_file: "+results_file);
        if ((new File(results_file)).exists()) {
            System.out.println("exists!!!  =======  " + results_file);
            return;
        }
        String[] tmp = String.valueOf(ver).split("\\.");
        String tpl_type = tmp[tmp.length - 1];
        // get *.jar file
        String target_file = ver.getPath();
        if (tpl_type.equals("aar")) {
            target_file = transAar2Jar(ver, tmp_folder);
            System.out.println("----aar->jar done");
        }

        // get entry points
        SootEnvironmentForGraph.init(target_file, KzConfig.platformPath, output_path);
        //
        List<SootMethod> entrypoints = new ArrayList<>();
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(entrypoint_file));

        for (SootClass sootClass : Scene.v().getClasses()) {
            if (sootClass.isInterface() || sootClass.getMethods().size() == 0 || sootClass.isAbstract()) {
                continue;
            }
            for (SootMethod sootMethod : sootClass.getMethods()) {
                if (sootMethod.isPublic() && sootMethod.isConcrete()) {
                    entrypoints.add(sootMethod);
//                    System.out.println(sootMethod.getSignature() + '\n');
                    bw.write(sootMethod.getSignature() + '\n');
                }
            }
        }
        bw.close();
        // for testing
//        System.out.println( Scene.v().getMainMethod());
        System.out.println("----entry point number:  "+entrypoints.size());
        Scene.v().setEntryPoints(entrypoints);

        List<String> dataList=new LinkedList<String>();
        List<String> data2sinkList=new LinkedList<String>();
        BufferedWriter data_flow_writer = new BufferedWriter(
                new FileWriter(data_flow_file));
        int flag=0;
        for (SootClass sootClass : Scene.v().getClasses()) {
            for (SootMethod sootMethod : sootClass.getMethods()) {
//                Body body = sootMethod.retrieveActiveBody();
                Body body;
                if (!sootMethod.isConcrete()) {
                    continue;
                }
                try {
                    body = sootMethod.retrieveActiveBody();
                } catch (Exception e) {
                    continue;
                }

                PatchingChain<Unit> cUnits = body.getUnits();

                for (Unit unit : cUnits) {
                    String stmt_tmp = unit.toString().toLowerCase();


                    for (String pi_pack : sources) {
                        if (stmt_tmp.contains(pi_pack.toLowerCase())) {
                            System.out.println(pi_pack);
                            data_flow_writer.write("stmt:\t" + unit.toString() +"  in method: "+sootMethod.getSignature()+ "\n");
                            data_flow_writer.write("\t data \t" + pi_pack + "\n");
                            if(!dataList.contains(pi_pack)){
                                dataList.add(pi_pack);
                            }
                            Local tar_local = null;
                            tar_local=getLocal0(unit);
                            if(tar_local==null){
                                continue;
                            }
//                            if(unit instanceof AssignStmt){
//                                System.out.println("   assignmentstmt");
//                                System.out.println("   unit: "+unit.toString());
//                                System.out.println("   tarlocal: "+tar_local.toString());
//                            }else if(unit instanceof ReturnStmt){
//                                System.out.println("   returnstmt");
//                                System.out.println("   unit: "+unit.toString());
//                                System.out.println("   tarlocal: "+tar_local.toString());
//                            }
                            List<String> results = InterProcedureVariableAnalysis.findUses(
                                    body,
                                    (Stmt) unit,
                                    tar_local);

                            for (String unitValueBoxPair : results) {
                                for (String sink : sinks) {
                                    if (unitValueBoxPair.contains(sink)) {

                                        String sink_key = unitValueBoxPair;//sink_key就是找到的使用敏感信息的sink语句
                                        //System.out.println("\t === moon out put:\t" + unitValueBoxPair.toString());
                                        data_flow_writer.write("\t\t\t results::\t" + unitValueBoxPair + "\n");
                                        flag=1;
                                        if(!data2sinkList.contains(pi_pack)){
                                            data2sinkList.add(pi_pack);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (String target_data : target_dataset) {
                        if (stmt_tmp.contains(target_data.toLowerCase())) {
                            data_flow_writer.write("stmt:\t" + unit.toString() +"  in method: "+sootMethod.getSignature()+ "\n");
                            data_flow_writer.write("\t data \t" + target_data + "\n");
                            if(!dataList.contains(target_data)){
                                dataList.add(target_data);
                            }

                            Local tar_local = null;
                            tar_local=getLocal0(unit);
                            if(tar_local==null){
                                continue;
                            }

                            List<String> results = InterProcedureVariableAnalysis.findUses(
                                    body,
                                    (Stmt) unit,
                                    tar_local);

                            for (String unitValueBoxPair : results) {
                                for (String sink : sinks) {
                                    if (unitValueBoxPair.contains(sink)) {

                                        String sink_key = unitValueBoxPair;//sink_key就是找到的使用敏感信息的sink语句
                                        //System.out.println("\t === moon out put:\t" + unitValueBoxPair.toString());
                                        data_flow_writer.write("\t\t\t results::\t" + unitValueBoxPair + "\n");
                                        flag=1;
                                        if(!data2sinkList.contains(target_data)){
                                            data2sinkList.add(target_data);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        if(flag==0){
            data_flow_writer.write("no findings");
            System.out.println("no findings");
        }else{
            System.out.println("have some findings");
        }
        data_flow_writer.write("\n");
        data_flow_writer.write("the data in this tpl but not delivered to the sink stmt"+"\n");
        for(String str:dataList){
            data_flow_writer.write("\t"+str+"\n");
        }
        data_flow_writer.write("\n");
        data_flow_writer.write("the data in this tpl and delivered to the sink stmt"+"\n");
        if(data2sinkList.size()==0){
            data_flow_writer.write("\t"+"have no PI sent to sink"+"\n");
        }
        for(String str:data2sinkList){
            data_flow_writer.write("\t"+str+"\n");
        }
        data_flow_writer.close();
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
//                List<ValueBox> boxes=((JIdentityStmt)unit).getUseBoxes();
//                int len=boxes.size();
//                for (int i = 0; i < boxes.size(); i++) {
//                    if(boxes.get(i).getValue() instanceof Constant){
//
//                    }else if(boxes.get(i).getValue() instanceof Local){
//                        def_Local.add((Local)boxes.get(i).getValue());
//                    }
//                }
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
            Value v=((ReturnStmt) unit).getOpBox().getValue();
            if(v instanceof Local){
                tar_local=(Local)((ReturnStmt)unit).getOpBox().getValue();
            }
        }
//        else {
//            tar_local = (Local) ((JAssignStmt) unit).getLeftOpBox().getValue();
//        }
        return tar_local;
    }

    public static Local getLocal0(Unit unit){
        Local tar_local=null;
        //getlocal原版，本来tpl用的这个进行运行，后面改成getLocal，是app的获得Local方法
        if (unit instanceof IdentityStmt) {
            tar_local = (Local) ((JIdentityStmt) unit).leftBox.getValue();

        } else if (unit instanceof InvokeStmt) {
            for (ValueBox box : unit.getUseBoxes()) {
                if (box instanceof JimpleLocalBox) {
                    tar_local = (Local) box.getValue();
                    break;
                }
            }
        } else if (unit instanceof AssignStmt) {
            try{
                if( ((AssignStmt) unit).getLeftOpBox() instanceof JInstanceFieldRef) {
                    AssignStmt a=(AssignStmt) unit;
                    tar_local=(Local)a.getRightOpBox().getValue();
                    //tar_local = (Local) ((AssignStmt) unit).getRightOpBox().getValue();

                }else if ( ((AssignStmt) unit).getLeftOpBox().getValue() instanceof  StaticFieldRef){
                    AssignStmt a=(AssignStmt) unit;
                    if(a.getRightOpBox().getValue() instanceof NullConstant){
                        return null;
                    }else if(a.getRightOpBox().getValue() instanceof IntConstant){
                        //tar_local = (Local) ((AssignStmt) unit).getRightOpBox().getValue();
                        //tar_local=(Local)a.getLeftOpBox().getValue();
                        //这种情况：
                        //<com.appnexus.opensdk.utils.Settings: boolean simpleDomainUsageAllowed> = 1
                        return null;
                    }else if(a.getRightOpBox().getValue() instanceof StringConstant){
                        //tar_local = (Local) ((AssignStmt) unit).getRightOpBox().getValue();
                        //tar_local=(Local)a.getLeftOpBox().getValue();
                        //这种情况：
                        //<com.appnexus.opensdk.ut.UTConstants: java.lang.String REQUEST_BASE_URL_SIMPLE> = "https://ib.adnxs-simple.com/ut/v3"
                        return null;
                    }else if(a.getRightOpBox().getValue() instanceof LongConstant){
                        //tar_local = (Local) ((AssignStmt) unit).getRightOpBox().getValue();
                        //tar_local=(Local)a.getLeftOpBox().getValue();
                        //这种情况
                        //<com.unity3d.services.core.configuration.InitializeThread$InitializeStateNetworkError: long _lastConnectedEventTimeMs> = 0L
                        return null;
                    }
                    else{
                        tar_local=(Local)a.getRightOpBox().getValue();
                        //tar_local = (Local) ((AssignStmt) unit).getRightOpBox().getValue();
                    }

                }else  if ( ((AssignStmt) unit).getLeftOpBox().getValue() instanceof VariableBox){
                    tar_local = (Local) ((AssignStmt) unit).getLeftOpBox().getValue();
                }else  if ( ((AssignStmt) unit).getLeftOpBox() instanceof VariableBox){
                    if(((AssignStmt) unit).getLeftOpBox().getValue() instanceof JInstanceFieldRef){
                        return null;
                    }else if(((AssignStmt) unit).getLeftOpBox().getValue() instanceof JArrayRef){
                        JArrayRef leftOp=(JArrayRef)((AssignStmt) unit).getLeftOpBox().getValue();
                        tar_local=(Local)leftOp.getBaseBox().getValue();
                        //这种情况：
                        //$r2[1] = "AmazonCustomerByEmail"
                    }else{
                        tar_local = (Local) ((AssignStmt) unit).getLeftOpBox().getValue();
                    }

                }
            }catch(ClassCastException e){
                e.printStackTrace();
                System.out.println("-------"+e.toString());
                System.out.println(unit);
                System.out.println("######################");
            }
        } else if(unit instanceof ReturnStmt){
            Value v=((ReturnStmt) unit).getOpBox().getValue();
            if(v instanceof Local){
                tar_local=(Local)((ReturnStmt)unit).getOpBox().getValue();
            }
        }
        return tar_local;
    }
}
