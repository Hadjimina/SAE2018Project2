package ch.ethz.sae;

import java.util.HashMap;
import java.util.Map;

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
import soot.jimple.internal.JimpleLocal;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;

public class PointsToVisitor extends P2SetVisitor {
	
	public boolean weldAtOK, weldBetweenOK;
	
	private final Analysis analysis;
	private final Map<AllocNode, Integer> cars;
	private JInvokeStmt call;
	private boolean weldAtCall;
	private boolean weldBetweenCall;
	private boolean DEBUG;
	
	
	public PointsToVisitor(Analysis analysis, PAG pointsToAnalysis) {
		
		//Set class variables
		this.analysis = analysis;
		//this.DEBUG = analysis.DEBUG;
		this.cars = new HashMap<AllocNode, Integer>();
		this.weldAtOK = true;
		this.weldBetweenOK = true;
		
		// Loop over all initCalls of the analysed class
		for (JSpecialInvokeExpr initExp : analysis.initCalls) {
			// Get arguments of the expression
//			IntConstant left = (IntConstant) initExp.getArg(0);
//			IntConstant right = (IntConstant) initExp.getArg(1);
			
			// Make sure in the constructor that left < right
//			if (!(left.value < right.value)) {
//				this.weldAtOK = false;
//				this.weldBetweenOK = false;
//			}
			
			// Get define the interval for which the robot is valid
//			Interval interval = new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);//.MIN_VALUE, Integer.MAX_VALUE);
//			
			// Get the robot the constructor was set on
			JimpleLocal robot = (JimpleLocal) initExp.getBase();
			
			// Get the pointsToSet of that robot
			PointsToSetInternal setOfConstructors = (PointsToSetInternal) pointsToAnalysis.reachingObjects(robot);
			
			// THIS IS A STUPID WORKAROUND, BUT IT WORKS
			StupidWorkaround sw = new StupidWorkaround();
			setOfConstructors.forall(sw);
			
			// Loop over list of calls and add call and matching interval to robots
			for (Node call : sw.list) {
				cars.put((AllocNode) call, Integer.MIN_VALUE);
			}
			
		}
	}

	@Override
	public void visit(Node arg0) {
		System.out.println(arg0.toString());
		AllocNode local = (AllocNode) arg0;
		
//		// Get the range of the robot
		Integer maxSpeed = this.cars.get(local);
//		
		JVirtualInvokeExpr tmp = (JVirtualInvokeExpr) call.getInvokeExprBox().getValue();
		Value argument = tmp.getArg(0);
		
		if (argument instanceof IntConstant) {
			int newMax = Math.max(maxSpeed, ((IntConstant) argument).value);
			this.cars.put(local, newMax);
		}
		else if (argument instanceof JimpleLocal) {
			
		}
		else {
			int i = 0;
		}
		
		
//		if (weldAtCall) {
//			if (this.weldAtOK) {
//				
//				// Get the argument of the call
//				JVirtualInvokeExpr tmp = (JVirtualInvokeExpr) call.getInvokeExprBox().getValue();
//				Value argument = tmp.getArg(0);
//				
//				this.weldAtOK = verifyWeldAt(argument, rangeOfRobot);
//			} else {
//				// ignore
//			}
//		} else if (weldBetweenCall) {
//			if (this.weldBetweenOK) {
//							
//				// Get the argument of the call
//				JVirtualInvokeExpr tmp = (JVirtualInvokeExpr) call.getInvokeExprBox().getValue();
//				Value leftBorder = tmp.getArg(0);
//				Value rightBorder = tmp.getArg(1);
//				
//				//TODO: Verify that leftBorder < rightBorder
//				
//				// Check whether the left border is a constant
//				try {
//					
//					if (DEBUG) {
//						System.out.println("leftBorder is: " + leftBorder + " of type: " + leftBorder.getClass());
//						System.out.println("rightBorder is: " + rightBorder + " of type: " + rightBorder.getClass());
//
//					}
//					
//					
//					if (leftBorder instanceof IntConstant){
//						
//						//get value of the constant
//						MpqScalar binLeftVal = new MpqScalar(((IntConstant) leftBorder).value);
//						
//						// Check whether the right border is a constant
//						if (rightBorder instanceof IntConstant){
//							
//							//get value of the constant
//							MpqScalar binRightVal = new MpqScalar(((IntConstant) rightBorder).value);
//												
//							// Check if leftBorder < rightBorder
//							if (binLeftVal.cmp(binRightVal) == -1) {
//								
//								// In that case calculate
//								this.weldBetweenOK = verifyWeldAt(leftBorder, rangeOfRobot) && verifyWeldAt(rightBorder, rangeOfRobot);
//								
//							// Otherwise give back false
//							} else {
//								this.weldBetweenOK = false;
//							}
//							
//						// Check if right border is a local variable
//						} else if (rightBorder instanceof JimpleLocal) {
//							
//							
//					
//							// Convert to needed object
//							String binRightName = ((JimpleLocal) rightBorder).getName();
//							Texpr1VarNode constraint = new Texpr1VarNode(binRightName);
//							Texpr1Intern convertArg = new Texpr1Intern(analysis.getEnv(), constraint);
//							
//							// Get the constraint before calling
//							Abstract1 constraintsBefore = analysis.getFlowBefore(call).get();
//							
//							// Get the interval for the local variable
//							Interval bound = constraintsBefore.getBound(analysis.getMan(), convertArg);
//							
//							// If the constant on the left is smaller than the inf, calculate
//							if (binLeftVal.cmp(bound.inf) == -1) {
//								this.weldBetweenOK = verifyWeldAt(leftBorder, rangeOfRobot) && verifyWeldAt(rightBorder, rangeOfRobot);
//							
//							// Otherwise give back false
//							} else {
//								this.weldBetweenOK = false;
//							}
//						} else {
//							// Should never happen
//							this.weldBetweenOK = false;
//						}
//						
//					} else if (leftBorder instanceof JimpleLocal) {
//						
//						// Convert to needed object
//						String binLeftName = ((JimpleLocal) leftBorder).getName();
//						Texpr1VarNode constLeft = new Texpr1VarNode(binLeftName);
//						Texpr1Intern convertArg = new Texpr1Intern(analysis.getEnv(), constLeft);
//						
//						// Get the constraint before calling
//						Abstract1 constraintsBefore = analysis.getFlowBefore(call).get();
//						
//						if (rightBorder instanceof IntConstant){
//							
//							//get value of the constant
//							MpqScalar binRightVal = new MpqScalar(((IntConstant) rightBorder).value);
//							
//							// Get the interval for the local variable
//							Interval bound = constraintsBefore.getBound(analysis.getMan(), convertArg);
//							
//							// If the constant on the left is smaller than the inf, calculate
//							if (binRightVal.cmp(bound.inf) == -1) {
//								this.weldBetweenOK = verifyWeldAt(leftBorder, rangeOfRobot) && verifyWeldAt(rightBorder, rangeOfRobot);
//							
//							// Otherwise give back false
//							} else {
//								this.weldBetweenOK = false;
//							}
//						} else if (rightBorder instanceof JimpleLocal) {
//							
//							if (DEBUG) System.out.println("CASE both local variables.");
//							
//							// Convert to needed object
//							String binRightName = ((JimpleLocal) rightBorder).getName();
//							Texpr1VarNode constRight = new Texpr1VarNode(binRightName);
//							
//							Texpr1BinNode invertedCondition = new Texpr1BinNode(Texpr1BinNode.OP_SUB, constLeft, constRight);
//							
//							Tcons1 invertedConstraint = new Tcons1(analysis.getEnv(), Tcons1.SUPEQ,invertedCondition);
//							
//							if (DEBUG) System.out.println("invertedConstraint is: " + invertedConstraint);
//							
//							Abstract1 invertedAbstract = new Abstract1(analysis.getMan(), toArray(invertedConstraint));
//							
//							if (DEBUG) {
//								System.out.println("invertedAbstract is: " + invertedAbstract);
//								System.out.println("constraintsBefore is: " + constraintsBefore);
//							}
//							
//							Abstract1 newConstr = constraintsBefore.meetCopy(analysis.getMan(), invertedAbstract);
//							
//							if (DEBUG) System.out.println("meet is: " + newConstr);
//							
//							if (newConstr.isBottom(analysis.getMan())) {
//								this.weldBetweenOK = verifyWeldAt(leftBorder, rangeOfRobot) && verifyWeldAt(rightBorder, rangeOfRobot);
//							} else {
//								this.weldBetweenOK = false;
//							}
//							
//						} else {
//							// Should never happen
//							this.weldBetweenOK = false;
//						}
//					}
//				} catch (ApronException e) {
//					// TODO Auto-generated catch block
//					this.weldBetweenOK = false;
//					if (DEBUG) e.printStackTrace();
//				}
//				
//			} else {
//				// ignore
//			}
//		} else {
//			// Should never happen if called correctly (always setCall first)
//			if (DEBUG) System.err.println("THIS SHOULD NEVER HAPPEN");
//		}
	}
	
	public boolean verifyWeldAt(Value argument, Interval rangeOfRobot) {
		
//		// Get the information before the call
//		Abstract1 constraintsBefore = analysis.getFlowBefore(call).get();
//		
//		Texpr1Node constraint = null;
//		
//		if (argument instanceof JimpleLocal){
//			
//			//get name of the local variable
//			String binName = ((JimpleLocal) argument).getName();
//			
//			constraint = new Texpr1VarNode(binName);
//		}
//		//check whether left operand is constant
//		else if (argument instanceof IntConstant){
//			
//			//get value of the constant
//			MpqScalar binVal = new MpqScalar(((IntConstant) argument).value);
//			
//			constraint = new Texpr1CstNode(binVal);
//		} else {
//			// Should never happen because of jimple
//			if (DEBUG) System.err.println("argument of call neither constant nor local variable");
//		}
//		
//		Texpr1Intern convertArg = new Texpr1Intern(analysis.getEnv(), constraint);
//		
//		Interval constraintNow;
//		
//		try {
//			
//			// Get the constraint for the call
//			constraintNow = constraintsBefore.getBound(analysis.getMan(), convertArg);
//			
//			// If bottom, it's fine
//			if (constraintNow.isBottom()) {
//				if (DEBUG) System.out.println("constraintNow is bottom.");
//				return true;
//			}
//			
//			if (DEBUG) {
//				System.out.println("# # # Verifier # # #");
//				System.out.println("range of Robot is :" + rangeOfRobot);
//				System.out.println("constraint for calling var is: " + constraintNow);
//				System.out.println("the calling var is: " + convertArg);
//			}
//			
//			if (constraintNow.isLeq(rangeOfRobot)) {
//				if (DEBUG) System.out.println("The constraint is OK \n");
//				return true;
//			}
//			
//		} catch (ApronException e) {
//			if (DEBUG) System.err.println("CRASHED!!!");
//			return false;
//		}
//		if (DEBUG) System.out.println("The constraint is NOT OK\n");
		return false;
	}
	
	public void setCall(JInvokeStmt call) {
		this.call = call;
		
//		//find out invokeExpr
//		InvokeExpr invExpr = call.getInvokeExpr();
//		
//		//find out function name
//		String functionName = invExpr.getMethod().getName();
//		
//		if (functionName.equals("weldAt")) {
//			this.weldAtCall = true;
//			this.weldBetweenCall = false;
//		} else if (functionName.equals("weldBetween")) {
//			this.weldBetweenCall = true;
//			this.weldAtCall = false;
//		} else {
//			// can be ignored since only ever called with elements from weldAtCalls and weldBetweenCalls which need to satisfy this to be in the DS.
//		}
	}
	
	public static Tcons1[] toArray(Tcons1 x) {
		Tcons1[] tmp = new Tcons1[1];
		tmp[0] = x;
		return tmp;
	}

}