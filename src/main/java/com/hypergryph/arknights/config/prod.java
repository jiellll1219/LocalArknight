package com.hypergryph.arknights.config;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.function.randomPwd;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/config/prod" })
public class prod {
  @RequestMapping({ "/official/refresh_config" })
  public JSONObject RefreshConfig() {
    ArknightsApplication.reloadServerConfig();
    JSONObject result = new JSONObject(true);
    result.put("statusCode", Integer.valueOf(200));
    return result;
  }

  @RequestMapping({ "/official/remote_config" })
  public JSONObject RemoteConfig(HttpServletRequest request) {
    return ArknightsApplication.serverConfig.getJSONObject("remote");
  }

  @RequestMapping({ "/official/network_config" })
  public JSONObject NetworkConfig(HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/official/network_config");

    JSONObject server_network = ArknightsApplication.serverConfig.getJSONObject("network");
    JSONObject network = new JSONObject(true);
    network.put("sign", server_network.getString("sign"));
    JSONObject content = new JSONObject(true);
    JSONObject configs = server_network.getJSONObject("configs");
    content.put("configVer", server_network.getString("configVer"));
    content.put("funcVer", server_network.getString("funcVer"));

    for (Map.Entry entry : configs.entrySet()) {
      JSONObject funcNetwork = configs.getJSONObject(entry.getKey().toString()).getJSONObject("network");
      for (Map.Entry funcNetworkEntry : funcNetwork.entrySet()) {
        String value = funcNetwork.getString(funcNetworkEntry.getKey().toString());
        funcNetwork.put(funcNetworkEntry.getKey().toString(),
            value.replace("{server}", ArknightsApplication.serverConfig.getJSONObject("server").getString("url")));
      }
    }

    content.put("configs", configs);
    network.put("content", content.toJSONString());
    return network;
  }

  @RequestMapping({ "/official/Android/version" })
  public JSONObject AndroidVersion(HttpServletRequest request) {
    JSONObject version = new JSONObject();
    version.put("resVersion", "22-02-18-07-51-58-" + randomPwd.randomHash(6));
    version.put("clientVersion",
        ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("android").getString("clientVersion"));
    return version;
  }

  @RequestMapping({ "/official/IOS/version" })
  public JSONObject IosVersion(HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/official/IOS/version");
    return ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("ios");
  }

  @RequestMapping({ "/b/remote_config" })
  public JSONObject BRemoteConfig(HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/b/remote_config");

    return ArknightsApplication.serverConfig.getJSONObject("remote");
  }

  @RequestMapping({ "/b/network_config" })
  public JSONObject BNetworkConfig(HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/b/network_config");
    return ArknightsApplication.serverConfig.getJSONObject("network");
  }

  @RequestMapping({ "/b/Android/version" })
  public JSONObject BAndroidVersion(HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/b/Android/version");
    return ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("android");
  }

  @RequestMapping({ "/announce_meta/Android/preannouncement.meta.json" })
  public JSONObject PreAnnouncement(HttpServletRequest request) {
    return ArknightsApplication.serverConfig.getJSONObject("announce").getJSONObject("preannouncement");
  }

  @RequestMapping({ "/announce_meta/Android/announcement.meta.json" })
  public JSONObject announcement(HttpServletRequest request) {
    return ArknightsApplication.serverConfig.getJSONObject("announce").getJSONObject("announcement");
  }

  @RequestMapping({ "/announce_meta/IOS/preannouncement.meta.json" })
  public JSONObject IOSPreAnnouncement(HttpServletRequest request) {
    return ArknightsApplication.serverConfig.getJSONObject("announce").getJSONObject("preannouncement");
  }

  @RequestMapping({ "/announce_meta/IOS/announcement.meta.json" })
  public JSONObject IOSannouncement(HttpServletRequest request) {
    return ArknightsApplication.serverConfig.getJSONObject("announce").getJSONObject("announcement");
  }
}