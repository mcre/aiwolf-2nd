package net.mchs_u.mc.aiwolf.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;

public class RateAdjustData {
	private static final double EPS = 0.000000000000001d;
	
	private String hashKey = null;
	private Team winner = null;
	private Map<String, Role> correctRoles = null;
	private Map<String, Double> rates = null;
	private String me = null;
	private int maxDay = 0;
	private Map<Integer, Map<String ,Map<String, Double>>> objectiveEstimate = null; //day, vpw, id, rate
	private Map<Integer, Map<String ,Map<String, Double>>> subjectiveEstimate = null;
	private Map<Integer, Map<String ,Map<String, Double>>> pretendVillagerEstimate = null;

	public RateAdjustData(File file) throws FileNotFoundException, IOException {
		rates = new HashMap<>(15);
		correctRoles = new HashMap<>(15);
		objectiveEstimate = new HashMap<>(8);
		subjectiveEstimate = new HashMap<>(8);
		pretendVillagerEstimate = new HashMap<>(8);
		StringBuffer buffer = new StringBuffer("");
		
		try(BufferedReader br = new BufferedReader(new FileReader(file))){
			String line;
			while((line = br.readLine()) != null){
				String[] c = line.split("\t");
				
				if(c[0].equals("MYSELF") && c[1].equals("0")) {
					me = c[2];
				} else if(c[0].equals("RATES")) {
					rates.put(c[1], Double.parseDouble(c[2]));
					buffer.append(c[1] + "," + c[2] + ",");
				} else if(c[0].equals("WINNER")) {
					if (c[1].equals("WEREWOLF_TEAM"))
						winner = Team.WEREWOLF;
					else
						winner = Team.VILLAGER;
				} else if(c[0].equals("CORRECT")) {
					correctRoles.put(c[2], Role.valueOf(c[4]));
					maxDay = Integer.parseInt(c[1]);
				} else if(c[0].equals("OBJ_VPW")) {
					int day = Integer.parseInt(c[1]);
					if(!objectiveEstimate.containsKey(day)){
						objectiveEstimate.put(day, new HashMap<String ,Map<String, Double>>(3));
						objectiveEstimate.get(day).put("VILLAGER_TEAM", new HashMap<String, Double>(15));
						objectiveEstimate.get(day).put("POSSESSED", new HashMap<String, Double>(15));
						objectiveEstimate.get(day).put("WEREWOLF", new HashMap<String, Double>(15));
					}
					objectiveEstimate.get(day).get("VILLAGER_TEAM").put(c[2], Double.parseDouble(c[4]));
					objectiveEstimate.get(day).get("POSSESSED").put(c[2], Double.parseDouble(c[5]));
					objectiveEstimate.get(day).get("WEREWOLF").put(c[2], Double.parseDouble(c[6]));
				} else if(c[0].equals("SBJ_VPW")) {
					int day = Integer.parseInt(c[1]);
					if(!subjectiveEstimate.containsKey(day)){
						subjectiveEstimate.put(day, new HashMap<String ,Map<String, Double>>(3));
						subjectiveEstimate.get(day).put("VILLAGER_TEAM", new HashMap<String, Double>(15));
						subjectiveEstimate.get(day).put("POSSESSED", new HashMap<String, Double>(15));
						subjectiveEstimate.get(day).put("WEREWOLF", new HashMap<String, Double>(15));
					}

					subjectiveEstimate.get(day).get("VILLAGER_TEAM").put(c[2], Double.parseDouble(c[4]));
					subjectiveEstimate.get(day).get("POSSESSED").put(c[2], Double.parseDouble(c[5]));
					subjectiveEstimate.get(day).get("WEREWOLF").put(c[2], Double.parseDouble(c[6]));
				} else if(c[0].equals("PRV_VPW")) {
					int day = Integer.parseInt(c[1]);
					if(!pretendVillagerEstimate.containsKey(day)){
						pretendVillagerEstimate.put(day, new HashMap<String ,Map<String, Double>>(3));
						pretendVillagerEstimate.get(day).put("VILLAGER_TEAM", new HashMap<String, Double>(15));
						pretendVillagerEstimate.get(day).put("POSSESSED", new HashMap<String, Double>(15));
						pretendVillagerEstimate.get(day).put("WEREWOLF", new HashMap<String, Double>(15));
					}
					pretendVillagerEstimate.get(day).get("VILLAGER_TEAM").put(c[2], Double.parseDouble(c[4]));
					pretendVillagerEstimate.get(day).get("POSSESSED").put(c[2], Double.parseDouble(c[5]));
					pretendVillagerEstimate.get(day).get("WEREWOLF").put(c[2], Double.parseDouble(c[6]));
				}
			}
		}
		hashKey = buffer.toString();
	}
	
	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hashKey == null) ? 0 : hashKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MontecarloData other = (MontecarloData) obj;
		if (ratesHashKey == null) {
			if (other.ratesHashKey != null)
				return false;
		} else if (!hashKey.equals(other.hashKey))
			return false;
		return true;
	}*/

	public Team getWinner() {
		return winner;
	}

	public Map<String, Role> getCorrectRoles() {
		return correctRoles;
	}

	public Map<String, Double> getRates() {
		return rates;
	}

	public String getMe() {
		return me;
	}
	
	public int getMaxDay() {
		return maxDay;
	}

	public Map<Integer, Map<String, Map<String, Double>>> getObjectiveEstimate() {
		return objectiveEstimate;
	}

	public Map<Integer, Map<String, Map<String, Double>>> getSubjectiveEstimate() {
		return subjectiveEstimate;
	}

	public Map<Integer, Map<String, Map<String, Double>>> getPretendVillagerEstimate() {
		return pretendVillagerEstimate;
	}
	
	//雑
	public double getScore(int day){
		int d = day;
		if(d > maxDay)
			d = maxDay;
		
		List<String> poss = maxList(objectiveEstimate.get(d).get("POSSESSED"), 1);
		double possScore = 0d;
		for(String p: poss){
			if(correctRoles.get(p) == Role.POSSESSED){
				possScore = 1;
				break;
			}
		}
		if(possScore > 0d)//正解があった場合
			possScore /= (double)poss.size(); //スコアは人数で割る
		
		Map<String, Double> wrates = objectiveEstimate.get(d).get("WEREWOLF");
		List<String> wolf = maxList(wrates, 3);
		double wolfScore = 0d;
		
		if(wolf.size() <= 3){//上位が3人の場合は満点カウントするだけ
			for(String w: wolf)
				if(correctRoles.get(w) == Role.WEREWOLF)
					wolfScore += 3d;
		} else if(wrates.get(wolf.get(1)) - wrates.get(wolf.get(2)) > EPS){ //２位と３位に差がある場合は１位と２位は満点カウントできる
			for(int i = 0; i < 2; i++)
				if(correctRoles.get(wolf.get(i)) == Role.WEREWOLF)
					wolfScore += 3d;
			double tmpWolfScore = 0d;
			for(int i = 2; i < wolf.size(); i++){
				if(correctRoles.get(wolf.get(i)) == Role.WEREWOLF){
					tmpWolfScore += 3d;
				}
			}
			wolfScore += tmpWolfScore / (double)(wolf.size() - 2) ;//3位以下の数で割ったものを足す
		} else if(wrates.get(wolf.get(0)) - wrates.get(wolf.get(1)) > EPS){ //１位と２位以下に差がある場合は１位は満点カウントできる
			for(int i = 0; i < 1; i++)//冗長
				if(correctRoles.get(wolf.get(i)) == Role.WEREWOLF)
					wolfScore += 3d;
			double tmpWolfScore = 0d;
			for(int i = 1; i < wolf.size(); i++){
				if(correctRoles.get(wolf.get(i)) == Role.WEREWOLF){
					tmpWolfScore += 3d;
				}
			}
			wolfScore += tmpWolfScore * 2d / (double)(wolf.size() - 1) ;//2位以下の数で割ったものを足す
		} else { //全部同じ点数の時は満点カウントできまへん
			for(int i = 0; i < wolf.size(); i++){
				if(correctRoles.get(wolf.get(i)) == Role.WEREWOLF){
					wolfScore += 3d;
				}
			}
			wolfScore = wolfScore * 3d / (double)wolf.size();//2位以下の数で割ったものを足す
		}
		return possScore + wolfScore;
	}
	
	//Doubleが高い順にcount個のList<String>を返す。count超えても同率がある場合はそれも一緒に返す。
	private static List<String> maxList(Map<String, Double> map, int count){ 
		List<StringDouble> list = new ArrayList<>();
		for(String id: map.keySet()){
			list.add(new StringDouble(id, map.get(id)));
		}
		Collections.sort(list);
		
		List<String> ret = new ArrayList<>();
		for(int i = 0; i < list.size(); i++){
			ret.add(list.get(i).getString());

			if(i != list.size() -1  && ret.size() >= count && (list.get(i).getDouble() - list.get(i + 1).getDouble() > EPS)){
				break;
			}
		}
		
		return ret;
	}
	
	public String getHashKey() {
		return hashKey;
	}
	
	//テスト用
	public static void main(String[] args) throws FileNotFoundException, IOException {
		RateAdjustData md = new RateAdjustData(new File("montecarlo/003_1468858300499.txt")); 
		//MontecarloData md = new MontecarloData("tmp.txt"); 
		System.out.println(md.getScore(0));
	}

}
