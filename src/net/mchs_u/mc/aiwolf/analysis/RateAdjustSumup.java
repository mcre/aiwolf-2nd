package net.mchs_u.mc.aiwolf.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateAdjustSumup {
	private static final String RATE_ADJUST_DIR = "./rate_adjust/";
	private static final String RATE_ADJUST_PREFIX = "005_";

	private Map<Integer, Map<String, Double>>  scoreSums = null;
	private Map<String, Integer> counts = null;
	
	
	public RateAdjustSumup(int max) throws FileNotFoundException, IOException {
		scoreSums = new HashMap<>();
		counts    = new HashMap<>();
		File dir = new File(RATE_ADJUST_DIR);
		File[] files = dir.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.startsWith(RATE_ADJUST_PREFIX);
			}
		});
		
		//Map<String, Integer> keys = new HashMap<>(); 
		for(File f: files){
			RateAdjustData md = new RateAdjustData(f);
			String key = md.getHashKey();
			if(counts.containsKey(key) && counts.get(key) >= max)
				continue;
			//keys.put(key, key.hashCode());
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
			
			//何回くらいで収束するか確認
			//System.out.println(key.hashCode() + "," + counts.get(key) + "," + (scoreSums.get(14).get(key) / (double)counts.get(key)));
		}
		/*
		for(String k: keys.keySet()){
			System.out.println("☆" + k + "|" + keys.get(k));
		}*/
	}
	
	public Map<String, Integer> getCounts(){
		return counts;
	}
	
	public Map<String, Double> getScoreSums(int day){
		return scoreSums.get(day);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		int max = 1000;
		if(args.length >= 1)
			max = Integer.parseInt(args[0]);
		
		RateAdjustSumup ms = new RateAdjustSumup(max);
		for(int i = 14; i < 15; i++){ //もう14日目だけで十分
			Map<String, Double> scoreSums = ms.getScoreSums(i);
			Map<String, Integer> counts = ms.getCounts();
			int j = 0;
			
			List<String> keyList = new ArrayList<>(scoreSums.keySet());
			Collections.sort(keyList);
			for(String key: keyList){
				System.out.print(key);
				System.out.print(counts.get(key) + ","); 
				System.out.print(j++ + ",");
				System.out.print(i + ",");
				System.out.println(scoreSums.get(key) / counts.get(key));
			}
		}
	}

}
