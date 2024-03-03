package com.hypergryph.arknights.auth;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.function.httpClient;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/user" })
public class user {
  private static String Key = "IxMMveJRWsxStJgX";

  @RequestMapping({ "/info/v1/need_cloud_auth" })
  public JSONObject need_cloud_auth() {
    JSONObject result = new JSONObject(true);
    result.put("status", Integer.valueOf(0));
    result.put("msg", "faq");
    return result;
  }

  @RequestMapping({ "/oauth2/v1/grant" })
  public JSONObject grant() {
    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(0));

    return result;
  }

  @RequestMapping({ "/v1/guestLogin" })
  public JSONObject GuestLogin() {
    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(6));
    result.put("message", "单机版禁止游客登录");
    return result;
  }

  @RequestMapping({ "/authenticateUserIdentity" })
  public JSONObject AuthenticateUserIdentity() {
    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(0));
    result.put("message", "OK");
    result.put("isMinor", Boolean.valueOf(false));
    return result;
  }

  @RequestMapping({ "/updateAgreement" })
  public JSONObject updateAgreement() {
    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(0));
    result.put("message", "OK");
    result.put("isMinor", Boolean.valueOf(false));
    return result;
  }

  @RequestMapping({ "/checkIdCard" })
  public JSONObject CheckIdCard() {
    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(0));
    result.put("message", "OK");
    result.put("isMinor", Boolean.valueOf(false));
    return result;
  }

  @RequestMapping({ "/sendSmsCode" })
  public JSONObject SendSmsCode(@RequestBody JSONObject JsonBody, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /v1/sendSmsCode");

    String account = JsonBody.getString("account");

    if (!ArknightsApplication.serverConfig.getJSONObject("server").getBooleanValue("captcha")) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(4));
      return jSONObject;
    }

    if (httpClient.sentSmsCode(account).getIntValue("code") == 200) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(0));
      return jSONObject;
    }

    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(4));
    return result;
  }

  @PostMapping(value = { "/register" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject Register(@RequestBody JSONObject JsonBody, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /user/register");

    String account = JsonBody.getString("account");
    String password = JsonBody.getString("password");
    String smsCode = JsonBody.getString("smsCode");

    String secret = DigestUtils.md5DigestAsHex((account + Key).getBytes());

    if (userDao.queryAccountByPhone(account).size() != 0) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(5));
      jSONObject.put("errMsg", "该用户已存在，请确认注册信息");
      return jSONObject;
    }

    if (ArknightsApplication.serverConfig.getJSONObject("server").getBooleanValue("captcha") &&
        httpClient.verifySmsCode(account, smsCode).getIntValue("code") == 503) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(5));
      jSONObject.put("errMsg", "验证码错误");
      return jSONObject;
    }

    if (userDao.RegisterAccount(account, DigestUtils.md5DigestAsHex((password + Key).getBytes()), secret) != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(5));
      jSONObject.put("errMsg", "注册失败，未知错误");
      return jSONObject;
    }

    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(0));
    result.put("uid", Integer.valueOf(0));
    result.put("token", secret);
    result.put("isAuthenticate", Boolean.valueOf(true));
    result.put("isMinor", Boolean.valueOf(false));
    result.put("needAuthenticate", Boolean.valueOf(false));
    result.put("isLatestUserAgreement", Boolean.valueOf(true));
    return result;
  }

  @PostMapping(value = { "/login" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject Login(@RequestBody JSONObject JsonBody, HttpServletRequest request) {
    String account = JsonBody.getString("account");
    String password = JsonBody.getString("password");

    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /user/login");

    if (userDao.queryAccountByPhone(account).size() == 0) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(2));
      return jSONObject;
    }

    List<Account> accounts = userDao.LoginAccount(account, DigestUtils.md5DigestAsHex((password + Key).getBytes()));

    if (accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(1));
      return jSONObject;
    }

    JSONObject result = new JSONObject(true);
    result.put("result", Integer.valueOf(0));
    result.put("uid", Long.valueOf(((Account) accounts.get(0)).getUid()));
    result.put("token", ((Account) accounts.get(0)).getSecret());
    result.put("isAuthenticate", Boolean.valueOf(true));
    result.put("isMinor", Boolean.valueOf(false));
    result.put("needAuthenticate", Boolean.valueOf(false));
    result.put("isLatestUserAgreement", Boolean.valueOf(true));
    return result;
  }

  @PostMapping(value = { "/auth" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject Auth(@RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /user/auth");

    String secret = JsonBody.getString("token");

    if (secret == null && secret.length() < 0) {
      response.setStatus(400);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(400));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "invalid token");
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
    result.put("uid", uid);
    result.put("isMinor", Boolean.valueOf(false));
    result.put("isAuthenticate", Boolean.valueOf(true));
    result.put("isGuest", Boolean.valueOf(false));
    result.put("needAuthenticate", Boolean.valueOf(false));
    result.put("isLatestUserAgreement", Boolean.valueOf(true));
    return result;
  }
}