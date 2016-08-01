package net.mchs_u.mc.aiwolf.baikin.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import net.mchs_u.mc.aiwolf.baikin.Constants;

public class McreWerewolfA extends AbstractMcreRole {	
	protected Agent declaredVoteTarget = null; //今日最後に投票宣言をした対象
	protected Agent declaredAttackTarget = null; //今日最後に襲撃宣言をした対象

	public McreWerewolfA() {
		super();
	}
	
	public McreWerewolfA(Map<String,Double> estimateRates) {
		super(estimateRates);
	}
	
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
	
	protected List<Agent> getWolfList(){
		List<Agent> wolfList = new ArrayList<Agent>();

		Map<Agent, Role> wolfMap = getLatestDayGameInfo().getRoleMap();
		for(Entry<Agent, Role> set: wolfMap.entrySet()){
			if(set.getValue() == Role.WEREWOLF){
				wolfList.add(set.getKey());
			}
		}
		return wolfList;
	}
	
	//襲撃対象を決める
	protected Agent decideAttackTarget(){
		switch (Constants.PATTERN_WEREWOLF) {
		case 0:
			return decideAttackTargetA();
		case 1:
			return decideAttackTargetB();
		case 2:
			return decideAttackTargetC();
		case 3:
			return decideAttackTargetD();
		default:
			return decideAttackTargetA();
		}		
	}
	
	//客観的に、人狼らしくない人
	protected Agent decideAttackTargetA(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a: getWolfList()){
			candidate.remove(a);
		}
		return min(candidate, objectiveEstimate.getWerewolfLikeness(), false);
	}	
	
	//客観的に、人狼らしくない人（ただし投票宣言Targetが多い人を除く）
	protected Agent decideAttackTargetB(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a: getWolfList()){
			candidate.remove(a);
		}
		List<Agent> backup = new ArrayList<>(candidate);
		for(Agent a: objectiveEstimate.getMostVotePlanedAgents()){
			candidate.remove(a);
		}
		if(candidate.size() > 0){
			return min(candidate, objectiveEstimate.getWerewolfLikeness(), false);
		}else{
			return min(backup, objectiveEstimate.getWerewolfLikeness(), false);
		}
	}
	
	//占いCOのうち自分目線で人間らしい人を襲撃、いなければB
	protected Agent decideAttackTargetC(){
		Collection<Agent> candidate = subjectiveEstimate.getCoSeerSet();
		candidate = removeDeadAgent(candidate);
		for(Agent a: getWolfList()){
			candidate.remove(a);
		}
		if(candidate.size() <= 0)
			return decideAttackTargetB();
		
		return max(candidate, subjectiveEstimate.getVillagerTeamLikeness(), true);
	}
	
	//霊能COのうち自分目線で人間らしい人を襲撃、いなければB
	protected Agent decideAttackTargetD(){
		Collection<Agent> candidate = subjectiveEstimate.getCoMediumSet();
		candidate = removeDeadAgent(candidate);
		for(Agent a: getWolfList()){
			candidate.remove(a);
		}
		if(candidate.size() <= 0)
			return decideAttackTargetB();
		
		return max(candidate, subjectiveEstimate.getVillagerTeamLikeness(), true);
	}
	
	//投票対象を決める
	protected Agent decideVoteTarget(){
		//村人目線で、生存者のうち最も人狼っぽいひと
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		return max(candidate, pretendVillagerEstimate.getWerewolfLikeness(), false);
	}

}
