package tests_with_orm;

import core.server.ServerDAO;
import core.server.StatusDAO;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by Grey on 29.06.2017.
 */
//@Service
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes = {TestConf.class, HibernateConfigTest.class})
public class TestGetLastStatus extends OrmTests {


//	@Autowired
//	ServerDAO serverDAO;
//
//	@Autowired
//	StatusDAO statusDAO;

//	@After
//	//@Transactional
//	public void afterTests(){
//		//sessionFactory.getCurrentSession().clear();
//		serverDAO.clear();
//	}

	@Test
	public void testGetLastStatus(){
		//assertTrue(serverDAO.allServers().size()==0);

		Server server = new Server();
		serverDAO.createNew(server);

		Date currentDate = new Date();

		ServerStatusCached statusOld = new ServerStatusCached();
		statusOld.setOwner(server);
		statusOld.setDate(currentDate);
		statusOld.setMin(1);
		statusOld.setHours(12);
		statusDAO.save(statusOld);

		ServerStatusCached statusNew = new ServerStatusCached();
		statusNew.setOwner(server);
		statusNew.setDate(currentDate);
		statusNew.setMin(0);
		statusNew.setHours(16);
		statusDAO.save(statusNew);

		serverDAO.refresh(server);

		assertTrue(server.getStatuses().size()==2);
		assertTrue(statusDAO.getLastStatus(server).getId()==statusNew.getId());
	}

	@Test
	public void testGetLastStatus3() {
		assertTrue(serverDAO.allServers().size() == 0);
	}

}
