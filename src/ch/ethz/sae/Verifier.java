package ch.ethz.sae;

import gmp.Mpq;
import apron.Manager;

import java.util.ArrayList;
import java.util.HashMap;

import apron.ApronException;
import apron.Abstract1;
import apron.Interval;
import apron.Scalar;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.pag.PAG;
import soot.Local;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.toolkits.graph.BriefUnitGraph;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;

public class Verifier {
	
	private static String noSpeedSet = "NO SPEED SET";
	private static String functionName = "setSpeed";

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err
					.println("Usage: java -classpath soot-2.5.0.jar:./bin ch.ethz.sae.Verifier <class to test>");
			System.exit(-1);
		}
		
		String className = args[0];
		SootClass sootClass = loadClass(className);
		PAG pointsToAnalysis = doPointsToAnalysis(sootClass);
		int speedcounter = 0;
		String result = noSpeedSet;
		ArrayList<String> results = new ArrayList<String>();

		/* iterating over all test methods (you can assume that these is only one test method per test class) */
		for (SootMethod method : sootClass.getMethods()) {

			if (method.getName().contains("<init>")) {
				/* skipping the constructor of the class */
				continue;
			}
			Analysis analysis = new Analysis(new BriefUnitGraph(
					method.retrieveActiveBody()), sootClass);
			analysis.run();
	
			
			PointsToVisitor visitor = new PointsToVisitor(analysis, pointsToAnalysis);
			
			for (JInvokeStmt call : analysis.setSpeedCalls) {
        		
        		// Get invoking expression
        		JVirtualInvokeExpr vInvokeExpr = (JVirtualInvokeExpr) call.getInvokeExpr();
        		
        		// Extract robot
        		Value callingCar = vInvokeExpr.getBase();
        		System.out.println(callingCar.toString());
        		
        		// Find the possible constructors of that robot
        		PointsToSetInternal possibleConstructors = (PointsToSetInternal) pointsToAnalysis.reachingObjects((Local) callingCar);
        		
        		// Set call
        		visitor.setCall(call);
        		
        		// Iterate over all possible constructors and call the visit method of visitor
        		possibleConstructors.forall(visitor);
        	}
			
			/*
			for(JReturnStmt rt: analysis.returnStmts)
			{
				 Value v = rt.getOpBox().getValue();
				 
				 PointsToSet pts = pointsToAnalysis.reachingObjects((Local) v);
				 PointsToSetInternal ptsi = (PointsToSetInternal) pts;
				 
				 //ptsi.forall(v);
				 int i = 0;
			}
			PointsToVisitor visitor = new PointsToVisitor(analysis, pointsToAnalysis);
			
			
			/*
			int abstractNum = 0;
			for(JInvokeStmt call : analysis.setSpeedCalls){
				
				speedcounter++;
				
				Abstract1 before = analysis.setSpeedAbstract.get(abstractNum);
				
				
				abstractNum++;
				
				JVirtualInvokeExpr virExpr = (JVirtualInvokeExpr) call.getInvokeExprBox().getValue();
				Value v = virExpr.getArg(0);
				
				Interval i = null;				
	
				if (v instanceof IntConstant) {
					i = new Interval(new Mpq(v.toString()),new Mpq(v.toString()));
				}else{
					try {
						i = before.getBound(analysis.man, v.toString());
					} catch (ApronException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
										
				if(i.isBottom()|| speedcounter == 0){
					result = noSpeedSet;
				}else if (i.sup().isInfty() == 1){
					result = ""+Integer.MAX_VALUE;
				}
				else{
					result = i.sup().toString();
				}
				
				results.add(result);
			}
			*/
			
			result = getMaxResult(results);
			System.out.println(result);
		}
	}
	
	public static String getMaxResult(ArrayList<String> list){
		String toReturn = noSpeedSet;
		int currentMax = Integer.MIN_VALUE;
		boolean isSet = false;
		for(String  s: list){
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
	
	/* =================================================================================== */

	/* no need to change the methods below; they are already used in the main method above */

	/* load Soot class */
	private static SootClass loadClass(String name) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		return c;
	}

	/* run the Soot Spark points-to analysis */
	private static PAG doPointsToAnalysis(SootClass c) {
		Scene.v().setEntryPoints(c.getMethods());

		HashMap<String, String> options = new HashMap<String, String>();
		options.put("enabled", "true");
		options.put("verbose", "false");
		options.put("propagator", "worklist");
		options.put("simple-edges-bidirectional", "false");
		options.put("on-fly-cg", "true");
		options.put("set-impl", "double");
		options.put("double-set-old", "hybrid");
		options.put("double-set-new", "hybrid");

		SparkTransformer.v().transform("", options);
		PAG pag = (PAG) Scene.v().getPointsToAnalysis();

		return pag;
	}
}