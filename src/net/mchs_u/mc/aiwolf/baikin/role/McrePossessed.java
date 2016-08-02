package net.mchs_u.mc.aiwolf.baikin.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import net.mchs_u.mc.aiwolf.baikin.Constants;

public class McrePossessed extends AbstractMcreRole {
	private Agent declaredVoteTarget = null; //今日最後に投票宣言をした対象
	
	private boolean co = false;
	private boolean divinedToday = false;
	private List<Agent> divinedList = null;

	public McrePossessed() {
		super();
	}
	
	public McrePossessed(Map<String,Double> estimateRates) {
		super(estimateRates);
	}
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		co = false;
		divinedList = new ArrayList<>();
	}	
	
	@Override
	public void dayStart() {
		super.dayStart();
		divinedToday = false;
		declaredVoteTarget = null;
	}
	
	@Override
	public String talk() {
		//COしてない場合はCO
		if(!co){
			co = true;
			return TemplateTalkFactory.comingout(getMe(), Role.SEER);
		}
		
		//占い結果を言ってなければ占い
		if(getDay()>0 && !divinedToday){
			divinedToday = true;
			Agent target = decideDivineTarget();
			if(target != null){
				divinedList.add(target);
				pretendVillagerEstimate.updateDefinedSpecies(target, Species.HUMAN);//村人目線に占い情報を更新
				switch (Constants.PATTERN_POSSESSED) {
				case 0:
					return TemplateTalkFactory.divined(target, Species.HUMAN);
				case 1:
					return TemplateTalkFactory.divined(target, Species.WEREWOLF);
				case 2:
					return TemplateTalkFactory.divined(target, Species.WEREWOLF);
				case 3:
					return TemplateTalkFactory.divined(target, Species.WEREWOLF);
				}
				return null;
			}
		}
		
		//投票対象を宣言
		Agent target = decideVoteTarget();
		
		if(target == null)
			return TemplateTalkFactory.skip();
		if(declaredVoteTarget != null && target.equals(declaredVoteTarget))
			return TemplateTalkFactory.skip();
		declaredVoteTarget = target;
		return TemplateTalkFactory.vote(target);
	}
	
	@Override
	public Agent vote() {
		return decideVoteTarget();
	}
	
	private Agent decideDivineTarget(){
		//CASE変えるところもう一個あるので注意
		switch (Constants.PATTERN_POSSESSED) {
		case 0:
			return decideDivineTargetA();
		case 1:
			return decideDivineTargetB();
		case 2:
			return decideDivineTargetC();
		case 3:
			return decideDivineTargetD();
		}
		return null;
	}

	//村人目線で最も人狼っぽい人を占って白出し
	private Agent decideDivineTargetA(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a:divinedList){
			candidate.remove(a);
		}
		return max(candidate, pretendVillagerEstimate.getWerewolfLikeness(), false);
	}
	
	//自分目線で最も人間っぽい人を占って黒出し
	private Agent decideDivineTargetB(){		
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a:divinedList){
			candidate.remove(a);
		}
		return max(candidate, subjectiveEstimate.getVillagerTeamLikeness(), true);
	}

	//自分目線で最も人間っぽい人を占って黒出し(3人まで)
	private Agent decideDivineTargetC(){
		if(divinedList.size() >= 3)//3人黒出ししたらもう占わない
			return null;
		
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a:divinedList){
			candidate.remove(a);
		}
		return max(candidate, subjectiveEstimate.getVillagerTeamLikeness(), true);
	}
	
	//自分目線で最も人間っぽい人を占って黒出し(3人まで)、ただし占霊COの人を除く
	private Agent decideDivineTargetD(){
		if(divinedList.size() >= 3)//3人黒出ししたらもう占わない
			return null;
		
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a: divinedList){
			candidate.remove(a);
		}
		for(Agent a: subjectiveEstimate.getCoMediumSet()){
			candidate.remove(a);
		}
		for(Agent a: subjectiveEstimate.getCoSeerSet()){
			candidate.remove(a);
		}
		return max(candidate, subjectiveEstimate.getVillagerTeamLikeness(), true);
	}
	
	//村人目線で、生存者のうち最も人狼っぽいひとに投票
	private Agent decideVoteTarget(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		return max(candidate, pretendVillagerEstimate.getWerewolfLikeness(), false);
	}

}
