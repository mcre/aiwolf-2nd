package net.mchs_u.mc.aiwolf.anpan.role;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class McrePossessed extends AbstractMcreRole {
	private Agent declaredVoteTarget = null; //今日最後に投票宣言をした対象
	
	private boolean co = false;
	private boolean divinedToday = false;
	private List<Agent> divinedList = null;
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		divinedList = new ArrayList<>();
	}	
	
	@Override
	public void dayStart() {
		super.dayStart();
		divinedToday = false;
		declaredVoteTarget = null;
	}
	
	@Override
	public String talk() {
		//COしてない場合はCO
		if(!co){
			co = true;
			return TemplateTalkFactory.comingout(getMe(), Role.SEER);
		}
		
		//占い結果を言ってなければ占い
		if(getDay()>0 && !divinedToday){
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
	
	@Override
	public Agent vote() {
		return decideVoteTarget();
	}
	
	//占い対象を決める
	private Agent decideDivineTarget(){
		//村人目線で、占っていない生存者のうち最も人狼っぽいひと
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		for(Agent a:divinedList){
			candidate.remove(a);
		}
		return max(candidate, pretendVillagerEstimate.getWerewolfLikeness(), false);
	}
	
	//投票対象を決める
	private Agent decideVoteTarget(){
		//村人目線で、生存者のうち最も人狼っぽいひと
		List<Agent> candidate = new ArrayList<>(getLatestDayGameInfo().getAliveAgentList());
		candidate.remove(getMe());
		
		return max(candidate, pretendVillagerEstimate.getWerewolfLikeness(), false);
	}

}
