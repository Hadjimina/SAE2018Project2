package soot.JastAddJ;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;

/**
 * @ast node
 * @declaredat java.ast:172
 */
public abstract class RelationalExpr extends Binary implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    type_computed = false;
    type_value = null;
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public RelationalExpr clone() throws CloneNotSupportedException {
    RelationalExpr node = (RelationalExpr)super.clone();
    node.type_computed = false;
    node.type_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:204
   */
  public void typeCheck() {
    if(!getLeftOperand().type().isNumericType())
      error(getLeftOperand().type().typeName() + " is not numeric");
    if(!getRightOperand().type().isNumericType())
      error(getRightOperand().type().typeName() + " is not numeric");
  }
  /**
   * @ast method 
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:102
   */
  public soot.Value eval(Body b) { return emitBooleanCondition(b); }
  /**
   * @ast method 
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:225
   */
  public void emitEvalBranch(Body b) {
    b.setLine(this);
    if(isTrue())
      b.add(b.newGotoStmt(true_label(), this));
    else if(isFalse())
      b.add(b.newGotoStmt(false_label(), this));
    else {
      soot.Value left;
      soot.Value right;
      TypeDecl type = getLeftOperand().type();
      if(type.isNumericType()) {
        type = binaryNumericPromotedType();
        left = getLeftOperand().type().emitCastTo(b, // Binary numeric promotion
          getLeftOperand(),
          type
        );
        right = getRightOperand().type().emitCastTo(b, // Binary numeric promotion
          getRightOperand(),
          type
        );
        if(type.isDouble() || type.isFloat() || type.isLong()) {
          Local l;
          if(type.isDouble() || type.isFloat()) {
            if(this instanceof GEExpr || this instanceof GTExpr) {
              l = asLocal(b, b.newCmplExpr(asImmediate(b, left), asImmediate(b, right), this));
            }
            else {
              l = asLocal(b, b.newCmpgExpr(asImmediate(b, left), asImmediate(b, right), this));
            }
          }
          else {
            l = asLocal(b, b.newCmpExpr(asImmediate(b, left), asImmediate(b, right), this));
          }
          b.add(b.newIfStmt(comparisonInv(b, l, BooleanType.emitConstant(false)), false_label(), this));
          b.add(b.newGotoStmt(true_label(), this));
        }
        else {
          b.add(b.newIfStmt(comparison(b, left, right), true_label(), this));
          b.add(b.newGotoStmt(false_label(), this));
          //b.add(b.newIfStmt(comparisonInv(b, left, right), false_label(), this));
          //b.add(b.newGotoStmt(true_label(), this));
        }
      }
      else {
        left = getLeftOperand().eval(b);
        right = getRightOperand().eval(b);
        b.add(b.newIfStmt(comparison(b, left, right), true_label(), this));
        b.add(b.newGotoStmt(false_label(), this));
        //b.add(b.newIfStmt(comparisonInv(b, left, right), false_label(), this));
        //b.add(b.newGotoStmt(true_label(), this));
      }
    }
  }
  /**
   * @ast method 
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:279
   */
  public soot.Value comparison(Body b, soot.Value left, soot.Value right) {
    throw new Error("comparison not supported for " + getClass().getName());
  }
  /**
   * @ast method 
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:301
   */
  public soot.Value comparisonInv(Body b, soot.Value left, soot.Value right) {
    throw new Error("comparisonInv not supported for " + getClass().getName());
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public RelationalExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public RelationalExpr(Expr p0, Expr p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:14
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:20
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for LeftOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setLeftOperand(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for LeftOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getLeftOperand() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getLeftOperandNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Setter for RightOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setRightOperand(Expr node) {
    setChild(node, 1);
  }
  /**
   * Getter for RightOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getRightOperand() {
    return (Expr)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getRightOperandNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * @apilevel internal
   */
  protected boolean type_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl type_value;
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:344
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
    if(type_computed) {
      return type_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    type_value = type_compute();
if(isFinal && num == state().boundariesCrossed) type_computed = true;
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {  return typeBoolean();  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:29
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean definesLabel() {
      ASTNode$State state = state();
    boolean definesLabel_value = definesLabel_compute();
    return definesLabel_value;
  }
  /**
   * @apilevel internal
   */
  private boolean definesLabel_compute() {  return false;  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:69
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
    if(caller == getRightOperandNoTransform()) {
      return false_label();
    }
    if(caller == getLeftOperandNoTransform()) {
      return false_label();
    }
    return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:70
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
    if(caller == getRightOperandNoTransform()) {
      return true_label();
    }
    if(caller == getLeftOperandNoTransform()) {
      return true_label();
    }
    return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
