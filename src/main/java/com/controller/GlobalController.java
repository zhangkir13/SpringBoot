package com.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.annotation.MyLog;
import com.config.CustomerProperties;
import com.utils.LogUtil;

@Controller
public class GlobalController {
	
	@Autowired
	public CustomerProperties properties;
	
	private static Logger loggerXml = LogUtil.getLoggerByXml("default");
	@ResponseBody
	@RequestMapping("/start")
	@MyLog(value = "queryDays")
	public Map<String, Object> queryDays(){

		loggerXml.info("test");

		Map<String, Object> map = new HashMap<>();
		map.put("result", "success");
		return map;
	}
	
	@RequestMapping("/index.do")
	public String showIndex(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		
		return "index";
	}
}
