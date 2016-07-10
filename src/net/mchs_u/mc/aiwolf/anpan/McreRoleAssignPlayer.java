package net.mchs_u.mc.aiwolf.anpan;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

import net.mchs_u.mc.aiwolf.anpan.role.McreBodyguard;
import net.mchs_u.mc.aiwolf.anpan.role.McreMedium;
import net.mchs_u.mc.aiwolf.anpan.role.McrePossessed;
import net.mchs_u.mc.aiwolf.anpan.role.McreSeer;
import net.mchs_u.mc.aiwolf.anpan.role.McreVillager;
import net.mchs_u.mc.aiwolf.anpan.role.McreWerewolf;

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
		return "m_creA";
	}

}
