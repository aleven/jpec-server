package it.attocchi.jpec.server.servlet;

import it.attocchi.jpa2.IJpaListernes;
import it.attocchi.jpec.server.bl.ConfigurazioneBL;
import it.attocchi.jpec.server.entities.ConfigurazionePec;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class StartupContext implements ServletContextListener {

	protected static final Logger logger = Logger.getLogger(ServletContextListener.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			String contextPath = sce.getServletContext().getContextPath();
			String contextRealPath = sce.getServletContext().getRealPath("/");
			logger.info("initializing Context: " + contextPath);
			logger.info("context real path: " + contextRealPath);
			
			EntityManagerFactory emf = (EntityManagerFactory) sce.getServletContext().getAttribute(IJpaListernes.APPLICATION_EMF);
			if (emf != null) {
				ConfigurazioneBL.initializeFromContextPath(emf, contextRealPath);
			} else {
				logger.warn("EntityManagerFactory per questo ServletContext non inizializzato");
			}
			
		} catch (Exception ex) {
			logger.error("Error in contextInitialized", ex);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("destroyed Context: " + sce.getServletContext().getContextPath());
	}

}
