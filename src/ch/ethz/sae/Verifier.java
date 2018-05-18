package ch.ethz.sae;

import gmp.Mpq;

import java.util.HashMap;

import apron.ApronException;
import apron.Interval;
import apron.Scalar;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.pag.PAG;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.toolkits.graph.BriefUnitGraph;

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

		/* iterating over all test methods (you can assume that these is only one test method per test class) */
		for (SootMethod method : sootClass.getMethods()) {

			if (method.getName().contains("<init>")) {
				/* skipping the constructor of the class */
				continue;
			}
			Analysis analysis = new Analysis(new BriefUnitGraph(
					method.retrieveActiveBody()), sootClass);
			analysis.run();
			
			for(Unit u : method.getActiveBody().getUnits()){
				
				if(u instanceof JInvokeStmt){
					Stmt stmt = (Stmt) u;
					InvokeExpr expr = stmt.getInvokeExpr();
					if(expr.getMethod().getName().equals(functionName)){
						speedcounter++;
						
						AWrapper before = analysis.getFlowBefore(u);
						Value  v = expr.getArgs().get(0); 
						
						//MAYBE CHECK IF INT CONSTANT (v)
						Interval i = null;
						
        				if (v instanceof IntConstant) {
        					i = new Interval(new Mpq(v.toString()),new Mpq(v.toString()));
        				}else{
        					
        					try {
								i = before.get().getBound(Analysis.man, v.toString());
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
							
					}
				}
			}
			
			System.out.println(result);
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