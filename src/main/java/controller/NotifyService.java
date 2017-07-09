package controller;

import core.enums.ServerStatus;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alexander on 12.05.2017.
 */
@Service
public class NotifyService {

    @Autowired
    List<SseEmitter> emitters;

    public void notifyAboutDelete(Server server){
        sendMsg(createFullMessage("delete", server.getId()));
    }

    public void notifyAboutUpdate(Server server){
        sendMsg(createFullMessage("update", server));
    }

    /** Send SSE response with server's status or detail info (in case of notifyDetailInfo method).
     * Response is a jason object where:
     * {
     *   type: operation type,
     *   msg: {
     *      server_id: Server id,
     *      server_status: Servers's status object,
     *      }
     * }
     * **/
    public void notifyStatus(ServerStatusCached status){
        if(status!=null) {
            sendMsg(statusMsg(status));
        }
    }

    public void notifyDetailInfo(Server server){
        sendMsg(detailInfoMsg(server));
    }

    public HashMap<String, Object> statusMsg(ServerStatusCached status){
        HashMap<String, Object> msg = new HashMap<>();
        msg.put("server_id", status.getOwner().getId());
        msg.put("server_status", status);
        return createFullMessage("status", msg);
    }

    public HashMap<String, Object> detailInfoMsg(Server server){
        HashMap<String, Object> msg = new HashMap<>();
        msg.put("server_id", server.getId());
        msg.put("detailInfo", server.getDetailInfo());
        return createFullMessage("detailinfo", msg);
    }

    public void notifyPing(Server server, String ping){
        sendMsg(pingMsg(server, ping));
    }

    public HashMap<String, Object> pingMsg(Server server, String ping){
        HashMap<String, Object> msg = new HashMap<>();
        msg.put("server_id", server.getId());
        msg.put("ping", ping);
        return createFullMessage("ping", msg);
    }

    public HashMap<String, Object> createFullMessage(String type, Object msg){
        HashMap<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("msg", msg);
        return result;
    }

    public void sendMsg(Object msg){
        List<SseEmitter> toDelete = new ArrayList<>();
        for(SseEmitter emitter : emitters){
            try {
                emitter.send(msg, MediaType.APPLICATION_JSON_UTF8);
                //emitter.send(server);
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("emitter delete error");
                toDelete.add(emitter);
            }
        }
        synchronized (emitters) {
            emitters.removeAll(toDelete);
        }
    }

}
