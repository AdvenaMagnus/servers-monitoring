package core.server;

import core.enums.DowntimeReason;
import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.utils.DateUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Alexander on 05.07.2017.
 */

@Service
public class PingService {

	public static String inService = "Н/О";
	public static String noPing = "нет";
	public static String noConnection = "Нет сети";

	@Autowired
	StatusDAO statusDAO;

	public String getPingToServer(Server server){
		if (server.getInService()) {
			updateMaintenanceStatus(server, DowntimeReason.inService);
			return inService;
		}
		if (checkForConnection()) {
				if (server != null && server.getIp() != null) {

					OnMaintenanceStatus lastMStatus = statusDAO.getLastOnMaintenanceStatus(server);
					if(lastMStatus!=null && lastMStatus.getDateTo()==null){
						lastMStatus.setDateTo(DateUtils.getCurrentTime());
						statusDAO.saveOnMaintenanceServer(lastMStatus);
					}
					long ping = getPing(server.getIp().split(":")[0]);
					if (ping != Long.MAX_VALUE) {
						return ping / 100 + " мс";
					} else {
						return noPing;
					}
				} else return noPing;
		} else {
			updateMaintenanceStatus(server, DowntimeReason.noInternet);
			return noConnection;
		}
	}

	private void updateMaintenanceStatus(Server server, DowntimeReason reason){
		OnMaintenanceStatus lastMStatus = statusDAO.getLastOnMaintenanceStatus(server);
		if(lastMStatus==null || lastMStatus.getCause()!= reason){
			if(lastMStatus!=null) {
				lastMStatus.setDateTo(DateUtils.getCurrentTime());
				statusDAO.saveOnMaintenanceServer(lastMStatus);
			}
			OnMaintenanceStatus newMStatus = new OnMaintenanceStatus();
			newMStatus.setOwner(server);
			newMStatus.setDateFrom(DateUtils.getCurrentTime());
			newMStatus.setCause(reason);
			statusDAO.saveOnMaintenanceServer(newMStatus);
		}
	}

	public long getPing(String ipAddress) {
		long currentTime = System.currentTimeMillis();
		boolean isPinged = false; // 5 seconds
		try {
			isPinged = InetAddress.getByName(ipAddress).isReachable(5000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(isPinged) return System.currentTimeMillis() - currentTime;
		else return Long.MAX_VALUE;
	}

	public boolean checkForConnection(){
//
//		long ping = 0;
//		try {
//			ping = getPing(InetAddress.getByName("www.e1.ru").getHostAddress().toString());
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		return ping != Long.MAX_VALUE ? true : false;

		//TODO make option for pinging on *nix servers by changing -n to -s

		String osConf = "";
		if(SystemUtils.IS_OS_LINUX) osConf = "-s";
		else {
			if(SystemUtils.IS_OS_WINDOWS) osConf = "-n";
		}

		Process p1 = null;
		try {
			p1 = Runtime.getRuntime().exec("ping "+ osConf+" 1 www.google.com");
			int returnVal = p1.waitFor();
			return returnVal==0;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

}
