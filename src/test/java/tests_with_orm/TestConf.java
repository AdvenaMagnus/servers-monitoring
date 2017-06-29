package tests_with_orm;

import com.gargoylesoftware.htmlunit.util.Cookie;
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

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Properties;

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

}
