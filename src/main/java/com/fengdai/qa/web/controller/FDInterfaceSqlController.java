package com.fengdai.qa.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 *
 * @author hzweisc
 *
 */

import com.alibaba.druid.pool.DruidDataSource;
import com.fengdai.qa.dao.admin.FengdaiSqlDao;
import com.fengdai.qa.meta.BizSqlOper;
import com.fengdai.qa.meta.FDSqlInfo;
import com.fengdai.qa.service.FengdaiSqlService;
import com.fengdai.qa.service.impl.RedisServiceImpl;
import com.fengdai.qa.utils.GetUserUtil;
import com.fengdai.qa.utils.LocalDateTimeUtils;
@Controller
public class FDInterfaceSqlController {

	private static final Logger logger = LoggerFactory.getLogger(FDInterfaceSqlController.class);

	@Autowired
	public FengdaiSqlService fengdaiSqlServiceImpl;

	@Autowired
	public RedisServiceImpl redisServiceImpl;





	@RequestMapping({ "api/captureSQL" })
	public String getBusinessSQL(BizSqlOper bizSqlOper,HttpServletRequest request, HttpServletResponse response, ModelMap map) throws UnsupportedEncodingException {
		String actiontype = bizSqlOper.getActiontype();
		if("start".equals(actiontype)) {
			Date start=fengdaiSqlServiceImpl.getDbtime();
			String username=GetUserUtil.getUserName(request);
			redisServiceImpl.set(username+"_start", start);
			map.addAttribute("resultmsg", "已经开启了捕获，关闭捕获后会展示出当前时间段截获到的sql");
			return "display";
		}
		if("stop".equals(actiontype)) {
			Date stoptime= fengdaiSqlServiceImpl.getDbtime();
			String username=GetUserUtil.getUserName(request);
			logger.debug("start:{},stop:{},mode:{}",redisServiceImpl.get(username+"_start"),fengdaiSqlServiceImpl.getDbtime(),bizSqlOper.getModelist());
			List<FengdaiSqlDao> BizSqlList = new ArrayList<>();
			for(String var: bizSqlOper.getModelist()) {
				BizSqlList.addAll( (Collection<? extends FengdaiSqlDao>) fengdaiSqlServiceImpl.getDbSQLs((Date) redisServiceImpl.get(username+"_start"),stoptime,"%"+var+"%"));
			}
			map.addAttribute("BusinessSQLs", BizSqlList);
			return "display";
		}
		return "index";

	}




	/**
	 * 超时3s后自动返回
	 * 由于需要记录当时的DB状况 同步请求 性能影响很严重
	 * 如果不需要记录同步请求，可以使用消息队列，减少性能问题（待定，消息队列存在时间上的问题）
	 * 记录反向SQL及执行SQL之前DB状态存在内存或者访问jdbc超时问题，默认不开启，业务代码启动参数增加hasResult参数开启
	 *
	 * @param fdSqlInfo
	 * @param request
	 * @param response
	 * @param map
	 */
	@RequestMapping({ "sqlprocess" })
	public void captureSQL(FDSqlInfo fdSqlInfo,HttpServletRequest request, HttpServletResponse response, ModelMap map) {
		if(fdSqlInfo.isHasResult()) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
				@Override
				public String call() throws  SQLException {


					DruidDataSource dataSource = new DruidDataSource();
					dataSource.setDriverClassName("com.mysql.jdbc.Driver");
					dataSource.setUsername(fdSqlInfo.getBusinessJdbcName());
					dataSource.setPassword(fdSqlInfo.getBusinessJdbcPassword());
					dataSource.setUrl(fdSqlInfo.getBusinessJdbcUrl());
					dataSource.setInitialSize(5);
					dataSource.setMinIdle(1);
					dataSource.setMaxActive(10); // 启用监控统计功能
					dataSource.setFilters("stat");// for mysql  dataSource.setPoolPreparedStatements(false);

//					org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = new org.springframework.jdbc.datasource.DriverManagerDataSource();
//					dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//					dataSource.setUrl(fdSqlInfo.getBusinessJdbcUrl());
//					dataSource.setUsername(fdSqlInfo.getBusinessJdbcName());
//					dataSource.setPassword(fdSqlInfo.getBusinessJdbcPassword());
					String sql=fdSqlInfo.getSqlcontent();

					String checkNumSQL="";
					String reverseresult="";
					if(sql.toLowerCase().indexOf("from") >0  && sql.trim().toLowerCase().matches("^select.*")){
						checkNumSQL="SELECT count(*) "+sql.substring(sql.toLowerCase().indexOf("from"));
					}
					if(sql.toLowerCase().indexOf("where") >0 && sql.trim().toLowerCase().matches("^update.*")){
						String tablename=sql.toLowerCase().split("update")[1].split("set")[0].trim();
						checkNumSQL="SELECT count(*) from "+tablename+" "+sql.substring(sql.toLowerCase().indexOf("where"));
					}
					if(sql.toLowerCase().indexOf("from") >0  && sql.trim().toLowerCase().matches("^delete.*")){
						checkNumSQL="SELECT count(*) "+sql.substring(sql.toLowerCase().indexOf("from"));
					}

					org.springframework.jdbc.core.JdbcTemplate jdbcTemplate = new org.springframework.jdbc.core.JdbcTemplate(dataSource);
					if (sql.trim().toLowerCase().matches("^select.*") || sql.trim().toLowerCase().matches("^update.*") ||sql.trim().toLowerCase().matches("^delete.*")) {
						Long sqlsize = (Long) jdbcTemplate.queryForMap(checkNumSQL).get("count(*)");
						String selectSQL=checkNumSQL.replace("count(*)", "*");
						if (sqlsize.longValue() > 1) {
							reverseresult = com.alibaba.fastjson.JSON.toJSONString(jdbcTemplate.queryForList(selectSQL));
						} else if (sqlsize == 1) {
							reverseresult = com.alibaba.fastjson.JSON.toJSONString(jdbcTemplate.queryForMap(selectSQL));
						}
					}
					if(sql.trim().toLowerCase().matches("^insert.*")){
						String deleteSQL="";
					}
					fdSqlInfo.setReverseresult(reverseresult);
					logger.debug("fdsqlinfo is {}",ToStringBuilder.reflectionToString(fdSqlInfo));
					fengdaiSqlServiceImpl.addSql(fdSqlInfo);
					System.out.println(LocalDateTimeUtils.formatTime(LocalDateTimeUtils.convertDateToLDT(fengdaiSqlServiceImpl.getDbtime()), "yyyy-MM-dd HH:mm:ss"));
					return ToStringBuilder.reflectionToString(fdSqlInfo);
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
		}else {

			ExecutorService executor = Executors.newSingleThreadExecutor();
			FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
				@Override
				public String call() throws IOException {
					logger.debug("fdsqlinfo is {}",ToStringBuilder.reflectionToString(fdSqlInfo));
					fengdaiSqlServiceImpl.addSql(fdSqlInfo);
					return ToStringBuilder.reflectionToString(fdSqlInfo);
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







	}

}
