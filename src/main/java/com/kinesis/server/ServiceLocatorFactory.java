package com.kinesis.server;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.web.context.ServletContextAware;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mohit.hurkat on 15-Nov-18.
 */
public class ServiceLocatorFactory implements ApplicationContextAware, ServletContextAware {

    /*private static Logger logger = LoggerFactory.getLogger(ServiceLocatorFactory.class);*/

    private static ApplicationContext applicationContext;
    @SuppressWarnings("unused")
    private static ServletContext servletContext;

    private static InitialContext context;

    static {
        try {
            context = new InitialContext();
        } catch (NamingException ex) {
            /*logger.error("Error in CTX lookup", ex);*/

        }
    }

    /**
     * Holds a class to object mapping. Yes it is synchronized. It needs to be. Note that charAt() is also synchronized,
     * so in the end we save a bunch of String manipulation by using this map. JL Modified this so that what is stored
     * in here is the service name, indexed on the class, rather than the service object itself. This is so that
     * non-singelton services work, rather than returning the same object each time. AH
     */
    @SuppressWarnings("unchecked")
    private static final Map nameMap = new ConcurrentHashMap();

    private static final String IMPLEMENTATION_SUFFIX = "Impl";

    /**
     * @return Returns the applicationContext.
     */
    public static synchronized ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    public synchronized void setApplicationContext(ApplicationContext appContext) {
        /*System.out.println("SETTING APP CTX");*/
        applicationContext = appContext;
    }

    @Override
    public synchronized void setServletContext(ServletContext servContext) {
        servletContext = servContext;
    }

    /**
     * Returns the service that is associated with the interface whose class you provide. For right now, that's going to
     * be the bean in the Spring context whose name is the class name with the first letter lowercased (e.g.
     * SecurityDAO.class looks up a bean called "securityDAO"). Changed this so that it just caches the name of the
     * service rather than the service itself.
     *
     * @param interfaceClass the class of the interface you wish to return.
     * @return the appropriate object, or null if it doesn't exist.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> interfaceClass) {
        if (interfaceClass == null) {
            return null;
        }

        String cachedServiceName = (String) nameMap.get(interfaceClass);

        if (cachedServiceName == null) {
            // This is the first time... so get the name then map the class to
            // the object:
            int servicePackageLength = interfaceClass.getPackage().getName().length() + 1;
            StringBuffer serviceName = new StringBuffer(interfaceClass.getName().substring(servicePackageLength));
            serviceName.setCharAt(0, Character.toLowerCase(serviceName.charAt(0)));
            serviceName.append(IMPLEMENTATION_SUFFIX);
            cachedServiceName = serviceName.toString();
            nameMap.put(interfaceClass, cachedServiceName);
        }

        return (T) getService(cachedServiceName);
    }

    /**
     * Returns the service corresponding to the name you dto. This is private now because we want to force people to
     * get beans according to the interface, and not let them ask for any old bean.
     *
     * @param serviceName the name of the service to obtain.
     * @return the appropriate service object, or null if none can be found.
     */
    public static Object getService(String serviceName) {
        try {
            if (applicationContext == null) {
                return null;
            }

            StringBuffer serviceNameToLookUp = new StringBuffer(serviceName);
            serviceNameToLookUp.setCharAt(0, Character.toLowerCase(serviceName.charAt(0)));

            Object o = applicationContext.getBean(serviceNameToLookUp.toString());
            return o;
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    public static Object getProperty(String key) {
        Properties props = applicationContext.getBean("hkEnvProps", Properties.class);
        return props.get(key);
    }


    public MessageSource getMessageSource() {
        return getApplicationContext();
    }

}
