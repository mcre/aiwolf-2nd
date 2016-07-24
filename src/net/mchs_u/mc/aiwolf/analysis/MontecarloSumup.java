package net.mchs_u.mc.aiwolf.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MontecarloSumup {
	private static final String MONTECARLO_DIR = "./montecarlo/";
	private static final String MONTECARLO_PREFIX = "003_";

	private Map<Integer, Map<String, Double>>  scoreSums = null;
	private Map<String, Integer> counts = null;
	
	
	public MontecarloSumup() throws FileNotFoundException, IOException {
		scoreSums = new HashMap<>();
		counts    = new HashMap<>();
		File dir = new File(MONTECARLO_DIR);
		File[] files = dir.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.startsWith(MONTECARLO_PREFIX);
			}
		});
		for(File f: files){
			MontecarloData md = new MontecarloData(f);
			String key = md.getHashKey();
			for(int i = 0; i < 15; i++){
				if(!scoreSums.containsKey(i))
					scoreSums.put(i, new HashMap<String, Double>());
				if(!scoreSums.get(i).containsKey(key)){
					scoreSums.get(i).put(key, 0d);
					counts.put(key, 0);
				}
				scoreSums.get(i).put(key, scoreSums.get(i).get(key) + md.getScore(i));	
			}
			counts.put(key, counts.get(key) + 1);
		}
	}
	
	public Map<String, Integer> getCounts(){
		return counts;
	}
	
	public Map<String, Double> getScoreSums(int day){
		return scoreSums.get(day);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		MontecarloSumup ms = new MontecarloSumup();
		for(int i = 0; i < 15; i++){
			Map<String, Double> scoreSums = ms.getScoreSums(i);
			Map<String, Integer> counts = ms.getCounts();
			int j = 0;
			for(String key: scoreSums.keySet()){
				System.out.print(key);
				System.out.print(counts.get(key) + ",");
				System.out.print(j++ + ",");
				System.out.print(i + ",");
				System.out.println(scoreSums.get(key) / counts.get(key));
			}
		}
	}

}
