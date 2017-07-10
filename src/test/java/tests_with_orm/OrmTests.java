package tests_with_orm;

import configuration.HibernateConfig;
import controller.MainController;
import core.server.PingService;
import core.server.ServerDAO;
import core.server.StatusDAO;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.EntityType;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 28.06.2017.
 */

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes = {TestConf.class, HibernateConfig.class})
//@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrmTests {

	@Autowired
	public ServerDAO serverDAO;

	@Autowired
	public StatusDAO statusDAO;

	@Autowired
	public SessionFactory sessionFactory;

	@Autowired
	public TestDAO testDAO;

	@Autowired
	public MainController mainController;

	@Autowired
	public PingService pingService;

//	@After
//	//@Transactional(transactionManager = "transactionManager")
//	public void afterTests(){
//		System.out.println("Clear db");
//		//sessionFactory.getCurrentSession().clear();
//		//serverDAO.clear();
//
//		Session s = sessionFactory.openSession();
//		sessionFactory.getMetamodel().getEntities();
//		for(EntityType type : sessionFactory.getMetamodel().getEntities()){
//			s.beginTransaction();
//			for(Object ent : s.createCriteria(type.getJavaType()).list()){
//				//serverDAO.delete((Server) server);
//				s.delete(ent);
//			}
//			s.getTransaction().commit();
//		}
//
////		Session s = sessionFactory.openSession();
////		s.beginTransaction();
////		//List list = s.createSQLQuery("SHOW TABLES").list();
////		for(Object obj: s.createSQLQuery("SHOW TABLES").list()){
////			s.createSQLQuery("truncate table " + ((Object[])obj)[0].toString().toLowerCase()).executeUpdate();
////		}
////		//s.createSQLQuery("DROP ALL OBJECTS").executeUpdate();
////		s.getTransaction().commit();
//
////		s.beginTransaction();
////		for(Object server : s.createCriteria(Server.class).list()){
////			//serverDAO.delete((Server) server);
////			s.delete(server);
////		}
////		s.getTransaction().commit();
//	}




}
