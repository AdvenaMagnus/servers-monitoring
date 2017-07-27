package tests_with_orm.daos.m_status_dao;

import configuration.HibernateConfig;
import core.server.ServerDAO;
import core.server.ServerDAOHib;
import core.server.entities.OnMaintenanceStatus;
import core.server.entities.Server;
import core.server.m_status.MStatusDAO;
import core.server.m_status.MStatusDAOimpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tests_with_orm.DAOforTests;
import tests_with_orm.EmbeddedDBConf;

import java.util.Date;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Alexander on 27.07.2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class,
        classes = {HibernateConfig.class, EmbeddedDBConf.class, DAOforTests.class, MStatusDAOimpl.class, ServerDAOHib.class})
//@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class gettingLastStatusTests {

    @Autowired
    MStatusDAO mStatusDAO;

    @Autowired
    DAOforTests daoforTests;

    @Autowired
    ServerDAO serverDAO;

    @Test
    public void whenLastStatusHasNotDateTo(){
        Server server = new Server();
        //server.setId(1);
        serverDAO.saveOrUpdate(server);
        assertTrue(server.getId()>=0);

        OnMaintenanceStatus oldStatus = new OnMaintenanceStatus();
        oldStatus.setOwner(server);
        oldStatus.setDateFrom(new Date(new Date().getTime()-10*60*60*1000));
        oldStatus.setDateTo(new Date(new Date().getTime()-5*60*60*1000));
        mStatusDAO.saveOrUpdate(oldStatus);

        OnMaintenanceStatus newStatus = new OnMaintenanceStatus();
        newStatus.setOwner(server);
        newStatus.setDateFrom(new Date(new Date().getTime()-3*60*60*1000));
        mStatusDAO.saveOrUpdate(newStatus);
        assertTrue(daoforTests.getAll(OnMaintenanceStatus.class).size()==2);

        OnMaintenanceStatus lastStatus = mStatusDAO.getLast(server);

        assertTrue(lastStatus.getId()==newStatus.getId());
    }

    @Test
    public void whenLastStatusHasDateTo(){
        Server server = new Server();
        //server.setId(1);
        serverDAO.saveOrUpdate(server);
        assertTrue(server.getId()>=0);

        OnMaintenanceStatus oldStatus = new OnMaintenanceStatus();
        oldStatus.setOwner(server);
        oldStatus.setDateFrom(new Date(new Date().getTime()-10*60*60*1000));
        oldStatus.setDateTo(new Date(new Date().getTime()-5*60*60*1000));
        mStatusDAO.saveOrUpdate(oldStatus);

        OnMaintenanceStatus newStatus = new OnMaintenanceStatus();
        newStatus.setOwner(server);
        newStatus.setDateFrom(new Date(new Date().getTime()-3*60*60*1000));
        newStatus.setDateTo(new Date(new Date().getTime()-2*60*60*1000));
        mStatusDAO.saveOrUpdate(newStatus);
        assertTrue(daoforTests.getAll(OnMaintenanceStatus.class).size()==2);

        OnMaintenanceStatus lastStatus = mStatusDAO.getLast(server);

        assertTrue(lastStatus.getId()==newStatus.getId());
    }

}
