package com.dsky.netty.pvpser.common;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class ConfigUtil {
    private static Map<String, String> configMap = new HashMap<String, String>();

    /**
     * 获取配置项
     *
     * @param key 配置项
     * @return 配置信息
     */
    public static String get(String key) {
        if(configMap.isEmpty()){
            init();
        }

        return configMap.containsKey(key) ? configMap.get(key) : "";
    }

    /**
     * 加载配置文件
     */
    public static void init() {
        configMap.clear();

        ResourceBundle bundle = ResourceBundle.getBundle("config");
        Set<String> keySet = bundle.keySet();

        for(String key : keySet){
            configMap.put(key, bundle.getString(key));
        }
    }
}
