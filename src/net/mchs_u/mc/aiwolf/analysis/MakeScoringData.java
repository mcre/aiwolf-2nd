package net.mchs_u.mc.aiwolf.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//Azureにくわせるスコア用データ作成。適当
public class MakeScoringData {
	public static void main(String[] args) throws IOException {
		/*
		double step = 0.5d;
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter("tmp/scoring_data.csv"))){
			bw.write("BLACK_DIVINED_WEREWOLF_TO_WEREWOLF,NEVER_CO_FROM_POSSESSED,VOTE_WEREWOLF_TO_WEREWOLF,TEAM_MEMBER_WOLF,FALSE_INQUESTED_FROM_VILLAGER_TEAM,FALSE_DIVINED_FROM_VILLAGER_TEAM,BLACK_DIVINED_WEREWOLF_TO_POSSESSED,VOTE_WEREWOLF_TO_POSSESSED,ONLY_SEER_CO_FROM_WEREWOLF_TEAM,2_SEER_CO_FROM_VILLGER_TEAM,VOTE_POSSESSED_TO_WEREWOLF,BLACK_DIVINED_POSSESSED_TO_WEREWOLF,2_BODYGUARD_CO_FROM_VILLAGER_TEAM,ONLY_MEDIUM_CO_FROM_WEREWOLF_TEAM,2_MEDIUM_CO_FROM_VILLAGER_TEAM\n");	
			double i[] = new double[15];
			for(i[0] = 0d; i[0] <= 1d; i[0] += step)
				for(i[1] = 0d; i[1] <= 1d; i[1] += step)
					for(i[2] = 0d; i[2] <= 1d; i[2] += step)
						for(i[3] = 0d; i[3] <= 1d; i[3] += step)
							for(i[4] = 0d; i[4] <= 1d; i[4] += step)
								for(i[5] = 0d; i[5] <= 1d; i[5] += step)
									for(i[6] = 0d; i[6] <= 1d; i[6] += step)
										for(i[7] = 0d; i[7] <= 1d; i[7] += step)
											for(i[8] = 0d; i[8] <= 1d; i[8] += step)
												for(i[9] = 0d; i[9] <= 1d; i[9] += step)
													for(i[10] = 0d; i[10] <= 1d; i[10] += step)
														for(i[11] = 0d; i[11] <= 1d; i[11] += step)
															for(i[12] = 0d; i[12] <= 1d; i[12] += step)
																for(i[13] = 0d; i[13] <= 1d; i[13] += step)
																	for(i[14] = 0d; i[14] <= 1d; i[14] += step)
																		bw.append(i[0] + "," + i[1] + "," + i[2] + "," + i[3] + "," + i[4] + "," + i[5] + "," + i[6] + "," + i[7] + "," + i[8] + "," + i[9] + "," + i[10] + "," + i[11] + "," + i[12] + "," + i[13] + "," + i[14] + "\n");
		}
		*/
		
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
