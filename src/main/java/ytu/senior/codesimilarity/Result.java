package ytu.senior.codesimilarity;

public class Result {
	private int projectCount;
	private int maxIteration;
	private int combination;
	private double progress;
	private double[][] similarityResult;
	
	public Result(int projectCount, int maxIteration) {
		this.projectCount = projectCount;
		this.maxIteration = maxIteration;
		combination = projectCount * (projectCount - 1) / 2;
		progress = 0.0;
		similarityResult = new double[projectCount][projectCount];
	}
	
	public void setSimilarityResult(int row, int column, double value) {
		if(row < projectCount && column < projectCount)
			similarityResult[row][column] = value;
	}
	
	public double[][] getSimilarityResults(){
		if(progress == 1.0)
			return similarityResult;
		return null;
	}
	
	public double getSingleResult(int row, int column) {
		return similarityResult[row][column];
	}
	
	public void incrementProgress(double prg) {
		progress += prg / maxIteration / combination;
	}
	
	public double getProgress() {
		return progress;
	}
	
	public void setProgress(double progress) {
		this.progress = progress;
	}
	
}
