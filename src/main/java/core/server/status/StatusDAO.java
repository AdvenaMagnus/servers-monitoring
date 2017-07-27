package core.server.status;

import core.server.entities.Server;
import core.server.entities.ServerStatusCached;

/**
 * Created by Alexander on 23.07.2017.
 */
public interface StatusDAO {

    void saveOrUpdate(ServerStatusCached status);
    ServerStatusCached getLastStatus(Server server);

}
