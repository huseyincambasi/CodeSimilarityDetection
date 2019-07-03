import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ytu.senior.codesimilarity.GraphSimilarityAlgorithm;
import ytu.senior.codesimilarity.Result;
import ytu.senior.codesimilarity.SimilarityDetection;

public class Main {
	
	public static String format(Duration d) {
	    long days = d.toDays();
	    d = d.minusDays(days);
	    long hours = d.toHours();
	    d = d.minusHours(hours);
	    long minutes = d.toMinutes();
	    d = d.minusMinutes(minutes);
	    long seconds = d.getSeconds() ;
	    return 
	            (days ==  0?"":days+" day,")+ 
	            (hours == 0?"":hours+" hour,")+ 
	            (minutes ==  0?"":minutes+" minute,")+ 
	            (seconds == 0?"":seconds+" second,");
	}

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		int iteration = 100;
		double epsilon = 0.001;	
		List<String> id = new ArrayList<String>();
		List<String> path = new ArrayList<String>();
		if(args.length < 2) {
			System.out.println("Min 2 Input required...\nRequired : \"First_Project_Path\" \"Second_Project_Path\" "
					+ "Optional : {Epsilon} {Max_Iteration}");
			System.exit(-1);
		}
		else if(args.length < 3) {
			path.add(args[0]);
			path.add(args[1]);
		}
		else if(args.length < 4) {
			path.add(args[0]);
			path.add(args[1]);
			epsilon = Double.valueOf(args[2]);
		}
		else if(args.length < 5) {
			path.add(args[0]);
			path.add(args[1]);
			epsilon = Double.valueOf(args[2]);
			iteration = Integer.valueOf(args[3]);
		}
		else {
			System.out.println("Too many arguments...\nRequired : \"First_Project_Path\" "
					+ "\"Second_Project_Path\" Optional : {Epsilon} {Max_Iteration}");
			System.exit(1);
		}
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println("Start : " + dtf.format(now));
		id.add("1");
		id.add("2");			
		Result page = new SimilarityDetection().checkSimilarity(id, path, GraphSimilarityAlgorithm.NeighborMatching, epsilon, iteration);
		while(page.getProgress() != 1.0)
			Thread.sleep(100);
		System.out.println("Measured Similarity Results = " + page.getSingleResult(0, 1) + "%");
		if(page.getSingleResult(0, 1) > 80.0) {
			System.out.println("Code plagiarism detected!!!\nProject Similarity is over threshold....");
		}
		LocalDateTime now2 = LocalDateTime.now();
		System.out.println("End : " + dtf.format(now2));
		Duration duration = Duration.between(now, now2);
		System.out.println("Total calculation time : " + format(duration));
	}

}
