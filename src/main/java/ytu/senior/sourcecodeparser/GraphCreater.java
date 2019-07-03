package ytu.senior.sourcecodeparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import ytu.senior.graph.Graph;

public class GraphCreater {
	private String[] classNames;
	private String[] classPaths;
	private int nodeCount = 0;
	private int classCount;
	private CompilationUnit[] trees;
	
	public GraphCreater(String path) {
		searchProjectFiles(path);
	}
	
	@SuppressWarnings("unchecked")
	public Graph createGraph() throws FileNotFoundException {
		createAST();
		Graph projectGraph = new Graph(nodeCount);
		Map<String,Integer> classIndexer = new HashMap<String, Integer>();
		List<Object> t_G_Result = treeToGraphConverter(projectGraph, classIndexer);
		projectGraph = (Graph) t_G_Result.get(0);
		classIndexer = ((Map<String, Integer>) t_G_Result.get(1));
		projectGraph = umlExtracter(projectGraph, classIndexer);		
		return projectGraph;
	}
	
	private void searchProjectFiles(String projectPath){	
		// Search project files in given path
		File projectDir = new File(projectPath);
		List<String> temp = new ArrayList<String>();
		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            temp.add(path);
            try {
				new VoidVisitorAdapter<List<String>>() {
				    @Override
				    public void visit(ClassOrInterfaceDeclaration n, List<String> temp) {
				        super.visit(n, temp);
				        temp.add(n.getNameAsString());
				    }
				}.visit(StaticJavaParser.parse(file), temp);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        
        }).explore(projectDir);
		classCount = temp.size() / 2;
		classNames = new String[classCount];
		classPaths = new String[classCount];
	
		for(int i = 0; i < temp.size(); i = i + 2) {
			classPaths[i / 2] = projectPath + temp.get(i);
			classNames[i / 2] = temp.get(i + 1);
		}
	}
	
	private void createAST() throws FileNotFoundException {
		// Parse The AST and find the number of Nodes in Project Graph
		trees = new CompilationUnit[classCount];
		for(int i = 0; i < classCount; i++) {
			trees[i] = StaticJavaParser.parse(new FileInputStream(classPaths[i]));
			trees[i].walk(node -> {
				nodeCount++;
			});
		}
	}
	
	private List<Object> treeToGraphConverter(Graph projectGraph,Map<String, Integer> classIndexer) {
		// Converts the ASTs to Directed Graph and Store Trees root Index of Graph
		int nodeIndex = 0;
		int parent;
		Queue<Node> qu = new LinkedList<Node>();
		Queue<Integer> qu_Index = new LinkedList<Integer>();
		for(int i = 0; i < classCount; i++) {
			qu.add(trees[i]);
			qu_Index.add(nodeIndex);
			classIndexer.put(classNames[i], nodeIndex);
			while(!qu.isEmpty()) {
				List<Node> nodes = qu.poll().getChildNodes();
				parent = qu_Index.poll();
				for(Node no : nodes) {
					qu.add(no);
					projectGraph.addEdge(parent, ++nodeIndex);
					qu_Index.add(nodeIndex);
				}
			}
			qu.clear();
			qu_Index.clear();
			nodeIndex++;
		}
		return Arrays.asList(projectGraph, classIndexer);
	}
	
	private Graph umlExtracter(Graph projectGraph, Map<String, Integer> classIndex) {
		// Extract the UML of given project and bind the projectGraph according to UML diagram.
		Set<String> classReferences = new LinkedHashSet<>();
		VoidVisitor<Set<String>> classNameCollector = new ClassNameCollector();

		for(int i = 0; i < classCount; i++) {
			classNameCollector.visit( trees[i], classReferences);
			for(String ref : classReferences) {
				if(classIndex.get(ref) != null) {
					projectGraph.addEdge(classIndex.get(classNames[i]), classIndex.get(ref));
				}
			}
			projectGraph.removeEdge(classIndex.get(classNames[i]), classIndex.get(classNames[i]));
			classReferences.clear();
		}
		return projectGraph;
	}
	
	public int getNodeCount() {
		return nodeCount;
	}
	
	public int getClassCount() {
		return classCount;
	}
}