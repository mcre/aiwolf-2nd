package net.mchs_u.mc.aiwolf.baikin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;

public class Estimate {
	private final static double RATE_VOTE_POSSESSED_TO_WEREWOLF = 0.9d;
	private final static double RATE_VOTE_WEREWOLF_TO_POSSESSED = 0.9d;
	private final static double RATE_VOTE_WEREWOLF_TO_WEREWOLF = 0.9d;
	private final static double RATE_FALSE_INQUESTED_FROM_VILLAGER_TEAM = 0.01d;
	private final static double RATE_FALSE_DIVINED_FROM_VILLAGER_TEAM = 0.01d;
	private final static double RATE_BLACK_DIVINED_POSSESSED_TO_WEREWOLF = 0.9d;
	private final static double RATE_BLACK_DIVINED_WEREWOLF_TO_POSSESSED = 0.5d;
	private final static double RATE_BLACK_DIVINED_WEREWOLF_TO_WEREWOLF = 0.1d;
	private final static double RATE_2_SEER_CO_FROM_VILLGER_TEAM = 0.001d;
	private final static double RATE_2_MEDIUM_CO_FROM_VILLAGER_TEAM = 0.001d;
	private final static double RATE_2_BODYGUARD_CO_FROM_VILLAGER_TEAM = 0.001d;
	private final static double RATE_NEVER_CO_FROM_POSSESSED = 0.1d;
	private final static double RATE_ONLY_SEER_CO_FROM_WEREWOLF_TEAM = 0.01d;
	private final static double RATE_ONLY_MEDIUM_CO_FROM_WEREWOLF_TEAM = 0.01d;
	private final static double RATE_TEAM_MEMBER_WOLF = 0.5d;
	
	private List<Agent> agents = null;
	
	private Map<RoleCombination,Double> probabilities = null;
	private Set<Agent> coSeerSet = null; 
	private Set<Agent> coMediumSet = null;
	private Set<Agent> coBodyguardSet = null;
	private Set<Agent> coSet = null;
	
	private boolean updated = true;
	
	private Map<Agent, Double> werewolfLikeness = null;
	private Map<Agent, Double> villagerTeamLikeness = null;
	
	public Estimate(List<Agent> agents, Agent me) {
		if(agents.size() != 15){
			return;
		}
		
		this.agents = agents;
		
		coSeerSet = new HashSet<>();
		coMediumSet = new HashSet<>();
		coBodyguardSet = new HashSet<>();
		coSet = new HashSet<>();
		
		werewolfLikeness = new HashMap<>();
		villagerTeamLikeness = new HashMap<>();

		probabilities = new HashMap<>();
		for(int i = 0; i < 15; i++){
			for(int j = i + 1; j < 15; j++){
				for(int k = j + 1; k < 15; k++){
					for(int l = 0; l < 15; l++){
						if(l == i || l==j || l==k)
							continue;
						probabilities.put(new RoleCombination(agents.get(i),agents.get(j),agents.get(k),agents.get(l)), 1d); 
					}
				}
			}
		}
		
		// 狂人がCOしていない
		for(RoleCombination rc: probabilities.keySet()){
			update(rc, RATE_NEVER_CO_FROM_POSSESSED);
		}
	}
	
	public Map<Agent, Double> getWerewolfLikeness() {
		if(updated)
			calcLikeness();
		return werewolfLikeness;
	}

	public Map<Agent, Double> getVillagerTeamLikeness() {
		if(updated)
			calcLikeness();
		return villagerTeamLikeness;
	}

	//らしさを再計算
	private void calcLikeness(){
		for(Agent a: agents){
			werewolfLikeness.put(a, 0d);
			villagerTeamLikeness.put(a, 0d);
		}
		
		double sum = 0;
		for(RoleCombination rc: probabilities.keySet()){
			double d = probabilities.get(rc);
			sum += d;
			for(Agent a: agents){
				if(rc.isWolf(a)){
					werewolfLikeness.put(a, werewolfLikeness.get(a) + d);
				}else if(!rc.isPossessed(a)){
					villagerTeamLikeness.put(a, villagerTeamLikeness.get(a) + d);
				}
			}
		}
		
		for(Agent a: agents){
			werewolfLikeness.put(a, werewolfLikeness.get(a) / sum);
			villagerTeamLikeness.put(a, villagerTeamLikeness.get(a) / sum);
		}
		updated = false;
	}
	
	//終了条件を満たしているパターン(狼が全滅してるのにゲームが終わってないなど)を削除
	public void updateAliveAgentList(List<Agent> aliveAgents){		
		Set<RoleCombination> reserveRemove = new HashSet<>();
		for(RoleCombination rc: probabilities.keySet()){
			int countWerewolf = 0;
			for(Agent a: aliveAgents){
				if(rc.isWolf(a))
					countWerewolf++;
			}
			//狼が全滅
			if(countWerewolf == 0)
				reserveRemove.add(rc);
			//狼が人間と同数以上
			else if(countWerewolf >= aliveAgents.size() - countWerewolf)
				reserveRemove.add(rc);
		}
		for(RoleCombination rr:reserveRemove)
			remove(rr);
	}
	
	//確定した役職（自分の役職、仲間の狼など）以外の確率をゼロにする
	public void updateDefinedRole(Agent agent, Role role){
		Set<RoleCombination> reserveRemove = new HashSet<>();
		for(RoleCombination rc: probabilities.keySet()){
			if(role == Role.POSSESSED){
				if(!rc.isPossessed(agent))
					reserveRemove.add(rc);
			} else if(role == Role.WEREWOLF) {
				if(!rc.isWolf(agent))
					reserveRemove.add(rc);
			} else {
				if(!rc.isVillagerTeam(agent))
					reserveRemove.add(rc);
			}
		}
		for(RoleCombination rr:reserveRemove)
			remove(rr);
	}
	
	//確定した人種（自分目線の占い結果など）以外の確率をゼロにする
	public void updateDefinedSpecies(Agent agent, Species species){
		Set<RoleCombination> reserveRemove = new HashSet<>();
		for(RoleCombination rc: probabilities.keySet()){
			if(species == Species.WEREWOLF){
				if(!rc.isWolf(agent))
					reserveRemove.add(rc);
			} else {
				if(rc.isWolf(agent))
					reserveRemove.add(rc);
			}
		}
		for(RoleCombination rr:reserveRemove)
			remove(rr);
	}
	
	//仲間の狼の確率を下げる（身内切りのためゼロにはしない）（村人目線のときにつかう）
	public void updateTeamMemberWolf(List<Agent> agents){
		for(RoleCombination rc: probabilities.keySet()){
			for(Agent a: agents){
				if(rc.isWolf(a)){
					update(rc, RATE_TEAM_MEMBER_WOLF);
					break;
				}
			}
		}
	}
	
	public void updateVoteList(List<Vote> voteList){
		for(Vote v: voteList){
			for(RoleCombination rc: probabilities.keySet()){
				// 狂人から人狼への投票
				if(rc.isPossessed(v.getAgent()) && rc.isWolf(v.getTarget()))
					update(rc, RATE_VOTE_POSSESSED_TO_WEREWOLF);
				// 人狼から狂人への投票
				else if(rc.isWolf(v.getAgent()) && rc.isPossessed(v.getTarget()))
					update(rc, RATE_VOTE_WEREWOLF_TO_POSSESSED);
				// 人狼から人狼への投票
				else if(rc.isWolf(v.getAgent()) && rc.isWolf(v.getTarget()))
					update(rc, RATE_VOTE_WEREWOLF_TO_WEREWOLF);
			}
		}
	}	
	
	public void updateAttackedAgent(Agent agent){
		if(agent == null)
			return;
		
		Set<RoleCombination> reserveRemove = new HashSet<>();
		
		for(RoleCombination rc: probabilities.keySet()){
			//人狼が襲撃される
			if(rc.getWolfs().contains(agent)){
				reserveRemove.add(rc);
			}
		}
		
		for(RoleCombination rr:reserveRemove)
			remove(rr);
	}
	
	public void updateTalk(Talk talk){
		Utterance ut = new Utterance(talk.getContent());
		
		switch (ut.getTopic()) {
		case COMINGOUT:
			if(!talk.getAgent().equals(ut.getTarget()))//自分自身のCOじゃない場合は無視
				break;
			if(coSet.contains(talk.getAgent())) //同じAgentの2回目以降のCOは無視
				break;
			coSet.add(talk.getAgent());
			
			if(ut.getRole() == Role.BODYGUARD){
				coBodyguardSet.add(talk.getAgent());
				for(RoleCombination rc: probabilities.keySet()){
					if(rc.isVillagerTeam(talk.getAgent())){
						// 村人陣営から二人目の狩人CO
						if(countVillagerTeam(rc, coBodyguardSet) == 2)
							update(rc, RATE_2_BODYGUARD_CO_FROM_VILLAGER_TEAM);
					}
				}
			}else if(ut.getRole() == Role.SEER){
				coSeerSet.add(talk.getAgent());
				for(RoleCombination rc: probabilities.keySet()){
					if(rc.isVillagerTeam(talk.getAgent())){
						// 村人陣営から二人目の占いCO
						if(countVillagerTeam(rc, coSeerSet) == 2)
							update(rc, RATE_2_SEER_CO_FROM_VILLGER_TEAM);
						// 既に人狼陣営が占いCOしている状態での初めての村人陣営占いCO(①を解除)
						if(countWereWolfTeam(rc, coSeerSet) > 0 && countVillagerTeam(rc, coSeerSet) == 1)
							restore(rc, RATE_ONLY_SEER_CO_FROM_WEREWOLF_TEAM);
					}else{
						// 村人陣営が占いCOしていない状態で初めての人狼陣営占いCO(①)
						if(countVillagerTeam(rc, coSeerSet) < 1 && countWereWolfTeam(rc, coSeerSet) == 1)
							update(rc, RATE_ONLY_SEER_CO_FROM_WEREWOLF_TEAM);
					}
				}
			}else if(ut.getRole() == Role.MEDIUM){
				coMediumSet.add(talk.getAgent());
				for(RoleCombination rc: probabilities.keySet()){
					if(rc.isVillagerTeam(talk.getAgent())){
						// 村人陣営から二人目の霊能CO
						if(countVillagerTeam(rc, coMediumSet) == 2)
							update(rc, RATE_2_MEDIUM_CO_FROM_VILLAGER_TEAM);
						// 既に人狼陣営が霊能COしている状態での初めての村人陣営霊能CO(②を解除)
						if(countWereWolfTeam(rc, coMediumSet) > 0 && countVillagerTeam(rc, coMediumSet) == 1)
							restore(rc, RATE_ONLY_MEDIUM_CO_FROM_WEREWOLF_TEAM);
					}else{
						// 村人陣営が霊能COしていない状態で初めての人狼陣営霊能CO(②)
						if(countVillagerTeam(rc, coMediumSet) < 1 && countWereWolfTeam(rc, coMediumSet) == 1)
							update(rc, RATE_ONLY_MEDIUM_CO_FROM_WEREWOLF_TEAM);
					}
				}
			}

			//狂人がCO(霊能か占い）
			if(ut.getRole() == Role.MEDIUM || ut.getRole() == Role.SEER){
				for(RoleCombination rc: probabilities.keySet()){
					if(rc.isPossessed(talk.getAgent())){
						restore(rc, RATE_NEVER_CO_FROM_POSSESSED);
					}
				}
			}
			
			break;
		case DIVINED:
			for(RoleCombination rc: probabilities.keySet()){
				//狂人が人狼に黒出し
				if(rc.isPossessed(talk.getAgent()) && rc.isWolf(ut.getTarget()))
					update(rc, RATE_BLACK_DIVINED_POSSESSED_TO_WEREWOLF);
				//人狼が狂人に黒出し
				else if(rc.isWolf(talk.getAgent()) && rc.isPossessed(ut.getTarget()))
					update(rc, RATE_BLACK_DIVINED_WEREWOLF_TO_POSSESSED);
				//人狼が人狼に黒出し
				else if(rc.isWolf(talk.getAgent()) && rc.isWolf(ut.getTarget()))
					update(rc, RATE_BLACK_DIVINED_WEREWOLF_TO_WEREWOLF);	
				//村人陣営が嘘の占い
				else if(rc.isVillagerTeam(talk.getAgent())){
					if(rc.isWolf(ut.getTarget()) && ut.getResult() == Species.HUMAN){
						update(rc, RATE_FALSE_DIVINED_FROM_VILLAGER_TEAM);
					}else if(!rc.isWolf(ut.getTarget()) && ut.getResult() == Species.WEREWOLF){
						update(rc, RATE_FALSE_DIVINED_FROM_VILLAGER_TEAM);
					}
				}
			}			
			break;
		case INQUESTED:
			for(RoleCombination rc: probabilities.keySet()){
				//村人陣営が嘘の霊能
				if(rc.isVillagerTeam(talk.getAgent())){
					if(rc.isWolf(ut.getTarget()) && ut.getResult() == Species.HUMAN){
						update(rc, RATE_FALSE_INQUESTED_FROM_VILLAGER_TEAM);
					}else if(!rc.isWolf(ut.getTarget()) && ut.getResult() == Species.WEREWOLF){
						update(rc, RATE_FALSE_INQUESTED_FROM_VILLAGER_TEAM);
					}
				}
			}
			break;
		default:
			break;
		}
			
	}
	
	private void update(RoleCombination rc, double rate){
		probabilities.put(rc, probabilities.get(rc) * rate);
		updated = true;
	}
	
	private void restore(RoleCombination rc, double rate){
		probabilities.put(rc, probabilities.get(rc) / rate);
		updated = true;
	}
	
	private void remove(RoleCombination rc){
		probabilities.remove(rc);
		updated = true;
	}
	
	private static int countVillagerTeam(RoleCombination rc, Collection<Agent> collection){
		int count = 0;
		for(Agent a: collection){
			if(rc.isVillagerTeam(a))
				count++;
		}
		return count;
	}
	
	private static int countWereWolfTeam(RoleCombination rc, Collection<Agent> collection){
		int count = 0;
		for(Agent a: collection){
			if(!rc.isVillagerTeam(a))
				count++;
		}
		return count;
	}
	
	public void print(){
		Map<Agent, Double> w = getWerewolfLikeness();
		Map<Agent, Double> v = getVillagerTeamLikeness();
		for(Agent a: agents){
			System.out.print("[" + a.getAgentIdx() + "]\t");
			System.out.printf("%.4f\t",w.get(a));
			System.out.printf("%.4f\t",v.get(a));
			System.out.printf("%.4f\n",1d - v.get(a) - w.get(a));
		}
	}

	public Set<Agent> getCoSeerSet() {
		return coSeerSet;
	}

	public void setCoSeerSet(Set<Agent> coSeerSet) {
		this.coSeerSet = coSeerSet;
	}

	public Set<Agent> getCoMediumSet() {
		return coMediumSet;
	}

	public void setCoMediumSet(Set<Agent> coMediumSet) {
		this.coMediumSet = coMediumSet;
	}

	public Set<Agent> getCoBodyguardSet() {
		return coBodyguardSet;
	}

	public void setCoBodyguardSet(Set<Agent> coBodyguardSet) {
		this.coBodyguardSet = coBodyguardSet;
	}
	
}
