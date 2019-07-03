package ytu.senior.similarityalgorithm;
// Revised Version of => https://wadsashika.wordpress.com/2014/09/19/measuring-graph-similarity-using-neighbor-matching/
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ytu.senior.codesimilarity.Result;
import ytu.senior.graph.Graph;

public class NeighborMatchingSimilarity implements ISimilarity{
	private Graph out_graphA;
	private Graph out_graphB;
	private Graph in_graphA;
	private Graph in_graphB;
	private Result resultpage;
	private double epsilon;
	private int maxIteration;
    private int graphSizeA;
    private int graphSizeB;
    private double[][] nodeSimilarity;
	
	public NeighborMatchingSimilarity(Graph graphA, Graph graphB, double epsilon, int maxIteration) {
		this.out_graphA = graphA;
		this.out_graphB = graphB;
		this.epsilon = epsilon;
		this.maxIteration = maxIteration;
		graphSizeA = graphA.getGraphSize();
		graphSizeB = graphB.getGraphSize();
		nodeSimilarity = new double[graphSizeA][graphSizeB];
		initializeInGraphs();
		initializeSimilarityMatrices();
	}
	
	public Double getGraphSimilarity() {
        Double finalGraphSimilarity = 0.0;
        DecimalFormat dformat = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
        measureSimilarity();
        List<Integer> graphANodeList = new ArrayList<Integer>();
        List<Integer> graphBNodeList = new ArrayList<Integer>();
        for(int i = 0; i < graphSizeA; i++)
        	graphANodeList.add(i);
        for(int i = 0; i < graphSizeB; i++)
        	graphBNodeList.add(i);    	
        	 
        if (graphSizeA < graphSizeB) 
            finalGraphSimilarity = enumerationFunction(graphANodeList, graphBNodeList, 0) / graphSizeA;
        else 
            finalGraphSimilarity = enumerationFunction(graphBNodeList, graphANodeList, 1) / graphSizeB;
       
        finalGraphSimilarity = Double.valueOf(dformat.format(finalGraphSimilarity*100));
        return finalGraphSimilarity;
    }
	
	private void initializeInGraphs() {
		in_graphA = new Graph(graphSizeA);
		in_graphB = new Graph(graphSizeB);
		for(int index = 0; index < graphSizeA; index++) {
			for(Integer edge : out_graphA.getGraph().get(index)) {
				in_graphA.addEdge(edge, index);
			}
		}
		for(int index = 0; index < graphSizeB; index++) {
			for(Integer edge : out_graphB.getGraph().get(index)) {
				in_graphB.addEdge(edge, index);
			}
		}
	}
	
	private void initializeSimilarityMatrices() {
		for (int i = 0; i < graphSizeA; i++) {
            for (int j = 0; j < graphSizeB; j++) {
            	double inSimilarity = 0.0;
            	double outSimilarity = 0.0;
            	double maxDegree = Math.max(in_graphA.getGraph().get(i).size(), in_graphB.getGraph().get(j).size());
            	if(maxDegree != 0) 
            		inSimilarity = ( (Math.min(in_graphA.getGraph().get(i).size(), in_graphB.getGraph().get(j).size())) / (maxDegree));
   
            	maxDegree = Math.max(out_graphA.getGraph().get(i).size(), out_graphB.getGraph().get(j).size());
            	if(maxDegree != 0) 
            		outSimilarity = ( (Math.min(out_graphA.getGraph().get(i).size(), out_graphB.getGraph().get(j).size())) / (maxDegree));
            	
            	nodeSimilarity[i][j] = (inSimilarity + outSimilarity) / 2;
            }
        }
	}
	
	private void measureSimilarity() {
        double maxDifference;
        boolean terminate = false;
        int iteration = 0;
        double inSimilarity;
    	double outSimilarity;
    	double tempSimilarity;
        double similaritySum;
        int maxDegree;
        int minDegree;
        while(!terminate) {
        	maxDifference = 0.0;
        	for(int i = 0; i < graphSizeA; i++) {
        		for(int j = 0; j < graphSizeB; j++) {
                    // Calculate in-degree Similarity
                    similaritySum = 0.0;
                    maxDegree = Math.max(in_graphA.getGraph().get(i).size(), in_graphB.getGraph().get(j).size());
                    minDegree = Math.min(in_graphA.getGraph().get(i).size(), in_graphB.getGraph().get(j).size());
                    if (minDegree == in_graphA.getGraph().get(i).size()) 
                        similaritySum = enumerationFunction(in_graphA.getGraph().get(i), in_graphB.getGraph().get(j), 0);
                    else 
                        similaritySum = enumerationFunction(in_graphB.getGraph().get(j), in_graphA.getGraph().get(i), 1);
                    
                    if (maxDegree == 0 && similaritySum == 0.0) 
                    	inSimilarity = 1.0;
                    else if (maxDegree == 0) 
                    	inSimilarity = 0.0;
                    else 
                    	inSimilarity = similaritySum / maxDegree;
                    
                    // Calculate out-degree Similarity
                    similaritySum = 0.0;
                    maxDegree = Math.max(out_graphA.getGraph().get(i).size(), out_graphB.getGraph().get(j).size());
                    minDegree = Math.min(out_graphA.getGraph().get(i).size(), out_graphB.getGraph().get(j).size());
                    if (minDegree == out_graphA.getGraph().get(i).size()) 
                        similaritySum = enumerationFunction(out_graphA.getGraph().get(i), out_graphB.getGraph().get(j), 0);
                    else 
                        similaritySum = enumerationFunction(out_graphB.getGraph().get(j), out_graphA.getGraph().get(i), 1);
                    
                    if (maxDegree == 0.0 && similaritySum == 0.0) 
                    	outSimilarity = 1.0;
                    else if (maxDegree == 0.0) 
                    	outSimilarity = 0.0;
                    else 
                    	outSimilarity = similaritySum / maxDegree;
                    
                    // Calculate Node Similarity
        			tempSimilarity = (inSimilarity + outSimilarity) / 2;
                    if (Math.abs(nodeSimilarity[i][j] - tempSimilarity) > maxDifference)
                    	maxDifference = Math.abs(nodeSimilarity[i][j] - tempSimilarity);                 
                    nodeSimilarity[i][j] = tempSimilarity;
        		}
        	}
        	resultpage.incrementProgress(1.0);
        	iteration++;
            if (maxDifference < epsilon || iteration > maxIteration - 1) 
                terminate = true;
        }
        DecimalFormat dformat = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
        for (int i = 0; i < graphSizeA; i++) {
            for (int j = 0; j < graphSizeB; j++) 
                nodeSimilarity[i][j] = Double.valueOf(dformat.format(nodeSimilarity[i][j]));  
        }
	}
			
	private double enumerationFunction(List<Integer> neighborListMin, List<Integer> neighborListMax, int graph) {
		double similaritySum = 0.0;
        Map<Integer, Double> valueMap = new HashMap<Integer, Double>();
        if (graph == 0) {
            for (int i = 0; i < neighborListMin.size(); i++) {
                int node = neighborListMin.get(i);
                double max = 0.0;
                int maxIndex = -1;
                for (int j = 0; j < neighborListMax.size(); j++) {
                    int key = neighborListMax.get(j);
                    if (!valueMap.containsKey(key)) {
                        if (max < nodeSimilarity[node][key]) {
                            max = nodeSimilarity[node][key];
                            maxIndex = key;
                        }
                    }
                }
                valueMap.put(maxIndex, max);
            }
        } else {
            for (int i = 0; i < neighborListMin.size(); i++) {
                int node = neighborListMin.get(i);
                double max = 0.0;
                int maxIndex = -1;
                for (int j = 0; j < neighborListMax.size(); j++) {
                    int key = neighborListMax.get(j);
                    if (!valueMap.containsKey(key)) {
                        if (max < nodeSimilarity[key][node]) {
                            max = nodeSimilarity[key][node];
                            maxIndex = key;
                        }
                    }
                }
                valueMap.put(maxIndex, max);
            }
        }

        for (double value : valueMap.values()) {
            similaritySum += value;
        }
        return similaritySum;
    }
	
	public void setResultPage(Result resultpage) {
		this.resultpage = resultpage;
	}
}
