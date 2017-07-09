package tests_with_orm.prepare_db;

import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import core.utils.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
import tests_with_orm.OrmTests;

import java.util.Date;

/**
 * Created by Alexander on 05.07.2017.
 */
@Ignore
public class OneServerTowServerStatusesTowOnMaintenanceStatuses extends OrmTests {

	public Server server;
	public Date currentDate = DateUtils.getCurrentTime();

	public ServerStatusCached statusOld;
	public ServerStatusCached statusNew;

	public OnMaintenanceStatus maintenanceStatusNew;
	public OnMaintenanceStatus maintenanceStatusOld;


	@Before
	public void initObjects(){

		server = new Server();
		server.setIp("127.0.0.1:8080");
		serverDAO.createNew(server);

		statusOld = new ServerStatusCached();
		statusOld.setOwner(server);
		statusOld.setDate(new Date(currentDate.getTime()-10*60*1000));
		statusDAO.save(statusOld);

		statusNew = new ServerStatusCached();
		statusNew.setOwner(server);
		statusNew.setDate(currentDate);
		statusDAO.save(statusNew);

		maintenanceStatusOld = new OnMaintenanceStatus();
		maintenanceStatusOld.setOwner(server);
		maintenanceStatusOld.setDateFrom(DateUtils.getCurrentTime());
		maintenanceStatusOld.setDateTo(new Date(currentDate.getTime()+120*60*1000));
		statusDAO.saveOnMaintenanceServer(maintenanceStatusOld);

		maintenanceStatusNew = new OnMaintenanceStatus();
		maintenanceStatusNew.setOwner(server);
		maintenanceStatusNew.setDateFrom(new Date(currentDate.getTime()+130*60*1000));
		statusDAO.saveOnMaintenanceServer(maintenanceStatusNew);

		testDAO.clear();

	}


}
