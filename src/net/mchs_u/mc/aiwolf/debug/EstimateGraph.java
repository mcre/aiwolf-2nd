package net.mchs_u.mc.aiwolf.debug;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.aiwolf.common.data.Agent;

public class EstimateGraph extends JPanel{
	private static final long serialVersionUID = 1L;
	List<AgentGraph> agentGraphs = null;

	public EstimateGraph() {
		GridLayout l = new GridLayout(3,11);
		l.setHgap(10);
		l.setVgap(5);
		this.setLayout(l);
		
		agentGraphs = new ArrayList<>();
		for(int i = 0; i < 15; i++)
			agentGraphs.add(new AgentGraph());
		
		this.add(new JPanel());
		this.add(agentGraphs.get(0));
		this.add(agentGraphs.get(1));
		this.add(agentGraphs.get(2));
		this.add(agentGraphs.get(3));
		this.add(agentGraphs.get(4));
		this.add(agentGraphs.get(5));
		this.add(new JPanel());
		this.add(new JPanel());
		this.add(agentGraphs.get(14));
		this.add(new JPanel());
		this.add(new JPanel());
		this.add(new JPanel());
		this.add(new JPanel());
		this.add(new JPanel());
		this.add(new JPanel());
		this.add(new JPanel());
		this.add(agentGraphs.get(6));
		this.add(new JPanel());
		this.add(agentGraphs.get(13));
		this.add(agentGraphs.get(12));
		this.add(agentGraphs.get(11));
		this.add(agentGraphs.get(10));
		this.add(agentGraphs.get(9));
		this.add(agentGraphs.get(8));
		this.add(agentGraphs.get(7));
		this.add(new JPanel());
	}
	
	public void refreshWerewolfLikeness(List<Agent> agents, Map<Agent, Double> likeness){
		for(int i=0; i < 15; i++){
			agentGraphs.get(i).setRateWerewolf(likeness.get(agents.get(i)));
		}
	}

	public void refreshVillagerTeamLikeness(List<Agent> agents, Map<Agent, Double> likeness){
		for(int i=0; i < 15; i++){
			agentGraphs.get(i).setRateVillagerTeam(likeness.get(agents.get(i)));
		}		
	}
	
	//test
	public static void main(String[] args) {
		JFrame f = new JFrame("debug");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(400, 250);
		f.setLocation(1010, 0);
		f.add(new EstimateGraph());
		
		f.setVisible(true);
	}

}
