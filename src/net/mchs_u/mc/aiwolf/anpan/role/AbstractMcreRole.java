package net.mchs_u.mc.aiwolf.anpan.role;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import net.mchs_u.mc.aiwolf.anpan.Estimate;

public abstract class AbstractMcreRole extends AbstractRole {
	protected int readTalkNum = 0;
	protected Estimate objectiveEstimate = null; //客観
	protected Estimate subjectiveEstimate = null; //主観
	protected Estimate pretendVillagerEstimate = null; //村人目線

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		objectiveEstimate = new Estimate(gameInfo.getAgentList(), getMe());
		
		subjectiveEstimate = new Estimate(gameInfo.getAgentList(), getMe());
		subjectiveEstimate.updateDefinedRole(getMe(), getMyRole());
		
		pretendVillagerEstimate = new Estimate(gameInfo.getAgentList(), getMe());
		pretendVillagerEstimate.updateDefinedRole(getMe(), Role.VILLAGER);
		print();
	}	
	
	@Override
	public void dayStart() {
		readTalkNum = 0;
		
		GameInfo info = getLatestDayGameInfo();
		
		objectiveEstimate.updateAliveAgentList(info.getAliveAgentList());
		objectiveEstimate.updateAttackedAgent(info.getAttackedAgent());
		objectiveEstimate.updateVoteList(info.getVoteList());
		
		subjectiveEstimate.updateAliveAgentList(info.getAliveAgentList());
		subjectiveEstimate.updateAttackedAgent(info.getAttackedAgent());
		subjectiveEstimate.updateVoteList(info.getVoteList());

		pretendVillagerEstimate.updateAliveAgentList(info.getAliveAgentList());
		pretendVillagerEstimate.updateAttackedAgent(info.getAttackedAgent());
		pretendVillagerEstimate.updateVoteList(info.getVoteList());
		print();
	}
	
	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		
		List<Talk> talkList = gameInfo.getTalkList();
		for(int i = readTalkNum; i < talkList.size(); i++){
			objectiveEstimate.updateTalk(talkList.get(i));
			subjectiveEstimate.updateTalk(talkList.get(i));
			pretendVillagerEstimate.updateTalk(talkList.get(i));
			
			readTalkNum++;
		}
		print();
	}
	
	@Override
	public Agent attack() {
		return null;
	}

	@Override
	public Agent divine() {
		return null;
	}

	@Override
	public Agent guard() {
		return null;
	}

	@Override
	public String whisper() {
		return null;
	}
	
	@Override
	public void finish() {
		print();
	}
	
	protected Agent randomSelect(List<Agent> agents){
		int num = new Random().nextInt(agents.size());
		return agents.get(num);
	}
	
	protected Agent min(List<Agent> candidate, Map<Agent, Double> likeness){
		Agent ret = null;
		double min = 2d;
		for(Agent a: candidate){
			if(min > likeness.get(a)){
				min = likeness.get(a);
				ret = a;
			}
		}
		return ret;
	}
	
	protected Agent max(List<Agent> candidate, Map<Agent, Double> likeness){
		Agent ret = null;
		double max = -1;
		for(Agent a: candidate){
			if(max < likeness.get(a)){
				max = likeness.get(a);
				ret = a;
			}
		}
		return ret;
	}
	
	private void print(){
		System.out.println("+++++++++++++++ 客観視点 +++++++++++++++");
		objectiveEstimate.print(); 
		System.out.println("+++++++++++++++ 自分視点 +++++++++++++++");
		subjectiveEstimate.print();
		System.out.println("+++++++++++++++ 村人視点 +++++++++++++++");
		pretendVillagerEstimate.print();
		System.out.println();
	}
	
}
