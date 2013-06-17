package org.hibernate.envers.test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Base class for testing envers with Session when the same session and
 * auditReader must be used for the hole test.
 * 
 * @author Hern&aacute;n Chanfreau
 * 
 */
public abstract class AbstractOneSessionTest  {

	
	protected Configuration config;
	private SessionFactory sessionFactory;
	private Session session ;
	private AuditReader auditReader;
	
	
	@BeforeClass
    @Parameters("auditStrategy")
    public void init(@Optional String auditStrategy) throws URISyntaxException {
        config = new Configuration();
        URL url = Thread.currentThread().getContextClassLoader().getResource(getHibernateConfigurationFileName());
        config.configure(new File(url.toURI()));

        if (auditStrategy != null && !"".equals(auditStrategy)) {
            config.setProperty("org.hibernate.envers.audit_strategy", auditStrategy);
        }

        this.initMappings();
		
		sessionFactory = config.buildSessionFactory();
    }
	
	protected abstract void initMappings() throws MappingException, URISyntaxException ;

	protected String getHibernateConfigurationFileName(){
		return "hibernate.test.session-cfg.xml";
	}	


	private SessionFactory getSessionFactory(){
		return sessionFactory;
    }
	
	@AfterClass
	public void closeSessionFactory() {
	    sessionFactory.close();
	}
	
	/**
	 * Creates a new session and auditReader only if there is nothing created
	 * before
	 */
	@BeforeMethod
	public void initializeSession() {
		if (getSession() == null) {
		      session = getSessionFactory().openSession();
		      auditReader = AuditReaderFactory.get(session);
		}
	}

	/**
	 * Creates a new session and auditReader.
	 */
	public void forceNewSession() {
	      session = getSessionFactory().openSession();
	      auditReader = AuditReaderFactory.get(session);
	}	
	
	protected Session getSession() {
		return session;
	}



	protected AuditReader getAuditReader() {
		return auditReader;
	}
	


}
