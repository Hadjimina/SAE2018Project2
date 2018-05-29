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
		String result = "";
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
			

            Visitor visit = new Visitor(analysis, pointsToAnalysis);
            
        	for (JInvokeStmt call : analysis.setSpeedCalls) {
        		JVirtualInvokeExpr expr = (JVirtualInvokeExpr) call.getInvokeExpr();
        		PointsToSetInternal possibleConstructors = (PointsToSetInternal) pointsToAnalysis.reachingObjects((Local) expr.getBase());

        		visit.setCall(call);
        		possibleConstructors.forall(visit);
        	}
            
        	System.out.println(visit.getMaxResult());

			
		}
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