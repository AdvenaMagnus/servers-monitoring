package core.server;

import core.LightObject;
import core.enums.ServerStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Grey on 17.03.2017.
 */
public interface ServerDAO {

	List<Server> allServers();
	Server serverById(long id);
	Server createNew(Server server);
	Server update(Server server);
	boolean delete(Server server);

	default String getPing(Server server) throws Exception{
		if(server!=null && server.getIp()!=null){
			long currentTime = System.currentTimeMillis();
			boolean isPinged = InetAddress.getByName(server.getIp().split(":")[0]).isReachable(5000); // 5 seconds
			currentTime = System.currentTimeMillis() - currentTime;
			if(isPinged) {
				return currentTime/100+" мс";
			} else {
				return "нет";
			}
		} else return "нет";
	}

	default ServerStatus checkStatus(Server server) throws Exception{
		if(server!=null && server.getIp()!=null) {
			URL url = new URL("http://" + server.getIp());
			URLConnection yc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) if(inputLine.contains("modulesMenu.jspx")) return ServerStatus.online;
			in.close();
		}
		return ServerStatus.offline;
	}

	default List<LightObject> allServersLight(){
		List<LightObject> result = new ArrayList<LightObject>();
		allServers().forEach(s -> result.add(s.getLight()));
		return result;
	}

}
