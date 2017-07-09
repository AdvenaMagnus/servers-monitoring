package tests_with_orm.status_dao;

import core.server.entities.ServerStatusCached;
import org.junit.Assert;
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
public class whenLastStatusAlreadyExists extends OneServerTowServerStatusesTowOnMaintenanceStatuses {

    String[] revisionAndDate = {"7000", "4.02.2017"};


    @Before
    public void init2(){
        doReturn(null).when(statusDAO).getRevisionAndDate(any());
        doReturn(true).when(statusDAO).isUpdateAvailable(any(), anyInt());
    }


    /**
     * 1) current server status is online, last status exists in DB -> update status time and return status
     * */
    @Test
    public void whenCurrentStatusIsOnline(){


        doReturn(revisionAndDate).when(statusDAO).getRevisionAndDate(any());

        ServerStatusCached status = statusDAO.updateStatus(server);

        assertTrue(status.getId()==statusNew.getId());
        assertTrue(status.getDate()!=statusNew.getDate());
        assertTrue(status.getRevision().equals(revisionAndDate[0]));
        assertTrue(status.getRevisionDate()!=null);
        assertTrue(status.getIsClosed()==false);

    }

    @Test
    public void whenCurrentStatusIsOfflineAndLastStatusNotClosed(){


        statusNew.setIsClosed(false);
        statusNew.setRevision(revisionAndDate[0]);
        statusDAO.save(statusNew);

        ServerStatusCached status = statusDAO.updateStatus(server);

        assertTrue(status.getId()==statusNew.getId());
        //assertTrue(status.getDate()!=statusNew.getDate());
        assertTrue(status.getDate().getTime()==statusNew.getDate().getTime());
        assertTrue(status.getRevision().equals(statusNew.getRevision()));
        assertTrue(status.getIsClosed()==true);

    }

    @Test
    public void whenCurrentStatusIsOfflineAndLastStatusIsClosed(){

        statusNew.setIsClosed(true);
        statusNew.setRevision(revisionAndDate[0]);
        statusDAO.save(statusNew);

        ServerStatusCached status = statusDAO.updateStatus(server);

        assertTrue(status.getId()==statusNew.getId());
        //assertTrue(status.getDate()==statusNew.getDate());
        assertTrue(status.getDate().getTime()==statusNew.getDate().getTime());
        assertTrue(status.getRevision().equals(statusNew.getRevision()));
        assertTrue(status.getIsClosed()==true);

    }



}
