package core.server.status;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import core.server.AutoupdateTimer;
import core.server.NetworkService;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 12.05.2017.
 */

@Service
public class StatusServiceImplGEO implements StatusService {

    @Autowired
    NetworkService networkService;

    @Autowired
    StatusDAO statusDAO;

    /** Update status of server if last update was more than interval minutes ago
     * 1) current server status is online, last status exists in DB -> update status time and return status
     * 2) current server status is offline, last status exists in DB -> close and return status
     * 3) current server status is online, last status doesn't exist in DB -> create and return new status
     * 4) current server status is offline, last status doesn't exist in DB -> return temp status
     * */
    public ServerStatusCached updateStatus(Server server){
        ServerStatusCached status = statusDAO.getLastStatus(server);
        if(status==null || (status !=null && isUpdateAvailable(status, AutoupdateTimer.updateInterval))){
            status = updateData(server, status);
        }
        if(status!=null){
            statusDAO.saveOrUpdate(status);
            return status;
        } else {
            // temp object, don't saveOrUpdate
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
            page = networkService.makeRequest(ip);
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


}
