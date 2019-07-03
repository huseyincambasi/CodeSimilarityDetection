package ytu.senior.sourcecodeparser;

import java.util.Set;

import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ClassNameCollector extends VoidVisitorAdapter<Set<String>>{
	 
	 @Override
	 public void visit(SimpleName simple, Set<String> collector) {
         super.visit(simple, collector);
         collector.add(simple.getIdentifier());
     }
	
}