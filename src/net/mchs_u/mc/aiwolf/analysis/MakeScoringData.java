package net.mchs_u.mc.aiwolf.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//Azureにくわせるスコア用データ作成。適当
public class MakeScoringData {
	public static void main(String[] args) throws IOException {
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter("tmp/scoring_data.csv"))){
			bw.write("BLACK_DIVINED_WEREWOLF_TO_WEREWOLF,NEVER_CO_FROM_POSSESSED,VOTE_WEREWOLF_TO_WEREWOLF,TEAM_MEMBER_WOLF,FALSE_INQUESTED_FROM_VILLAGER_TEAM,FALSE_DIVINED_FROM_VILLAGER_TEAM,BLACK_DIVINED_WEREWOLF_TO_POSSESSED,VOTE_WEREWOLF_TO_POSSESSED,ONLY_SEER_CO_FROM_WEREWOLF_TEAM,2_SEER_CO_FROM_VILLGER_TEAM,VOTE_POSSESSED_TO_WEREWOLF,BLACK_DIVINED_POSSESSED_TO_WEREWOLF,2_BODYGUARD_CO_FROM_VILLAGER_TEAM,ONLY_MEDIUM_CO_FROM_WEREWOLF_TEAM,2_MEDIUM_CO_FROM_VILLAGER_TEAM\n");	
			for(int i=0; i < 1000000; i++){
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f,",Math.random()));
				bw.append(String.format("%5.3f\n",Math.random()));
			}
		}
	}
}
