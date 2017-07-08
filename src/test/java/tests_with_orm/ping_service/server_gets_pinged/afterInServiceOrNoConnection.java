package tests_with_orm.ping_service.server_gets_pinged;

import core.enums.DowntimeReason;
import core.server.entities.OnMaintenanceStatus;
import org.junit.Before;
import org.junit.Test;
import tests_with_orm.prepare_db.OneServerTowServerStatusesTowOnMaintenanceStatuses;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Alexander on 05.07.2017.
 */
public class afterInServiceOrNoConnection extends OneServerTowServerStatusesTowOnMaintenanceStatuses {

	@Before
	public void init2(){

		doReturn(true).when(pingService).checkForConnection();
		doReturn(1000L).when(pingService).getPing(any());
	}

	@Test
	public void afterInService() throws Exception {
		maintenanceStatusNew.setCause(DowntimeReason.inService);
		testDAO.saveOrUpdate(maintenanceStatusNew);
		assertTrue(((OnMaintenanceStatus)testDAO.getById(OnMaintenanceStatus.class, maintenanceStatusNew.getId()))
						.getCause()==DowntimeReason.inService);

		String ping = pingService.getPingToServer(server);

		assertTrue(ping.equals("10 мс"));
		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==2);
		OnMaintenanceStatus lastMStatus = statusDAO.getLastOnMaintenanceStatus(server);
		assertTrue(lastMStatus!=null);
		assertTrue(lastMStatus.getId()==maintenanceStatusNew.getId());
		assertTrue(lastMStatus.getDateTo()!=null);
	}

	@Test
	public void afterNoConnection() throws Exception {
		maintenanceStatusNew.setCause(DowntimeReason.noInternet);
		testDAO.saveOrUpdate(maintenanceStatusNew);
		assertTrue(((OnMaintenanceStatus)testDAO.getById(OnMaintenanceStatus.class, maintenanceStatusNew.getId()))
						.getCause()==DowntimeReason.noInternet);

		String ping = pingService.getPingToServer(server);

		assertTrue(ping.equals("10 мс"));
		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==2);
		OnMaintenanceStatus lastMStatus = statusDAO.getLastOnMaintenanceStatus(server);
		assertTrue(lastMStatus != null);
		assertTrue(lastMStatus.getId() == maintenanceStatusNew.getId());
		assertTrue(lastMStatus.getDateTo()!=null);
	}

	@Test
	public void afterNoMaintenanceStatusIsEmpty() throws Exception {
		testDAO.getAll(OnMaintenanceStatus.class).forEach(status -> {
			testDAO.delete(status);
		});
		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==0);

		String ping = pingService.getPingToServer(server);

		assertTrue(ping.equals("10 мс"));
		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==0);
	}

}
