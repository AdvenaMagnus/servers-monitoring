package mstatus_service;

import core.enums.DowntimeReason;
import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.server.m_status.MStatusDAO;
import core.server.m_status.MStatusServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Created by Alexander on 24.07.2017.
 */
public class UpdateMaintenanceStatusTest {

    @InjectMocks
    MStatusServiceImpl mStatusService;

    @Mock
    MStatusDAO mStatusDAO;

    Server server;

    @Before
    public void init(){
        mStatusService = spy(MStatusServiceImpl.class);
        server = new Server();
        server.setId(1);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public  void whenLastMstatusExistsAndReasonsDontEqual(){
        OnMaintenanceStatus lastStatus = new OnMaintenanceStatus();
        lastStatus.setOwner(server);
        lastStatus.setCause(DowntimeReason.inService);
        doReturn(lastStatus).when(mStatusDAO).getLast(server);

        OnMaintenanceStatus newStatus = mStatusService.updateMaintenanceStatus(server, DowntimeReason.noInternet);

        assertTrue(newStatus!=lastStatus);
        assertTrue(lastStatus.getDateTo()!=null);
        assertTrue(newStatus.getCause()==DowntimeReason.noInternet);
        assertTrue(newStatus.getDateFrom()!=null);
        assertTrue(newStatus.getDateTo()==null);

    }

    @Test
    public  void whenLastMstatusExistsAndReasonsEqual(){
        OnMaintenanceStatus lastStatus = new OnMaintenanceStatus();
        lastStatus.setOwner(server);
        lastStatus.setCause(DowntimeReason.noInternet);
        doReturn(lastStatus).when(mStatusDAO).getLast(server);

        OnMaintenanceStatus newStatus = mStatusService.updateMaintenanceStatus(server, DowntimeReason.noInternet);

        assertTrue(newStatus==lastStatus);
        assertTrue(newStatus.getCause()==DowntimeReason.noInternet);
        assertTrue(newStatus.getDateFrom()==null);
        assertTrue(newStatus.getDateTo()==null);
    }

    @Test
    public void whenLastStatusDoesntExists(){
        doReturn(null).when(mStatusDAO).getLast(server);

        OnMaintenanceStatus newStatus = mStatusService.updateMaintenanceStatus(server, DowntimeReason.noInternet);

        assertTrue(newStatus.getCause()==DowntimeReason.noInternet);
        assertTrue(newStatus.getDateFrom()!=null);
        assertTrue(newStatus.getDateTo()==null);
    }

}
