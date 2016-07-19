package net.mchs_u.mc.aiwolf.starter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.common.util.Counter;
import org.aiwolf.common.util.Pair;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.GameData;
import org.aiwolf.server.net.DirectConnectServer;
import org.aiwolf.server.util.FileGameLogger;
import org.aiwolf.server.util.GameLogger;
import org.aiwolf.server.util.MultiGameLogger;
import org.aiwolf.ui.GameViewer;
import org.aiwolf.ui.log.ContestResource;
import org.aiwolf.ui.util.AgentLibraryReader;

/**
 * @author m_cre
 * スターター。
 */
@SuppressWarnings("deprecation")
public class DirectStarter {
	public static final boolean IS_VISUALIZE  = false; // TODO 大会時はFALSE
	public static final boolean IS_MONTECARLO = false; // TODO 大会時はFALSE
	
	private static final String LOG_DIR = "./log/";
	private static final String RESULT_DIR = "./result/";
	
	private int gameNum, setNum;
	private List<Pair<String, Role>> players;
	@SuppressWarnings("rawtypes")
	private Map<String, Class> playerClassMap;
	private boolean isVisualize, isLog, isSaveResult;
	
	private AIWolfGame game;

	private Map<String, Counter<Role>> winCounterMap;
	private Map<String, Counter<Role>> roleCounterMap;
	
	private Map<String,Double> myAgentEstimateRates = null;
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		int set = 1;
		int times = 100;
		boolean isVisualize = IS_VISUALIZE;
		boolean isLog = false;
		boolean isSaveResult = true;
		boolean isMontecarlo = IS_MONTECARLO;
		
		List<Pair<String, Role>> players = new ArrayList<>();
		players.add(new Pair<String, Role>("net.mchs_u.mc.aiwolf.anpan.McreRoleAssignPlayer", null));
		//players.add(new Pair<String, Role>("net.mchs_u.mc.aiwolf.baikin.McreRoleAssignPlayer", Role.SEER));
		players.add(new Pair<String, Role>("net.mchs_u.mc.aiwolf.baikin.McreRoleAssignPlayer", null));
		players.add(new Pair<String, Role>("org.aiwolf.kajiClient.player.KajiRoleAssignPlayer", null));
		players.add(new Pair<String, Role>("jp.ac.tohoku.ecei.shino.takaaki_okawa.agent.KatakanaRoleAssignPlayer", null));
		players.add(new Pair<String, Role>("com.si.maekawa.MaekawaRoleAssignPlayer", null));
		players.add(new Pair<String, Role>("jp.ac.kyoto_u.sugaryAgent.SugaryRoleAssignPlayer", null));
		players.add(new Pair<String, Role>("mskdel.myAgent.mskdelRoleAssignPlayer", null));
		players.add(new Pair<String, Role>("kainoueAgent.MyRoleAssignPlayer", null));
		players.add(new Pair<String, Role>("com.github.haretaro.pingwo.role.PingwoRoleAssignPlayer", null));
		players.add(new Pair<String, Role>("jp.halfmoon.inaba.aiwolf.strategyplayer.StrategyPlayer", null));
		players.add(new Pair<String, Role>("jp.ac.shibaura_it.ma15082.WasabiPlayer", null));
		players.add(new Pair<String, Role>("com.carlo.aiwolf.bayes.player.BayesPlayer", null));
		players.add(new Pair<String, Role>("aaaaa.aaaaaaa.aaaaa.Router", null));
		players.add(new Pair<String, Role>("jp.ac.aitech.k13009kk.aiwolf.client.player.AndoRoleAssignPlayer", null));
		//players.add(new Pair<String, Role>("com.gmail.the.seventh.layers.RoleAssignPlayer", null));
		players.add(new Pair<String, Role>("tkAI.tkAIPlayer", null));
		
		Map<String,Double> rates = null;
		while(true){
			if(isMontecarlo){
				rates = new HashMap<>();
				rates.put("VOTE_POSSESSED_TO_WEREWOLF"         , Math.random());
				rates.put("VOTE_WEREWOLF_TO_POSSESSED"         , Math.random());
				rates.put("VOTE_WEREWOLF_TO_WEREWOLF"          , Math.random());
				rates.put("FALSE_INQUESTED_FROM_VILLAGER_TEAM" , Math.random());
				rates.put("FALSE_DIVINED_FROM_VILLAGER_TEAM"   , Math.random());
				rates.put("BLACK_DIVINED_POSSESSED_TO_WEREWOLF", Math.random());
				rates.put("BLACK_DIVINED_WEREWOLF_TO_POSSESSED", Math.random());
				rates.put("BLACK_DIVINED_WEREWOLF_TO_WEREWOLF" , Math.random());
				rates.put("2_SEER_CO_FROM_VILLGER_TEAM"        , Math.random());
				rates.put("2_MEDIUM_CO_FROM_VILLAGER_TEAM"     , Math.random());
				rates.put("2_BODYGUARD_CO_FROM_VILLAGER_TEAM"  , Math.random());
				rates.put("NEVER_CO_FROM_POSSESSED"            , Math.random());
				rates.put("ONLY_SEER_CO_FROM_WEREWOLF_TEAM"    , Math.random());
				rates.put("ONLY_MEDIUM_CO_FROM_WEREWOLF_TEAM"  , Math.random());
				rates.put("TEAM_MEMBER_WOLF"                   , Math.random());
			}
			DirectStarter ds = new DirectStarter(players, times, set, isVisualize, isLog, isSaveResult, rates);
			ds.start();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public DirectStarter(List<Pair<String, Role>> players, int gameNum, int setNum, boolean isVisualize, boolean isLog, boolean isSaveResult, Map<String, Double> rates) throws IOException {
		this.gameNum = gameNum;
		this.setNum = setNum;
		this.isVisualize = isVisualize;
		this.players = players;
		this.isLog = isLog;
		this.isSaveResult = isSaveResult;
		this.myAgentEstimateRates = rates;

		playerClassMap = new HashMap<String, Class>();
		for(File file:AgentLibraryReader.getJarFileList(new File("./players"))){
			for(Class c:AgentLibraryReader.getPlayerClassList(file)){
				playerClassMap.put(c.getName(), c);
			}
		}
		
		winCounterMap = new HashMap<>();
		roleCounterMap = new HashMap<>();
	}
	
	private Map<Player,Role> getNewPlayersInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		Map<Player,Role> map = new HashMap<>();
		
		for(Pair<String, Role> pair:players){
			String clsName = pair.getKey();
			Role role = pair.getValue();
			
			Player player = null;
			if(playerClassMap.containsKey(clsName)){
				player = (Player)playerClassMap.get(clsName).newInstance();
			}
			else{
				if(clsName.equals("net.mchs_u.mc.aiwolf.baikin.McreRoleAssignPlayer") && myAgentEstimateRates != null){
					player = new net.mchs_u.mc.aiwolf.baikin.McreRoleAssignPlayer(myAgentEstimateRates);
				}else{
					player = (Player)Class.forName(clsName).newInstance();
				}
			}
			map.put(player,role);
		}
		return map;
	}
	
	public void start() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		for(int i = 0; i < setNum; i++){
			game = null;
			
			Map<Player,Role> map = getNewPlayersInstance();
			game = new AIWolfGame(GameSetting.getDefaultGame(map.size()), new DirectConnectServer(map));
			for(int j = 0; j < gameNum; j++){
				File logFile = new File(LOG_DIR + (new Date()).getTime() + ".txt"); 
				GameLogger logger = null;
				if(isLog)
					logger = new FileGameLogger(logFile);
				if(isVisualize){
					ContestResource resource = new ContestResource(game);
					GameViewer gameViewer = new GameViewer(resource, game);
					gameViewer.setAutoClose(true);
					if(logger == null)
						logger = gameViewer;
					else
						logger = new MultiGameLogger(logger, gameViewer);
				}
				game.setGameLogger(logger);
				game.start();
				Team winner = game.getWinner();
				GameData gameData = game.getGameData();
				for(Agent agent:gameData.getAgentList()){
					String n = game.getAgentName(agent);
					String agentName = n.substring(0, Math.min(6,n.length()));
					if(!winCounterMap.containsKey(agentName)){
						winCounterMap.put(agentName, new Counter<Role>());
					}
					if(!roleCounterMap.containsKey(agentName)){
						roleCounterMap.put(agentName, new Counter<Role>());
					}

					if(winner == gameData.getRole(agent).getTeam()){
						winCounterMap.get(agentName).add(gameData.getRole(agent));
					}
					roleCounterMap.get(agentName).add(gameData.getRole(agent));
				}
			}
			result(i, isSaveResult);
		}
	}

	public void result(int set, boolean isSave) throws IOException{
		StringBuffer s = new StringBuffer();
		
		s.append("set : " + set + "\n");
		/*
		if(myAgentEstimateRates != null){
			for(String key: myAgentEstimateRates.keySet()){
				s.append("*\t" + key + "\t" + myAgentEstimateRates.get(key) + "\n");
			}
			s.append("\n");
		}
		*/
		s.append("       ");
		
		for(Role role:Role.values()){
			if(role == Role.FREEMASON) continue;
			String r = role.toString();
			s.append(String.format("\t%4s",r.substring(0, Math.min(4,r.length()))));
		}
		s.append("\tTotal\n");
		
		for(String name:new TreeSet<>(roleCounterMap.keySet())){
			s.append(String.format("%7s\t",name));
			double win = 0;
			double cnt = 0;
			for(Role role:Role.values()){
				if(role == Role.FREEMASON) continue;
				double w = winCounterMap.get(name).get(role);
				double c = roleCounterMap.get(name).get(role);
				s.append(String.format("%.4f\t", w / c ));
				win += w;
				cnt += c;
			}
			s.append(String.format("%.4f\n", win/cnt));
		}
		s.append("\n");
		
		for(String name:new TreeSet<>(roleCounterMap.keySet())){
			s.append(String.format("%7s\t",name));
			int cnt = 0;
			for(Role role:Role.values()){
				if(role == Role.FREEMASON) continue;
				int c = roleCounterMap.get(name).get(role);
				s.append(String.format("%6d\t", c ));
				cnt += c;
			}
			s.append(String.format("%6d\n", cnt));
		}
		
		System.out.println(s.toString());
		
		if(isSave){
			try(FileWriter fw = new FileWriter(new File(RESULT_DIR + (new Date()).getTime() + ".txt"))){
				fw.write(s.toString());
			}
		}
	}
}
