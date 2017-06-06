package core.server;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import core.enums.ServerStatus;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import core.utils.DateUtils;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 12.05.2017.
 */
@Transactional
@Service
public class StatusDAO {

    @Autowired
    @Qualifier("server_dao_persist")
    ServerDAO serverDAO;

    @Autowired
    SessionFactory sessionFactory;

    /** Update status of server if last update was more than interval minutes ago*/
    public ServerStatusCached updateStatus(Server server, int interval){
        ServerStatusCached status;
        if(server.getServerStatusCached()==null){
            status = new ServerStatusCached();
            server.setServerStatusCached(status);
            sessionFactory.getCurrentSession().persist(status);
        }
        else status = server.getServerStatusCached();

        if(isUpdateAvailable(status, interval))
            updateData(server.getIp(), status);
        //sessionFactory.getCurrentSession().update(status);
        serverDAO.update(server);
        return status;
    }

    /** Update specified status on specified ip-address*/
    private String[] updateData(String ip, ServerStatusCached status) {
        String[] revisionInfo = null;
        try {
            HtmlPage responsePage = makeRequest("http://"+ip);
            if(responsePage!=null){
                revisionInfo = getRevisionAndDate(responsePage);
                if(revisionInfo[0]!=null) status.setRevision(revisionInfo[0]);
                if(revisionInfo[1]!=null) status.setRevisionDate(DateUtils.parseDate(revisionInfo[1]));
//                DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
//                this.setUpdateTime(df.format(new Date()));
                status.setStatus(ServerStatus.online);
                setUpdateDate(status, new Date());
            }
            else status.setStatus(ServerStatus.offline);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("updateData exeption");
            status.setStatus(ServerStatus.offline);
        }
        return revisionInfo;
    }

    /** Parse revision and revision date in Array[revision, revision date] */
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

    /** Get page on specified url*/
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
            HtmlPage resultPage = webClient.getPage(url);
            //webClient.waitForBackgroundJavaScript(8000);
            return resultPage;
        }
    }

    /** Set current date and time of status*/
    private void setUpdateDate(ServerStatusCached status, Date date){
        Date currentDate = date;//new Date();
        status.setDate(currentDate);
        int[] minHour = DateUtils.getMinHour(currentDate);
        status.setMin(minHour[0]);
        status.setHours(minHour[1]);
    }

    /** Whether last update was more than *interval* minutes ago*/
    private boolean isUpdateAvailable(ServerStatusCached status, int interval){
        Date currentDate = new Date();
        if(status!=null && status.getDate()!=null && DateUtils.isSameDay(currentDate, status.getDate())){
            int[] currentMinHour = DateUtils.getMinHour(currentDate);
            if(currentMinHour[1] == status.getHours() && currentMinHour[0] - status.getMin() <= interval){
                return false;
            }
        }
        return true;
    }
}
