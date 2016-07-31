package net.mchs_u.mc.aiwolf.baikin.role;

import java.util.Map;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;

import net.mchs_u.mc.aiwolf.baikin.Constants;

public class McreMedium extends McreVillager {	
	private boolean co = false;
	private boolean inquestedToday = false;

	public McreMedium() {
		super();
	}
	
	public McreMedium(Map<String,Double> estimateRates) {
		super(estimateRates);
	}
	
	@Override
	public void dayStart() {
		super.dayStart();
		inquestedToday = false;
	}
	
	@Override
	public String talk() {
		switch (Constants.PATTERN_MEDIUM) {
		case 0:
			return talk0();
		case 1:
			return talk1();
		}
		return null;
	}
	
	//0日目CO
	private String talk0(){
		//COしてない場合はCO
		if(!co){
			co = true;
			return TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
		}
		
		//今日霊能結果を言ってなくて霊能結果があれば霊能結果を言う
		Judge j = getLatestDayGameInfo().getMediumResult();
		if(!inquestedToday && j != null){
			inquestedToday = true;
			subjectiveEstimate.updateDefinedSpecies(j.getTarget(), j.getResult());//自分目線に霊能情報を更新
			return TemplateTalkFactory.inquested(j.getTarget(), j.getResult());
		}
		
		//村人と同じtalk
		return super.talk();
	}
	
	//霊能結果があるとき初めてCO
	private String talk1(){
		Judge j = getLatestDayGameInfo().getMediumResult();
		
		//COしてなくて霊能結果があればCO
		if(!co && j != null){
			co = true;
			return TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
		}
		
		//COしてて今日霊能結果を言ってなくて霊能結果があれば霊能結果を言う
		if(co && !inquestedToday && j != null){
			inquestedToday = true;
			subjectiveEstimate.updateDefinedSpecies(j.getTarget(), j.getResult());//自分目線に霊能情報を更新
			return TemplateTalkFactory.inquested(j.getTarget(), j.getResult());
		}
		
		//村人と同じtalk
		return super.talk();
	}
}
