package core.server.status;

import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;

/**
 * Created by Alexander on 23.07.2017.
 */
public interface StatusService {

    ServerStatusCached updateStatus(Server server);

}
