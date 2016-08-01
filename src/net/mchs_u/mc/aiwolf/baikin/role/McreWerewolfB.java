package net.mchs_u.mc.aiwolf.baikin.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

//占い師が人狼じゃない側から1人しか出ない場合は占い師CO、あとは狂人のパターン0と同様
public class McreWerewolfB extends McreWerewolfA {
	private boolean co = false;
	private boolean divinedToday = false;
	private List<Agent> divinedList = null;
	
	public McreWerewolfB() {
		super();
	}
	
	public McreWerewolfB(Map<String,Double> estimateRates) {
		super(estimateRates);
	}
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		co = false;
		divinedList = new ArrayList<>();
	}
	
	@Override
	public void dayStart() {
		super.dayStart();
		divinedToday = false;
	}
	
	@Override
	public String talk() {
		Collection<Agent> coSeerSet = pretendVillagerEstimate.getCoSeerSet();
		for(Agent a: getWolfList())
			coSeerSet.remove(a);
		//1日目以降で、COしてなくて、占い師COが人狼じゃない側で1人以下のとき占い師CO
		if(getDay() > 0 && !co && coSeerSet.size() <= 1){
			co = true;
			return TemplateTalkFactory.comingout(getMe(), Role.SEER);
		}
		
		//coしてて占い結果を言ってなければ占い
		if(co && getDay()>0 && !divinedToday){
			divinedToday = true;
			Agent target = decideDivineTarget();
			if(target != null){
				divinedList.add(target);
				pretendVillagerEstimate.updateDefinedSpecies(target, Species.HUMAN);//村人目線に占い情報を更新
				return TemplateTalkFactory.divined(target, Species.HUMAN);
			}
		}
		
		//投票対象を宣言
		Agent target = decideVoteTarget();
		
		if(target == null)
			return TemplateTalkFactory.skip();
		if(declaredVoteTarget != null && target.equals(declaredVoteTarget))
			return TemplateTalkFactory.skip();
		declaredVoteTarget = target;
		return TemplateTalkFactory.vote(target);
	}
	
	private Agent decideDivineTarget(){
		return decideDivineTargetA();
	}

	//村人目線で最も人狼っぽい人を占って白出し
	private Agent decideDivineTargetA(){
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a:divinedList){
			candidate.remove(a);
		}
		return max(candidate, pretendVillagerEstimate.getWerewolfLikeness(), false);
	}


}
