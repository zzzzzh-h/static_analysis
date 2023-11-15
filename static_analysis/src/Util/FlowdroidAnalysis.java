//package Util;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.Iterator;
//
//import org.apache.commons.io.FilenameUtils;
//
//import soot.Body;
//import soot.Scene;
//import soot.SootClass;
//import soot.SootMethod;
//import soot.Unit;
//import soot.jimple.Jimple;
//import soot.jimple.Stmt;
//import soot.options.Options;
//import soot.toolkits.graph.Block;
//import soot.toolkits.graph.BlockGraph;
//import soot.toolkits.graph.ExceptionalBlockGraph;
//import soot.util.dot.DotGraph;
//import soot.util.queue.QueueReader;
//
//public class FlowdroidAnalysis {
//
//    /**
//     * 使用Soot和Flowdroid生成指定方法内两个Jimple句子之间的控制流图，并输出.dot文件
//     *
//     * @param apkFile          APK文件路径
//     * @param methodSignature1 句子1所在的方法的方法签名
//     * @param stmt1            Jimple句子1
//     * @param methodSignature2 句子2所在的方法的方法签名
//     * @param stmt2            Jimple句子2
//     * @param outputFile       输出.dot文件路径
//     */
//    public static void generateCFG(String apkFile, String methodSignature1, String stmt1, String methodSignature2, String stmt2,
//                                   String outputFile) {
//        // 设置Soot的参数
//        Options.v().set_allow_phantom_refs(true);
//        Options.v().set_whole_program(true);
//        Options.v().set_process_dir(Collections.singletonList(apkFile));
//        Options.v().set_android_jars(System.getenv("ANDROID_HOME") + "/platforms");
//
//        // 加载类
//        Scene.v().loadNecessaryClasses();
//        SootClass clazz1 = Scene.v().forceResolve(methodSignature1.substring(0, methodSignature1.indexOf("(")), SootClass.BODIES);
//        clazz1.setApplicationClass();
//        SootMethod method1 = clazz1.getMethod(methodSignature1.substring(methodSignature1.indexOf("(") + 1));
//        Body body1 = method1.retrieveActiveBody();
//        SootClass clazz2 = Scene.v().forceResolve(methodSignature2.substring(0, methodSignature2.indexOf("(")), SootClass.BODIES);
//        clazz2.setApplicationClass();
//        SootMethod method2 = clazz2.getMethod(methodSignature2.substring(methodSignature2.indexOf("(") + 1));
//        Body body2 = method2.retrieveActiveBody();
//
//        // 获取方法内所有的Jimple语句
//        Iterator<Unit> stmts1 = body1.getUnits().snapshotIterator();
//        Iterator<Unit> stmts2 = body2.getUnits().snapshotIterator();
//
//        // 找到Jimple句子1所在的语句和块
//        Stmt targetStmt1 = (Stmt) Jimple.v().newStmtBox(Jimple.v().newNopStmt()).getUnit();
//        while (stmts1.hasNext()) {
//            Unit stmt = stmts1.next();
//            if (stmt.toString().equals(stmt1)) {
//                targetStmt1 = (Stmt) stmt;
//                break;
//            }
//        }
//        Block targetBlock1 = (Block) body1.getUnits().getPredOf(targetStmt1);
//
//        // 找到Jimple句子2所在的语句和块
//        Stmt targetStmt2 = (Stmt) Jimple.v().newStmtBox(Jimple.v().newNopStmt()).getUnit();
//        while (stmts2.hasNext()) {
//            Unit stmt = stmts2.next();
//            if (stmt.toString().equals(stmt2)) {
//                targetStmt2 = (Stmt) stmt;
//                break;
//            }
//        }
//        Block targetBlock2 = (Block) body2.getUnits().getPredOf(targetStmt2);
//
//        // 构建CFG
//        BlockGraph cfg = new ExceptionalBlockGraph(body1);
//
//        // 从CFG中提取指定块之间的控制流
//        DotGraph dotGraph = new DotGraph(method1.getSignature() + "_to_" + method2.getSignature());
//        QueueReader<Block> blocks = new QueueReader<>(cfg.getBlocksBetween(targetBlock1, targetBlock2));
//        while (blocks.hasNext()) {
//            Block block = blocks.next();
//            dotGraph.drawBlock(block);
//            dotGraph.drawEdges(block);
//        }
//
//        // 输出.dot文件
//        try (FileWriter writer = new FileWriter(outputFile)) {
//            writer.write(dotGraph.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}