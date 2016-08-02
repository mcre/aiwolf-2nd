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

public class McreWerewolfC extends McreWerewolfA {	
	private boolean co = false;
	private boolean divinedToday = false;
	private List<Agent> divinedList = null;
	
	public McreWerewolfC() {
		super();
	}
	
	public McreWerewolfC(Map<String,Double> estimateRates) {
		super(estimateRates);
	}
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		co = false;
		divinedList = new ArrayList<>();
		divinedToday = false;
	}
	
	@Override
	public String talk() {
		//COしてない場合はCO
		if(!co){
			co = true;
			return TemplateTalkFactory.comingout(getMe(), Role.SEER);
		}
		
		//coしてて今日占い結果を言ってなければ占い
		if(co && getDay() > 0  && !divinedToday){
			divinedToday = true;
			Agent target = decideDivineTarget();
			if(target != null){
				divinedList.add(target);
				pretendVillagerEstimate.updateDefinedSpecies(target, Species.WEREWOLF);//村人目線に占い情報を更新
				return TemplateTalkFactory.divined(target, Species.WEREWOLF);	
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
	
	private Agent decideDivineTarget(){
		if(divinedList.size() >= 3)//3人黒出ししたらもう占わない
			return null;
		
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		for(Agent a: divinedList){
			candidate.remove(a);
		}
		for(Agent a: getWolfList()){
			candidate.remove(a);
		}
		
		return max(candidate, pretendVillagerEstimate.getWerewolfLikeness(), false);
	}

}
