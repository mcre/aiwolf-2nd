package net.mchs_u.mc.aiwolf.anpan.role;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Agent;

public class McreBodyguard extends McreVillager {
	
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
