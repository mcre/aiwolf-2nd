package net.mchs_u.mc.aiwolf.baikin.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.Agent;

public class McreBodyguard extends McreVillager {
	public McreBodyguard() {
		super();
	}
	
	public McreBodyguard(Map<String,Double> estimateRates) {
		super(estimateRates);
	}
	
	@Override
	public Agent guard() {
		return decideGuardTarget();
	}

	//護衛対象を決める
	private Agent decideGuardTarget(){
		//自分目線で、生存者のうち最も人間っぽいひと
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		return max(candidate, subjectiveEstimate.getVillagerTeamLikeness(), true);
	}
}
