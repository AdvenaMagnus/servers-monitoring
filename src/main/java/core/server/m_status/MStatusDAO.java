package core.server.m_status;

import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;

/**
 * Created by Alexander on 23.07.2017.
 */
public interface MStatusDAO {

    void saveOrUpdate(OnMaintenanceStatus status);
    OnMaintenanceStatus getLast(Server server);
}
