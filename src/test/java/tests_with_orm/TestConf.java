package tests_with_orm;

import com.gargoylesoftware.htmlunit.util.Cookie;
import controller.MainController;
import controller.NotifyService;
import core.server.PingService;
import core.server.ServerDAO;
import core.server.ServerDAOHib;
import core.server.StatusDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Grey on 27.06.2017.
 */

@Configuration
@EnableTransactionManagement
public class TestConf {

	@Bean
	public ServerDAO getServerDaoForTest(){
		//return new ServerDAOimpl();
		return new ServerDAOHib();
	}

	@Bean
	public PingService getPingService(){
		return spy(PingService.class);
	}


	@Bean
	public StatusDAO getStatusDao(){
		return new StatusDAO();
	}

	@Bean
	@Qualifier("sessionIds")
	public  HashMap<String, Cookie> getSessionsIds(){
		return new HashMap<String, Cookie>();
	}

	@Bean
	@Qualifier("hibernate_properties")
	public Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		properties.put("hibernate.show_sql", "false");
		properties.put("hibernate.hbm2ddl.auto", "create-drop");
		return properties;
	}

	@Bean
	public DataSource getDataSource() {
		return new EmbeddedDatabaseBuilder()
						.setType(EmbeddedDatabaseType.H2)
						.setName("test_embedded_db")
						//.addScript("classpath:schema.sql")
						//.addScript("classpath:test-data.sql")
						.build();
	}

	@Bean
	public TestDAO getTestDAO(){
		return new TestDAO();
	}

	@Bean
	public MainController getMainController(){
		return new MainController();
	}

	@Bean
	public NotifyService getNotifyService(){
		NotifyService notifyService =  spy(NotifyService.class);
		//doNothing().when(notifyService).notifyStatus(any());
		return notifyService;
	}

	@Bean
	public SseEmitter getEmmitter(){
		return mock(SseEmitter.class);
	}

}
