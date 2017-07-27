package tests_with_orm.daos.m_status_dao;

import configuration.HibernateConfig;
import core.server.entities.OnMaintenanceStatus;
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
    classes = {HibernateConfig.class, EmbeddedDBConf.class, DAOforTests.class, MStatusDAOimpl.class})
//@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class saveTests { //extends OrmTests {

    @Autowired
    MStatusDAO mStatusDAO;

    @Autowired
    DAOforTests daoforTests;

    @Test
    public  void saveNew(){
        Date curDate = new Date();
        OnMaintenanceStatus status = new OnMaintenanceStatus();
        status.setDateFrom(curDate);
        assertTrue(daoforTests.getAll(OnMaintenanceStatus.class).size()==0);

        mStatusDAO.saveOrUpdate(status);

        OnMaintenanceStatus statusFromDB = (OnMaintenanceStatus) daoforTests.getAll(OnMaintenanceStatus.class).get(0);
        assertTrue(statusFromDB.getDateFrom().getTime()==status.getDateFrom().getTime());
    }

    @Test
    public void saveExists(){
        Date curDate = new Date();
        OnMaintenanceStatus status = new OnMaintenanceStatus();
        status.setDateFrom(curDate);
        assertTrue(daoforTests.getAll(OnMaintenanceStatus.class).size()==0);
        mStatusDAO.saveOrUpdate(status);
        OnMaintenanceStatus statusFromDB1 = (OnMaintenanceStatus) daoforTests.getAll(OnMaintenanceStatus.class).get(0);
        Date curDate2 = new Date();
        statusFromDB1.setDateFrom(curDate2);

        mStatusDAO.saveOrUpdate(statusFromDB1);

        OnMaintenanceStatus statusFromDB2 = (OnMaintenanceStatus) daoforTests.getAll(OnMaintenanceStatus.class).get(0);
        assertTrue(daoforTests.getAll(OnMaintenanceStatus.class).size()==1);
        assertTrue(statusFromDB2.getDateFrom().getTime() == curDate2.getTime());
    }

}
