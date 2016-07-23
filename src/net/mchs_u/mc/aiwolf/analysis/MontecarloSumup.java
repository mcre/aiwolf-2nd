package net.mchs_u.mc.aiwolf.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MontecarloSumup {
	private static final String MONTECARLO_DIR = "./montecarlo/";
	private static final String MONTECARLO_PREFIX = "003_";

	private Map<String, List<MontecarloData>> datas = null;
	
	public MontecarloSumup() throws FileNotFoundException, IOException {
		datas = new HashMap<>();
		File dir = new File(MONTECARLO_DIR);
		File[] files = dir.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.startsWith(MONTECARLO_PREFIX);
			}
		});
		int i = 0;
		for(File f: files){
			MontecarloData md = new MontecarloData(f);
			String key = md.getHashKey();
			if(!datas.containsKey(key))
				datas.put(key, new ArrayList<MontecarloData>());
			datas.get(key).add(md);
			System.out.println(i++ + "/" + files.length);
			
		}
	}
	
	public Map<String, Integer> getCounts(int day){
		Map<String, Integer> ret = new HashMap<>();
		for(String key: datas.keySet()){
			ret.put(key, datas.get(key).size());
		}
		return ret;
	}
	
	public Map<String, Double> getScores(int day){
		Map<String, Double> ret = new HashMap<>();
		
		for(String key: datas.keySet()){
			double sum = 0d;
			for(MontecarloData md: datas.get(key)){
				sum += md.getScore(day);
			}
			ret.put(key, sum / (double)datas.get(key).size());
		}
		
		return ret;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		MontecarloSumup ms = new MontecarloSumup();
		for(int i = 0; i < 15; i++){
			Map<String, Double> scores = ms.getScores(i);
			Map<String, Integer> counts = ms.getCounts(i);
			for(String key: scores.keySet()){
				System.out.print(key);
				System.out.print(counts.get(key) + ",");
				System.out.println(scores.get(key));
			}
		}
	}

}
