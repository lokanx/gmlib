package com.lokan.gmlib;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

class TUtils {
    
    //
    // Logger
    //
    
    /** Application logger. */
    private static final Logger  APP_LOG =  LogManager.getLogger(TUtils.class);
    
    
    //
    // Constants
    //
    
    private static final String LF = System.getProperty("line.separator");
    
    /* gmail username. */
    static final String GM_USERNAME = "com.lokan.gmlib.test.gm.username";
    /* gmail password. */
    static final String GM_PASSWORD = "com.lokan.gmlib.test.gm.password";
    /** proxy (host)name. */
    static final String PROXY_NAME = "com.lokan.gmlib.test.proxy.name";
    /** proxy port. */
    static final String PROXY_PORT = "com.lokan.gmlib.test.proxy.port";
    /** proxy username. */
    static final String PROXY_USERNAME = "com.lokan.gmlib.test.proxy.username";
    /** proxy password. */
    static final String PROXY_PASSWORD = "com.lokan.gmlib.test.proxy.password";
    
        
    //
    // Constructors
    //
    
    
    /**
     * Creates a new instance of <code>TUtils</code>.
     */
    private TUtils() {
        // Procied to prevent instansiations due its a util class with only statics visible 
    }    
    
    //
    // Methods
    //
    
    /**
     * Loads and returns properties file content as a property instance.
     * @param clazz  Class who classloader we will use for resource lookup.
     * @param name   Resource name.
     * @return Returns a properties instance populated with data from the properties file.
     * @throws IllegalArgumentException is thrown if file load files for some reason.
     */
    static Properties getProperties(Class clazz, String name) throws IllegalArgumentException {
        try {
            Properties result = new Properties();
            result.load(clazz.getResourceAsStream(name));        
            return result;
        } catch (Exception e) {
            createTemplate(clazz, name);
            throw new IllegalArgumentException("Failed load property file <a template has been created for you in src/test/resources directory>: " + name, e);
        }
    }
    
    static void createTemplate(Class clazz, String name) {
        name = name.replace('/', ' ').trim();
        File f2 = new File("src/test/resources/" + name);
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f2, "rw");
            raf.writeBytes("# gmail login information (mandatory)" + LF); 
            raf.writeBytes("com.lokan.gmlib.test.gm.username=<gm username goes here>" + LF);
            raf.writeBytes("com.lokan.gmlib.test.gm.password=<gm password goes here>" + LF);
            raf.writeBytes(LF);
            raf.writeBytes("# Proxy information, uncomment if you using a proxy (optional)" + LF);
            raf.writeBytes("#com.lokan.gmlib.test.proxy.name=proxy.company.com" + LF);
            raf.writeBytes("#com.lokan.gmlib.test.proxy.port=8080" + LF);
            raf.writeBytes(LF);
            raf.writeBytes("# Proxy login information, uncomment if proxy requires login (optional)" + LF); 
            raf.writeBytes("#com.lokan.gmlib.test.proxy.username=" + LF);
            raf.writeBytes("#com.lokan.gmlib.test.proxy.password=" + LF);

        } catch (Exception e) {
            APP_LOG.error("Failed create property file templace: " + name, e);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (Exception e) {
                    APP_LOG.error("Failed close file for some freaky reson.", e);
                }
                raf = null;
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println(TUtils.class.getSimpleName());
    }
    
    /**
     * Returns a proxy or null if no are configures.
     * @param properties Properties to instanciate from.
     * @return Returns proxy instance or null if no proxy are configured.
     */
    static Proxy getProxy(Properties properties) {
        String name = properties.getProperty(PROXY_NAME);
        String port = properties.getProperty(PROXY_PORT);
        String username = properties.getProperty(PROXY_USERNAME);
        String password = properties.getProperty(PROXY_PASSWORD);

        Proxy result = null;
        
        if (
        		name != null  &&  name.trim().length() > 0  &&  
        		port != null  &&  port.trim().length() > 0) {
        
	        if (username != null  &&  username.trim().length() > 0) {
	            result = new Proxy(name, Integer.parseInt(port));
	        } else {
	            result = new Proxy(name, Integer.parseInt(port), username, password);
	        }
        }
        
        return result;
    }
    
    /**
     * Returns session instance.
     * @param properties Properties to instanciate from.
     * @return Returns session instance.
     */
    static GMSession getSession(Properties properties) {
        String username = properties.getProperty(GM_USERNAME);
        String password = properties.getProperty(GM_PASSWORD);
        return new GMSession(username, password, getProxy(properties));
    }
    
    /**
     * Loads a properties file and instanceiates a GMSession from data.
     * @param clazz Class which class loader to use to load property file with.
     * @param name Property resource name.
     * @return Returns instanciated session instance.
     * @throws IllegalArgumentException is thrown if file load files for some reason.
     */
    static GMSession createSession(Class clazz, String name) throws IllegalArgumentException {
        Properties properties = getProperties(clazz, name);
        return getSession(properties);
    }
}
