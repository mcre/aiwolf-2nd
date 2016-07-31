package net.mchs_u.mc.aiwolf.baikin.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import net.mchs_u.mc.aiwolf.baikin.Constants;

public class McreSeer extends McreVillager {
	private boolean co = false;
	private boolean divinedToday = false;
	private List<Agent> divinedList = null;

	public McreSeer() {
		super();
	}
	
	public McreSeer(Map<String,Double> estimateRates) {
		super(estimateRates);
	}
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		divinedList = new ArrayList<>();
	}	
	
	@Override
	public void dayStart() {
		super.dayStart();
		divinedToday = false;
	}

	@Override
	public String talk() {
		//COしてない場合はCO
		if(!co){
			co = true;
			return TemplateTalkFactory.comingout(getMe(), Role.SEER);
		}
		
		//占い結果を言ってなければ占い
		Judge j = getLatestDayGameInfo().getDivineResult();
		if(!divinedToday && j != null){
			divinedToday = true;
			divinedList.add(j.getTarget());
			subjectiveEstimate.updateDefinedSpecies(j.getTarget(), j.getResult());//自分目線に占い情報を更新
			return TemplateTalkFactory.divined(j.getTarget(), j.getResult());
		}
		
		//村人と同じtalk
		return super.talk();
	}
	
	@Override
	public Agent divine() {
		switch (Constants.PATTERN_SEER) {
		case 0:
			return decideDivineTarget0();
		case 1:
			return decideDivineTarget1();
		case 2:
			return decideDivineTarget2();
		case 3:
			return decideDivineTarget3();
		}
		return null;
	}
	
	//占っていない生存者のうち自分目線で最も人狼っぽい人を占う。占い師COした人はあとまわし。
	private Agent decideDivineTarget0(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a:divinedList){
			candidate.remove(a);
		}
		
		List<Agent> tmp = candidate;

		//占い師COした人は除く
		for(Agent a:subjectiveEstimate.getCoSeerSet()){
			candidate.remove(a);
		}
		if(candidate.size() == 0)
			//誰も占う人が居ない場合のみ占い師COの人を占う
			return max(tmp, subjectiveEstimate.getWerewolfLikeness(), false);
		else
			return max(candidate, subjectiveEstimate.getWerewolfLikeness(), false);
	}
	
	//占っていない生存者のうち客観目線で最も人狼っぽい人を占う。占い師COした人はあとまわし。
	private Agent decideDivineTarget1(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a:divinedList){
			candidate.remove(a);
		}
		
		List<Agent> tmp = candidate;

		//占い師COした人は除く
		for(Agent a:objectiveEstimate.getCoSeerSet()){
			candidate.remove(a);
		}
		if(candidate.size() == 0)
			//誰も占う人が居ない場合のみ占い師COの人を占う
			return max(tmp, objectiveEstimate.getWerewolfLikeness(), false);
		else
			return max(candidate, objectiveEstimate.getWerewolfLikeness(), false);
	}
	
	//占っていない生存者のうち自分目線で最も人狼っぽい人を占う。占いCOした人も含む
	private Agent decideDivineTarget2(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a:divinedList){
			candidate.remove(a);
		}
		return max(candidate, subjectiveEstimate.getWerewolfLikeness(), false);
	}
	
	//占っていない生存者のうち客観目線で最も人狼っぽい人を占う。占いCOした人も含む
	private Agent decideDivineTarget3(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a:divinedList){
			candidate.remove(a);
		}
		return max(candidate, objectiveEstimate.getWerewolfLikeness(), false);
	}

}
