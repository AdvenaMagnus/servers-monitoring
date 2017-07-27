package core.server;

import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by Grey on 20.03.2017.
 */
@Service
@Transactional
public class ServerDAOHib implements ServerDAO{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public List<Server> allServers() {
//		List<Server> result = (List<Server>) sessionFactory.getCurrentSession()
//						.createCriteria(Server.class).list();
		CriteriaBuilder builder =  sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Server> criteria = builder.createQuery(Server.class);
		Root<Server> contactRoot = criteria.from(Server.class);
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public Server serverById(long id) {
//		Server server = (Server) sessionFactory.getCurrentSession()
//						.createCriteria(Server.class).add(Restrictions.eq("id", id)).uniqueResult();
		return sessionFactory.getCurrentSession().get(Server.class, id);
	}

	@Override
	public void saveOrUpdate(Server server) {
		sessionFactory.getCurrentSession().saveOrUpdate(server);
	}

	@Override
	public void refresh(Server server) {
		 sessionFactory.getCurrentSession().refresh(server);
	}

//	@Override
//	public void clear() {
//		sessionFactory.getCurrentSession().clear();
//	}

	@Override
	public boolean delete(Server server) {
		sessionFactory.getCurrentSession().delete(server);
		//notifyAboutDelete(server);
		return true;
	}

	@Override
	public List<ServerStatusCached> getStatuses(Server server) {
		Criteria cr =  sessionFactory.getCurrentSession()
						.createCriteria(ServerStatusCached.class).add(Restrictions.eq("owner", server));
		return cr.list();
	}

	@Override
	public List<OnMaintenanceStatus> getMaintenanceStatuses(Server server) {
		Criteria cr =  sessionFactory.getCurrentSession()
						.createCriteria(OnMaintenanceStatus.class).add(Restrictions.eq("owner", server));
		return cr.list();
	}

}
