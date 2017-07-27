package core.server.m_status;

import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
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
public class MStatusDAOimpl implements  MStatusDAO{

    @Autowired
    SessionFactory sessionFactory;

    public void saveOrUpdate(OnMaintenanceStatus status){
        sessionFactory.getCurrentSession().saveOrUpdate(status);
    }

    public OnMaintenanceStatus getLast(Server server){
        //TODO optimize
        Criteria cr =  sessionFactory.getCurrentSession()
                .createCriteria(OnMaintenanceStatus.class).add(Restrictions.eq("owner", server)).add(Restrictions.isNull("dateTo"));
        cr.setMaxResults(1);
        //List<ServerStatusCached>
        for(Object statusCached : cr.list()){
            return (OnMaintenanceStatus) statusCached;
        }

        Criteria cr2 =  sessionFactory.getCurrentSession()
                .createCriteria(OnMaintenanceStatus.class).add(Restrictions.eq("owner", server)).addOrder(Order.desc("dateTo"));
        cr2.setMaxResults(1);

        for(Object statusCached : cr2.list()){
            return (OnMaintenanceStatus) statusCached;
        }

        return null;
    }

}
