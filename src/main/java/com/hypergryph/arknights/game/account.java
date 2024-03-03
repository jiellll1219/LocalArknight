package com.hypergryph.arknights.game;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/account" })
public class account {
  private static final Logger LOGGER = LogManager.getLogger();

  @PostMapping(value = { "/login" }, produces = { "application/json; charset=utf-8" })
  public JSONObject Login(@RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /account/login");

    String secret = JsonBody.getString("token");
    String assetsVersion = JsonBody.getString("assetsVersion");
    String clientVersion = JsonBody.getString("clientVersion");

    List<Account> Accounts = userDao.queryAccountBySecret(secret);
    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(2));
      jSONObject.put("error", "无法查询到此账户");
      return jSONObject;
    }

    Long uid = Long.valueOf(((Account) Accounts.get(0)).getUid());

    if (((Account) Accounts.get(0)).getBan() == 1L) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(1));
      jSONObject.put("error", "您已被此服务器封禁");
      return jSONObject;
    }

    if (!clientVersion.equals(ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("android")
        .getString("clientVersion"))) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(2));
      jSONObject.put("error", "客户端版本需要更新");
      return jSONObject;
    }

    if (!assetsVersion.equals(
        ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("android").getString("resVersion")))
      ;

    if (((Account) Accounts.get(0)).getUser().equals("{}")) {

      ArknightsApplication.DefaultSyncData.getJSONObject("status").put("registerTs",
          Long.valueOf((new Date()).getTime() / 1000L));
      ArknightsApplication.DefaultSyncData.getJSONObject("status").put("lastApAddTime",
          Long.valueOf((new Date()).getTime() / 1000L));

      userDao.setUserData(uid, ArknightsApplication.DefaultSyncData);
    }

    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(0));
    result.put("uid", uid);
    result.put("secret", secret);
    result.put("serviceLicenseVersion", Integer.valueOf(0));
    return result;
  }

  @PostMapping(value = { "/syncData" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject SyncData(@RequestHeader("secret") String secret, HttpServletResponse response,
      HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /account/syncData");

    if (!ArknightsApplication.enableServer) {
      response.setStatus(400);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(400));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "server is close");
      return jSONObject;
    }

    Long ts = Long.valueOf(ArknightsApplication.getTimestamp());

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

    UserSyncData.getJSONObject("status").put("lastOnlineTs", Long.valueOf((new Date()).getTime() / 1000L));
    UserSyncData.getJSONObject("status").put("lastRefreshTs", ts);

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(0));
    result.put("user", UserSyncData);

    result.put("ts", ts);
    return result;
  }

  @PostMapping(value = { "/syncStatus" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject SyncStatus(@RequestHeader("secret") String secret, HttpServletResponse response,
      HttpServletRequest request) {
    if (!ArknightsApplication.enableServer) {
      response.setStatus(400);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(400));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "server is close");
      return jSONObject;
    }

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

    UserSyncData.getJSONObject("status").put("lastOnlineTs", Long.valueOf((new Date()).getTime() / 1000L));
    UserSyncData.getJSONObject("status").put("lastRefreshTs", Long.valueOf(ArknightsApplication.getTimestamp()));
    UserSyncData.getJSONObject("pushFlags").put("hasGifts", Integer.valueOf(0));
    UserSyncData.getJSONObject("pushFlags").put("hasFriendRequest", Integer.valueOf(0));

    JSONArray listMailBox = JSONArray.parseArray(((Account) Accounts.get(0)).getMails());

    for (int i = 0; i < listMailBox.size(); i++) {
      if (listMailBox.getJSONObject(i).getIntValue("state") == 0) {
        if ((new Date()).getTime() / 1000L <= listMailBox.getJSONObject(i).getLongValue("expireAt")) {
          UserSyncData.getJSONObject("pushFlags").put("hasGifts", Integer.valueOf(1));
          break;
        }
        listMailBox.getJSONObject(i).put("remove", Integer.valueOf(1));
      }
    }

    JSONArray FriendRequest = JSONObject.parseObject(((Account) Accounts.get(0)).getFriend()).getJSONArray("request");

    if (FriendRequest.size() != 0) {
      UserSyncData.getJSONObject("pushFlags").put("hasFriendRequest", Integer.valueOf(1));
    }

    userDao.setMailsData(uid, listMailBox);
    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);

    JSONObject playerDataDelta = new JSONObject(true);
    JSONObject modified = new JSONObject(true);
    modified.put("status", UserSyncData.getJSONObject("status"));
    modified.put("gacha", UserSyncData.getJSONObject("gacha"));
    modified.put("inventory", UserSyncData.getJSONObject("inventory"));
    modified.put("pushFlags", UserSyncData.getJSONObject("pushFlags"));
    modified.put("consumable", UserSyncData.getJSONObject("consumable"));
    modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
    playerDataDelta.put("modified", modified);
    playerDataDelta.put("deleted", new JSONObject(true));
    result.put("playerDataDelta", playerDataDelta);
    JSONObject result_announcement = new JSONObject(true);
    result_announcement.put("4", ArknightsApplication.serverConfig.getJSONObject("announce").getJSONObject("status"));
    result.put("result", result_announcement);
    result.put("ts", Long.valueOf(ArknightsApplication.getTimestamp()));
    return result;
  }
}