package ytu.senior.graph;

import java.util.ArrayList;

public class Graph {
	private int graphSize;
	private ArrayList<ArrayList<Integer>> adjList;
		
	public Graph(int graphSize) {
     	this.graphSize = graphSize; 	
     	adjList = new ArrayList<ArrayList<Integer>>();
     	for(int i = 0; i < graphSize; i++)
     		adjList.add(new ArrayList<Integer>()); 
    }
    
    public void addEdge(int source, int destination) {
    	adjList.get(source).add(destination);
    }
    
    public boolean removeEdge(int source, int destination) {
    	return adjList.get(source).remove(Integer.valueOf(destination));
    }

    public ArrayList<ArrayList<Integer>> getGraph(){
    	return adjList;
    }

    public int getGraphSize() {
		return graphSize;
	}
		
	public String printGraph() {
		String result = "";
		for(int index = 0; index < graphSize; index++) {
			for(Integer pCrawl : adjList.get(index))
				result += pCrawl + " -> ";
			result += "\n";
		}
	    return result;
	}
}