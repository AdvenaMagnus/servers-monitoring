package tests_with_orm;

import core.server.entities.Server;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by Alexander on 01.07.2017.
 */

@Service
@Transactional
public class TestDAO {

    @Autowired
    SessionFactory sessionFactory;

    public void clear(){
        sessionFactory.getCurrentSession().clear();
    }

    public List getAll(Class cls){
        CriteriaBuilder builder =  sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Server> criteria = builder.createQuery(cls);
        Root<Server> contactRoot = criteria.from(cls);
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    public void delete(Object obj){
        sessionFactory.getCurrentSession().delete(obj);
    }

    public Object getById(Class cls, long id){
        return sessionFactory.getCurrentSession().get(cls, id);
    }

    public void saveOrUpdate(Object obj){
        sessionFactory.getCurrentSession().saveOrUpdate(obj);
    }

}
