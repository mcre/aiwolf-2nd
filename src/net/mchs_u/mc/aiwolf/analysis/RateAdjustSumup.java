package net.mchs_u.mc.aiwolf.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RateAdjustSumup {
	private static final String RATE_ADJUST_DIR = "./rate_adjust/";
	private static final String RATE_ADJUST_PREFIX = "004_";

	private Map<Integer, Map<String, Double>>  scoreSums = null;
	private Map<String, Integer> counts = null;
	
	
	public RateAdjustSumup() throws FileNotFoundException, IOException {
		scoreSums = new HashMap<>();
		counts    = new HashMap<>();
		File dir = new File(RATE_ADJUST_DIR);
		File[] files = dir.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.startsWith(RATE_ADJUST_PREFIX);
			}
		});
		for(File f: files){
			RateAdjustData md = new RateAdjustData(f);
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
		RateAdjustSumup ms = new RateAdjustSumup();
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
