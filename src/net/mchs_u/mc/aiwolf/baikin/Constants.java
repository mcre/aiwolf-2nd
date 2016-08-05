package net.mchs_u.mc.aiwolf.baikin;

import java.util.HashMap;
import java.util.Map;

import org.aiwolf.common.data.Role;

//gitの修正がいっぱい出るのが嫌なのでよく変える定数はまとめる
public class Constants {
	/* -- Estimate 確率設定
	 * 0 : cashさんのそのまま
	 * 1 : ver20160730 cashさんのベースにちょっと確率調整したもの。役職正解率は上がるけど、勝率はほぼ変わらない
	 * 2 : ver20160804 cashさんのとほぼ一緒だけどNeverCoのみ消したもの。RateAdjustの成績は一番よかったやつ
	 * 3 : ver20160804 人狼のみパターン0、ほかはパターン2
	 */
	public static final int PATTERN_ESTIMATE = 3;
	
	/* -- Werewolf 人狼
	 * 0 : COしない・客観目線で人狼らしくない人を襲撃・村人目線で最も人狼っぽい人に投票(？)
	 * 1 : COしない・客観目線で人狼らしくない人を襲撃（ただし投票宣言Targetが多い人を除く）・村人目線で最も人狼っぽい人に投票
	 * 2 : COしない・占いCOのうち自分目線で人間らしい人を襲撃、いなければ客観目線で人狼らしくない人を襲撃（ただし投票宣言Targetが多い人を除く）・村人目線で最も人狼っぽい人に投票
	 * 3 : COしない・霊能COのうち自分目線で人間らしい人を襲撃、いなければ客観目線で人狼らしくない人を襲撃（ただし投票宣言Targetが多い人を除く）・村人目線で最も人狼っぽい人に投票(☆)
	 * 4 : 占い師が人狼じゃない側から1人しか出ない場合は占い師CO、あとは狂人のパターン0と同様・客観目線で人狼らしくない人を襲撃(×)
	 * 5 : 0日目占い師CO、1日ずつ村人3人を黒出し。あとはパターン0と同じ(×)
	 */
	public static final int PATTERN_WEREWOLF = 0;
	
	/* -- Possessed 狂人 
	 * 0 : 0日目占い師CO、[村人]目線で最も[人狼]っぽい人を占って[白]出し・村人目線で最も人狼っぽい人に投票
	 * 1 : 0日目占い師CO、[自分]目線で最も[人間]っぽい人を占って[黒]出し・村人目線で最も人狼っぽい人に投票
	 * 2 : 0日目占い師CO、[自分]目線で最も[人間]っぽい人を占って[黒]出し(3人まで)・村人目線で最も人狼っぽい人に投票(☆)
	 * 3 : 0日目占い師CO、[自分]目線で最も[人間]っぽい人を占って[黒]出し(3人まで)、ただし占霊COの人を除く・村人目線で最も人狼っぽい人に投票(？)
	 */
	public static final int PATTERN_POSSESSED = 2;

	/* -- Villager 村人
	 * 0 : [自分]目線で最も[人狼っぽい]ひとに投票
	 * 1 : [客観]目線で最も[人狼っぽい]ひとに投票(？)
	 * 2 : [自分]目線で最も[村人陣営っぽくない]ひとに投票
	 * 3 : [客観]目線で最も[村人陣営っぽくない]ひとに投票
	 */
	public static final int PATTERN_VILLAGER = 1;
	
	/* -- Seer 占い師
	 * 0 : [自分]目線で最も人狼っぽい人を占う。占い師COした人は[あとまわし](☆？)
	 * 1 : [客観]目線で最も人狼っぽい人を占う。占い師COした人は[あとまわし]
	 * 2 : [自分]目線で最も人狼っぽい人を占う。占いCOした人も[含む]
	 * 3 : [客観]目線で最も人狼っぽい人を占う。占いCOした人も[含む]
	 * 4 : 占い結果が出たとき初めてCO。あとはPattern0と同じ。
	 */
	public static final int PATTERN_SEER = 0;
	
	/* -- Medium 霊能
	 * 0 : 0日目CO(☆)
	 * 1 : 霊能結果があるとき初めてCO(×)
	 */
	public static final int PATTERN_MEDIUM = 0; 
	
	/* -- BodyGuard 狩人
	 * 0 : 自分目線で最も[村人陣営っぽい]ひとを護衛(？)
	 * 1 : 占い師COで自分目線で最も[村人陣営っぽい]人、いなければ同様に霊能CO、いなければ全体から護衛
	 * 2 : 自分目線で最も[狼っぽくない]ひとを護衛(×)
	 * 3 : 占い師COで自分目線で最も[狼っぽくない]人、いなければ同様に霊能CO、いなければ全体から護衛
	 */
	public static final int PATTERN_BODYGUARD = 0;
	
	public static Map<String,Double> getDefaultRates(Role myRole){
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
		case 2:
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
			rates.put("NEVER_CO_FROM_POSSESSED"            , 1.000d);//☆
			rates.put("ONLY_SEER_CO_FROM_WEREWOLF_TEAM"    , 0.010d);
			rates.put("ONLY_MEDIUM_CO_FROM_WEREWOLF_TEAM"  , 0.010d);
			rates.put("TEAM_MEMBER_WOLF"                   , 0.500d);
			break;
		case 3:
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
			rates.put("ONLY_SEER_CO_FROM_WEREWOLF_TEAM"    , 0.010d);
			rates.put("ONLY_MEDIUM_CO_FROM_WEREWOLF_TEAM"  , 0.010d);
			rates.put("TEAM_MEMBER_WOLF"                   , 0.500d);
			if(myRole == Role.WEREWOLF)
				rates.put("NEVER_CO_FROM_POSSESSED"            , 0.100d);
			else
				rates.put("NEVER_CO_FROM_POSSESSED"            , 1.000d);
			break;
		}
		
		return rates;
	}	

}
