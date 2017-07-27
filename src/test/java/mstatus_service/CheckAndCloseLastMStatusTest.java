package mstatus_service;

import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.server.m_status.MStatusDAO;
import core.server.m_status.MStatusServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Alexander on 27.07.2017.
 */
public class CheckAndCloseLastMStatusTest {


    @InjectMocks
    MStatusServiceImpl mStatusService;

    @Mock
    MStatusDAO mStatusDAO;

    Server server;

    OnMaintenanceStatus onMaintenanceStatus;

    @Before
    public void init(){

        server = new Server();
        server.setId(1);

        MockitoAnnotations.initMocks(this);

    }


    @Test
    public void whenLastStatusExistsAndDateToEqualsNull(){
        onMaintenanceStatus = new OnMaintenanceStatus();
        onMaintenanceStatus.setId(2);
        onMaintenanceStatus.setOwner(server);
        doReturn(onMaintenanceStatus).when(mStatusDAO).getLast(any());

        mStatusService.checkAndCloseLastMStatus(server);

        assertTrue(onMaintenanceStatus.getDateTo()!=null);
    }

    @Test
    public void whenLastStatusExistsAndDateToDoesntEqualNull(){
        Date curDate = new Date();
        onMaintenanceStatus = new OnMaintenanceStatus();
        onMaintenanceStatus.setId(2);
        onMaintenanceStatus.setOwner(server);
        onMaintenanceStatus.setDateTo(curDate);
        doReturn(onMaintenanceStatus).when(mStatusDAO).getLast(any());

        mStatusService.checkAndCloseLastMStatus(server);

        assertTrue(onMaintenanceStatus.getDateTo()==curDate);
        assertTrue(onMaintenanceStatus.getDateTo().getTime()==curDate.getTime());
    }

    @Test
    public void whenLastStatusDoesntExist(){
        doReturn(null).when(mStatusDAO).getLast(any());

        mStatusService.checkAndCloseLastMStatus(server);
    }

}
