package core.server;

import core.LightObject;
import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 17.03.2017.
 */
public interface ServerDAO {

	List<Server> allServers();
	Server serverById(long id);
	Server createNew(Server server);
	Server update(Server server);
	void refresh(Server server);
	//void clear();
	boolean delete(Server server);
	List<ServerStatusCached> getStatuses(Server server);
	List<OnMaintenanceStatus> getMaintenanceStatuses(Server server);
	//SystemInfo getSystemInfo(Server server);
	//SystemInfo getSystemInfoSaved(Server server);

	default List<LightObject> allServersLight(){
		List<LightObject> result = new ArrayList<LightObject>();
		allServers().forEach(s -> result.add(s.getLight()));
		return result;
	}

}
