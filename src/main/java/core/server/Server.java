package core.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.LightObject;
import core.SystemInfo;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	@ManyToOne
	ServerDetailInfo detailInfo;
	String lastUpdateRevision;
	String lastUpdateRevisionDate;
	String lastUpdateTime;
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

	public String getLastUpdateRevision() {
		return lastUpdateRevision;
	}
	public void setLastUpdateRevision(String lastUpdateRevision) {
		this.lastUpdateRevision = lastUpdateRevision;
	}

	public String getLastUpdateRevisionDate() {
		return lastUpdateRevisionDate;
	}
	public void setLastUpdateRevisionDate(String lastUpdateRevisionDate) {
		this.lastUpdateRevisionDate = lastUpdateRevisionDate;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public ServerDetailInfo getDetailInfo() {
		return detailInfo;
	}
	public void setDetailInfo(ServerDetailInfo detailInfo) {
		this.detailInfo = detailInfo;
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

//	@JsonIgnore
//	@Transient
//	public SystemInfo getSystemInfo(){
//		SystemInfo sysInfo = new SystemInfo(this);
//		if(sysInfo!=null && sysInfo.getRevision()!=null && sysInfo.getRevisionDate()!=null){
//			this.setLastUpdateRevision(sysInfo.getRevision());
//			this.setLastUpdateRevisionDate(sysInfo.getRevisionDate());
//			DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
//			this.setLastUpdateTime(df.format(new Date()));
//		}
//		return sysInfo;
//	}
//
//	@JsonIgnore
//	@Transient
//	public SystemInfo getSystemInfoSaved(){
//		SystemInfo sysInfo = new SystemInfo();
//		sysInfo.setServerId(this.getId());
//		sysInfo.setRevision(this.getLastUpdateRevision());
//		sysInfo.setRevisionDate(this.getLastUpdateRevisionDate());
//		return sysInfo;
//	}




	@JsonIgnore
	@Transient
	public LightObject getLight(){
		return new LightObject(this.id, this.name+" "+this.ip);
	}

	@JsonIgnore
	@Transient
	public LightServer getLightServer(){
		return new LightServer(this);
	}

	@JsonIgnore
	@Transient
	public String getUrl(){
		if(!this.getIp().isEmpty()) return "http://"+ this.getIp();
		else return "";
	}

}
