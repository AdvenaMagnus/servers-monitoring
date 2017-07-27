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
 * 4) current server status is offline, last status doesn't exist in DB -> return temp status
 * */
public class CurStatusIsOffline_lastStatusDoesntExists {
    @InjectMocks
    StatusServiceImplGEO statusService;

    @Mock
    NetworkService networkService;

    @Mock
    StatusDAO statusDAO;

    Server server;

    String[] revisionAndDate = {"7000", "4.02.2017"};

    @Before
    public void init(){
        statusService = spy(StatusServiceImplGEO.class);

        server = new Server();
        server.setId(1);
        server.setName("test");

        MockitoAnnotations.initMocks(this);
        doReturn(null).when(networkService).makeRequest(any());

        doReturn(null).when(statusDAO).getLastStatus(server);
        doReturn(null).when(statusService).getRevisionAndDate(any());
    }

    @Test
    public void test(){

        ServerStatusCached serverStatusCached = statusService.updateStatus(server);

        assertTrue(serverStatusCached!=null);
        assertTrue(serverStatusCached.getRevision()==null);
        assertTrue(serverStatusCached.getRevisionDate()==null);
        assertTrue(serverStatusCached.getIsClosed()==true);
        assertTrue(serverStatusCached.getOwner()==server);
    }

}
