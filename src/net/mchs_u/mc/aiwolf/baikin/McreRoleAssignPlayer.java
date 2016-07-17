package net.mchs_u.mc.aiwolf.baikin;

import java.util.Map;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

import net.mchs_u.mc.aiwolf.baikin.role.McreBodyguard;
import net.mchs_u.mc.aiwolf.baikin.role.McreMedium;
import net.mchs_u.mc.aiwolf.baikin.role.McrePossessed;
import net.mchs_u.mc.aiwolf.baikin.role.McreSeer;
import net.mchs_u.mc.aiwolf.baikin.role.McreVillager;
import net.mchs_u.mc.aiwolf.baikin.role.McreWerewolf;

public class McreRoleAssignPlayer extends AbstractRoleAssignPlayer {
	protected Map<String,Double> estimateRates = null;

	public McreRoleAssignPlayer() {
		setWerewolfPlayer(new McreWerewolf());
		setPossessedPlayer(new McrePossessed());
		setVillagerPlayer(new McreVillager());
		setSeerPlayer(new McreSeer());
		setBodyguardPlayer(new McreBodyguard());
		setMediumPlayer(new McreMedium());
	}

	public McreRoleAssignPlayer(Map<String,Double> estimateRates) {
		setWerewolfPlayer(new McreWerewolf(estimateRates));
		setPossessedPlayer(new McrePossessed(estimateRates));
		setVillagerPlayer(new McreVillager(estimateRates));
		setSeerPlayer(new McreSeer(estimateRates));
		setBodyguardPlayer(new McreBodyguard(estimateRates));
		setMediumPlayer(new McreMedium(estimateRates));
	}

	public void setEstimateRates(Map<String, Double> estimateRates) {
		this.estimateRates = estimateRates;
	}

	@Override
	public String getName() {
		return "m_creB";
	}

}
