package core.server.status;

import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Alexander on 23.07.2017.
 */

@Service
@Transactional
public class StatusDAOimpl implements StatusDAO{

    @Autowired
    SessionFactory sessionFactory;

    public void saveOrUpdate(ServerStatusCached status){
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
}
