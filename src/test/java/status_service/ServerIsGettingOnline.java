package status_service;

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
 * Created by Alexander on 27.07.2017.
 */
/**Server is getting online, last status exists -> create(open) new status*/
public class ServerIsGettingOnline {

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
        status.setIsClosed(true);

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

        assertTrue(statusCached!=status);
        assertTrue(statusCached.getRevision().equals(revisionAndDate[0]));
        assertTrue(statusCached.getRevisionDate()!=null);
        assertTrue(statusCached.getIsClosed()==false);
    }

}
