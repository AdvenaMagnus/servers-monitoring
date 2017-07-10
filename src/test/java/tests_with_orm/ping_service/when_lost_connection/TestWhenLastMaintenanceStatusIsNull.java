package tests_with_orm.ping_service.when_lost_connection;

import core.enums.DowntimeReason;
import core.server.PingService;
import core.server.entities.OnMaintenanceStatus;
import org.junit.Before;
import org.junit.Test;
import tests_with_orm.prepare_db.OneServerTowServerStatusesTowOnMaintenanceStatuses;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Alexander on 05.07.2017.
 */
public class TestWhenLastMaintenanceStatusIsNull extends OneServerTowServerStatusesTowOnMaintenanceStatuses{

	@Before
	public void init2(){
		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==2);
		testDAO.delete(maintenanceStatusOld);
		testDAO.delete(maintenanceStatusNew);
		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==0);
		assertTrue(statusDAO.getLastOnMaintenanceStatus(serverDAO.serverById(server.getId()))==null);
		server.setInService(false);
		serverDAO.update(server);

		doReturn(false).when(pingService).checkForConnection();
	}

	@Test
	public void testLastOnMainstatusIsEmpty() throws Exception {
		String ping = pingService.getPingToServer(server);

		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==1);
		OnMaintenanceStatus maintenanceStatus = (OnMaintenanceStatus) testDAO.getAll(OnMaintenanceStatus.class).get(0);
		assertTrue(maintenanceStatus.getCause()== DowntimeReason.noInternet);
		assertTrue(maintenanceStatus.getDateTo()== null);
		assertTrue(maintenanceStatus.getDateFrom()!= null);
		assertTrue(maintenanceStatus.getOwner().getId()== server.getId());
		assertTrue(ping.equals(PingService.noConnection));
	}

}
