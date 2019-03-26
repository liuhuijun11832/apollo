package com.ctrip.framework.apollo.portal.api;


import com.ctrip.framework.apollo.portal.component.RetryableRestTemplate;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class API {

  //API基类，声明一个封装好的重发restTemplate
  @Autowired
  protected RetryableRestTemplate restTemplate;

}
