package core;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import core.enums.ServerStatus;
import core.server.Server;
import org.apache.commons.logging.LogFactory;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander Dublyanin on 18.03.2017.
 */
public class SystemInfo {

	long id;
	Server owner;
	ServerStatus status;
	String revision;
	String revisionDate;

	public SystemInfo(){
	}

	public SystemInfo(Server server){
		if(server!=null && server.getIp()!=null){
			updateData(server.getIp());
		}
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public ServerStatus getStatus() {
		return status;
	}
	public void setStatus(ServerStatus status) {
		this.status = status;
	}

	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getRevisionDate() {
		return revisionDate;
	}
	public void setRevisionDate(String revisionDate) {
		this.revisionDate = revisionDate;
	}

	private void updateData(String ip) {
		try {
			HtmlPage responsePage = makeRequest("http://"+ip);
			if(responsePage!=null){
				String[] revisionAndDate = getRevisionAndDate(responsePage);
				if(revisionAndDate[0]!=null)this.setRevision(revisionAndDate[0]);
				if(revisionAndDate[1]!=null)this.setRevisionDate(revisionAndDate[1]);
				this.setStatus(ServerStatus.online);
			}
			else this.setStatus(ServerStatus.offline);
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("updateData exeption");
			this.setStatus(ServerStatus.offline);
		}
	}

	private String[] getRevisionAndDate(HtmlPage page){
		//String[] result = new String[2];
		final String[] result = new String[2];
		page.getByXPath("//*[text()[contains(.,'ревизия')]]").forEach(elem -> {
			if(elem instanceof HtmlElement){
				String fullMessage = ((HtmlElement)elem).getFirstChild().asXml();
				Pattern patternRevision = Pattern.compile("(ревизия: )+([\\d]+)");
				Matcher matcher = patternRevision.matcher(fullMessage);
				while(matcher.find()) {
					result[0] = matcher.group(2);
				}
				//(дата: )+(([\d]+).([\d]+).([\d]+))
				//Pattern patternRevisionDate = Pattern.compile("(дата: )+([\\d]+).([\\d]+).([\\d]+)");
				Pattern patternRevisionDate = Pattern.compile("(дата: )+(([\\d]+).([\\d]+).([\\d]+))");
				Matcher matcher2 = patternRevisionDate.matcher(fullMessage);
				while(matcher2.find()) {
					result[1] = matcher2.group(2);//.replace(".", "/");
				}
			}
		});
		return result;
	}

	private HtmlPage makeRequest(String url) throws Exception {
		System.out.println("Current thread id " + Thread.currentThread().getId() + ", url:" + url);
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setTimeout(9000);
			//HtmlPage myPage = ((HtmlPage) webClient.getPage("http://" + this.getIp()+secondUrl));
			//HtmlPage resultPage = ((HtmlPage) webClient.getPage(url+"/faces/muncontrol/pages/login.jspx"));
			HtmlPage resultPage = ((HtmlPage) webClient.getPage(url));
			//webClient.waitForBackgroundJavaScript(8000);
			return resultPage;
		}
	}
}
