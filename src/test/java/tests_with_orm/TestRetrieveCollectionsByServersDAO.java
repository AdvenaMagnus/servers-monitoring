package tests_with_orm;

import core.server.entities.OnMaintenanceStatus;
import core.server.entities.ServerStatusCached;
import org.junit.Test;
import tests_with_orm.prepare_db.OneServerTowServerStatusesTowOnMaintenanceStatuses;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 04.07.2017.
 */
public class TestRetrieveCollectionsByServersDAO extends OneServerTowServerStatusesTowOnMaintenanceStatuses {


	@Test
	public void testGetStatuses(){
		List<ServerStatusCached> statuses = serverDAO.getStatuses(server);
		assertEquals(statuses.size(), 2);
		assertTrue(statuses.get(0).getId() == statusOld.getId() || statuses.get(1).getId() == statusOld.getId());
		assertTrue(statuses.get(0).getId() == statusNew.getId() || statuses.get(1).getId() == statusNew.getId());

		List<OnMaintenanceStatus> statusesMaintenance = serverDAO.getMaintenanceStatuses(server);
		assertEquals(statusesMaintenance.size(), 2);
		assertTrue(statusesMaintenance.get(0).getId() == maintenanceStatusNew.getId() || statusesMaintenance.get(1).getId() == maintenanceStatusNew.getId());
		assertTrue(statusesMaintenance.get(0).getId() == maintenanceStatusOld.getId() || statusesMaintenance.get(1).getId() == maintenanceStatusOld.getId());

	}


}
