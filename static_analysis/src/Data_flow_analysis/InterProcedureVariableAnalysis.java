package Data_flow_analysis;
// copyright moon

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.UnitValueBoxPair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class InterProcedureVariableAnalysis {
    public static List<String> findDefs(Body body, Stmt stmt, Local local){
        HashSet<SootMethod> visited = new HashSet<SootMethod>();
        List<Unit> intraDefs = IntraProcedureVariableAnalysis.findDefs(body, stmt, local);//找到方法内所有def变量
        SootMethod curMethod=body.getMethod();
        CallGraph cg = Scene.v().getCallGraph();
        List<String> defs = new ArrayList<String>();
        for(Unit intraDef:intraDefs){
            defs.add("[sameMethod]"+intraDef.toString()+" ---- in method: "+body.getMethod().getSignature());
            //1. invoke类型，def变量可能来自函数调用的返回值
            if(intraDef instanceof AssignStmt){
                AssignStmt assignUnit=(AssignStmt) intraDef;
                if (!assignUnit.containsInvokeExpr()) {
                    continue;
                }
                SootMethod callee = assignUnit.getInvokeExpr().getMethod();
                if (!callee.isConcrete()) continue;
                visited.add(callee);
                Body calleeBody = callee.retrieveActiveBody();
                UnitGraph calleeCfg = new BriefUnitGraph(calleeBody);
                List<Unit> tails = calleeCfg.getTails();
                for (Unit tail : tails) {
                    if (!(tail instanceof ReturnStmt))
                        continue;
                    ReturnStmt tgtStmt = (ReturnStmt) tail;
                    Value tgtValue = tgtStmt.getOp();
                    if (tgtValue instanceof Constant) {
                        Local fakeLocal = Jimple.v().newLocal("fakeLocal", tgtValue.getType());
                        AssignStmt fakeStmt = Jimple.v().newAssignStmt(fakeLocal, tgtValue);
                        defs.add(fakeStmt.toString()+" ---- in method: "+callee.getSignature());
                    } else if (tgtValue instanceof Local) {
                        Local tgtLocal = (Local) tgtValue;
                        findDefsUtil(calleeBody, tgtStmt, tgtLocal, cg, visited, defs);
                    }
                }
            }
            //2. parameterRef类型，说明def变量来自参数传递
            if(intraDef instanceof IdentityStmt){
                IdentityStmt identityUnit = (IdentityStmt) intraDef;
                Value identityRop = identityUnit.getRightOp();
                if (!(identityRop instanceof ParameterRef)) {
                    continue;
                }
                ParameterRef parameterRefUnit=(ParameterRef)identityRop;
                int index=parameterRefUnit.getIndex();
                Iterator<Edge> intoEdges=cg.edgesInto(curMethod);//通过call graph获得调用当前方法的源方法
                while (intoEdges.hasNext()){
                    Edge intoEdge=intoEdges.next();
                    SootMethod srcMethod=intoEdge.src();//获得源方法
                    if (visited.contains(srcMethod)) {
                        continue;
                    }
                    visited.add(srcMethod);
                    Stmt srcStmt = intoEdge.srcStmt();//源方法中调用curMethod的语句
                    defs.add(srcStmt.toString()+" ---- in method: "+srcMethod.getSignature());
                    Value srcArg = srcStmt.getInvokeExpr().getArg(index);
                    if(srcArg instanceof Constant){
                        //常量，说明def变量的初始定义在该语句，停止搜索
                        Local fakeLocal = Jimple.v().newLocal("fakeLocal", srcArg.getType());
                        AssignStmt fakeStmt = Jimple.v().newAssignStmt(fakeLocal, srcArg);
                        defs.add(fakeStmt.toString()+" ---- in method: "+srcMethod.getSignature());
                    }else if(srcArg instanceof Local){
                        Local srcLocal = (Local) srcArg;
                        findDefsUtil(srcMethod.retrieveActiveBody(), srcStmt, srcLocal, cg, visited, defs);
                    }
                }
            }
        }
        return defs;
    }
    private static void findDefsUtil(Body body, Stmt stmt, Local local,
                                      CallGraph cg, HashSet<SootMethod> visited,
                                      List<String> defs) {
        SootMethod curMethod=body.getMethod();
        //过程内def分析
        List<Unit> intraDefs = IntraProcedureVariableAnalysis.findDefs(body, stmt, local);
        for (Unit intraDef : intraDefs) {
            defs.add(intraDef.toString()+" ---- in method: "+curMethod.getSignature());
            //1. invoke类型，def变量可能来自函数调用的返回值
            if(intraDef instanceof AssignStmt){
                AssignStmt assignUnit=(AssignStmt) intraDef;
                if (!assignUnit.containsInvokeExpr()) {
                    continue;
                }
                SootMethod callee = assignUnit.getInvokeExpr().getMethod();
                if (!callee.isConcrete()) continue;
                visited.add(callee);
                Body calleeBody = callee.retrieveActiveBody();
                UnitGraph calleeCfg = new BriefUnitGraph(calleeBody);
                List<Unit> tails = calleeCfg.getTails();
                for (Unit tail : tails) {
                    if (!(tail instanceof ReturnStmt))
                        continue;
                    ReturnStmt tgtStmt = (ReturnStmt) tail;
                    Value tgtValue = tgtStmt.getOp();
                    if (tgtValue instanceof Constant) {
                        Local fakeLocal = Jimple.v().newLocal("fakeLocal", tgtValue.getType());
                        AssignStmt fakeStmt = Jimple.v().newAssignStmt(fakeLocal, tgtValue);
                        defs.add(fakeStmt.toString()+" ---- in method: "+callee.getSignature());
                    } else if (tgtValue instanceof Local) {
                        Local tgtLocal = (Local) tgtValue;
                        findDefsUtil(calleeBody, tgtStmt, tgtLocal, cg, visited, defs);
                    }
                }
            }
            //2. parameterRef类型，说明def变量来自参数传递
            if(intraDef instanceof IdentityStmt){
                IdentityStmt identityUnit = (IdentityStmt) intraDef;
                Value identityRop = identityUnit.getRightOp();
                if (!(identityRop instanceof ParameterRef)) {
                    continue;
                }
                ParameterRef parameterRefUnit=(ParameterRef)identityRop;
                int index=parameterRefUnit.getIndex();
                Iterator<Edge> intoEdges=cg.edgesInto(curMethod);//通过call graph获得调用当前方法的源方法
                while (intoEdges.hasNext()){
                    Edge intoEdge=intoEdges.next();
                    SootMethod srcMethod=intoEdge.src();//获得源方法
                    if (visited.contains(srcMethod)) {
                        continue;
                    }
                    visited.add(srcMethod);
                    Stmt srcStmt = intoEdge.srcStmt();//源方法中调用curMethod的语句
                    defs.add(srcStmt.toString()+" ---- in method: "+srcMethod.getSignature());
                    Value srcArg = srcStmt.getInvokeExpr().getArg(index);
                    if(srcArg instanceof Constant){
                        //常量，说明def变量的初始定义在该语句，停止搜索
                        Local fakeLocal = Jimple.v().newLocal("fakeLocal", srcArg.getType());
                        AssignStmt fakeStmt = Jimple.v().newAssignStmt(fakeLocal, srcArg);
                        defs.add(fakeStmt.toString()+" ---- in method: "+srcMethod.getSignature());
                    }else if(srcArg instanceof Local){
                        Local srcLocal = (Local) srcArg;
                        findDefsUtil(srcMethod.retrieveActiveBody(), srcStmt, srcLocal, cg, visited, defs);
                    }
                }
            }
        }
    }
    public static List<String> findUses(Body body, Stmt stmt, Local local) {
        HashSet<SootMethod> visited = new HashSet<SootMethod>();
        SootMethod curMethod=body.getMethod();
        visited.add(curMethod);
        CallGraph cg = Scene.v().getCallGraph();
        List<String> uses = new ArrayList<String>();
        List<Unit> intraDefs = IntraProcedureVariableAnalysis.findDefs(body, stmt, local);
        /*
         def 变量表示本地变量的定义点，而我们需要找到该变量在整个方法中的使用情况，
         包括在当前点之前和之后的使用情况。因此，在调用 findUsesBackward1 函数时，
         需要从定义点开始向后分析该变量的使用情况，以覆盖整个方法。
         */
        for (Unit intraDef : intraDefs) {
            if (intraDef instanceof IdentityStmt) {
                IdentityStmt identityUnit = (IdentityStmt) intraDef;
                Value identityRop = identityUnit.getRightOp();
                if (!(identityRop instanceof ParameterRef)) {
                    continue;
                }
                ParameterRef parameterRefUnit = (ParameterRef) identityRop;
                int index = parameterRefUnit.getIndex();
                Iterator<Edge> intoEdges = cg.edgesInto(curMethod);
                //找到指向当前method的这些调用边
                while (intoEdges.hasNext()) {
                    Edge intoEdge = intoEdges.next();
                    SootMethod caller = intoEdge.src();
                    //srcmethod调用tgtmethod
                    if (visited.contains(caller)) {
                        continue;
                    }
                    visited.add(caller);//表示已经处理过了
                    Stmt srcStmt = intoEdge.srcStmt();//The unit at which the call occurs
                    Value srcArg = srcStmt.getInvokeExpr().getArg(index);
                    if (srcArg instanceof Local) {
                        Local srcLocal = (Local) srcArg;
                        findUsesBckwardUtil(caller.retrieveActiveBody(), srcStmt, srcLocal, cg, visited, uses);
                    } else if (srcArg instanceof Constant) {
                        Local fakeLocal = Jimple.v().newLocal("fakeLocal", srcArg.getType());
                        AssignStmt fakeStmt = Jimple.v().newAssignStmt(fakeLocal, srcArg);
//                        ValueBox fakeValueBox = fakeStmt.getLeftOpBox();
//                        UnitValueBoxPair pair = new UnitValueBoxPair(fakeStmt, fakeValueBox);
                        uses.add(fakeStmt.toString()+" ---- in method: "+caller.getSignature());
                    }
                }
            }
            if (intraDef instanceof AssignStmt) {
                AssignStmt assignUnit = (AssignStmt) intraDef;
                if(!assignUnit.containsInvokeExpr()){
                    continue;
                }
                SootMethod callee = assignUnit.getInvokeExpr().getMethod();
                if (visited.contains(callee)) {
                    continue;
                }
                if (!callee.isConcrete())
                    continue;
                visited.add(callee);
                Body calleeBody = callee.retrieveActiveBody();
                UnitGraph calleeCfg = new BriefUnitGraph(calleeBody);
                List<Unit> tails = calleeCfg.getTails();
                for (Unit tail : tails) {
                    if (!(tail instanceof ReturnStmt))
                        continue;
                    ReturnStmt tgtStmt = (ReturnStmt) tail;
                    Value tgtValue = tgtStmt.getOp();
                    if (tgtValue instanceof Local) {
                        Local tgtLocal = (Local) tgtValue;
                        findUsesBckwardUtil(calleeBody, tgtStmt, tgtLocal, cg, visited, uses);
                    } else if (tgtValue instanceof Constant) {
                        Local fakeLocal = Jimple.v().newLocal("fakeLocal", tgtValue.getType());
                        AssignStmt fakeStmt = Jimple.v().newAssignStmt(fakeLocal, tgtValue);
//                        ValueBox fakeValueBox = fakeStmt.getLeftOpBox();
//                        UnitValueBoxPair pair = new UnitValueBoxPair(fakeStmt, fakeValueBox);
                        uses.add(fakeStmt.toString()+" ---- in method: "+callee.getSignature());
                    }
                }
            }
        }
        List<UnitValueBoxPair> intraUses = IntraProcedureVariableAnalysis.findUses(body, stmt, local);
        //finduses是一个粗略的分析，返回的结果既包含stmt前使用local的语句，也包含stmt后使用local的语句
        for (UnitValueBoxPair pair : intraUses) {
            Stmt intraUse = (Stmt) pair.unit;
            uses.add(pair.toString()+" ---- in method: "+body.getMethod().getSignature());
            //调用其他方法将local作为参数传递给其他方法
            if(intraUse instanceof AssignStmt){
                AssignStmt assignUnit=(AssignStmt) intraUse;
                if(assignUnit.containsInvokeExpr()&&assignUnit.getLeftOpBox()!=pair.getValueBox()){
                    SootMethod callee = intraUse.getInvokeExpr().getMethod();
                    if (visited.contains(callee)) {
                        continue;
                    }
                    if (!callee.isConcrete())
                        continue;
                    visited.add(callee);
                    int argIdx = -1;
                    for (int idx = 0; idx < intraUse.getInvokeExpr().getArgCount(); idx++) {
                        ValueBox value = intraUse.getInvokeExpr().getArgBox(idx);
                        if (pair.getValueBox() == value)
                            argIdx = idx;
                    }

                    Body calleeBody = callee.retrieveActiveBody();
                    Stmt tgtStmt = null;
                    Local tgtLocal = null;
                    for (Unit u : calleeBody.getUnits()) {
                        if (argIdx == -1) {
                            if (u instanceof IdentityStmt && ((IdentityStmt) u).getRightOp() instanceof ThisRef) {
                                tgtStmt = (Stmt) u;
                                tgtLocal = (Local) ((IdentityStmt) u).getLeftOp();
                                break;
                            }
                        } else {
                            if (u instanceof IdentityStmt && ((IdentityStmt) u).getRightOp() instanceof ParameterRef) {
                                if (((ParameterRef) ((IdentityStmt) u).getRightOp()).getIndex() == argIdx) {
                                    tgtStmt = (Stmt) u;
                                    tgtLocal = (Local) ((IdentityStmt) u).getLeftOp();
                                    break;
                                }
                            }
                        }
                    }
                    if (tgtStmt == null || tgtLocal == null)
                        continue;
                    findUsesForwardUtil(calleeBody, tgtStmt, tgtLocal, cg, visited, uses);
                }
            }
        }
        return uses;
    }
    private static void findUsesBckwardUtil(Body body,
                                             Stmt stmt, Local local, CallGraph cg,
                                             HashSet<SootMethod> visited,
                                             List<String> uses) {
        SootMethod curMethod=body.getMethod();
        List<Unit> intraDefs = IntraProcedureVariableAnalysis.findDefs(body, stmt, local);
        for (Unit intraDef : intraDefs) {
            if (intraDef instanceof IdentityStmt) {
                IdentityStmt identityUnit = (IdentityStmt) intraDef;
                Value identityRop = identityUnit.getRightOp();
                if (!(identityRop instanceof ParameterRef)) {
                    continue;
                }
                ParameterRef parameterRefUnit = (ParameterRef) identityRop;
                int index = parameterRefUnit.getIndex();
                Iterator<Edge> intoEdges = cg.edgesInto(curMethod);
                while (intoEdges.hasNext()) {
                    Edge intoEdge = intoEdges.next();
                    SootMethod caller = intoEdge.src();
                    if (visited.contains(caller)) {
                        continue;
                    }
                    visited.add(caller);
                    Stmt srcStmt = intoEdge.srcStmt();
                    Value srcArg = srcStmt.getInvokeExpr().getArg(index);
                    if (srcArg instanceof Local) {
                        Local srcLocal = (Local) srcArg;
                        findUsesBckwardUtil(caller.retrieveActiveBody(), srcStmt, srcLocal, cg, visited, uses);
                        //如果srcstmt里面仍然是一个变量，那么继续backward analysis
                    } else if (srcArg instanceof Constant) {
                        Local fakeLocal = Jimple.v().newLocal("fakeLocal", srcArg.getType());
                        AssignStmt fakeStmt = Jimple.v().newAssignStmt(fakeLocal, srcArg);
//                        ValueBox fakeValueBox = fakeStmt.getLeftOpBox();
//                        UnitValueBoxPair pair = new UnitValueBoxPair(fakeStmt, fakeValueBox);
                        uses.add(fakeStmt.toString()+" ---- in method: "+caller.getSignature());
                        //如果是一个常量，我们就认为找到了
                    }
                }
            }
            if (intraDef instanceof AssignStmt){
                AssignStmt assignUnit = (AssignStmt) intraDef;
                if(!assignUnit.containsInvokeExpr()){
                    continue;
                }
                SootMethod callee = assignUnit.getInvokeExpr().getMethod();
                if (visited.contains(callee)) {
                    continue;
                }
                if (!callee.isConcrete())
                    continue;
                visited.add(callee);
                Body calleeBody = callee.retrieveActiveBody();
                UnitGraph calleeCfg = new BriefUnitGraph(calleeBody);
                List<Unit> tails = calleeCfg.getTails();
                for (Unit tail : tails) {
                    if (!(tail instanceof ReturnStmt))
                        continue;
                    ReturnStmt tgtStmt = (ReturnStmt) tail;
                    Value tgtValue = tgtStmt.getOp();
                    if (tgtValue instanceof Local) {
                        Local tgtLocal = (Local) tgtValue;
                        findUsesBckwardUtil(calleeBody, tgtStmt, tgtLocal, cg, visited, uses);
                    } else if (tgtValue instanceof Constant) {
                        Local fakeLocal = Jimple.v().newLocal("fakeLocal", tgtValue.getType());
                        AssignStmt fakeStmt = Jimple.v().newAssignStmt(fakeLocal, tgtValue);
//                        ValueBox fakeValueBox = fakeStmt.getLeftOpBox();
//                        UnitValueBoxPair pair = new UnitValueBoxPair(fakeStmt, fakeValueBox);
                        uses.add(fakeStmt.toString()+" ---- in method: "+callee.getSignature());
                    }
                }
            }
        }
        List<UnitValueBoxPair> intraUses = IntraProcedureVariableAnalysis.findUsesBckward(body, stmt, local);
        for (UnitValueBoxPair pair : intraUses)
            uses.add("[backward] "+pair.toString()+" ---- in method: "+body.getMethod().getSignature());
    }
    private static void findUsesForwardUtil(Body body, Stmt stmt,
                                             Local local, CallGraph cg,
                                             HashSet<SootMethod> visited,
                                             List<String> uses) {
        SootMethod curMethod=body.getMethod();
        List<UnitValueBoxPair> intraUses = IntraProcedureVariableAnalysis.findUsesForward(body, stmt, local);



        for (UnitValueBoxPair pair : intraUses) {
            Stmt intraUse = (Stmt) pair.unit;
            uses.add(pair.toString()+" ---- in method: "+curMethod.getSignature());
            if(intraUse instanceof AssignStmt){
                AssignStmt assignUnit=(AssignStmt) intraUse;
                if(assignUnit.containsInvokeExpr()&&assignUnit.getLeftOpBox()!=pair.getValueBox()){
                    SootMethod callee = intraUse.getInvokeExpr().getMethod();
                    if (visited.contains(callee)) {
                        continue;
                    }
                    if (!callee.isConcrete())
                        continue;
                    visited.add(callee);
                    int argIdx = -1;
                    for (int idx = 0; idx < intraUse.getInvokeExpr().getArgCount(); idx++) {
                        ValueBox value = intraUse.getInvokeExpr().getArgBox(idx);
                        if (pair.getValueBox() == value)
                            argIdx = idx;
                    }

                    Body calleeBody = callee.retrieveActiveBody();
                    Stmt tgtStmt = null;
                    Local tgtLocal = null;
                    for (Unit u : calleeBody.getUnits()) {
                        if (argIdx == -1) {
                            if (u instanceof IdentityStmt && ((IdentityStmt) u).getRightOp() instanceof ThisRef) {
                                tgtStmt = (Stmt) u;
                                tgtLocal = (Local) ((IdentityStmt) u).getLeftOp();
                                break;
                            }
                        } else {
                            if (u instanceof IdentityStmt && ((IdentityStmt) u).getRightOp() instanceof ParameterRef) {
                                if (((ParameterRef) ((IdentityStmt) u).getRightOp()).getIndex() == argIdx) {
                                    tgtStmt = (Stmt) u;
                                    tgtLocal = (Local) ((IdentityStmt) u).getLeftOp();
                                    break;
                                }
                            }
                        }
                    }
                    if (tgtStmt == null || tgtLocal == null)
                        continue;
                    findUsesForwardUtil(calleeBody, tgtStmt, tgtLocal, cg, visited, uses);
                }
            }
        }
    }
}
