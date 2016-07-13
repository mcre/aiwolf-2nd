package net.mchs_u.mc.aiwolf.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author m_cre
 * DirectStarterで生成されたresultの推移をファイル出力する
 */
public class WinningPercentageTransition {
	private static final String RESULT_DIR = "./result/";
	private List<Map<String, Double>> winningPercentageTransition = null;

	public WinningPercentageTransition(String start,String end) throws FileNotFoundException, IOException {
		winningPercentageTransition = new ArrayList<Map<String,Double>>();
		
		File dir = new File(RESULT_DIR);
		File[] files = dir.listFiles();

		for(File f: files){			
			String n = f.getName();
			if(n.compareTo(start) < 0 || n.compareTo(end) > 0 )
				continue;
			
			Map<String,Double> map = new HashMap<>();
			try(BufferedReader br = new BufferedReader(new FileReader(f))){
				String str;
				while((str = br.readLine()) != null){
					if(str.startsWith("set : ") || str.endsWith("Total"))
						continue;
					if(str.equals(""))
						break;
					String[] sp = str.split("\t");
					map.put(sp[0].trim(), Double.valueOf(sp[7]));
				}
				br.close();
			}
			winningPercentageTransition.add(map);
		}
	}
	
	
	public String getCsv(){
		StringBuffer sb = new StringBuffer("");
		for(int i = 0; i < winningPercentageTransition.size(); i++){
			for(String k: winningPercentageTransition.get(i).keySet()){
				sb.append(i +",");
				sb.append(k +",");
				sb.append(winningPercentageTransition.get(i).get(k) +"\n");
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String s = "1468256109123.txt";
		String e = "1468299418268.txt";
		WinningPercentageTransition wpt = new WinningPercentageTransition(s, e);
		String csv = wpt.getCsv();
		
		FileWriter fw = new FileWriter(new File("./tmp/tmp.csv"));
		fw.write(csv);
		fw.close();
	}

}
