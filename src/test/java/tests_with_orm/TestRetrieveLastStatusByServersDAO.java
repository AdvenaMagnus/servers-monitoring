package tests_with_orm;

import core.server.entities.OnMaintenanceStatus;
import org.junit.Test;
import tests_with_orm.prepare_db.OneServerTowServerStatusesTowOnMaintenanceStatuses;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 04.07.2017.
 */
public class TestRetrieveLastStatusByServersDAO extends OneServerTowServerStatusesTowOnMaintenanceStatuses {

	@Test
	public void testGetLasOnMaintenanceStatus(){
		List<OnMaintenanceStatus> statusesMaintenance = serverDAO.getMaintenanceStatuses(server);
		assertEquals(statusesMaintenance.size(), 2);

		OnMaintenanceStatus lastStatus = statusDAO.getLastOnMaintenanceStatus(server);
		assertTrue(lastStatus.getId()==maintenanceStatusNew.getId());

	}


}
