package net.mchs_u.mc.aiwolf.baikin;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

import net.mchs_u.mc.aiwolf.baikin.role.McreBodyguard;
import net.mchs_u.mc.aiwolf.baikin.role.McreMedium;
import net.mchs_u.mc.aiwolf.baikin.role.McrePossessed;
import net.mchs_u.mc.aiwolf.baikin.role.McreSeer;
import net.mchs_u.mc.aiwolf.baikin.role.McreVillager;
import net.mchs_u.mc.aiwolf.baikin.role.McreWerewolf;

public class McreRoleAssignPlayer extends AbstractRoleAssignPlayer {

	public McreRoleAssignPlayer() {
		setWerewolfPlayer(new McreWerewolf());
		setPossessedPlayer(new McrePossessed());

		setVillagerPlayer(new McreVillager());
		setSeerPlayer(new McreSeer());
		setBodyguardPlayer(new McreBodyguard());
		setMediumPlayer(new McreMedium());
	}

	@Override
	public String getName() {
		return "m_creB";
	}

}
