package net.mchs_u.mc.aiwolf.baikin.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;

import net.mchs_u.mc.aiwolf.baikin.Constants;

public class McreVillager extends AbstractMcreRole {	
	protected Agent declareVotedTarget = null; //今日最後に投票宣言をした対象

	public McreVillager() {
		super();
	}
	
	public McreVillager(Map<String,Double> estimateRates) {
		super(estimateRates);
	}
	
	@Override
	public void dayStart() {
		super.dayStart();
		declareVotedTarget = null;
	}
	
	@Override
	public String talk() {
		Agent target = decideVoteTarget();
		if(target == null)
			return TemplateTalkFactory.skip();
		if(declareVotedTarget != null && target.equals(declareVotedTarget))
			return TemplateTalkFactory.skip();
		declareVotedTarget = target;
		return TemplateTalkFactory.vote(target);
	}

	@Override
	public Agent vote() {
		return decideVoteTarget();
	}
	
	//投票対象を決める
	protected Agent decideVoteTarget(){
		switch (Constants.PATTERN_VILLAGER) {
		case 0:
			return decideVoteTargetA();
		case 1:
			return decideVoteTargetB();
		case 2:
			return decideVoteTargetC();
		case 3:
			return decideVoteTargetD();
		}
		return null;
	}
	
	//生存者のうち、自分目線で最も人狼っぽいひとに投票
	private Agent decideVoteTargetA(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		return max(candidate, subjectiveEstimate.getWerewolfLikeness(), false);
	}

	//生存者のうち、客観目線で最も人狼っぽいひとに投票
	private Agent decideVoteTargetB(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		return max(candidate, objectiveEstimate.getWerewolfLikeness(), false);
	}

	//生存者のうち、自分目線で最も村人陣営っぽくないひとに投票
	private Agent decideVoteTargetC(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		return min(candidate, subjectiveEstimate.getVillagerTeamLikeness(), true);
	}

	//生存者のうち、客観目線で最も村人陣営っぽくないひとに投票
	private Agent decideVoteTargetD(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		return min(candidate, objectiveEstimate.getVillagerTeamLikeness(), true);
	}

	
}
