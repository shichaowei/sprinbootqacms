package com.fengdai.qa.mq;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fengdai.qa.meta.BizMethodMonitor;
import com.fengdai.qa.service.FengdaiMonitorService;

@Component
@RabbitListener(queues = "monitor")
public class MonitorReceiver {

	private static final Logger logger = LoggerFactory.getLogger(MonitorReceiver.class);


	@Autowired
	public FengdaiMonitorService fengdaiMonitorServiceImpl;

    @RabbitHandler
    public void process(BizMethodMonitor bizMethodMonitor) {
        logger.info("Receiver  : " + ToStringBuilder.reflectionToString(bizMethodMonitor));
        fengdaiMonitorServiceImpl.addmethodMonitor(bizMethodMonitor);
    }

}