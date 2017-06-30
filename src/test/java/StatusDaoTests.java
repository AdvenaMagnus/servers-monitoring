import core.enums.ServerStatus;
import core.server.AutoupdateTimer;
import core.server.StatusDAO;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Alexander on 27.06.2017.
 */
public class StatusDaoTests {

	@InjectMocks
	StatusDAO statusDAO;

	@Mock
	SessionFactory sessionFactory;

	Server server;
	ServerStatusCached status;

	@Before
	public void init(){
		statusDAO = spy(StatusDAO.class);
		server = spy(Server.class);
		status = spy(ServerStatusCached.class);
		MockitoAnnotations.initMocks(this);
		Session session = mock(Session.class);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
	}

	@Test
	public void simpleTest(){
		assertTrue(true);
	}

	/**
	 * 1) current server status is online, last status exists in DB -> update status time and return status
	 * */
	@Test
	public void test1(){

		String[] revisionAndDate = {"7000", "4.02.2017"};
		status.setIsClosed(false);

		doReturn(status).when(statusDAO).getLastStatus(server);
		//when(statusDAO.getLastStatus(server)).thenReturn(status);
		doReturn(revisionAndDate).when(statusDAO).getRevisionAndDate(any());
		//when(statusDAO.getRevisionAndDate(any())).thenReturn(revisionAndDate);
		//doReturn(false).when(status).getIsClosed();
		//when(status.getIsClosed()).thenReturn(false);
		doReturn(true).when(statusDAO).isUpdateAvailable(status, AutoupdateTimer.updateInterval);
		//when(statusDAO.isUpdateAvailable(status, any())).thenReturn(true);

		assertTrue(statusDAO.updateStatus(server)==status);
		assertTrue(status.getRevision().equals(revisionAndDate[0]));
		assertTrue(status.getRevisionDate()!=null);
		assertTrue(status.getIsClosed()==false);
	}


	/**
	 * 2) current server status is offline, last status exists in DB -> close and return status
	 * */

	@Test
	public void test2(){
		status.setIsClosed(false);
		doReturn(status).when(statusDAO).getLastStatus(server);
		doReturn(null).when(statusDAO).getRevisionAndDate(any());
		doReturn(true).when(statusDAO).isUpdateAvailable(status, AutoupdateTimer.updateInterval);

		assertTrue(statusDAO.updateStatus(server)==status);
		assertTrue(status.getIsClosed()==true);
	}


	/**
	 * 3) current server status is online, last status doesn't exist in DB -> create and return new status
	 * */
	@Test
	public void test3(){
		String[] revisionAndDate = {"7000", "4.02.2017"};

		doReturn(null).when(statusDAO).getLastStatus(server);
		doReturn(revisionAndDate).when(statusDAO).getRevisionAndDate(any());
		doReturn(true).when(statusDAO).isUpdateAvailable(status, AutoupdateTimer.updateInterval);

		ServerStatusCached serverStatusCached = statusDAO.updateStatus(server);

		assertTrue(serverStatusCached!=null);
		assertTrue(serverStatusCached.getRevision().equals(revisionAndDate[0]));
		assertTrue(serverStatusCached.getRevisionDate()!=null);
		assertTrue(serverStatusCached.getIsClosed()==false);
		assertTrue(serverStatusCached.getCreateDate()!=null);
		//assertTrue(serverStatusCached.getCreateMin()!=0);
		//assertTrue(serverStatusCached.getCreateHours()!=0);
	}

	/**
	 * 4) current server status is offline, last status doesn't exist in DB -> return temp status
	 * */
	@Test
	public void test4(){

		doReturn(null).when(statusDAO).getLastStatus(server);
		doReturn(null).when(statusDAO).getRevisionAndDate(any());
		doReturn(true).when(statusDAO).isUpdateAvailable(status, AutoupdateTimer.updateInterval);

		ServerStatusCached serverStatusCached = statusDAO.updateStatus(server);

		assertTrue(serverStatusCached!=null);
		assertTrue(serverStatusCached.getRevision()==null);
		assertTrue(serverStatusCached.getRevisionDate()==null);
		assertTrue(serverStatusCached.getIsClosed()==true);
		assertTrue(serverStatusCached.getOwner()==server);
	}

	/**
	 * 5) Current server offline, last status exists and status.isClosed == false, last update was less that interval minutes ago
	 * return last status
	 * */
	@Test
	public void test5(){
		status.setIsClosed(false);
		doReturn(status).when(statusDAO).getLastStatus(server);
		doReturn(false).when(statusDAO).isUpdateAvailable(status, AutoupdateTimer.updateInterval);

		ServerStatusCached serverStatusCached = statusDAO.updateStatus(server);

		assertTrue(serverStatusCached!=null);
		assertTrue(serverStatusCached==status);
		assertTrue(serverStatusCached.getStatus() == ServerStatus.online);
	}

}
