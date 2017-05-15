package core.server;

import core.server.entities.Server;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Grey on 17.03.2017.
 */
@Service
@Qualifier("server_dao_hardcoded")
public class ServerDAOimpl implements ServerDAO{

	@Override
	public List<Server> allServers() {
		List<Server> result = new ArrayList<>();
		serversStatic.values().forEach(s -> result.add(s));
		return result;
	}

//	@Override
//	public List<LightObject> allServersLight() {
//		List<LightObject> result = new ArrayList<LightObject>();
//		allServers().forEach(s -> result.add(s.getLight()));
//		return result;
//	}
//	@Override
//	public ServerStatusCached checkStatus(Server server) throws Exception {
//		if(server!=null && server.getIp()!=null) {
//			URL url = new URL("http://" + server.getIp());
//			URLConnection yc = url.openConnection();
//			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
//			String inputLine;
//
//			while ((inputLine = in.readLine()) != null) if(inputLine.contains("modulesMenu.jspx")) return ServerStatusCached.online;
//			in.close();
//		}
//		return ServerStatusCached.offline;
//	}

	@Override
	public Server serverById(long id) {
		return serversStatic.get(id);
	}

	@Override
	public Server createNew(Server server) {
		return null;
	}

	@Override
	public Server update(Server server) {
		return null;
	}

	@Override
	public boolean delete(Server server) {
		return false;
	}


//	@Override
//	public String getPing(Server server) throws Exception {
//		if(server!=null && server.getIp()!=null){
//			long currentTime = System.currentTimeMillis();
//			boolean isPinged = InetAddress.getByName(server.getIp().split(":")[0]).isReachable(5000); // 5 seconds
//			currentTime = System.currentTimeMillis() - currentTime;
//			if(isPinged) {
//				return currentTime/100+" мс";
//			} else {
//				return "нет";
//			}
//		} else return "нет";
//	}

	public static HashMap<Long, Server> serversStatic = new HashMap<Long, Server>();
	static {
		serversStatic.put(1L, new Server(1, "Березовский", "172.16.5.39"));
		serversStatic.put(2L, new Server(2, "Югорск", "172.16.5.37"));
		serversStatic.put(3L, new Server(3, "100% unwork", "172.16.3.1:9091"));
		serversStatic.put(4L, new Server(4, "Нефтеюганский район", "172.16.5.5"));
		serversStatic.put(5L, new Server(5, "Нефтеюганск город", "172.16.5.42"));
		serversStatic.put(6L, new Server(6, "Лангепас", "172.16.5.9"));
		serversStatic.put(7L, new Server(7, "Пермский район", "172.16.5.8"));
		serversStatic.put(8L, new Server(8, "Нижние серги", "172.16.5.30"));
		serversStatic.put(9L, new Server(9, "Камышлов", "172.16.5.22"));
		serversStatic.put(10L, new Server(10, "Ирбит", "172.16.5.35"));
		serversStatic.put(11L, new Server(11, "Дегтярск", "172.16.5.29"));
		serversStatic.put(12L, new Server(12, "Чусовой", "172.16.5.36"));
		serversStatic.put(13L, new Server(13, "Соликамск", "172.16.5.40"));
		serversStatic.put(14L, new Server(14, "Пуровский", "172.16.5.47"));
	}

}
