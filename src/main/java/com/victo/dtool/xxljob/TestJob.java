package com.victo.dtool.xxljob;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@JobHandler(value = "testJob")
public class TestJob extends IJobHandler {


  @Override
  public ReturnT<String> execute(String param)  {
    log.info("TestJob param:{}, mills:{}", param, System.currentTimeMillis());
    return ReturnT.SUCCESS;
  }
}