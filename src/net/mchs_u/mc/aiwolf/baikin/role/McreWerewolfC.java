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
	}
	
	@Override
	public String talk() {
		//COしてない場合はCO
		if(!co){
			co = true;
			return TemplateTalkFactory.comingout(getMe(), Role.SEER);
		}
		
		//coしてたらひたすら占い
		if(co && getDay() > 0 ){
			Agent target = decideDivineTarget();
			if(target != null){
				divinedList.add(target);
				if(getWolfList().contains(target)){
					pretendVillagerEstimate.updateDefinedSpecies(target, Species.HUMAN);//村人目線に占い情報を更新
					return TemplateTalkFactory.divined(target, Species.HUMAN);
				} else {
					pretendVillagerEstimate.updateDefinedSpecies(target, Species.WEREWOLF);//村人目線に占い情報を更新
					return TemplateTalkFactory.divined(target, Species.WEREWOLF);	
				}
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
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a:divinedList){
			candidate.remove(a);
		}
		for(Agent a:candidate){
			if(getWolfList().contains(a)){
				return a;
			}
		}
		return max(candidate, pretendVillagerEstimate.getWerewolfLikeness(), false);
	}

}
