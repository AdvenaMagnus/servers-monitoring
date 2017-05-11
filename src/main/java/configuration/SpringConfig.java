package configuration;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Grey on 17.03.2017.
 */

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"core", "configuration", "controller"})
@EnableTransactionManagement
public class SpringConfig extends WebMvcConfigurerAdapter{

	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		//viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("/resources/");
	}

	@Bean
	public DataSource getDataSource() {
		try {
			Properties props = serverProperties();

			DriverManagerDataSource ds = new DriverManagerDataSource();
			ds.setDriverClassName(props.getProperty("driver_class_name"));
			ds.setUrl(props.getProperty("url"));
			ds.setUsername(props.getProperty("username"));
			ds.setPassword(props.getProperty("password"));
			return ds;
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(getDataSource());
		sessionFactory.setPackagesToScan(new String[] {"core"});
		sessionFactory.setHibernateProperties(hibernateProperties());
		return sessionFactory;
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.hbm2ddl.auto", "update");
		return properties;
	}

	@Bean
	public Properties serverProperties() throws NamingException {
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");

		Properties properties = new Properties();
		properties.put("driver_class_name", envCtx.lookup("driver_class_name"));
		properties.put("url", envCtx.lookup("url"));
		properties.put("username", envCtx.lookup("username"));
		properties.put("password", envCtx.lookup("password"));
		return properties;

	}

	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager(
				sessionFactory);
		return transactionManager;
	}

	@Bean
	public SseEmitter sseEmitter(){
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		return emitter;
	}

	@Bean
	@Qualifier("sseListToUpdate")
	public List<SseEmitter> sseEmittersForUpdate(){
		List<SseEmitter> emitters = new ArrayList<>();
		return emitters;
	}

	@Bean
	@Qualifier("sseListToDelete")
	public List<SseEmitter> sseEmittersForDelete(){
		List<SseEmitter> emitters = new ArrayList<>();
		return emitters;
	}

	@Bean
	@Qualifier("sseListStatus")
	public List<SseEmitter> sseEmittersForStatuses(){
		List<SseEmitter> emitters = new ArrayList<>();
		return emitters;
	}

}
