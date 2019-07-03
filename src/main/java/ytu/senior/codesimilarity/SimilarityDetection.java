package ytu.senior.codesimilarity;

import java.io.FileNotFoundException;
import java.util.List;

import ytu.senior.graph.Graph;
import ytu.senior.similarityalgorithm.NeighborMatchingSimilarity;
import ytu.senior.sourcecodeparser.GraphCreater;

public class SimilarityDetection {
	private static Result resultPage;
	private static boolean status;
		
	public Result checkSimilarity(List<String> projectIdentifier, List<String> projectPath, GraphSimilarityAlgorithm alg, double epsilon, int maxIteration) {
		resultPage = new Result(projectIdentifier.size(), maxIteration);
		status = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				int projectCount = projectIdentifier.size();
				GraphCreater[] projects = new GraphCreater[projectCount];
				Graph[] graphs = new Graph[projectCount];
				for(int i = 0; i < projectCount; i++) {
					projects[i] = new GraphCreater(projectPath.get(i));
					try {
						graphs[i] = projects[i].createGraph();
					} catch (FileNotFoundException e) {
						System.err.println("Project Files Could not found");
						e.printStackTrace();
					}
				}
				if(status == false)
					Thread.currentThread().interrupt();
				int index = 0;
				
				for(int i = 0; i < projectCount -1; i++) {
					for(int j = i + 1; j < projectCount; j++) {
						NeighborMatchingSimilarity nmSimilarity = new NeighborMatchingSimilarity(graphs[i], graphs[j], epsilon, maxIteration);
						nmSimilarity.setResultPage(resultPage);
						resultPage.setSimilarityResult(i, j, nmSimilarity.getGraphSimilarity().doubleValue());
						resultPage.setSimilarityResult(j, i, resultPage.getSingleResult(i, j));
						resultPage.setProgress((double) (++index) / (projectCount * (projectCount - 1) / 2));
						if(status == false)
							Thread.currentThread().interrupt();
					
					}
				}
				for(int i = 0; i < projectCount; i++)
					resultPage.setSimilarityResult(i, i, 100.0);
			}
			
		}).start();;
		return resultPage;
	}
	
	public void stopCalculation() {
		status = false;
	}
	
}
