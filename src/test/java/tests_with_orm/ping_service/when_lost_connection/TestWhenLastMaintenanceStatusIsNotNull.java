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
public class TestWhenLastMaintenanceStatusIsNotNull extends OneServerTowServerStatusesTowOnMaintenanceStatuses {

	@Before
	public void init2(){
		server.setInService(false);
		serverDAO.update(server);

		doReturn(false).when(pingService).checkForConnection();
	}

	@Test
	public void testWhenLastStatusCauseInService() throws Exception {
		OnMaintenanceStatus onMaintenanceStatus = statusDAO.getLastOnMaintenanceStatus(server);
		onMaintenanceStatus.setCause(DowntimeReason.inService);
		statusDAO.saveOnMaintenanceServer(onMaintenanceStatus);

		String ping = pingService.getPingToServer(server);

		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==3);
		OnMaintenanceStatus mStatusLast = statusDAO.getLastOnMaintenanceStatus(server);
		assertTrue(mStatusLast.getDateFrom()!=null);
		assertTrue(mStatusLast.getDateTo()==null);
		assertTrue(mStatusLast.getCause()==DowntimeReason.noInternet);

		OnMaintenanceStatus preLast = (OnMaintenanceStatus) testDAO.getById(OnMaintenanceStatus.class, maintenanceStatusNew.getId());
		assertTrue(preLast.getDateTo()!=null);
		assertTrue(preLast.getCause()==DowntimeReason.inService);
		assertTrue(ping.equals(PingService.noConnection));

	}


	@Test
	public void testWhenLastStatusCauseNoInternet() throws Exception {
		OnMaintenanceStatus onMaintenanceStatus = statusDAO.getLastOnMaintenanceStatus(server);
		onMaintenanceStatus.setCause(DowntimeReason.noInternet);
		statusDAO.saveOnMaintenanceServer(onMaintenanceStatus);

		String ping = pingService.getPingToServer(server);

		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==2);
		OnMaintenanceStatus mStatusLast = statusDAO.getLastOnMaintenanceStatus(server);
		assertTrue(mStatusLast.getId() == maintenanceStatusNew.getId());
		assertTrue(ping.equals(PingService.noConnection));

	}

}
