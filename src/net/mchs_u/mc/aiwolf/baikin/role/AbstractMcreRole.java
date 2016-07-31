package net.mchs_u.mc.aiwolf.baikin.role;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

import net.mchs_u.mc.aiwolf.baikin.Estimate;
import net.mchs_u.mc.aiwolf.debug.EstimateGraph;
import net.mchs_u.mc.aiwolf.starter.DirectStarter;

public abstract class AbstractMcreRole extends AbstractRole {
	private static final String RATE_ADJUST_PREFIX = "006_";
	private static final String RATE_ADJUST_DIR = "./rate_adjust/";
	
	private static final boolean DEBUG_ESTIMATE_GRAPH = DirectStarter.IS_VISUALIZE;
	private static final boolean DEBUG_ESTIMATE_PRINT = false;
	private static final boolean IS_SAVE_RATE_ADJUST = DirectStarter.IS_RATE_ADJUST_MODE;
	private StringBuffer montecarloOutput = null;
	
	protected List<Agent> agents = null;
	protected int readTalkNum = 0;
	protected Estimate objectiveEstimate = null; //客観
	protected Estimate subjectiveEstimate = null; //主観
	protected Estimate pretendVillagerEstimate = null; //村人目線
	
	private JFrame frame = null;
	private List<EstimateGraph> estimateGraphs = null;
	
	private List<Double> random = null; //min,maxを選ぶ際に、同率のうちどれを優先にするかを乱数で決める。
	
	private Map<String,Double> estimateRates = null;

	public AbstractMcreRole() {
		super();
	}
	
	public AbstractMcreRole(Map<String,Double> estimateRates) {
		super();
		this.estimateRates = estimateRates;
	}
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		agents = gameInfo.getAgentList();

		if(estimateRates == null){
			objectiveEstimate = new Estimate(agents, getMe());
			subjectiveEstimate = new Estimate(agents, getMe());
			pretendVillagerEstimate = new Estimate(agents, getMe());
		} else {
			objectiveEstimate = new Estimate(agents, getMe(), estimateRates);
			subjectiveEstimate = new Estimate(agents, getMe(), estimateRates);
			pretendVillagerEstimate = new Estimate(agents, getMe(), estimateRates);
		}
		
		subjectiveEstimate.updateDefinedRole(getMe(), getMyRole());
		pretendVillagerEstimate.updateDefinedRole(getMe(), Role.VILLAGER);
		
		initDebugEstimateGraph();
		debugEstimateRefresh();
		
		random = new ArrayList<>();
		for(int i = 0; i < 15; i++){
			random.add(Math.random() / 100000d);
		}
		
		if(IS_SAVE_RATE_ADJUST){
			montecarloOutput = new StringBuffer("");
			for(String key: estimateRates.keySet())
				montecarloOutput.append("RATES\t" + key + "\t" + estimateRates.get(key) + "\n");
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
		
		if(IS_SAVE_RATE_ADJUST){
			montecarloOutput.append(monteLoop());
		}
	}
	
	private String monteLoop(){
		GameInfo info = getLatestDayGameInfo();
		
		StringBuffer s = new StringBuffer("");
		s.append("MYSELF\t" + info.getDay() +"\t" + getMe().getAgentIdx() + "\t" + (info.getAliveAgentList().contains(getMe()) ? "ALIVE":"DEAD" ) + "\n");
		
		Map<Agent,Double> ov = objectiveEstimate.getVillagerTeamLikeness();
		Map<Agent,Double> ow = objectiveEstimate.getWerewolfLikeness();
		Map<Agent,Double> sv = subjectiveEstimate.getVillagerTeamLikeness();
		Map<Agent,Double> sw = subjectiveEstimate.getWerewolfLikeness();
		Map<Agent,Double> pv = pretendVillagerEstimate.getVillagerTeamLikeness();
		Map<Agent,Double> pw = pretendVillagerEstimate.getWerewolfLikeness();
		
		for(Agent a: agents)
			s.append("OBJ_VPW\t"+ info.getDay() +"\t" + a.getAgentIdx() + "\t" + (info.getAliveAgentList().contains(a) ? "ALIVE":"DEAD" ) + "\t" + ov.get(a) + "\t" + (1d - ov.get(a) - ow.get(a))  + "\t" + ow.get(a) + "\n");
		for(Agent a: agents)
			s.append("SBJ_VPW\t"+ info.getDay() +"\t" + a.getAgentIdx() + "\t" + (info.getAliveAgentList().contains(a) ? "ALIVE":"DEAD" ) + "\t" + sv.get(a) + "\t" + (1d - sv.get(a) - sw.get(a))  + "\t" + sw.get(a) + "\n");
		for(Agent a: agents)
			s.append("PRV_VPW\t"+ info.getDay() +"\t" + a.getAgentIdx() + "\t" + (info.getAliveAgentList().contains(a) ? "ALIVE":"DEAD" ) + "\t" + pv.get(a) + "\t" + (1d - pv.get(a) - pw.get(a))  + "\t" + pw.get(a) + "\n");
		
		return s.toString();
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
		if(IS_SAVE_RATE_ADJUST){
			GameInfo info = getLatestDayGameInfo();
			montecarloOutput.append(monteLoop());
			for(Agent a: agents)
				montecarloOutput.append("CORRECT\t" + getDay() + "\t" + a.getAgentIdx() + "\t" + (info.getAliveAgentList().contains(a) ? "ALIVE":"DEAD" ) + "\t" + info.getRoleMap().get(a).toString() + "\n");
			boolean winVil = true;
			for(Agent a: info.getAliveAgentList()){
				if(info.getRoleMap().get(a) == Role.WEREWOLF){
					winVil = false;
					break;
				}
			}
			if(winVil)
				montecarloOutput.append("WINNER\tVILLAGER_TEAM\n");
			else
				montecarloOutput.append("WINNER\tWEREWOLF_TEAM\n");
			
			try(FileWriter fw = new FileWriter(new File(RATE_ADJUST_DIR + RATE_ADJUST_PREFIX + (new Date()).getTime() + ".txt"))){
				fw.write(montecarloOutput.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected Agent randomSelect(List<Agent> agents){
		int num = new Random().nextInt(agents.size());
		return agents.get(num);
	}
	
	protected Agent min(Collection<Agent> candidate, Map<Agent, Double> likeness, boolean plus){//村人らしさの場合true,人狼らしさの場合false
		List<Agent> candidateList = new ArrayList<Agent>(candidate); 
		
		Agent ret = null;
		double min = 2d;
		for(int i = 0; i < candidateList.size(); i++){
			Agent a = candidateList.get(i);
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
	
	protected Agent max(Collection<Agent> candidate, Map<Agent, Double> likeness, boolean plus){//村人らしさの場合true,人狼らしさの場合false
		List<Agent> candidateList = new ArrayList<Agent>(candidate); 
		
		Agent ret = null;
		double max = -1;
		for(int i = 0; i < candidateList.size(); i++){
			Agent a = candidateList.get(i);
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
	
	//しんだひとをリストから除く
	protected List<Agent> removeDeadAgent(Collection<Agent> agents){
		List<Agent> ret = new ArrayList<>();
		List<Agent> aliveList = getLatestDayGameInfo().getAliveAgentList();
		for(Agent a: agents){
			for(Agent al: aliveList){
				if(a.getAgentIdx() == al.getAgentIdx()){
					ret.add(a);
					break;
				}
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
