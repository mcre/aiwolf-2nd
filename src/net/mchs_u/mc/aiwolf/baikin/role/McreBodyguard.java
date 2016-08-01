package net.mchs_u.mc.aiwolf.baikin.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.Agent;

import net.mchs_u.mc.aiwolf.baikin.Constants;

public class McreBodyguard extends McreVillager {
	public McreBodyguard() {
		super();
	}
	
	public McreBodyguard(Map<String,Double> estimateRates) {
		super(estimateRates);
	}
	
	@Override
	public Agent guard() {
		switch (Constants.PATTERN_BODYGUARD) {
		case 0:
			return decideGuardTargetA();
		case 1:
			return decideGuardTargetB();
		case 2:
			return decideGuardTargetC();
		case 3:
			return decideGuardTargetD();
		}
		return null;
	}

	//生存者のうち、自分目線で最も村人陣営っぽいひと
	private Agent decideGuardTargetA(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		return max(candidate, subjectiveEstimate.getVillagerTeamLikeness(), true);
	}

	//生存者のうち、占い師COで自分目線で最も村人陣営っぽい人、いなければ同様に霊能CO、いなければ全体から
	private Agent decideGuardTargetB(){
		List<Agent> coSeerSet = removeDeadAgent(subjectiveEstimate.getCoSeerSet());
		if(!coSeerSet.isEmpty()){
			return max(coSeerSet, subjectiveEstimate.getVillagerTeamLikeness(), true);
		}
		
		List<Agent> coMediumSet = removeDeadAgent(subjectiveEstimate.getCoMediumSet());
		if(!coMediumSet.isEmpty()){
			return max(coMediumSet, subjectiveEstimate.getVillagerTeamLikeness(), true);
		}
		
		return decideGuardTargetA();
	}
	
	//生存者のうち、自分目線で最も狼っぽくないひと
	private Agent decideGuardTargetC(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		return min(candidate, subjectiveEstimate.getWerewolfLikeness(), false);
	}
	
	//生存者のうち、占い師COで自分目線で最も狼っぽくない人、いなければ同様に霊能CO、いなければ全体から
	private Agent decideGuardTargetD(){
		List<Agent> coSeerSet = removeDeadAgent(subjectiveEstimate.getCoSeerSet());
		if(!coSeerSet.isEmpty()){
			return min(coSeerSet, subjectiveEstimate.getWerewolfLikeness(), false);
		}
		
		List<Agent> coMediumSet = removeDeadAgent(subjectiveEstimate.getCoMediumSet());
		if(!coMediumSet.isEmpty()){
			return min(coMediumSet, subjectiveEstimate.getWerewolfLikeness(), false);
		}
		
		return decideGuardTargetC();
	}
	
	
}
