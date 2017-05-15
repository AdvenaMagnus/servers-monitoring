package controller;

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
        for(SseEmitter emitter : emitters){
            try {
                HashMap<String, Object> toSend = new HashMap<>();
                toSend.put("type", "delete");
                toSend.put("msg", server.getId());
                emitter.send(toSend, MediaType.APPLICATION_JSON_UTF8);
                //emitter.send(server);
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("emitter delete error");
                toDelete.add(emitter);
            }
        }
        emitters.removeAll(toDelete);
    }

    public void notifyAboutUpdate(Server server){
        List<SseEmitter> toDelete = new ArrayList<>();
        for(SseEmitter emitter : emitters){
            try {
                HashMap<String, Object> toSend = new HashMap<>();
                toSend.put("type", "update");
                toSend.put("msg", server);
                emitter.send(toSend, MediaType.APPLICATION_JSON_UTF8);
                //emitter.send(server, MediaType.APPLICATION_JSON_UTF8);
                //emitter.send(server);
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("emitter update error");
                toDelete.add(emitter);
            }
        }
        emitters.removeAll(toDelete);
    }

    public void notifyStatus(Server server){
        List<SseEmitter> toDelete = new ArrayList<>();
        for(SseEmitter emitter : emitters){
            try {
                HashMap<String, Object> msg = new HashMap<>();
                msg.put("server_id", server.getId());
                msg.put("server_status", server.getServerStatusCached());

                HashMap<String, Object> toSend = new HashMap<>();
                toSend.put("type", "status");
                toSend.put("msg", msg);
                emitter.send(toSend, MediaType.APPLICATION_JSON_UTF8);
            } catch (Exception e) {
                System.out.println("emitter status error");
                toDelete.add(emitter);
            }
        }
        emitters.removeAll(toDelete);
    }

    public void notifyDetailInfo(Server server){
        List<SseEmitter> toDelete = new ArrayList<>();
        for(SseEmitter emitter : emitters){
            try {
                HashMap<String, Object> msg = new HashMap<>();
                msg.put("server_id", server.getId());
                msg.put("detailInfo", server.getDetailInfo());

                HashMap<String, Object> toSend = new HashMap<>();
                toSend.put("type", "detailinfo");
                toSend.put("msg", msg);
                emitter.send(toSend, MediaType.APPLICATION_JSON_UTF8);
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("emitter detail info error");
                toDelete.add(emitter);
            }
        }
        emitters.removeAll(toDelete);
    }

}
