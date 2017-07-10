package tests_with_orm;

import core.server.ServerDAO;
import core.server.StatusDAO;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import tests_with_orm.prepare_db.OneServerTowServerStatusesTowOnMaintenanceStatuses;

import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by Grey on 29.06.2017.
 */
public class TestGetLastStatus extends OneServerTowServerStatusesTowOnMaintenanceStatuses {

	@Test
	public void testGetLastStatus(){
		ServerStatusCached lastStatus = statusDAO.getLastStatus(server);
		assertTrue(lastStatus.getId()==statusNew.getId());
	}

	@Test
	public void testGetLastStatus3() {
		assertTrue(serverDAO.allServers().size() == 1);
	}

}
