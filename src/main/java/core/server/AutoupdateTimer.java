package core.server;

import controller.NotifyService;
import core.server.entities.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by Alexander on 27.06.2017.
 */

@Configurable
public class AutoupdateTimer implements Runnable{

	static final int updateInterval = 2;

	@Autowired
	StatusDAO statusDAO;

	@Autowired
	@Qualifier("server_dao_persist")
	ServerDAO serverDAO;

	@Autowired
	NotifyService notifyService;

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(updateInterval * 1000 * 60);
				//Thread.sleep(10000);
				System.out.println("Autoupdate servers");

				for(Server server : serverDAO.allServers()){
//					new Thread(() -> {
//						statusDAO.updateStatus(server);
//						notifyService.notifyStatus(server);
//					}).start();
					statusDAO.updateStatus(server);
					notifyService.notifyStatus(server);
				}
				System.out.println("Autoupdate servers finished");

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
