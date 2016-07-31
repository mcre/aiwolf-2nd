package net.mchs_u.mc.aiwolf.baikin;

import java.util.HashMap;
import java.util.Map;

//gitの修正がいっぱい出るのが嫌なのでよく変える定数はまとめる
public class Constants {
	// 0 : cashさんのそのまま
	// 1 : ver20160730 cashさんのベースにちょっと確率調整したもの。役職正解率は上がるけど、勝率はほぼ変わらない
	public static final int PATTERN_ESTIMATE = 1;
	
	// 0 : 占っていない生存者のうち[自分]目線で最も人狼っぽい人を占う。占い師COした人はあとまわし。
	// 1 : 占っていない生存者のうち[客観]目線で最も人狼っぽい人を占う。占い師COした人はあとまわし。
	// 2 : 占っていない生存者のうち[自分]目線で最も人狼っぽい人を占う。占いCOした人も含む
	// 3 : 占っていない生存者のうち[客観]目線で最も人狼っぽい人を占う。占いCOした人も含む
	public static final int PATTERN_SEER = 0;
	
	public static final int PATTERN_BODYGUARD = 0;
	public static final int PATTERN_VILLAGER = 0;
	public static final int PATTERN_WEREWOLF = 0;
	public static final int PATTERN_MEDIUM = 0;
	public static final int PATTERN_POSSESSED = 0;	
	
	public static Map<String,Double> getDefaultRates(){
		Map<String,Double> rates = new HashMap<>();

		switch(PATTERN_ESTIMATE){
		case 0:
			rates.put("VOTE_POSSESSED_TO_WEREWOLF"         , 0.900d);
			rates.put("VOTE_WEREWOLF_TO_POSSESSED"         , 0.900d);
			rates.put("VOTE_WEREWOLF_TO_WEREWOLF"          , 0.900d);
			rates.put("FALSE_INQUESTED_FROM_VILLAGER_TEAM" , 0.010d);
			rates.put("FALSE_DIVINED_FROM_VILLAGER_TEAM"   , 0.010d);
			rates.put("BLACK_DIVINED_POSSESSED_TO_WEREWOLF", 0.900d);
			rates.put("BLACK_DIVINED_WEREWOLF_TO_POSSESSED", 0.500d);
			rates.put("BLACK_DIVINED_WEREWOLF_TO_WEREWOLF" , 0.100d);
			rates.put("2_SEER_CO_FROM_VILLGER_TEAM"        , 0.001d);
			rates.put("2_MEDIUM_CO_FROM_VILLAGER_TEAM"     , 0.001d);
			rates.put("2_BODYGUARD_CO_FROM_VILLAGER_TEAM"  , 0.001d);
			rates.put("NEVER_CO_FROM_POSSESSED"            , 0.100d);
			rates.put("ONLY_SEER_CO_FROM_WEREWOLF_TEAM"    , 0.010d);
			rates.put("ONLY_MEDIUM_CO_FROM_WEREWOLF_TEAM"  , 0.010d);
			rates.put("TEAM_MEMBER_WOLF"                   , 0.500d);
			break;
		case 1:
			rates.put("VOTE_POSSESSED_TO_WEREWOLF"         , 0.900d);
			rates.put("VOTE_WEREWOLF_TO_POSSESSED"         , 0.900d);
			rates.put("VOTE_WEREWOLF_TO_WEREWOLF"          , 0.200d);
			rates.put("FALSE_INQUESTED_FROM_VILLAGER_TEAM" , 0.010d);
			rates.put("FALSE_DIVINED_FROM_VILLAGER_TEAM"   , 0.000d);
			rates.put("BLACK_DIVINED_POSSESSED_TO_WEREWOLF", 0.900d);
			rates.put("BLACK_DIVINED_WEREWOLF_TO_POSSESSED", 0.600d);
			rates.put("BLACK_DIVINED_WEREWOLF_TO_WEREWOLF" , 0.500d);
			rates.put("2_SEER_CO_FROM_VILLGER_TEAM"        , 0.001d);
			rates.put("2_MEDIUM_CO_FROM_VILLAGER_TEAM"     , 0.001d);
			rates.put("2_BODYGUARD_CO_FROM_VILLAGER_TEAM"  , 0.200d);
			rates.put("NEVER_CO_FROM_POSSESSED"            , 1.000d);
			rates.put("ONLY_SEER_CO_FROM_WEREWOLF_TEAM"    , 0.010d);
			rates.put("ONLY_MEDIUM_CO_FROM_WEREWOLF_TEAM"  , 0.010d);
			rates.put("TEAM_MEMBER_WOLF"                   , 0.500d);
			break;
		}
		
		return rates;
	}	

}
