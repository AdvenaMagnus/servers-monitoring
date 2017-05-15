package core.server;

import core.server.entities.Server;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Grey on 20.03.2017.
 */
@Service
@Transactional
@Qualifier("server_dao_persist")
public class ServerDAOHib implements ServerDAO{

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
		//notifyAboutUpdate(server);
		return server;
	}

	@Override
	public Server update(Server server) {
		sessionFactory.getCurrentSession().update(server);
		//if(emit)notifyAboutUpdate(server);
		return server;
	}

	@Override
	public boolean delete(Server server) {
		sessionFactory.getCurrentSession().delete(server);
		//notifyAboutDelete(server);
		return true;
	}

//	public SystemInfo getSystemInfo(Server server){
//		SystemInfo sysInfo = new SystemInfo(server);
//		if(sysInfo!=null){
//			if(sysInfo.getRevision()!=null && sysInfo.getRevisionDate()!=null) {
//				//TODO
////				server.setLastUpdateRevision(sysInfo.getRevision());
////				server.setLastUpdateRevisionDate(sysInfo.getRevisionDate());
////				server.setLastUpdateTime(sysInfo.getUpdateTime());
//			} else {
//				sysInfo = getSystemInfoSaved(server);
//				sysInfo.setStatus(ServerStatus.offline);
//			}
//			notifyStatus(sysInfo);
//		}
//		return sysInfo;
//	}
//
//	public SystemInfo getSystemInfoSaved(Server server){
//		SystemInfo sysInfo = new SystemInfo();
//		sysInfo.setServerId(server.getId());
//		//TODO
////		sysInfo.setRevision(server.getLastUpdateRevision());
////		sysInfo.setRevisionDate(server.getLastUpdateRevisionDate());
////		sysInfo.setUpdateTime(server.getLastUpdateTime());
//		return sysInfo;
//	}

}
