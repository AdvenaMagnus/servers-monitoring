package status_service;

/**
 * Created by Alexander on 23.07.2017.
 */

import core.server.AutoupdateTimer;
import core.server.NetworkService;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import core.server.status.StatusDAO;
import core.server.status.StatusServiceImplGEO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * 1) current server status is online, last status exists in DB -> update status time and return status
 * */
public class CurStatusIsOnline_lastStatusExists {
    @InjectMocks
    StatusServiceImplGEO statusService;

    @Mock
    NetworkService networkService;

    @Mock
    StatusDAO statusDAO;

    Server server;
    ServerStatusCached status;

    String[] revisionAndDate = {"7000", "4.02.2017"};

    @Before
    public void init(){
        statusService = spy(StatusServiceImplGEO.class);

        server = new Server();
        server.setId(1);
        server.setName("test");

        status = new ServerStatusCached();
        status.setOwner(server);
        status.setIsClosed(false);

        MockitoAnnotations.initMocks(this);
        doReturn(null).when(networkService).makeRequest(any());
        doReturn(status).when(statusDAO).getLastStatus(server);
        //when(statusService.getLastStatus(server)).thenReturn(status);
        doReturn(revisionAndDate).when(statusService).getRevisionAndDate(any());
        doReturn(true).when(statusService).isUpdateAvailable(status, AutoupdateTimer.updateInterval);
    }

    @Test
    public void test(){
        ServerStatusCached statusCached = statusService.updateStatus(server);

        assertTrue(statusCached==status);
        assertTrue(status.getRevision().equals(revisionAndDate[0]));
        assertTrue(status.getRevisionDate()!=null);
        assertTrue(status.getIsClosed()==false);
    }

}
