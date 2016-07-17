package net.mchs_u.mc.aiwolf.baikin.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;

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
		//自分目線で、生存者のうち最も人狼っぽいひと
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		return max(candidate, subjectiveEstimate.getWerewolfLikeness(), false);
	}
}
