package ch.ethz.sae;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import soot.jimple.InvokeExpr;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import apron.Abstract1;
import apron.ApronException;
import apron.Environment;
import apron.Manager;
import apron.Polka;
import apron.Tcons1;
import apron.Texpr1BinNode;
import apron.Texpr1Intern;
import apron.Texpr1Node;
import apron.Texpr1VarNode;
import soot.IntegerType;
import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Stmt;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JMulExpr;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JSubExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.LoopNestTree;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;
import soot.util.Chain;
import gmp.Mpq;
import apron.Manager;
import apron.Abstract1;
import apron.ApronException;
import apron.Interval;
import apron.MpqScalar;
import apron.Tcons1;
import apron.Texpr1BinNode;
import apron.Texpr1CstNode;
import apron.Texpr1Intern;
import apron.Texpr1Node;
import apron.Texpr1VarNode;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;

public class Visitor extends P2SetVisitor {

	private static String noSpeedSet = "NO SPEED SET";
	private final Analysis analysis;
	private final ArrayList<AllocNode> carSet;
	private final ArrayList<Value> returnSet;
	private JInvokeStmt call;
	public String result;
	private  static ArrayList<String> resultList;
	
public Visitor(Analysis analysis, PAG pointsToAnalysis) {
	
		//Set class variables
		this.analysis = analysis;
		this.carSet = new ArrayList<AllocNode>();
		this.returnSet = new ArrayList<Value>();
		this.resultList = new ArrayList<String>();
		this.result= noSpeedSet;

		//loop over return statements of class
		for(JReturnStmt ret: analysis.returnStmts){
			// Get the robot the constructor was set on
			Value v = ret.getOpBox().getValue();
			returnSet.add(v);
							
		}
		
		// Loop over all initCalls of the analysed class
		for (JSpecialInvokeExpr initExp : analysis.setCarConstructors) {
						
			JimpleLocal car = (JimpleLocal) initExp.getBase();
			
			PointsToSetInternal setOfConstructors = (PointsToSetInternal) pointsToAnalysis.reachingObjects(car);
			
			workaround sw = new workaround();
			setOfConstructors.forall(sw);

			for (Node call : sw.list) {
				carSet.add((AllocNode) call);
			}	
		}
		
	}

	@Override
	public void visit(Node arg0) {
		AllocNode local = (AllocNode) arg0;
		JVirtualInvokeExpr tmp = (JVirtualInvokeExpr) call.getInvokeExprBox().getValue();
		Value argument = tmp.getArg(0);
		Value carObject = tmp.getBaseBox().getValue();
		
		resultList.add(getReturnVal(argument, carObject));
	}
	
	public String getReturnVal(Value arg, Value car){
		if(!returnSet.contains(car))
			return noSpeedSet;
		
		
		String tempResult = "";
		Abstract1 before = analysis.getFlowBefore(call).get();
		
		
		Interval i = null;				

		//if int we do not check if it is reachable
		SootApronConverter converter = new SootApronConverter();
		try {
		if (arg instanceof IntConstant) {
			Texpr1Node val = converter.convertValueExpression(arg);
			Texpr1Intern exp = new Texpr1Intern(analysis.env, val);
			
			 i = before.getBound(analysis.man, exp);
			 
			
		}else{
			
				i = before.getBound(analysis.man, arg.toString());
			
		}} catch (ApronException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		if(i.isBottom()){
			tempResult = noSpeedSet;
		}else if (i.sup().isInfty() == 1){
			tempResult = ""+Integer.MAX_VALUE;
		}else if(i.sup().isInfty()==-1){
			tempResult = ""+Integer.MIN_VALUE;
		}
		else{
			tempResult = i.sup().toString();
		}
		
		
		return tempResult;	
		
	}
	
	public static String getMaxResult(){
		String toReturn = noSpeedSet;
		int currentMax = Integer.MIN_VALUE;
		boolean isSet = false;
		for(String  s: resultList){
			if(isNumeric(s)){
				isSet = true;
				int current = Integer.parseInt(s);
				if(currentMax < current){
					currentMax = current;
				}
			}
		}
		if(isSet){
			return currentMax+"";
		}else{
			return toReturn;
		}
		
	}
	
	public static boolean isNumeric(String str)
	{
		try
		{
			double d = Double.parseDouble(str);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		
		return true;
	}
	

	//ONly called on setSpeed statments
	public void setCall(JInvokeStmt call) {
		this.call = call;			
	}
}
