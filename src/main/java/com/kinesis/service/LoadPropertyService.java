package com.kinesis.service;

public interface LoadPropertyService {
    /**
     * Check if the property file is loaded, returns true if it has been loaded.
     *
     * @return
     */
    public boolean isLoaded();


    /**
     * Loads and sets project properties as system properties from file and returns true if properties file was loaded successfully.
     *
     * @param filePath
     * @return
     */
    public boolean load(String filePath);

    /**
     * Returns the value of the property specified by propertyName. Returns default value if propertyName is not found.
     *
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public Object getProperty(String propertyName, String defaultValue);

    public boolean load();

    /**
     * Returns value of the system property if found null otherwise.
     *
     * @param propertyName
     * @return
     */
    public Object getProperty(String propertyName);
}
