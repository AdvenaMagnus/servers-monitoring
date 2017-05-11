package core.server;

import core.SystemInfo;
import core.enums.ServerStatus;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

	@Autowired
	@Qualifier("sseListStatus")
	List<SseEmitter> emittersStatus;

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

	public void notifyStatus(SystemInfo systemInfo){
		List<SseEmitter> toDelete = new ArrayList<>();
		for(SseEmitter emitter : emittersStatus){
			try {
				emitter.send(systemInfo, MediaType.APPLICATION_JSON_UTF8);
				//emitter.send(server);
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println("emitter status error");
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
		notifyAboutUpdate(server);
		return server;
	}

	@Override
	public Server update(Server server, boolean emit) {
		sessionFactory.getCurrentSession().update(server);
		if(emit)notifyAboutUpdate(server);
		return server;
	}

	@Override
	public boolean delete(Server server) {
		sessionFactory.getCurrentSession().delete(server);
		notifyAboutDelete(server);
		return true;
	}

	public SystemInfo getSystemInfo(Server server){
		SystemInfo sysInfo = new SystemInfo(server);
		if(sysInfo!=null){
			if(sysInfo.getRevision()!=null && sysInfo.getRevisionDate()!=null) {
				server.setLastUpdateRevision(sysInfo.getRevision());
				server.setLastUpdateRevisionDate(sysInfo.getRevisionDate());
				server.setLastUpdateTime(sysInfo.getUpdateTime());
			} else {
				sysInfo = getSystemInfoSaved(server);
				sysInfo.setStatus(ServerStatus.offline);
			}
			notifyStatus(sysInfo);
		}
		return sysInfo;
	}

	public SystemInfo getSystemInfoSaved(Server server){
		SystemInfo sysInfo = new SystemInfo();
		sysInfo.setServerId(server.getId());
		sysInfo.setRevision(server.getLastUpdateRevision());
		sysInfo.setRevisionDate(server.getLastUpdateRevisionDate());
		sysInfo.setUpdateTime(server.getLastUpdateTime());
		return sysInfo;
	}

}
