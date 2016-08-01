package net.mchs_u.mc.aiwolf.baikin;

import java.util.Map;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

import net.mchs_u.mc.aiwolf.baikin.role.McreBodyguard;
import net.mchs_u.mc.aiwolf.baikin.role.McreMedium;
import net.mchs_u.mc.aiwolf.baikin.role.McrePossessed;
import net.mchs_u.mc.aiwolf.baikin.role.McreSeer;
import net.mchs_u.mc.aiwolf.baikin.role.McreVillager;
import net.mchs_u.mc.aiwolf.baikin.role.McreWerewolfA;
import net.mchs_u.mc.aiwolf.baikin.role.McreWerewolfB;
import net.mchs_u.mc.aiwolf.baikin.role.McreWerewolfC;

public class McreRoleAssignPlayer extends AbstractRoleAssignPlayer {
	protected Map<String,Double> estimateRates = null;

	public McreRoleAssignPlayer() {
		setPossessedPlayer(new McrePossessed());
		setVillagerPlayer(new McreVillager());
		setSeerPlayer(new McreSeer());
		setBodyguardPlayer(new McreBodyguard());
		setMediumPlayer(new McreMedium());
		
		switch (Constants.PATTERN_WEREWOLF) {
		case 0:
		case 1:
		case 2:
		case 3:	
			setWerewolfPlayer(new McreWerewolfA());
			break;
		case 4:
			setWerewolfPlayer(new McreWerewolfB());
			break;
		case 5:
			setWerewolfPlayer(new McreWerewolfC());
			break;
		default:
			setWerewolfPlayer(new McreWerewolfA());
		}
	}

	public McreRoleAssignPlayer(Map<String,Double> estimateRates) {
		setWerewolfPlayer(new McreWerewolfA(estimateRates));
		setPossessedPlayer(new McrePossessed(estimateRates));
		setVillagerPlayer(new McreVillager(estimateRates));
		setSeerPlayer(new McreSeer(estimateRates));
		setBodyguardPlayer(new McreBodyguard(estimateRates));
		
		switch (Constants.PATTERN_WEREWOLF) {
		case 0:
		case 1:
		case 2:
			setWerewolfPlayer(new McreWerewolfA(estimateRates));
			break;
		case 3:
			setWerewolfPlayer(new McreWerewolfB(estimateRates));
			break;
		case 4:
			setWerewolfPlayer(new McreWerewolfC(estimateRates));
			break;
		default:
			setWerewolfPlayer(new McreWerewolfA(estimateRates));
		}
	}

	public void setEstimateRates(Map<String, Double> estimateRates) {
		this.estimateRates = estimateRates;
	}

	@Override
	public String getName() {
		return "m_creB";
	}

}
