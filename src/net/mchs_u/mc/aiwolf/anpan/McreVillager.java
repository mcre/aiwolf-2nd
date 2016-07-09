package net.mchs_u.mc.aiwolf.anpan;

import java.util.List;

import org.aiwolf.client.base.player.AbstractVillager;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class McreVillager extends AbstractVillager {
	private int readTalkNum = 0;
	private Estimate subjectiveEstimate = null;

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		
		subjectiveEstimate = new Estimate(gameInfo.getAgentList(), getMe());
		subjectiveEstimate.updateDefinedRole(getMe(), getMyRole());
	}
	
	@Override
	public void dayStart() {
		readTalkNum = 0;
		
		GameInfo info = getLatestDayGameInfo();
		subjectiveEstimate.updateAliveAgentList(info.getAliveAgentList());
		subjectiveEstimate.updateAttackedAgent(info.getAttackedAgent());
		subjectiveEstimate.updateVoteList(info.getVoteList());
	}
	
	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		
		List<Talk> talkList = gameInfo.getTalkList();
		for(int i = readTalkNum; i < talkList.size(); i++){
			subjectiveEstimate.updateTalk(talkList.get(i));
			readTalkNum++;
		}
	}

	@Override
	public void finish() {
	}

	@Override
	public String talk() {
		return null;
	}

	@Override
	public Agent vote() {
		return null;
	}

}
