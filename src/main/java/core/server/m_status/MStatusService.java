package core.server.m_status;

import core.enums.DowntimeReason;
import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;

/**
 * Created by Alexander on 23.07.2017.
 */
public interface MStatusService {

    OnMaintenanceStatus updateMaintenanceStatus(Server server, DowntimeReason reason);
    void checkAndCloseLastMStatus(Server server);

}
