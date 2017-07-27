package core.server.m_status;

import core.enums.DowntimeReason;
import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Alexander on 23.07.2017.
 */

@Service
public class MStatusServiceImpl implements MStatusService {

    @Autowired
    MStatusDAO mStatusDAO;

    /**
     * 1) Last mStatus exists and its reason != arg reason -> 'close' last status and 'open' new
     * 2) Last status doesnt exists -> create new with arg reason
     * 3) Last status exists and its reason == arg reason -> do nothing
     * */
    public OnMaintenanceStatus updateMaintenanceStatus(Server server, DowntimeReason reason){
        OnMaintenanceStatus lastMStatus = mStatusDAO.getLast(server);
        if(lastMStatus==null || lastMStatus.getCause()!= reason){
            if(lastMStatus!=null) {
                lastMStatus.setDateTo(DateUtils.getCurrentTime());
                mStatusDAO.saveOrUpdate(lastMStatus);
            }
            OnMaintenanceStatus newMStatus = new OnMaintenanceStatus();
            newMStatus.setOwner(server);
            newMStatus.setDateFrom(DateUtils.getCurrentTime());
            newMStatus.setCause(reason);
            mStatusDAO.saveOrUpdate(newMStatus);
            return newMStatus;
        }
        return lastMStatus;
    }

    /** When server goes online after being downtime, there is need to close last downtime status*, this method does it */
    public void checkAndCloseLastMStatus(Server server){
        OnMaintenanceStatus lastMStatus = mStatusDAO.getLast(server);
        if(lastMStatus!=null && lastMStatus.getDateTo()==null){
            lastMStatus.setDateTo(DateUtils.getCurrentTime());
            mStatusDAO.saveOrUpdate(lastMStatus);
        }
    }

}
