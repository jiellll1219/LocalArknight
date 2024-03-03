package com.hypergryph.arknights.game;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/story" })
public class story {
  @PostMapping(value = { "/finishStory" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject FinishStory(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /story/finishStory");

    if (!ArknightsApplication.enableServer) {
      response.setStatus(400);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(400));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "server is close");
      return jSONObject;
    }

    String storyId = JsonBody.getString("storyId");
    List<Account> Accounts = userDao.queryAccountBySecret(secret);
    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(2));
      jSONObject.put("error", "无法查询到此账户");
      return jSONObject;
    }

    Long uid = Long.valueOf(((Account) Accounts.get(0)).getUid());

    if (((Account) Accounts.get(0)).getBan() == 1L) {
      response.setStatus(500);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(403));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "error");
      return jSONObject;
    }
    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    UserSyncData.getJSONObject("status").getJSONObject("flags").put(storyId, Integer.valueOf(1));

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    JSONObject modified = new JSONObject(true);
    modified.put("status", UserSyncData.getJSONObject("status"));
    playerDataDelta.put("modified", modified);
    playerDataDelta.put("deleted", new JSONObject(true));
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }
}