package core.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.LightObject;
import core.SystemInfo;
import core.enums.ServerStatus;

import javax.persistence.Transient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alexander on 17.03.2017.
 */
public interface ServerDAO {

	List<Server> allServers();
	Server serverById(long id);
	Server createNew(Server server);
	Server update(Server server, boolean emit);
	boolean delete(Server server);
	SystemInfo getSystemInfo(Server server);
	SystemInfo getSystemInfoSaved(Server server);

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

	default List<LightObject> allServersLight(){
		List<LightObject> result = new ArrayList<LightObject>();
		allServers().forEach(s -> result.add(s.getLight()));
		return result;
	}

}
