package net.mchs_u.mc.aiwolf.anpan.role;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import net.mchs_u.mc.aiwolf.anpan.Estimate;
import net.mchs_u.mc.aiwolf.debug.EstimateGraph;

public abstract class AbstractMcreRole extends AbstractRole {
	private static final boolean DEBUG_ESTIMATE_GRAPH = false;
	private static final boolean DEBUG_ESTIMATE_PRINT = false;
	
	protected List<Agent> agents = null;
	protected int readTalkNum = 0;
	protected Estimate objectiveEstimate = null; //客観
	protected Estimate subjectiveEstimate = null; //主観
	protected Estimate pretendVillagerEstimate = null; //村人目線
	
	private JFrame frame = null;
	private List<EstimateGraph> estimateGraphs = null;
	
	private List<Double> random = null; //min,maxを選ぶ際に、同率のうちどれを優先にするかを乱数で決める。

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		agents = gameInfo.getAgentList();

		objectiveEstimate = new Estimate(agents, getMe());
		
		subjectiveEstimate = new Estimate(agents, getMe());
		subjectiveEstimate.updateDefinedRole(getMe(), getMyRole());
		
		pretendVillagerEstimate = new Estimate(agents, getMe());
		pretendVillagerEstimate.updateDefinedRole(getMe(), Role.VILLAGER);
		
		initDebugEstimateGraph();
		debugEstimateRefresh();
		
		random = new ArrayList<>();
		for(int i = 0; i < 15; i++){
			random.add(Math.random() / 100000d);
		}
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
		debugEstimateRefresh();
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
		debugEstimateRefresh();
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
		debugEstimateRefresh();
		if(DEBUG_ESTIMATE_GRAPH){
			frame.dispose();
		}
	}
	
	protected Agent randomSelect(List<Agent> agents){
		int num = new Random().nextInt(agents.size());
		return agents.get(num);
	}
	
	protected Agent min(List<Agent> candidate, Map<Agent, Double> likeness, boolean plus){
		Agent ret = null;
		double min = 2d;
		for(int i = 0; i < candidate.size(); i++){
			Agent a = candidate.get(i);
			double l = likeness.get(a);
			if(plus)
				l += random.get(i);
			else
				l -= random.get(i);
			
			if(min > l){
				min = l;
				ret = a;
			}
		}
		return ret;
	}
	
	protected Agent max(List<Agent> candidate, Map<Agent, Double> likeness, boolean plus){//村人らしさをプラス、人狼らしさをマイナス
		Agent ret = null;
		double max = -1;
		for(int i = 0; i < candidate.size(); i++){
			Agent a = candidate.get(i);
			double l = likeness.get(a);
			if(plus)
				l += random.get(i);
			else
				l -= random.get(i);
			
			if(max < l){
				max = l;
				ret = a;
			}
		}
		return ret;
	}
	
	private void initDebugEstimateGraph(){
		if(DEBUG_ESTIMATE_GRAPH){
			estimateGraphs = new ArrayList<>();
			for(int i = 0; i < 3; i++)
				estimateGraphs.add(new EstimateGraph());
			
			frame = new JFrame("debug");
			frame.setSize(400, 800);
			frame.setLocation(1030, 0);
			
			GridLayout gl = new GridLayout(3,1);
			gl.setVgap(50);
			frame.setLayout(gl);
			
			for(EstimateGraph eg: estimateGraphs)
				frame.add(eg);
			
			frame.setVisible(true);
		}
	}
	
	private void debugEstimateRefresh(){
		if(DEBUG_ESTIMATE_PRINT){
			System.out.println("+++++++++++++++ 客観視点 +++++++++++++++");
			objectiveEstimate.print(); 
			System.out.println("+++++++++++++++ 自分視点 +++++++++++++++");
			subjectiveEstimate.print();
			System.out.println("+++++++++++++++ 村人視点 +++++++++++++++");
			pretendVillagerEstimate.print();
			System.out.println();
		}
		
		if(DEBUG_ESTIMATE_GRAPH){
			estimateGraphs.get(0).refreshVillagerTeamLikeness(agents, subjectiveEstimate.getVillagerTeamLikeness());
			estimateGraphs.get(0).refreshWerewolfLikeness(agents, subjectiveEstimate.getWerewolfLikeness());
			
			estimateGraphs.get(1).refreshVillagerTeamLikeness(agents, objectiveEstimate.getVillagerTeamLikeness());
			estimateGraphs.get(1).refreshWerewolfLikeness(agents, objectiveEstimate.getWerewolfLikeness());
			
			estimateGraphs.get(2).refreshVillagerTeamLikeness(agents, pretendVillagerEstimate.getVillagerTeamLikeness());
			estimateGraphs.get(2).refreshWerewolfLikeness(agents, pretendVillagerEstimate.getWerewolfLikeness());
		}
	}
	
}
