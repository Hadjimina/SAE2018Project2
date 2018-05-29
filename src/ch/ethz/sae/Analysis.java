package ch.ethz.sae;

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

/* numerical analysis */
public class Analysis extends ForwardBranchedFlowAnalysis<AWrapper> {

	/* Apron abstract domain instance */
	public static Manager man;

	/* Apron environment */
	public static Environment env;

	//list of calls
	public ArrayList<JInvokeStmt> setSpeedCalls = new ArrayList<JInvokeStmt>();
	public ArrayList<JSpecialInvokeExpr> setCarConstructors = new ArrayList<JSpecialInvokeExpr>();
	
	//list of return statements
	public ArrayList<JReturnStmt> returnStmts = new ArrayList<JReturnStmt>();
	
	public ArrayList<JSpecialInvokeExpr> initCalls = new ArrayList<JSpecialInvokeExpr>();
	//list type
	private static List<String> intTypes = Arrays.asList("int","short","byte");
	
	//Converter
	public static SootApronConverter converter = new SootApronConverter();
	
	public void run() {
		doAnalysis();
	}

	@Override
	protected void flowThrough(AWrapper inWrapper, Unit op,
			List<AWrapper> fallOutWrappers, List<AWrapper> branchOutWrappers) {
		Stmt s = (Stmt) op;
		Abstract1 o_fallout = null, o_branchout = null;
		boolean invalidFlag = false;

		try {
			if (s instanceof DefinitionStmt) {
				/* assignment statement */
				o_fallout = new Abstract1(man, inWrapper.get());
				o_branchout = new Abstract1(man, inWrapper.get());

				DefinitionStmt sd = (DefinitionStmt) s;
				Value lhs = sd.getLeftOp();
				Value rhs = sd.getRightOp();
				
				if (lhs instanceof JimpleLocal) {

					if (rhs instanceof IntConstant) {
						Texpr1Node val = converter.convertValueExpression(rhs);
						Texpr1Intern exp = new Texpr1Intern(env, val);
						o_fallout.assign(man, lhs.toString(), exp, null);	
						assignmentIterFallout(inWrapper, fallOutWrappers, o_fallout, inWrapper.get().meetCopy(man, o_fallout));
					}
					else if (rhs instanceof JimpleLocal) {
						if (isInt(rhs)) {
							Texpr1Node val = converter.convertValueExpression(rhs);
							Texpr1Intern exp = new Texpr1Intern(env, val);
							o_fallout.assign(man, lhs.toString(), exp, null);
							assignmentIterFallout(inWrapper, fallOutWrappers, o_fallout, inWrapper.get().meetCopy(man, o_fallout));
							
						} else {
							invalidFlag = true;
						}
					} else if ( rhs instanceof JAddExpr ) {
						if (isInt(rhs)) { //TODO delete isInt ???
							Texpr1Intern[] a1 = converter.convertArithExpression(rhs, env);						
							o_fallout.assign(man, lhs.toString(), a1[0], null);	
							Abstract1 temp = null;
							
							if (((JAddExpr) rhs).getOp1() instanceof JimpleLocal) {
								if (((JAddExpr) rhs).getOp2() instanceof JimpleLocal) {
									if (lhs == ((JAddExpr) rhs).getOp1() || lhs == ((JAddExpr) rhs).getOp2()) {
										temp = inWrapper.get().substituteCopy(man, lhs.toString(), a1[1], null);
									} else {
										temp = inWrapper.get().meetCopy(man, o_fallout);		
									}			
								} else {
									if (lhs == ((JAddExpr) rhs).getOp1()) {
										temp = inWrapper.get().substituteCopy(man, lhs.toString(), a1[1], null);
									} else {
										temp = inWrapper.get().meetCopy(man, o_fallout);		
									}						
								}
							} else {																			
								if (lhs == ((JAddExpr) rhs).getOp1()) {
									if (lhs == ((JAddExpr) rhs).getOp2()) {
										temp = inWrapper.get().substituteCopy(man, lhs.toString(), a1[1], null);
									} else {
										temp = inWrapper.get().meetCopy(man, o_fallout);		
									}
									assignmentIterFallout(inWrapper, fallOutWrappers, o_fallout, temp);													
								} else {
									temp = inWrapper.get().meetCopy(man, o_fallout);							
								}
							}
							
							assignmentIterFallout(inWrapper, fallOutWrappers, o_fallout, temp);		
						}
					}
					else if (rhs instanceof JSubExpr) {
						if (isInt(rhs)) {
							Texpr1Intern[] a1 = converter.convertArithExpression(rhs, env);
							o_fallout.assign(man, lhs.toString(), a1[0], null);	
							Abstract1 temp = null;
							
							if (((JSubExpr) rhs).getOp1() instanceof JimpleLocal) {

								if (((JSubExpr) rhs).getOp2() instanceof JimpleLocal) {
									if (lhs == ((JSubExpr) rhs).getOp1() || lhs == ((JSubExpr) rhs).getOp2()) {
										temp = inWrapper.get().substituteCopy(man, lhs.toString(), a1[1], null);
									} else {
										temp = inWrapper.get().meetCopy(man, o_fallout);		
									}								
								} else {
									if (lhs == ((JSubExpr) rhs).getOp1())  {
										temp = inWrapper.get().substituteCopy(man, lhs.toString(), a1[1], null);
									} else {
										temp = inWrapper.get().meetCopy(man, o_fallout);		
									}
								}
							} else {
								if (((JSubExpr) rhs).getOp2() instanceof JimpleLocal) {
									if (lhs == ((JSubExpr) rhs).getOp1())  { // TODO maybe getOp2??
										temp = inWrapper.get().substituteCopy(man, lhs.toString(), a1[1], null);
									} else {
										temp = inWrapper.get().meetCopy(man, o_fallout);		
									}
								} else {
									temp = inWrapper.get().meetCopy(man, o_fallout);		
								}
							}
			
							assignmentIterFallout(inWrapper, fallOutWrappers, o_fallout, temp);
						}
					} else if (rhs instanceof JMulExpr) {
						if (isInt(rhs)) {
							Texpr1Intern[] a1 = converter.convertArithExpression(rhs, env);
							o_fallout.assign(man, lhs.toString(), a1[0], null);	
							Abstract1 temp = null;
							
							if (((JMulExpr) rhs).getOp1() instanceof JimpleLocal) {
								if (((JMulExpr) rhs).getOp2() instanceof JimpleLocal) {
									if (lhs == ((JMulExpr) rhs).getOp1() || lhs == ((JMulExpr) rhs).getOp2())  {
										temp = inWrapper.get().substituteCopy(man, lhs.toString(), a1[1], null);
									} else {
										temp = inWrapper.get().meetCopy(man, o_fallout);		
									}
	
								} else {
									if (lhs == ((JMulExpr) rhs).getOp1())  {
										temp = inWrapper.get().substituteCopy(man, lhs.toString(), a1[1], null);
									} else {
										temp = inWrapper.get().meetCopy(man, o_fallout);		
									}	
								}
							} else {
								if (((JMulExpr) rhs).getOp2() instanceof JimpleLocal) {
									
									if (lhs == ((JMulExpr) rhs).getOp2()) {
										temp = inWrapper.get().substituteCopy(man, lhs.toString(), a1[1], null);
									} else {
										temp = inWrapper.get().meetCopy(man, o_fallout);		
									}
								} else {
									temp = inWrapper.get().meetCopy(man, o_fallout);			
								}
							}
							assignmentIterFallout(inWrapper, fallOutWrappers, o_fallout, temp);
						}
					}else {
						invalidFlag = true;
					}
				}
				
				 
			} else if (s instanceof JIfStmt) {
				IfStmt Sif= (JIfStmt) s;
				Value c = Sif.getCondition();
				AbstractBinopExpr cond = (AbstractBinopExpr) Sif.getCondition();
				Value valL = cond.getOp1();
				Value valR = cond.getOp2();
				Texpr1Node nodeL = converter.convertValueExpression(valL);
				Texpr1Node nodeR = converter.convertValueExpression(valR);

				Texpr1Node l_r = new Texpr1BinNode(Texpr1BinNode.OP_SUB, nodeL, nodeR);
				Texpr1Node r_l = new Texpr1BinNode(Texpr1BinNode.OP_SUB, nodeR, nodeL);

				Texpr1Intern expR_L = new Texpr1Intern(env,r_l);
				Texpr1Intern expL_R = new Texpr1Intern(env,l_r);
				Tcons1[] tcs1 = null;
				Tcons1[] tcs2 = null;

				if (c instanceof JEqExpr) {
				tcs1 = new Tcons1[]{ new Tcons1(Tcons1.EQ, expR_L) };
				tcs2 = new Tcons1[]{ new Tcons1(Tcons1.DISEQ, expR_L) };
				} else if (c instanceof JGeExpr) {
				tcs1 = new Tcons1[]{ new Tcons1(Tcons1.SUPEQ, expL_R) };
				tcs2 = new Tcons1[]{ new Tcons1(Tcons1.SUP, expR_L) };
				} else if (c instanceof JGtExpr) {
				tcs1 = new Tcons1[]{ new Tcons1(Tcons1.SUP, expL_R) };
				tcs2 = new Tcons1[] { new Tcons1(Tcons1.SUPEQ, expR_L) };
				} else if (c instanceof JLeExpr) {
				tcs1 = new Tcons1[]{ new Tcons1(Tcons1.SUPEQ, expR_L) };
				tcs2 = new Tcons1[]{ new Tcons1(Tcons1.SUP, expL_R) };
				} else if (c instanceof JLtExpr) {
				tcs1 = new Tcons1[]{ new Tcons1(Tcons1.SUP, expR_L) };
				tcs2 = new Tcons1[]{ new Tcons1(Tcons1.SUPEQ, expL_R) };
				} else if (c instanceof JNeExpr) {
				tcs1 = new Tcons1[]{ new Tcons1(Tcons1.DISEQ, expR_L) };
				tcs2 = new Tcons1[]{ new Tcons1(Tcons1.EQ, expR_L) };
				} else {
				invalidFlag = true;
				}

				if(!invalidFlag){
				o_fallout = new Abstract1(man, tcs1);
				o_branchout = new Abstract1(man, tcs2);

				assignmentIterFallout(inWrapper, fallOutWrappers, o_fallout, inWrapper.get().meetCopy(man, o_fallout));
				assignmentIterBranchout(inWrapper, fallOutWrappers, o_fallout, inWrapper.get().meetCopy(man, o_branchout));
				}
			}else if(s instanceof JReturnStmt){
				
				//pass
				o_fallout = new Abstract1(man, inWrapper.get());
				o_branchout = new Abstract1(man, inWrapper.get());
				
				assignmentIterFallout(inWrapper, fallOutWrappers, o_fallout, inWrapper.get().meetCopy(man, o_fallout));
				assignmentIterBranchout(inWrapper, fallOutWrappers, o_fallout, inWrapper.get().meetCopy(man, o_branchout));	
				
				JReturnStmt stmt = (JReturnStmt) s;
				returnStmts.add(stmt);
				
			} else if (s instanceof JInvokeStmt){
				o_fallout = new Abstract1(man, inWrapper.get());
				o_branchout = new Abstract1(man, inWrapper.get());
				
				assignmentIterFallout(inWrapper, fallOutWrappers, o_fallout, inWrapper.get().meetCopy(man, o_fallout));
				assignmentIterBranchout(inWrapper, fallOutWrappers, o_fallout, inWrapper.get().meetCopy(man, o_branchout));	
				
				JInvokeStmt stmt = (JInvokeStmt)s;
				InvokeExpr expr = ((JInvokeStmt) s).getInvokeExpr();
				String functName = expr.getMethod().getName();
				String className = expr.getMethod().getDeclaringClass().getName();
				
				
				if(className.equals("Car") && functName.equals("setSpeed")){
					setSpeedCalls.add(stmt);
				
				}
				else if (className.equals("Car") && functName.equals("<init>")) {
					if (!initCalls.contains(expr)) initCalls.add((JSpecialInvokeExpr) expr);
				}
				
			}
			else {
				invalidFlag = true;	
			}
			
			if(invalidFlag){
					if (!fallOutWrappers.isEmpty()) {
						AWrapper f = fallOutWrappers.get(0);
						f.set(inWrapper.get());
					}
					if (!branchOutWrappers.isEmpty()) {
						AWrapper b = branchOutWrappers.get(0);
						b.set(inWrapper.get());
					}
				
			}	
			}catch (ApronException e) {
					e.printStackTrace();
					System.exit(1);
		}	
	}

	
	public static void assignmentIterFallout(AWrapper inWrapper , List<AWrapper> fallOutWrappers,Abstract1 o_fallout,  Abstract1 toSetF){
		
			for (Iterator<AWrapper> it = fallOutWrappers.iterator(); it.hasNext();) {
				AWrapper op1 = it.next();
				
				if (o_fallout != null) {
					op1.set(toSetF);
				}
			}
		}
	
	public static void assignmentIterBranchout(AWrapper inWrapper, List<AWrapper> branchOutWrappers, Abstract1 o_branchout,Abstract1 toSetB){
		
		for (Iterator<AWrapper> it = branchOutWrappers.iterator(); it.hasNext();) {
			AWrapper op1 = it.next();
			if (o_branchout != null) {
				op1.set(toSetB);
			}
		}
	}
	
	public static void setInvalidExpr(AWrapper inWrapper, List<AWrapper> branchOutWrappers, Abstract1 o_branchout, List<AWrapper> fallOutWrappers,Abstract1 o_fallout){
		
		if (!fallOutWrappers.isEmpty()) {
			assignmentIterFallout(inWrapper, fallOutWrappers, o_fallout, inWrapper.get());
		}
		if (!branchOutWrappers.isEmpty()) {
			assignmentIterBranchout(inWrapper, branchOutWrappers, o_branchout, inWrapper.get());
		}
			
	}

	/* ======================================================== */

	/* no need to use or change the variables and methods below */

	/* collect all the local int variables in the class */
	private void recordIntLocalVars() {

		Chain<Local> locals = g.getBody().getLocals();

		int count = 0;
		Iterator<Local> it = locals.iterator();
		while (it.hasNext()) {
			JimpleLocal next = (JimpleLocal) it.next();
			if (next.getType() instanceof IntegerType)
				count += 1;
		}

		local_ints = new String[count];

		int i = 0;
		it = locals.iterator();
		while (it.hasNext()) {
			JimpleLocal next = (JimpleLocal) it.next();
			String name = next.getName();
			if (next.getType() instanceof IntegerType)
				local_ints[i++] = name;
		}
	}

	/* collect all the int fields of the class */
	private void recordIntClassVars() {

		Chain<SootField> ifields = jclass.getFields();

		int count = 0;
		Iterator<SootField> it = ifields.iterator();
		while (it.hasNext()) {
			SootField next = it.next();
			if (next.getType() instanceof IntegerType)
				count += 1;
		}

		class_ints = new String[count];

		int i = 0;
		it = ifields.iterator();
		while (it.hasNext()) {
			SootField next = it.next();
			String name = next.getName();
			if (next.getType() instanceof IntegerType)
				class_ints[i++] = name;
		}
	}

	/* build an environment with integer variables */
	public void buildEnvironment() {

		recordIntLocalVars();
		recordIntClassVars();

		String ints[] = new String[local_ints.length + class_ints.length];

		/* add local ints */
		for (int i = 0; i < local_ints.length; i++) {
			ints[i] = local_ints[i];
		}

		/* add class ints */
		for (int i = 0; i < class_ints.length; i++) {
			ints[local_ints.length + i] = class_ints[i];
		}

		env = new Environment(ints, reals);
	}

	/* instantiate the polyhedra domain */
	private void instantiateDomain() {
		man = new Polka(true);
	}

	/* constructor */
	public Analysis(UnitGraph g, SootClass jc) {
		super(g);

		this.g = g;
		this.jclass = jc;

		buildEnvironment();
		instantiateDomain();

		loopHeads = new HashMap<Unit, Counter>();
		backJumps = new HashMap<Unit, Counter>();
		for (Loop l : new LoopNestTree(g.getBody())) {
			loopHeads.put(l.getHead(), new Counter(0));
			backJumps.put(l.getBackJumpStmt(), new Counter(0));
		}
	}

	@Override
	protected AWrapper entryInitialFlow() {
		Abstract1 top = null;
		try {
			top = new Abstract1(man, env);
		} catch (ApronException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return new AWrapper(top);
	}

	private static class Counter {
		int value;

		Counter(int v) {
			value = v;
		}
	}






	@Override
	protected void merge(Unit succNode, AWrapper in_w1, AWrapper in_w2, AWrapper out_w) {
		Counter count = loopHeads.get(succNode);

		Abstract1 in1 = in_w1.get();
		Abstract1 in2 = in_w2.get();
		Abstract1 out = null;

		try {
			if (count != null) {
				++count.value;
				if (count.value < WIDENING_THRESHOLD) {
					out = in1.joinCopy(man, in2);
				} else {
					out = in1.widening(man, in1.joinCopy(man, in2));
				}
			} else {
				out = in1.joinCopy(man, in2);
			}
			out_w.set(out);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	protected void merge(AWrapper in_w1, AWrapper in_w2, AWrapper out_w) {

		Abstract1 in1 = in_w1.get();
		Abstract1 in2 = in_w2.get();
		Abstract1 out = null;

		try {
			out = in1.joinCopy(man, in2);
		} catch (ApronException e) {
			e.printStackTrace();
		}

		out_w.set(out);
	}

	@Override
	protected AWrapper newInitialFlow() {
		Abstract1 bottom = null;

		try {
			bottom = new Abstract1(man, env, true);
		} catch (ApronException e) {
		}
		AWrapper a = new AWrapper(bottom);
		a.man = man;
		return a;

	}

	@Override
	protected void copy(AWrapper source, AWrapper dest) {
		try {
			dest.set(new Abstract1(man, source.get()));
		} catch (ApronException e) {
			e.printStackTrace();
		}
	}
	
	public static final boolean isInt(Value val) {
		return intTypes.contains(val.getType().toString());
		
	}


	/* widening threshold and widening points */
	private static final int WIDENING_THRESHOLD = 6;
	private HashMap<Unit, Counter> loopHeads, backJumps;

	/* Soot unit graph */
	public UnitGraph g;
	public SootClass jclass;

	/* integer local variables of the method; */
	public String local_ints[];
	/* integer class variables where the method is defined */
	private String class_ints[];

	/* real variables */
	public static String reals[] = { "x" };
}