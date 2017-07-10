package tests_with_orm.ping_service.server_gets_in_service;

import core.enums.DowntimeReason;
import core.server.PingService;
import core.server.entities.OnMaintenanceStatus;
import org.junit.Before;
import org.junit.Test;
import tests_with_orm.prepare_db.OneServerTowServerStatusesTowOnMaintenanceStatuses;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 05.07.2017.
 */
public class TestWhenLastMaintenanceStatusIsNotNull extends OneServerTowServerStatusesTowOnMaintenanceStatuses {

	@Before
	public void init2(){
		server.setInService(true);
		serverDAO.update(server);
	}

	@Test
	public void testWhenLastStatusCauseNoInternet() throws Exception {
		OnMaintenanceStatus onMaintenanceStatus = statusDAO.getLastOnMaintenanceStatus(server);
		onMaintenanceStatus.setCause(DowntimeReason.noInternet);
		statusDAO.saveOnMaintenanceServer(onMaintenanceStatus);

		String ping = pingService.getPingToServer(server);

		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==3);
		OnMaintenanceStatus mStatusLast = statusDAO.getLastOnMaintenanceStatus(server);
		assertTrue(mStatusLast.getDateFrom()!=null);
		assertTrue(mStatusLast.getDateTo()==null);
		assertTrue(mStatusLast.getCause()==DowntimeReason.inService);

		OnMaintenanceStatus preLast = (OnMaintenanceStatus) testDAO.getById(OnMaintenanceStatus.class, maintenanceStatusNew.getId());
		assertTrue(preLast.getDateTo()!=null);
		assertTrue(ping.equals(PingService.inService));

	}


	@Test
	public void testWhenLastStatusCauseInService() throws Exception {
		OnMaintenanceStatus onMaintenanceStatus = statusDAO.getLastOnMaintenanceStatus(server);
		onMaintenanceStatus.setCause(DowntimeReason.inService);
		statusDAO.saveOnMaintenanceServer(onMaintenanceStatus);

		String ping = pingService.getPingToServer(server);

		assertTrue(testDAO.getAll(OnMaintenanceStatus.class).size()==2);
		OnMaintenanceStatus mStatusLast = statusDAO.getLastOnMaintenanceStatus(server);
		assertTrue(mStatusLast.getId() == maintenanceStatusNew.getId());
		assertTrue(ping.equals(PingService.inService));

	}

}
