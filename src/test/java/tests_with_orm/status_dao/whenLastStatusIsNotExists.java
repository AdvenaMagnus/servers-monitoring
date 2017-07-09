package tests_with_orm.status_dao;

import core.enums.ServerStatus;
import core.server.entities.ServerStatusCached;
import org.junit.Before;
import org.junit.Test;
import tests_with_orm.prepare_db.OneServerTowServerStatusesTowOnMaintenanceStatuses;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Alexander on 08.07.2017.
 */
public class whenLastStatusIsNotExists extends OneServerTowServerStatusesTowOnMaintenanceStatuses {

    String[] revisionAndDate = {"7000", "4.02.2017"};

    @Before
    public void init(){
        doReturn(true).when(statusDAO).isUpdateAvailable(any(), anyInt());
        doReturn(null).when(statusDAO).getLastStatus(any());
    }


    @Test
    public void whenCurrentServerStatusIsOnline(){

        doReturn(revisionAndDate).when(statusDAO).getRevisionAndDate(any());

        ServerStatusCached status = statusDAO.updateStatus(server);

        assertTrue(status.getId()>0);
        assertTrue(status.getRevision().equals(revisionAndDate[0]));
        assertTrue(status.getRevisionDate()!=null);
        assertTrue(status.getIsClosed()==false);

    }

    @Test
    public void whenCurrentServerIsOffline(){

        doReturn(null).when(statusDAO).getRevisionAndDate(any());

        ServerStatusCached status = statusDAO.updateStatus(server);

        assertTrue(status.getId()==0);
        assertTrue(status.getRevision()==null);
        assertTrue(status.getRevisionDate()==null);


    }

}
