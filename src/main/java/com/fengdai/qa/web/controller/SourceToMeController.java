package com.fengdai.qa.web.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fengdai.qa.constants.ServerInfo;
import com.fengdai.qa.utils.SshUtil;
/**
 *
 *
 *
 * @author hzweisc
 *
 */
@Controller
public class SourceToMeController {
	private static final Logger logger = LoggerFactory.getLogger(SourceToMeController.class);




	@RequestMapping({ "api/sourcetome" })
	public void sourcetome(@RequestParam("jenkinstype") String jenkinstype,HttpServletRequest request, HttpServletResponse res, ModelMap map) throws IOException {



		System.out.println("jenkinstype:"+jenkinstype);
		String mastertemp = "scp -r  /trdata/jobs/蜂贷3.0/jobs/fengdai_3.0_all/jobs/fengdai-common/workspace root@10.200.141.37:/root/fd-server";
		String onlinetemp = "scp -r /root/.jenkins/workspace/new_fengdai_git/fengdai-common-test root@10.200.141.37:/root/fd-server";
		if("master".equals(jenkinstype)) {
			SshUtil.remoteRunCmd("10.200.130.105", "root", "Jenkinstest@123098", mastertemp);
		}else {
			SshUtil.remoteRunCmd("10.200.141.78", "root", "Tairan@2017", onlinetemp);
		}
		SshUtil.remoteRunCmd("10.200.141.37", "root", "fdtest@123098", "cd /root/fd-server/workspace;zip -r -q  fdserver.zip *;");
		String fileName = "fdserver.zip";
		res.setHeader("content-type", "application/octet-stream");
		res.setContentType("application/octet-stream");
		res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		byte[] buff = new byte[1024];
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			os = res.getOutputStream();
			bis = new BufferedInputStream(new FileInputStream(new File(ServerInfo.downloadsourceDir + fileName)));
			int i = bis.read(buff);
			while (i != -1) {
				os.write(buff, 0, buff.length);
				os.flush();
				i = bis.read(buff);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("success");

		//			String yuancheng="curl -u hzweisc:111111 http://10.200.130.105:8080/job/蜂贷3.0/view/01_编译_打包-spring-test/job/fengdai-common/build?token=fengdai-common";
//			OkHttpUtil.get(URLEncoder.encode(yuancheng,"utf-8"));
//			given().auth().preemptive().basic("hzweisc", "111111").when().
//				get("http://10.200.130.105:8080/job/蜂贷3.0/view/01_编译_打包-spring-test/job/"+path+"/build?token="+path);
//			String var1 = path;
//			if(!var1.contains("trdata")&&!var1.contains("dubbo")&&!var1.contains("rest")&&!var1.contains("client")) {
//				var1="/trdata/jobs/蜂贷3.0/jobs/fengdai_3.0_all/jobs/"+var1+"/workspace";
//			}else {
//				var1="/trdata/jobs/蜂贷3.0/jobs/"+var1+"/workspace";
//
//			}
//			if (var1.matches(".*workspace$")) {
//				if (var1.contains("fengdai-")&&!var1.contains("deploy")) {
////					System.out.print("\""+var1+"\",");
////					String temp = "scp -r  " + var1 + " sshuser@"+ipaddress+":/D:/51/"+var1.replace("/trdata/jobs/蜂贷3.0/jobs/", "").replace("fengdai_3.0_all/jobs/", "").replace("workspace", "");
//					String temp = "scp -r  " + var1 + " sshuser@"+ipaddress+":/D:/51/"+var1.replace("/trdata/jobs/蜂贷3.0/jobs/", "").replace("fengdai_3.0_all/jobs/", "").replace("workspace", "");
//					SshUtil.remoteRunCmd("10.200.130.105", "root", "Jenkinstest@123098", temp);
//				}
//			}
	}

	@RequestMapping({ "sourcetome" })
	public String sourcetomePage(HttpServletRequest request, HttpServletResponse response, ModelMap map) {

		return "sourcetome";
	}


}
