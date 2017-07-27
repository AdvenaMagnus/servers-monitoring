package core.server;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import core.enums.DowntimeReason;
import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.server.m_status.MStatusDAO;
import core.server.m_status.MStatusService;
import core.server.status.StatusService;
import core.utils.DateUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Created by Alexander on 05.07.2017.
 */

@Service
public class NetworkService {

	public static String inService = "Н/О";
	public static String noPing = "нет";
	public static String noConnection = "Нет сети";

	@Autowired
	MStatusService mStatusService;

	@Bean
	@Qualifier("sessionIds")
	private HashMap<String, Cookie> getSessionsIds(){
		return new HashMap<String, Cookie>();
	}

	@Autowired
	@Qualifier("sessionIds")
	HashMap<String, Cookie> sessionIds;

	public String getPingToServer(Server server){
		if (server.getInService()) {
			mStatusService.updateMaintenanceStatus(server, DowntimeReason.inService);
			return inService;
		}
		if (checkForConnection()) {
				if (server != null && server.getIp() != null) {
					mStatusService.checkAndCloseLastMStatus(server);
					long ping = getPing(server.getIp().split(":")[0]);
					if (ping != Long.MAX_VALUE) {
						return ping / 100 + " мс";
					} else {
						return noPing;
					}
				} else return noPing;
		} else {
			mStatusService.updateMaintenanceStatus(server, DowntimeReason.noInternet);
			return noConnection;
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
		String osConf = "";
		if(SystemUtils.IS_OS_LINUX) osConf = "-c";
		else {
			if(SystemUtils.IS_OS_WINDOWS) osConf = "-n";
		}

		Process p1;
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

	/** Get page on specified url*/
	public HtmlPage makeRequest(String url) {
		System.out.println("Current thread id " + Thread.currentThread().getId() + ", url:" + url);
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setTimeout(9000);

			CookieManager cookieMan = webClient.getCookieManager();
			cookieMan.setCookiesEnabled(true);

			Cookie savedCookie = sessionIds.get(url);
			if(savedCookie!=null)
				cookieMan.addCookie(savedCookie);

			//HtmlPage myPage = ((HtmlPage) webClient.getPage("http://" + this.getIp()+secondUrl));
			//HtmlPage resultPage = ((HtmlPage) webClient.getPage(url+"/faces/muncontrol/pages/login.jspx"));
			HtmlPage resultPage = webClient.getPage(url);

			for(Cookie cookie : cookieMan.getCookies()){
				if(cookie.getName().equals("JSESSIONID"))
					sessionIds.put(url, cookie);
			}

			//webClient.waitForBackgroundJavaScript(8000);
			return resultPage;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
