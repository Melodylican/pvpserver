package com.dsky.netty.pvpser.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * 配置文件读取类
 *
 * @author chris.li
 */
public class ProReaderUtil {

    private static Logger logger = Logger.getLogger(ProReaderUtil.class.getName());

    private static ProReaderUtil instance = new ProReaderUtil();

    /**
     * 配置文件路径
     */
    private String confPath = "confFiles";

    /**
     * redis配置缓存
     */
    private HashMap<String, String> redisConf = null;

    public static ProReaderUtil getInstance() {
        return instance;
    }

    /**
     * 设置配置文件根路径
     *
     * @param path
     */
    public void setConfPath(String path) {
        confPath = path;
    }

    /**
     * 获取配置文件根路径
     *
     * @return
     */
    public String getConfPath() {
        return confPath;
    }

    /**
     * 读取配置文件内容
     *
     * @param file
     * @return
     */
    private Properties getPro(String file) {
        Properties properties = new Properties();
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(confPath + "/conf/" + file + ".properties");
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            logger.error(file + " config file not found.");
        }

        return properties;
    }

    /**
     * 获取log4j配置
     *
     * @return
     */
    public Properties getLog4jPro() {
        Properties properties = getPro("log4j");
        return properties;
    }

    /**
     * 获取redis配置
     *
     * @return
     */
    public HashMap<String, String> getRedisPro() {
        if (redisConf == null) {
            redisConf = new HashMap<String, String>();
            Properties properties = getPro("redis");
            redisConf.put("redis.host", properties.getProperty("redis.host").trim());
            redisConf.put("redis.port", properties.getProperty("redis.port").trim());
            redisConf.put("redis.maxTotal", properties.getProperty("redis.maxTotal").trim());
            redisConf.put("redis.maxIdle", properties.getProperty("redis.maxIdle").trim());
            redisConf.put("redis.timeOut", properties.getProperty("redis.timeOut").trim());
            redisConf.put("redis.retryNum", properties.getProperty("redis.retryNum").trim());
            redisConf.put("redis.pass", properties.getProperty("redis.pass").trim());
        }

        return redisConf;
    }

    /**
     * 获取netty配置
     *
     * @return
     */
    public HashMap<String, String> getNettyPro() {
        HashMap<String, String> conf = new HashMap<String, String>();
        Properties properties = getPro("netty");
        conf.put("netty.port", properties.getProperty("netty.port").trim());
        conf.put("netty.host", properties.getProperty("netty.host").trim());
        conf.put("netty.heartBeatTimeOut", properties.getProperty("netty.heartBeatTimeOut").trim());

        return conf;
    }
    
    public HashMap<String, String> getHeartbeat() {
        HashMap<String, String> conf = new HashMap<String, String>();
        Properties properties = getPro("heartbeat");
        conf.put("heartbeat.timeout", properties.getProperty("heartbeat.timeout").trim());
        conf.put("heartbeat.interval", properties.getProperty("heartbeat.interval").trim());

        return conf;
    }

    /**
     * 获取逻辑工作线程配置
     *
     * @return
     */
    public String getWorkersPro() {
        Properties properties = getPro("workers");
        return properties.getProperty("workers.member").trim();
    }

    /**
     * 获取jdbc配置
     *
     * @return
     */
    public HashMap<String, String> getJdbcPro() {
        HashMap<String, String> conf = new HashMap<String, String>();
        Properties properties = getPro("jdbc");
        conf.put("jdbc.driver", properties.getProperty("jdbc.driver").trim());
        conf.put("jdbc.url", properties.getProperty("jdbc.url").trim());
        conf.put("jdbc.userName", properties.getProperty("jdbc.userName").trim());
        conf.put("jdbc.password", properties.getProperty("jdbc.password").trim());
        conf.put("jdbc.initialSize", properties.getProperty("jdbc.initialSize").trim());
        conf.put("jdbc.maxTotal", properties.getProperty("jdbc.maxTotal").trim());
        conf.put("jdbc.maxConnLifetimeMillis", properties.getProperty("jdbc.maxConnLifetimeMillis").trim());
        conf.put("jdbc.maxIdle", properties.getProperty("jdbc.maxIdle").trim());
        conf.put("minIdle", properties.getProperty("jdbc.minIdle").trim());
        conf.put("maxWaitMillis", properties.getProperty("jdbc.maxWaitMillis").trim());
        return conf;
    }

    /**
     * 获取MyBatis配置
     *
     * @return
     * @throws Exception
     */
    public FileInputStream getMyBatisPro() throws Exception {
        return new FileInputStream(confPath + "/conf/mybatisConf.xml");
    }

    /**
     * 获取游戏业务模块配置
     *
     * @return
     * @throws Exception
     */
    public File getModulePro() throws Exception {
        return new File(confPath + "/conf/moduleConf.xml");
    }
}
