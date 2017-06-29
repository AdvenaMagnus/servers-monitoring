package configuration;

import core.server.AutoupdateTimer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.logging.Level;


/**
 * Created by Grey on 17.03.2017.
 */

public class SpringWebInit implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext container) throws ServletException {
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(SpringConfig.class);
		ctx.register(HibernateConfig.class);
		ctx.setServletContext(container);

		ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", new DispatcherServlet(ctx));
		servlet.setAsyncSupported(true);

		servlet.setLoadOnStartup(1);
		servlet.addMapping("/");

		/**Start another thread that updates statuses of servers*/
		ctx.refresh();
		AutoupdateTimer timer = new AutoupdateTimer();
		ctx.getAutowireCapableBeanFactory().autowireBean(timer);
		Thread autoupdateServers = new Thread(timer);
		autoupdateServers.start();

		//java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
	}
}
