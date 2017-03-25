package core.server;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Grey on 20.03.2017.
 */
@Service
@Transactional
@Qualifier("server_dao_persist")
public class ServerDAOHib implements ServerDAO{

	@Autowired
	@Qualifier("sseListToUpdate")
	List<SseEmitter> emittersUpdate;

	@Autowired
	@Qualifier("sseListToDelete")
	List<SseEmitter> emittersDelete;

	public void notifyAboutDelete(Server server){
		List<SseEmitter> toDelete = new ArrayList<>();
		for(SseEmitter emitter : emittersDelete){
			try {
				emitter.send(""+server.getId(), MediaType.TEXT_PLAIN);
				//emitter.send(server);
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println("emitter delete error");
				toDelete.add(emitter);
			}
		}
		emittersDelete.removeAll(toDelete);
	}

	public void notifyAboutUpdate(Server server){
		List<SseEmitter> toDelete = new ArrayList<>();
		for(SseEmitter emitter : emittersUpdate){
			try {
//				HashMap<String, Object> result = new HashMap<>();
//				result.put("id", server.getId());
//				result.put("server", server);
				emitter.send(server, MediaType.APPLICATION_JSON_UTF8);
				//emitter.send(server);
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println("emitter update error");
				toDelete.add(emitter);
			}
		}
		emittersUpdate.removeAll(toDelete);
	}

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public List<Server> allServers() {
		List<Server> result = (List<Server>) sessionFactory.getCurrentSession()
						.createCriteria(Server.class).list();
		return result;
	}

	@Override
	public Server serverById(long id) {
		Server server = (Server) sessionFactory.getCurrentSession()
						.createCriteria(Server.class).add(Restrictions.eq("id", id)).uniqueResult();
		return server;
	}

	@Override
	public Server createNew(Server server) {
		sessionFactory.getCurrentSession().persist(server);
//		try {
//			emitter.send("created new server: " + server.getIp() + " " + server.getIp(), MediaType.TEXT_PLAIN);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		//sendMsgByEmitters(ActionType.create, server);
		notifyAboutUpdate(server);
		return server;
	}

	@Override
	public Server update(Server server) {
		sessionFactory.getCurrentSession().update(server);
		notifyAboutUpdate(server);
		return server;
	}

	@Override
	public boolean delete(Server server) {
		//try {
			sessionFactory.getCurrentSession().delete(server);
//			emitter.send("deleted server: " + server.getIp() + " " + server.getIp(), MediaType.TEXT_PLAIN);
//		} catch (Exception e){
//			return false;
//		}
		notifyAboutDelete(server);
		return true;
	}

}
