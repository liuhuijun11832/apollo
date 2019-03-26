package com.ctrip.framework.apollo.biz.service;

import com.ctrip.framework.apollo.biz.entity.Cluster;
import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.core.ConfigConsts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AdminService {

  @Autowired
  private AppService appService;
  @Autowired
  private AppNamespaceService appNamespaceService;
  @Autowired
  private ClusterService clusterService;
  @Autowired
  private NamespaceService namespaceService;

  private final static Logger logger = LoggerFactory.getLogger(AdminService.class);

  @Transactional
  public App createNewApp(App app) {
    String createBy = app.getDataChangeCreatedBy();
    App createdApp = appService.save(app);

    String appId = createdApp.getAppId();
    //创建默认命名空间
    appNamespaceService.createDefaultAppNamespace(appId, createBy);
    //创建默认cluster
    clusterService.createDefaultCluster(appId, createBy);
    //创建claster默认命名空间
    namespaceService.instanceOfAppNamespaces(appId, ConfigConsts.CLUSTER_NAME_DEFAULT, createBy);

    return app;
  }

  @Transactional
  public void deleteApp(App app, String operator) {
    String appId = app.getAppId();

    logger.info("{} is deleting App:{}", operator, appId);

    List<Cluster> managedClusters = clusterService.findClusters(appId);

    // 1. delete clusters
    if (Objects.nonNull(managedClusters)) {
      for (Cluster cluster : managedClusters) {
        clusterService.delete(cluster.getId(), operator);
      }
    }

    // 2. delete appNamespace
    appNamespaceService.batchDelete(appId, operator);

    // 3. delete app
    appService.delete(app.getId(), operator);
  }
}
