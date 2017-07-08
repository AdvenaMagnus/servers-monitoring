package core.server;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import core.utils.DateUtils;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
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
    ServerDAO serverDAO;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    @Qualifier("sessionIds")
    HashMap<String, Cookie> sessionIds;

    @Bean
    @Qualifier("sessionIds")
    private HashMap<String, Cookie> getSessionsIds(){
        return new HashMap<String, Cookie>();
    }

    /** Update status of server if last update was more than interval minutes ago
     * 1) current server status is online, last status exists in DB -> update status time and return status
     * 2) current server status is offline, last status exists in DB -> close and return status
     * 3) current server status is online, last status doesn't exist in DB -> create and return new status
     * 4) current server status is offline, last status doesn't exist in DB -> return temp status
     * */
    public ServerStatusCached updateStatus(Server server){
        ServerStatusCached status = getLastStatus(server);
        if(status==null || (status !=null && isUpdateAvailable(status, AutoupdateTimer.updateInterval))){
            status = updateData(server, status);
        }
        if(status!=null){
            save(status);
            return status;
        } else {
            // temp object, don't save
            status = new ServerStatusCached();
            status.setIsClosed(true);
            status.setOwner(server);
            return status;
        }
        //serverDAO.update(server);
    }

    /** Update specified status on specified ip-address*/
    public ServerStatusCached updateData(Server server, ServerStatusCached lastStatus) {
        String[] revisionInfo = getRevisionAndDate("http://"+server.getIp());
        if(revisionInfo!=null){
            Date currentDate = DateUtils.getCurrentTime();
            ServerStatusCached status;
            if(lastStatus !=null && !lastStatus.getIsClosed()) status = lastStatus;
            else{
                status = new ServerStatusCached();
                status.setCreateDate(currentDate);
                status.setOwner(server);
                //server.getStatuses().add(status);
            }

            if(revisionInfo[0]!=null) status.setRevision(revisionInfo[0]);
            if(revisionInfo[1]!=null) status.setRevisionDate(DateUtils.parseDate(revisionInfo[1], DateUtils.revisionDateFormat));
            status.setDate(currentDate);
            return status;
        } else {
            if(lastStatus!=null) {
                lastStatus.setIsClosed(true);
                return lastStatus;
            }
        }
        return null;
    }

    /** Parse revision and revision date to Array[revision, revision date] */
    public String[] getRevisionAndDate(String ip){
        //String[] result = new String[2];
        HtmlPage page = null;
        try {
            page = makeRequest(ip);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("updateData exception");
        }
        if(page!=null) {
            final String[] result = new String[2];
            page.getByXPath("//*[text()[contains(.,'ревизия')]]").forEach(elem -> {
                if (elem instanceof HtmlElement) {
                    String fullMessage = ((HtmlElement) elem).getFirstChild().asXml();
                    Pattern patternRevision = Pattern.compile("(ревизия: )+([\\d]+)");
                    Matcher matcher = patternRevision.matcher(fullMessage);
                    while (matcher.find()) {
                        result[0] = matcher.group(2);
                    }
                    //(дата: )+(([\d]+).([\d]+).([\d]+))
                    //Pattern patternRevisionDate = Pattern.compile("(дата: )+([\\d]+).([\\d]+).([\\d]+)");
                    Pattern patternRevisionDate = Pattern.compile("(дата: )+(([\\d]+).([\\d]+).([\\d]+))");
                    Matcher matcher2 = patternRevisionDate.matcher(fullMessage);
                    while (matcher2.find()) {
                        result[1] = matcher2.group(2);//.replace(".", "/");
                    }
                }
            });
            return result;
        } else return null;
    }

    /** Get page on specified url*/
    public HtmlPage makeRequest(String url) throws Exception {
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
        }
    }

    /** Set date and time of status*/
    public void setUpdateDate(ServerStatusCached status, Date date){
        status.setDate(date);
//        int[] minHour = DateUtils.getMinHour(date);
//        status.setMin(minHour[0]);
//        status.setHours(minHour[1]);
    }

    public void setCreationDate(ServerStatusCached status, Date date){
        status.setCreateDate(date);
//        int[] minHour = DateUtils.getMinHour(date);
//        status.setCreateMin(minHour[0]);
//        status.setCreateHours(minHour[1]);
    }

    /** Whether last update was more than *interval* minutes ago*/
    public boolean isUpdateAvailable(ServerStatusCached status, int interval){
        Date currentDate = new Date();
        if(status!=null && status.getDate()!=null && DateUtils.isSameDay(currentDate, status.getDate())){
            int[] currentMinHour = DateUtils.getMinHour(currentDate);
            int[] statusMinHour = DateUtils.getMinHour(status.getDate());
            if(currentMinHour[1] == statusMinHour[1] && currentMinHour[0] - statusMinHour[0] <= interval){
                return false;
            }
        }
        return true;
    }

    public void save(ServerStatusCached status){
        sessionFactory.getCurrentSession().saveOrUpdate(status);
    }

    public ServerStatusCached getLastStatus(Server server){
        Criteria cr =  sessionFactory.getCurrentSession()
						.createCriteria(ServerStatusCached.class).add(Restrictions.eq("owner", server))
                .addOrder(Order.desc("date"));
        cr.setMaxResults(1);
        //List<ServerStatusCached>
        for(Object statusCached : cr.list()){
            return (ServerStatusCached) statusCached;
        }
        return null;
    }

    public void saveOnMaintenanceServer(OnMaintenanceStatus status){
        sessionFactory.getCurrentSession().saveOrUpdate(status);
    }

    public OnMaintenanceStatus getLastOnMaintenanceStatus(Server server){
        //TODO optimize
        Criteria cr =  sessionFactory.getCurrentSession()
                .createCriteria(OnMaintenanceStatus.class).add(Restrictions.eq("owner", server)).add(Restrictions.isNull("dateTo"));
        cr.setMaxResults(1);
        //List<ServerStatusCached>
        for(Object statusCached : cr.list()){
            return (OnMaintenanceStatus) statusCached;
        }

        Criteria cr2 =  sessionFactory.getCurrentSession()
                .createCriteria(OnMaintenanceStatus.class).add(Restrictions.eq("owner", server)).addOrder(Order.asc("dateTo"));
        cr2.setMaxResults(1);

        for(Object statusCached : cr2.list()){
            return (OnMaintenanceStatus) statusCached;
        }

        return null;
    }


}
