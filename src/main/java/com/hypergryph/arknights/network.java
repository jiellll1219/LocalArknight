package com.hypergryph.arknights;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class network {
  @RequestMapping({ "/" })
  public JSONObject network_config(HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/official/network_config");

    JSONObject server_network = ArknightsApplication.serverConfig.getJSONObject("network");
    JSONObject jSONObject1 = new JSONObject(true);
    jSONObject1.put("sign", server_network.getString("sign"));
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
    jSONObject1.put("content", content.toJSONString());
    return jSONObject1;
  }
}