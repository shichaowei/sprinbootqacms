package com.fengdai.qa.service.impl;

import org.springframework.stereotype.Service;

import com.fengdai.qa.constants.ServerInfo;
import com.fengdai.qa.service.ChangeTimeService;
import com.fengdai.qa.utils.SshUtil;

@Service
public class ChangeTimeServiceImpl implements ChangeTimeService{


	@Override
	public void changeServerTime(String[] ipaddress,String cmd) {
			boolean flag=false;
			do {
				try {
					SshUtil.remoteRunCmd(ServerInfo.quartzIpadd, ServerInfo.sshname, ServerInfo.sshpwd,
							ServerInfo.stopquartzCmd);
					for (String ipaddres : ipaddress) {
						SshUtil.remoteRunCmd(ipaddres, ServerInfo.sshname, ServerInfo.sshpwd, cmd);
					}
					SshUtil.remoteRunCmd(ServerInfo.quartzIpadd, ServerInfo.sshname, ServerInfo.sshpwd,
							ServerInfo.restartquartzCmd);
					flag = true;
				} catch (Exception e) {
					flag = false;
				}
			} while (!flag);
	}


	@Override
	public void newchangeServerTime(String[] ipaddress, String cmd) {

		for (String ipaddres : ipaddress) {
			 SshUtil.remoteRunCmd(ipaddres, ServerInfo.sshname, ServerInfo.sshpwd, cmd);
		}

	}

	public static void main(String[] args) {
		String date ="2017/10/27";
		String time ="03:29:10";
		String cmd = "date -s '" + date + " " + time + "'";
		new ChangeTimeServiceImpl().changeServerTime(ServerInfo.changeDubbotimeIps,cmd );
	}




}
