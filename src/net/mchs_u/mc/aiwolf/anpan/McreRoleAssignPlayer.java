package net.mchs_u.mc.aiwolf.anpan;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class McreRoleAssignPlayer extends AbstractRoleAssignPlayer {

	public McreRoleAssignPlayer() {
		setWerewolfPlayer(new McreWerewolf());
	}

	@Override
	public String getName() {
		return "m_cre";
	}

}
