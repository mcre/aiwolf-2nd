package net.mchs_u.mc.aiwolf.baikin.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class McreWerewolf extends AbstractMcreRole {
	private Agent declaredVoteTarget = null; //今日最後に投票宣言をした対象
	private Agent declaredAttackTarget = null; //今日最後に襲撃宣言をした対象
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		for(Agent a: getWolfList()){
			subjectiveEstimate.updateDefinedRole(a, Role.WEREWOLF); 
			pretendVillagerEstimate.updateTeamMemberWolf(getWolfList());
		}
		
	}	

	@Override
	public void dayStart() {
		super.dayStart();
		declaredVoteTarget = null;
		declaredAttackTarget = null;
	}
	
	@Override
	public String whisper() {
		Agent target = decideAttackTarget();
		if(target == null)
			return TemplateWhisperFactory.skip();
		if(declaredAttackTarget != null && target.equals(declaredAttackTarget))
			return TemplateWhisperFactory.skip();
		declaredAttackTarget = target;
		return TemplateWhisperFactory.attack(target);
	}
	
	@Override
	public String talk() {
		Agent target = decideVoteTarget();
		if(target == null)
			return TemplateTalkFactory.skip();
		if(declaredVoteTarget != null && target.equals(declaredVoteTarget))
			return TemplateTalkFactory.skip();
		declaredVoteTarget = target;
		return TemplateTalkFactory.vote(target);
	}

	@Override
	public Agent attack() {
		return decideAttackTarget();
	}
	
	@Override
	public Agent vote() {
		return decideVoteTarget();
	}
	
	//襲撃対象を決める
	protected Agent decideAttackTarget(){
		//客観的に、人狼らしくない人
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a: getWolfList()){
			candidate.remove(a);
		}

		return min(candidate, objectiveEstimate.getWerewolfLikeness(), false);
	}
	
	//投票対象を決める
	private Agent decideVoteTarget(){
		//村人目線で、生存者のうち最も人狼っぽいひと
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		return max(candidate, pretendVillagerEstimate.getWerewolfLikeness(), false);
	}
	
	private List<Agent> getWolfList(){
		List<Agent> wolfList = new ArrayList<Agent>();

		Map<Agent, Role> wolfMap = getLatestDayGameInfo().getRoleMap();
		for(Entry<Agent, Role> set: wolfMap.entrySet()){
			if(set.getValue() == Role.WEREWOLF){
				wolfList.add(set.getKey());
			}
		}
		return wolfList;
	}

}
