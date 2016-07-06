package net.mchs_u.mc.aiwolf.starter;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.common.net.TcpipClient;
import org.aiwolf.common.util.Counter;
import org.aiwolf.common.util.Pair;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.GameData;
import org.aiwolf.server.LostClientException;
import org.aiwolf.server.net.ServerListener;
import org.aiwolf.server.net.TcpipServer;
import org.aiwolf.server.util.FileGameLogger;
import org.aiwolf.server.util.GameLogger;
import org.aiwolf.server.util.MultiGameLogger;
import org.aiwolf.ui.GameViewer;
import org.aiwolf.ui.log.ContestResource;
import org.aiwolf.ui.util.AgentLibraryReader;

/**
 * @author m_cre
 * 使ってない
 */
public class AutoStarter {
	private static final int PORT = 10000;
	private static final String LOG_DIR = "./log/";
	
	private Map<String, Pair<String, Role>> roleAgentMap;
	
	private int agentNum,gameNum;
	private TcpipServer gameServer;
	private GameSetting gameSetting;
	private boolean isRunning,isVisualize,initServer=false;
	private Thread serverThread;
	private Map<String, Counter<Role>> winCounterMap;
	private Map<String, Counter<Role>> roleCounterMap;
	@SuppressWarnings("rawtypes")
	private Map<String, Class> playerClassMap;
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {		
		int set = 30;
		int times = 100;
		
		Map<String, Pair<String, Role>> players = new HashMap<>();
		players.put("m_cre______", new Pair<String, Role>("net.mchs_u.mc.aiwolf.anpan.McreRoleAssignPlayer", null));
		players.put("kaji_______", new Pair<String, Role>("org.aiwolf.kajiClient.player.KajiRoleAssignPlayer", null));
		players.put("katakana___", new Pair<String, Role>("jp.ac.tohoku.ecei.shino.takaaki_okawa.agent.KatakanaRoleAssignPlayer", null));
		players.put("maekawa____", new Pair<String, Role>("com.si.maekawa.MaekawaRoleAssignPlayer", null));
		players.put("marshmallow", new Pair<String, Role>("jp.ac.kyoto_u.sugaryAgent.SugaryRoleAssignPlayer", null));
		players.put("mskdel_____", new Pair<String, Role>("mskdel.myAgent.mskdelRoleAssignPlayer", null));
		players.put("kainoue____", new Pair<String, Role>("kainoueAgent.MyRoleAssignPlayer", null));
		players.put("pingwo_____", new Pair<String, Role>("com.github.haretaro.pingwo.role.PingwoRoleAssignPlayer", null));
		players.put("udonmini___", new Pair<String, Role>("jp.halfmoon.inaba.aiwolf.strategyplayer.StrategyPlayer", null));
		players.put("wasabi_____", new Pair<String, Role>("jp.ac.shibaura_it.ma15082.WasabiPlayer", null));
		players.put("carlo______", new Pair<String, Role>("com.carlo.aiwolf.bayes.player.BayesPlayer", null));
		players.put("aaa________", new Pair<String, Role>("aaaaa.aaaaaaa.aaaaa.Router", null));
		players.put("ando_______", new Pair<String, Role>("jp.ac.aitech.k13009kk.aiwolf.client.player.AndoRoleAssignPlayer", null));
		players.put("gaugau_____", new Pair<String, Role>("com.gmail.the.seventh.layers.RoleAssignPlayer", null));
		players.put("tkai_______", new Pair<String, Role>("tkAI.tkAIPlayer", null));	
		
		AutoStarter as = new AutoStarter(times,false,players);
		for(int i=0; i<set; i++){
			as.start();
		}
		as.result();
	}

	@SuppressWarnings("rawtypes")
	public AutoStarter(int gameNum,boolean isVisualize, Map<String, Pair<String, Role>> roleAgentMap) throws IOException{
		this.gameNum = gameNum;
		this.isVisualize = isVisualize;
		
		this.roleAgentMap = roleAgentMap;
		agentNum = roleAgentMap.size();

		playerClassMap = new HashMap<String, Class>();
		for(File file:AgentLibraryReader.getJarFileList(new File("./players"))){
			for(Class c:AgentLibraryReader.getPlayerClassList(file)){
				playerClassMap.put(c.getName(), c);
			}
		}
		
		winCounterMap = new HashMap<>();
		roleCounterMap = new HashMap<>();
	}
	
	public void start() throws SocketTimeoutException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		startServer();
		startClient();
		while(initServer || isRunning){
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}

	private void startServer() throws SocketTimeoutException, IOException {
		gameSetting = GameSetting.getDefaultGame(agentNum);
		gameServer = new TcpipServer(PORT, agentNum, gameSetting);
		gameServer.addServerListener(new ServerListener() {
			@Override public void unconnected(Socket socket, Agent agent, String name) {}
			@Override public void connected(Socket socket, Agent agent, String name) { System.out.println("Connected:"+name);}
		});
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try{
					gameServer.waitForConnection();
					isRunning = true;
					initServer = false;
					for(int i = 0; i < gameNum; i++){
						AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
						game.setRand(new Random(i));
						File logFile = new File(LOG_DIR + (new Date()).getTime() + ".txt"); 
						GameLogger logger = new FileGameLogger(logFile);
						if(isVisualize){
							ContestResource resource = new ContestResource(game);
							GameViewer gameViewer = new GameViewer(resource, game);
							gameViewer.setAutoClose(true);
							logger = new MultiGameLogger(logger, gameViewer);
						}
						game.setGameLogger(logger);
						
						try{
							game.start();
							Team winner = game.getWinner();
							GameData gameData = game.getGameData();
							for(Agent agent:gameData.getAgentList()){
								String agentName = game.getAgentName(agent);
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
						}catch(LostClientException e){
							Agent agent = e.getAgent();
							String teamName = gameServer.getName(agent);
							System.out.println("Lost:"+teamName);
							throw e;
						}
						
					}
					gameServer.close();
				}catch(LostClientException e){
					String teamName = gameServer.getName(e.getAgent());
					if(teamName != null){
						System.out.println("Lost connection "+teamName);
					}
				} catch(SocketTimeoutException e){
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isRunning = false;
			}
		};

		initServer = true;
		serverThread = new Thread(r);
		serverThread.start();

		while(!gameServer.isWaitForClient() && initServer){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startClient() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		for(String playerName:roleAgentMap.keySet()){
			String clsName = roleAgentMap.get(playerName).getKey();
			Role role = roleAgentMap.get(playerName).getValue();
			
			Player player = null;
			if(playerClassMap.containsKey(clsName)){
				player = (Player)playerClassMap.get(clsName).newInstance();
			}
			else{
				player = (Player)Class.forName(clsName).newInstance();
			}
			TcpipClient client = new TcpipClient("localhost", PORT, role);
			if(playerName != null){
				client.setName(playerName);
			}
			client.connect(player);
		}
	}

	private void result() {
		for(Role role:Role.values()){
			System.out.print("\t"+role);
		}
		System.out.println("\tTotal");
		for(String name:new TreeSet<>(roleAgentMap.keySet())){
			if(!winCounterMap.containsKey(name) || !roleCounterMap.containsKey(name)){
				continue;
			}
			System.out.print(name+"\t");
			double win = 0;
			double cnt = 0;
			for(Role role:Role.values()){
				System.out.printf("%d/%d\t", winCounterMap.get(name).get(role), roleCounterMap.get(name).get(role));
				win += winCounterMap.get(name).get(role);
				cnt += roleCounterMap.get(name).get(role);
			}
			System.out.printf("%.3f\n", win/cnt);
		} 
	}


}