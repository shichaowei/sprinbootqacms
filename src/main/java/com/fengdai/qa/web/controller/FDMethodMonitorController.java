package com.fengdai.qa.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fengdai.qa.meta.BizMethodMonitor;
import com.fengdai.qa.meta.BizMonitorOper;
import com.fengdai.qa.service.FengdaiMonitorService;
import com.fengdai.qa.service.impl.RedisServiceImpl;
import com.fengdai.qa.utils.GetUserUtil;

@Controller
public class FDMethodMonitorController {


	private static final Logger logger = LoggerFactory.getLogger(FDMethodMonitorController.class);


	@Autowired
	public RedisServiceImpl redisServiceImpl;

	@Autowired
	public FengdaiMonitorService fengdaiMonitorServiceImpl;

	@Autowired
	private AmqpTemplate rabbitTemplate;

	/**
	 * 用于接收监控数据
	 * @param bizMethodMonitor
	 * @param request
	 * @param response
	 * @param map
	 */
	@RequestMapping({ "monitorprocess" })
	public void captureMethodMonitor(BizMethodMonitor bizMethodMonitor,HttpServletRequest request, HttpServletResponse response, ModelMap map)  {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws IOException {
				logger.debug("fdmonitorprocess is {}",ToStringBuilder.reflectionToString(bizMethodMonitor));
//				fengdaiMonitorServiceImpl.addmethodMonitor(bizMethodMonitor);
				rabbitTemplate.convertAndSend("monitor", bizMethodMonitor);
				return ToStringBuilder.reflectionToString(bizMethodMonitor);
			}
		});
		executor.execute(future);
		String result = null;
		try {
			result = future.get(5* 1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			result = null;
		}
		try {
			executor.shutdown();
			if (!executor.awaitTermination(3000, TimeUnit.MILLISECONDS)) {
				// 超时的时候向线程池中所有的线程发出中断(interrupted)。
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
			e.printStackTrace();
		}

	}

	/**
	 * 供monitor页面使用
	 * @param bizMonitorOper
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping({ "api/captureMonitor" })
	public String getBuzMonitor(BizMonitorOper bizMonitorOper,HttpServletRequest request, HttpServletResponse response, ModelMap map) throws UnsupportedEncodingException {
		String actiontype = bizMonitorOper.getActiontype();
		if("start".equals(actiontype)) {
			Date start=fengdaiMonitorServiceImpl.getDbtime();
			String username=GetUserUtil.getUserName(request);
			redisServiceImpl.set(username+"_start_monitor", start);
			map.addAttribute("resultmsg", "已经开启了捕获，关闭捕获后会展示出当前时间段截获到的sql");
			return "display";
		}
		if("stop".equals(actiontype)) {
			Date stoptime= fengdaiMonitorServiceImpl.getDbtime();
			String username=GetUserUtil.getUserName(request);
			Date starttime= (Date) redisServiceImpl.get(username+"_start_monitor");
			logger.debug("start:{},stop:{}",redisServiceImpl.get(username+"_start_monitor"),fengdaiMonitorServiceImpl.getDbtime());

			map.addAttribute("BusinessMonitors",fengdaiMonitorServiceImpl.getMethodMonitors(starttime, stoptime));
			return "display";
		}
		return "index";
	}



}
