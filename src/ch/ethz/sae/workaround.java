package ch.ethz.sae;

import java.util.LinkedList;
import java.util.List;

import soot.jimple.spark.pag.Node;
import soot.jimple.spark.sets.P2SetVisitor;

public class workaround extends P2SetVisitor {
	
	public List<Node> list = new LinkedList<Node>();

	@Override
	public void visit(Node arg0) {
		list.add(arg0);
	}
}