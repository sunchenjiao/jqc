package com.baidu.ecomqaep.schedule.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseConfiguration {

    public static Logger logger = LoggerFactory
            .getLogger(BaseConfiguration.class);

    @SuppressWarnings("rawtypes")
    protected static void autowareConfig(Class clazz, String cfgFileName) {
        Properties prop = new Properties();

        ClassLoader loader = BaseConfiguration.class.getClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }

        String propFile = cfgFileName;
        URL url = loader.getResource(propFile);

        try {
            boolean isFileExist = false;
            try {
                prop.load(url.openStream());
                isFileExist = true;
            } catch (Exception e) {
                logger.error("Error Happens while read Config File:"
                        + cfgFileName + ", user default value.");
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                        && field.isAnnotationPresent(ConfigInterface.class)) {
                    field.setAccessible(true);
                    ConfigInterface config = field.getAnnotation(ConfigInterface.class);
                    String name = config.name();
                    if (name.equals(ConfigInterface.DEFAULT_NAME))
                        name = field.getName();
                    String defaultValue = config.defaultValue();
                    String value = (isFileExist ? prop.getProperty(name,
                            defaultValue) : defaultValue);
                    if (null == value) {
                        continue;
                    } else {
                        Class<?> type = field.getType();
                        String typeName = type.getName();
                        try {
                            if (typeName.equals("int")) {
                                if (value.equals(ConfigInterface.DEFAULT_VALUE)) {
                                    value = "0";
                                }
                                field.set(clazz, Integer.valueOf(value));
                            } else if (typeName.equals("long")) {
                                if (value.equals(ConfigInterface.DEFAULT_VALUE)) {
                                    value = "0";
                                }
                                field.set(clazz, Long.valueOf(value));
                            } else if (typeName.equals("boolean")) {
                                if (value.equals(ConfigInterface.DEFAULT_VALUE)) {
                                    value = "false";
                                }
                                field.set(clazz, Boolean.valueOf(value));
                            } else if (typeName.equals("double")) {
                                if (value.equals(ConfigInterface.DEFAULT_VALUE)) {
                                    value = "0.0";
                                }
                                field.set(clazz, Double.valueOf(value));
                            } else {
                                field.set(clazz, value);
                            }
                        } catch (Exception e) {
                            logger.error("Error Happens while processing Config File:"
                                    + cfgFileName);
                            logger.error(String.format("invalid config: %s@%s",
                                    name, propFile), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error Happens while processing Config File:"
                    + cfgFileName);
            logger.error(e.getMessage(), e);
        }

    }
}
