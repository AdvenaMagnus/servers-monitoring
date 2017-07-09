import controller.NotifyService;
import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.server.entities.ServerDetailInfo;
import core.server.entities.ServerStatusCached;
import core.utils.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by Alexander on 09.07.2017.
 */
public class NotifyServiceTest {

    NotifyService notifyService;
    Server server;
    ServerDetailInfo serverDetailInfo;
    ServerStatusCached serverStatusCached;
    OnMaintenanceStatus onMaintenanceStatus;

    String sysLogin = "syslogin";
    String sysPswd = "syspswd";

    String revision = "7001";
    String revisionDate = "06.11.2013";

    String ping = "7 мс";

    @Before
    public void init(){
        notifyService = spy(NotifyService.class);

        serverDetailInfo = new ServerDetailInfo();
        serverDetailInfo.setId(7);
        serverDetailInfo.setSystemLogin(sysLogin);
        serverDetailInfo.setSystemLogin(sysPswd);

        server = new Server();
        server.setId(1);
        server.setDetailInfo(serverDetailInfo);
        server.setInService(false);

        serverStatusCached = new ServerStatusCached();
        serverStatusCached.setId(12);
        serverStatusCached.setOwner(server);
        serverStatusCached.setRevision(revision);
        serverStatusCached.setRevisionDate(DateUtils.parseDate(revisionDate, DateUtils.revisionDateFormat));

        onMaintenanceStatus = new OnMaintenanceStatus();
        onMaintenanceStatus.setOwner(server);

    }


    @Test
    public void testDeleteNotifying(){
        HashMap<String, Object> msg = notifyService.createFullMessage("delete", server.getId());

        assertTrue(msg.get("type").equals("delete"));
        assertTrue((Long)msg.get("msg")==server.getId());
    }

    @Test
    public void testUpdateNotifying(){
        HashMap<String, Object> msg = notifyService.createFullMessage("update", server);

        assertTrue(msg.get("type").equals("update"));
        assertTrue(msg.get("msg")==server);
    }

    @Test
    public void testStatusNotifying(){
        HashMap<String, Object> msg = notifyService.statusMsg(serverStatusCached);

        assertTrue(msg.get("type").equals("status"));
        assertTrue(((Long)((HashMap)msg.get("msg")).get("server_id"))==server.getId());
        assertTrue(((HashMap)msg.get("msg")).get("server_status")==serverStatusCached);

    }

    @Test
    public void testDetailInfoNotifying(){
        HashMap<String, Object> msg = notifyService.detailInfoMsg(server);

        assertTrue(msg.get("type").equals("detailinfo"));
        assertTrue(((Long)((HashMap)msg.get("msg")).get("server_id"))==server.getId());
        assertTrue(((HashMap)msg.get("msg")).get("detailInfo")==serverDetailInfo);
    }

    @Test
    public void testPingNotifying(){
        HashMap<String, Object> msg = notifyService.pingMsg(server, ping);

        assertTrue(msg.get("type").equals("ping"));
        assertTrue(((Long)((HashMap)msg.get("msg")).get("server_id"))==server.getId());
        assertTrue(((String)((HashMap)msg.get("msg")).get("ping")).equals(ping));
    }



}
