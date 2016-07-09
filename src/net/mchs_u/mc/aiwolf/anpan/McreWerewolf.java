package net.mchs_u.mc.aiwolf.anpan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class McreWerewolf extends AbstractWerewolf {
	private Set<Agent> coSeerList = null;
	private List<Agent> divinedList = null;
	
	private boolean seerCo = false;
	private boolean divinedToday = false;
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		
		coSeerList = new HashSet<Agent>();
		divinedList = new ArrayList<Agent>();
	}
	
	@Override
	public Agent attack() {
		List<Agent> attackList = getLatestDayGameInfo().getAliveAgentList();
		
		//狼をリストから消す
		for(Agent a: getWolfList()){
			attackList.remove(a);
		}
		
		//占い師COが一人で、アタックリストにいる(生きてて狼じゃない)なら咬む
		if(coSeerList.size() == 1){
			if(attackList.contains(coSeerList.toArray()[0])){
				return (Agent)coSeerList.toArray()[0];
			}
		}
		
		//占い師COが二人以上のときはリストから消す
		if(coSeerList.size() >= 2){
			for(Agent a: coSeerList){
				attackList.remove(a);
			}
		}
		
		if(attackList.size() > 0){
			return randomSelect(attackList);
		}else{
			//対象が居ない場合は自分も含め生きてる中からランダム（ありえないはずだけど）
			return randomSelect(getLatestDayGameInfo().getAliveAgentList());
		}
		
	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		List<Talk> talkList = gameInfo.getTalkList();
		for(int i = 0; i< talkList.size(); i++){
			Talk talk = talkList.get(i);
			Utterance u = new Utterance(talk.getContent());
			switch(u.getTopic()){
			case COMINGOUT:
				if(u.getRole() == Role.SEER && u.getTarget().equals(talk.getAgent())){
					coSeerList.add(talk.getAgent());
				}
				break;
			case AGREE:
				break;
			case ATTACK:
				break;
			case DISAGREE:
				break;
			case DIVINED:
				break;
			case ESTIMATE:
				break;
			case GUARDED:
				break;
			case INQUESTED:
				break;
			case OVER:
				break;
			case SKIP:
				break;
			case VOTE:
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void dayStart() {
		divinedToday = false;
	}

	@Override
	public void finish() {
		coSeerList = new HashSet<Agent>();
		divinedList = new ArrayList<Agent>();
		seerCo = false;
		divinedToday = false;
	}

	@Override
	public String talk() {
		//よくわからないので初日は何もしない
		if(getDay()==0){
			return null;
		}
		
		//占い師COがいなければ占い師CO
		if(coSeerList.size() == 0){
			seerCo = true;
			return TemplateTalkFactory.comingout(getMe(), Role.SEER);
		}
		
		//占い師COしてれば、1発言目で人狼以外で生きててまだ占ってない人をランダムに黒指定
		if(seerCo && !divinedToday){
			List<Agent> divineList = getLatestDayGameInfo().getAliveAgentList();
			//狼をリストから消す
			for(Agent a: getWolfList()){
				divineList.remove(a);
			}
			
			//占った人をリストから消す
			for(Agent a: divinedList){
				divineList.remove(a);
			}
			
			//残った中からランダム
			if(divineList.size() > 0){
				Agent target = randomSelect(divineList);
				divinedToday = true;
				divinedList.add(target);
				return TemplateTalkFactory.divined(target, Species.WEREWOLF);
			}
		}
		
		//それ以外は何もしない
		return null;
	}

	@Override
	public Agent vote() {
		//とりあえず生きてる中から狼以外でランダム
		List<Agent> voteList = getLatestDayGameInfo().getAliveAgentList();
		for(Agent a: getLatestDayGameInfo().getRoleMap().keySet()){
			voteList.remove(a);
		}
		return randomSelect(voteList);
	}

	@Override
	public String whisper() {
		return null;
	}

	private Agent randomSelect(List<Agent> agentList){
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}

}
