package core.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import core.LightObject;
import core.SystemInfo;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Grey on 17.03.2017.
 */
@Entity
@Repository
public class Server {

	public Server(){
	}
	public Server(String name, String ip){
		this.setName(name);
		this.setIp(ip);
	}

	public Server(long id, String name, String ip){
		this.setId(id);
		this.setName(name);
		this.setIp(ip);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	String name;
	String ip;
	String systemLogin;
	String systemPassword;
	String serverLogin;
	String serverPassword;
	//ServerStatus status = ServerStatus.ok;


	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getSystemLogin() {
		return systemLogin;
	}
	public void setSystemLogin(String systemLogin) {
		this.systemLogin = systemLogin;
	}

	public String getSystemPassword() {
		return systemPassword;
	}
	public void setSystemPassword(String systemPassword) {
		this.systemPassword = systemPassword;
	}

	public String getServerLogin() {
		return serverLogin;
	}
	public void setServerLogin(String serverLogin) {
		this.serverLogin = serverLogin;
	}

	public String getServerPassword() {
		return serverPassword;
	}
	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}

	//	@JsonIgnore
//	public ServerStatus getStatus() throws Exception {
//		if(this.getIp()!=null) {
////			try {
////				URL url = new URL("http://" + this.getIp());
////				URLConnection con = url.openConnection();
////				con.setConnectTimeout(500);
////				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
////				String inputLine;
////
////				while ((inputLine = in.readLine()) != null){
////					System.out.println(inputLine);
////					if (inputLine.contains("modulesMenu.jspx")) return ServerStatus.ok;
////				}
////				in.close();
////			} catch(SocketTimeoutException e){
////				return ServerStatus.offline;
////			}
//			String firstResponse = makeRequest("http://" + this.getIp(), new String[]{"modulesMenu.jspx"});
//			if(!firstResponse.equals("timeout")){
//				String secondUrl = firstResponse.substring(firstResponse.indexOf("URL=")+4, firstResponse.indexOf(".jspx")+5);
//				String secondResponse = makeRequest("http://" + this.getIp(),  null);
//				//if(secondResponse.contains("Ревизия"))
//
//				try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
//					webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//					webClient.getOptions().setThrowExceptionOnScriptError(false);
//					webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//					//HtmlPage myPage = ((HtmlPage) webClient.getPage("http://" + this.getIp()+secondUrl));
//					HtmlPage myPage = ((HtmlPage) webClient.getPage("http://" + this.getIp()+"/faces/muncontrol/pages/login.jspx"));
//					webClient.waitForBackgroundJavaScript(500);
//
//					String theContent = myPage.asXml();
//					System.out.println(theContent);
//				}
//
//
//				return ServerStatus.ok;
//			}
//		}
//		return ServerStatus.offline;
//	}
//
//	public String makeRequest(String urlString, String[] onlyWithContent) throws Exception{
//		StringBuilder result = new StringBuilder();
//		try {
//			URL url = new URL(urlString);
//			URLConnection con = url.openConnection();
//			con.setConnectTimeout(500);
//			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//			String inputLine;
//
//			if(onlyWithContent!=null){
//				while ((inputLine = in.readLine()) != null){
//					for(String content : onlyWithContent){
//						if(inputLine.contains(content))result.append(inputLine);
//					}
//				}
//			} else while ((inputLine = in.readLine()) != null) result.append(inputLine);
//
//			in.close();
//		} catch(SocketTimeoutException e){
//			return "timeout";
//		}
//		return result.toString();
//	}
//	public void setStatus(ServerStatus status) {
//		this.status = status;
//	}

	@JsonIgnore
	@Transient
	public SystemInfo getSystemInfo(){
		return new SystemInfo(this);
	}


	@JsonIgnore
	@Transient
	public LightObject getLight(){
		return new LightObject(this.id, this.name+" "+this.ip);
	}

	@JsonIgnore
	@Transient
	public String getUrl(){
		if(!this.getIp().isEmpty()) return "http://"+ this.getIp();
		else return "";
	}

}
