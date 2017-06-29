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

//    @Autowired
//    @Qualifier("sseListToUpdate")
//    List<SseEmitter> emittersUpdate;
//
//    @Autowired
//    @Qualifier("sseListToDelete")
//    List<SseEmitter> emittersDelete;
//
//    @Autowired
//    @Qualifier("sseListStatus")
//    List<SseEmitter> emittersStatus;
//
//    @Autowired
//    @Qualifier("sseListDetailInfo")
//    List<SseEmitter> emittersDetailInfo;

    public void notifyAboutDelete(Server server){
        List<SseEmitter> toDelete = new ArrayList<>();
        HashMap<String, Object> toSend = new HashMap<>();
        toSend.put("type", "delete");
        toSend.put("msg", server.getId());
        for(SseEmitter emitter : emitters){
            try {
                emitter.send(toSend, MediaType.APPLICATION_JSON_UTF8);
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

    public void notifyAboutUpdate(Server server){
        List<SseEmitter> toDelete = new ArrayList<>();
        HashMap<String, Object> toSend = new HashMap<>();
        toSend.put("type", "update");
        toSend.put("msg", server);
        for(SseEmitter emitter : emitters){
            try {
                emitter.send(toSend, MediaType.APPLICATION_JSON_UTF8);
                //emitter.send(server, MediaType.APPLICATION_JSON_UTF8);
                //emitter.send(server);
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("emitter update error");
                toDelete.add(emitter);
            }
        }
        synchronized (emitters) {
            emitters.removeAll(toDelete);
        }
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
            List<SseEmitter> toDelete = new ArrayList<>();
            HashMap<String, Object> statusMsg = statusMsg(status);
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(statusMsg, MediaType.APPLICATION_JSON_UTF8);
                } catch (Exception e) {
                    System.out.println("emitter status error");
                    toDelete.add(emitter);
                }
            }
            synchronized (emitters) {
                emitters.removeAll(toDelete);
            }
        }
    }

    public void notifyDetailInfo(Server server){
        List<SseEmitter> toDelete = new ArrayList<>();
        HashMap<String, Object> detailInfoMsg = detailInfoMsg(server);
        for(SseEmitter emitter : emitters){
            try {
                emitter.send(detailInfoMsg, MediaType.APPLICATION_JSON_UTF8);
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("emitter detail info error");
                toDelete.add(emitter);
            }
        }
        synchronized (emitters) {
            emitters.removeAll(toDelete);
        }
    }

    public HashMap<String, Object> statusMsg(ServerStatusCached status){
        HashMap<String, Object> msg = new HashMap<>();
        msg.put("server_id", status.getOwner().getId());
        msg.put("server_status", status);

        HashMap<String, Object> toSend = new HashMap<>();
        toSend.put("type", "status");
        toSend.put("msg", msg);
        return toSend;
    }

    public HashMap<String, Object> detailInfoMsg(Server server){
        HashMap<String, Object> msg = new HashMap<>();
        msg.put("server_id", server.getId());
        msg.put("detailInfo", server.getDetailInfo());

        HashMap<String, Object> toSend = new HashMap<>();
        toSend.put("type", "detailinfo");
        toSend.put("msg", msg);
        return toSend;
    }

}
