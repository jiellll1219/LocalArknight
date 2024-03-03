package com.hypergryph.arknights.auth;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/u8" })
public class u8 {
  @PostMapping(value = { "/user/v1/getToken" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject GetToken(@RequestBody JSONObject JsonBody, HttpServletResponse response,
      HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /u8/user/v1/getToken");

    String secret = JsonBody.getJSONObject("extension").getString("access_token");

    if (!ArknightsApplication.enableServer) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(2));
      jSONObject.put("error", ArknightsApplication.serverConfig.getJSONObject("server").getString("closeMessage"));
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

    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(0));
    result.put("uid", uid);
    result.put("error", "");
    result.put("extension", "{\"isGuest\":false}");
    result.put("channelUid", uid);
    result.put("token", secret);
    result.put("isGuest", Integer.valueOf(0));
    return result;
  }

  @PostMapping(value = { "/user/verifyAccount" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject VerifyAccount(@RequestBody JSONObject JsonBody, HttpServletResponse response,
      HttpServletRequest request) {
    String secret = JsonBody.getJSONObject("extension").getString("access_token");

    if (!ArknightsApplication.enableServer) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(2));
      jSONObject.put("error", ArknightsApplication.serverConfig.getJSONObject("server").getString("closeMessage"));
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

    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(0));
    result.put("uid", uid);
    result.put("error", "");
    result.put("extension", "{\"isGuest\":false}");
    result.put("channelUid", uid);
    result.put("token", secret);
    result.put("isGuest", Integer.valueOf(0));
    return result;
  }

  @RequestMapping({ "/pay/getAllProductList" })
  public JSONObject GetAllProductList(HttpServletResponse response, HttpServletRequest request) {
    if (!ArknightsApplication.enableServer) {
      response.setStatus(400);
      JSONObject result = new JSONObject(true);
      result.put("statusCode", Integer.valueOf(400));
      result.put("error", "Bad Request");
      result.put("message", "server is close");
      return result;
    }
    return ArknightsApplication.AllProductList;
  }

  @PostMapping(value = { "/pay/confirmOrderState" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject confirmOrderState(HttpServletResponse response, HttpServletRequest request) {
    if (!ArknightsApplication.enableServer) {
      response.setStatus(400);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(400));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "server is close");
      return jSONObject;
    }
    JSONObject result = new JSONObject(true);
    result.put("payState", Integer.valueOf(3));
    return result;
  }
}