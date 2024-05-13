package com.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.DeleteAction;
import org.apache.logging.log4j.core.appender.rolling.action.Duration;
import org.apache.logging.log4j.core.appender.rolling.action.IfFileName;
import org.apache.logging.log4j.core.appender.rolling.action.IfLastModified;
import org.apache.logging.log4j.core.appender.rolling.action.PathCondition;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.config.CustomerProperties;

@Component
public class LogUtil {
	
	private static String loggerDir;
	
	@Autowired
	public LogUtil(CustomerProperties properties) {
		LogUtil.loggerDir = properties.getLogDir();
	}

	private static final String patternStr = "[%d{yyyy-MM-dd HH:mm:ss}] %p %t %c - %m%n";

	private static final LoggerContext context = (LoggerContext) LogManager.getContext(false);
	private static final Configuration config = context.getConfiguration();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void start(String loggerName) {

		// 创建一个展示的样式：PatternLayout
		Layout layout = PatternLayout.newBuilder().withConfiguration(config).withPattern(patternStr).build();

		// 单个日志文件大小
		TimeBasedTriggeringPolicy tbtp = TimeBasedTriggeringPolicy.newBuilder().build();
		TriggeringPolicy tp = SizeBasedTriggeringPolicy.createPolicy("10M");
		CompositeTriggeringPolicy policyComposite = CompositeTriggeringPolicy.createPolicy(tbtp, tp);

		// 日志删除策略
		IfFileName ifFileName = IfFileName.createNameCondition(null, loggerName + "\\.\\d{4}-\\d{2}-\\d{2}.*");
		IfLastModified ifLastModified = IfLastModified.createAgeCondition(Duration.parse("1d"));
		DeleteAction deleteAction = DeleteAction
				.createDeleteAction(loggerDir, false, 1, false, null, new PathCondition[] { ifLastModified, ifFileName }, null, config);
		Action[] actions = new Action[] { deleteAction };

		DefaultRolloverStrategy strategy = DefaultRolloverStrategy.newBuilder()
				.withMin("1")
				.withMax("7")
				.withStopCustomActionsOnError(false)
				.withCustomActions(actions)
				.withConfig(config)
				.build();

		String loggerPathPrefix = loggerDir + File.separator + loggerName;
		
		AppenderRef ref = AppenderRef.createAppenderRef(loggerName, null, null);
		AppenderRef[] refs = new AppenderRef[] { ref };
		LoggerConfig loggerConfig = LoggerConfig.createLogger(
				true, //是否输出到控制台，true-输出，false-不输出
				Level.ALL, 
				loggerName, 
				"true", 
				refs, 
				null, 
				config, 
				null);
		
		//输出到文件
		RollingFileAppender appender = RollingFileAppender.newBuilder()
				.setConfiguration(config)
				.withFileName(loggerPathPrefix + ".log")
				.withFilePattern(loggerPathPrefix + ".%d{yyyy-MM-dd}.%i.log")
				.withAppend(true)
				.withStrategy(strategy)
				.withName(loggerName)
				.withPolicy(policyComposite)
				.withLayout(layout)
				.build();
		appender.start();
		config.addAppender(appender);
		loggerConfig.addAppender(appender, null, null);
		
		config.addLogger(loggerName, loggerConfig);

		context.updateLoggers(config);
	}

	public static void stop(String loggerName) {
		synchronized (config) {
			config.getAppender(loggerName).stop();
			config.getLoggerConfig(loggerName).removeAppender(loggerName);
			config.removeLogger(loggerName);
			context.updateLoggers();
		}
	}

	/** 获取自定义Logger */
	public static Logger getLogger(String loggerName) {
		synchronized (config) {
			if (!config.getLoggers().containsKey(loggerName)) {
				start(loggerName);
			}
		}
		return LogManager.getLogger(loggerName);
	}
	
	/** 获取全局定义Logger */
	public static Logger getLoggerByXml(String name) {  
        if(StringUtils.isEmpty(name)) {
        	name = "default";
        }
        return LogManager.getLogger(name);
    }

	public static void main(String[] args) throws IOException, InterruptedException {
		for (int i = 0; i < 10; i++) {	
			String name = "s" + String.valueOf(i);
			Logger logger = getLogger(name);
			logger.info("asdfasdf");
			stop(name);
		}
	}
}
