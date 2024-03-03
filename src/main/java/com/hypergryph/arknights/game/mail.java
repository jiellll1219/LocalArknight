package com.hypergryph.arknights.game;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.admin;
import com.hypergryph.arknights.core.dao.mailDao;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import com.hypergryph.arknights.core.pojo.Mail;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/mail" })
public class mail {
  @PostMapping(value = { "/getMetaInfoList" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject getMetaInfoList(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /mail/getMetaInfoList");

    if (!ArknightsApplication.enableServer) {
      response.setStatus(400);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(400));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "server is close");
      return jSONObject;
    }

    int from = JsonBody.getIntValue("from");

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

    JSONArray resultMail = new JSONArray();

    JSONArray listMailBox = JSONArray.parseArray(((Account) Accounts.get(0)).getMails());
    for (int i = 0; i < listMailBox.size(); i++) {
      if (mailDao.queryMailById(listMailBox.getJSONObject(i).getIntValue("mailId")).size() == 1) {
        if (from <= listMailBox.getJSONObject(i).getIntValue("expireAt") &&
            listMailBox.getJSONObject(i).getIntValue("remove") == 0) {
          resultMail.add(listMailBox.getJSONObject(i));
        }
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("result", resultMail);
    return result;
  }

  @PostMapping(value = { "/listMailBox" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject listMailBox(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /mail/listMailBox");

    if (!ArknightsApplication.enableServer) {
      response.setStatus(400);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(400));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "server is close");
      return jSONObject;
    }

    JSONArray sysMailIdList = JsonBody.getJSONArray("sysMailIdList");
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

    JSONArray mailList = new JSONArray();
    JSONArray listMail = JSONArray.parseArray(((Account) Accounts.get(0)).getMails());

    for (int i = 0; i < sysMailIdList.size(); i++) {
      List<Mail> mails = mailDao.queryMailById(sysMailIdList.getIntValue(i));
      if (mails.size() == 1) {
        JSONObject MailBox = (JSONObject) JSONObject.toJSON(mails.get(0));
        MailBox.put("items", JSONArray.parseArray(MailBox.getString("items")));
        for (int n = 0; n < listMail.size(); n++) {
          if (listMail.getJSONObject(n).getIntValue("mailId") == sysMailIdList.getIntValue(i)) {
            JSONObject Mail = listMail.getJSONObject(n);

            MailBox.put("mailId", Integer.valueOf(Mail.getIntValue("mailId")));
            MailBox.put("createAt", Integer.valueOf(Mail.getIntValue("createAt")));
            MailBox.put("expireAt", Integer.valueOf(Mail.getIntValue("expireAt")));
            MailBox.put("state", Integer.valueOf(Mail.getIntValue("state")));
            MailBox.put("type", Integer.valueOf(Mail.getIntValue("type")));
            MailBox.put("hasItem", Integer.valueOf(Mail.getIntValue("hasItem")));
          }
        }
        mailList.add(MailBox);
      }
    }
    JSONObject result = new JSONObject(true);
    result.put("mailList", mailList);
    return result;
  }

  @PostMapping(value = { "/removeAllReceivedMail" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject removeAllReceivedMail(@RequestHeader("secret") String secret, HttpServletResponse response,
      HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /mail/removeAllReceivedMail");

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

    JSONArray listMail = JSONArray.parseArray(((Account) Accounts.get(0)).getMails());

    for (int i = 0; i < listMail.size(); i++) {
      int state = listMail.getJSONObject(i).getIntValue("state");
      if (state == 1) {
        listMail.getJSONObject(i).put("remove", Integer.valueOf(1));
      }
    }

    userDao.setMailsData(uid, listMail);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("modified", new JSONObject(true));
    playerDataDelta.put("deleted", new JSONObject(true));
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/receiveMail" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject receiveMail(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /mail/receiveMail");

    if (!ArknightsApplication.enableServer) {
      response.setStatus(400);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(400));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "server is close");
      return jSONObject;
    }

    int mailId = JsonBody.getIntValue("mailId");

    List<Mail> mailList = mailDao.queryMailById(mailId);
    if (mailList.size() != 1) {
      response.setStatus(500);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(403));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "error");
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

    JSONArray listMail = JSONArray.parseArray(((Account) Accounts.get(0)).getMails());
    JSONArray items = new JSONArray();
    for (int n = 0; n < listMail.size(); n++) {
      if (listMail.getJSONObject(n).getIntValue("mailId") == mailId) {
        JSONArray mailItems = JSONArray.parseArray(((Mail) mailList.get(0)).getItems());
        for (int j = 0; j < mailItems.size(); j++) {

          String reward_id = mailItems.getJSONObject(j).getString("id");
          String reward_type = mailItems.getJSONObject(j).getString("type");
          int reward_count = mailItems.getJSONObject(j).getIntValue("count");

          admin.GM_GiveItem(UserSyncData, reward_id, reward_type, reward_count, items);
        }
      }
    }
    int i;
    for (i = 0; i < listMail.size(); i++) {
      if (listMail.getJSONObject(i).getIntValue("mailId") == mailId) {
        listMail.getJSONObject(i).put("state", Integer.valueOf(1));
        UserSyncData.getJSONObject("pushFlags").put("hasGifts", Integer.valueOf(0));
        break;
      }
    }
    for (i = 0; i < listMail.size(); i++) {
      if (listMail.getJSONObject(i).getIntValue("state") == 0) {
        UserSyncData.getJSONObject("pushFlags").put("hasGifts", Integer.valueOf(1));
        break;
      }
    }
    userDao.setMailsData(uid, listMail);
    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);

    JSONObject playerDataDelta = new JSONObject(true);
    JSONObject modified = new JSONObject(true);

    modified.put("status", UserSyncData.getJSONObject("status"));
    modified.put("inventory", UserSyncData.getJSONObject("inventory"));
    modified.put("troop", UserSyncData.getJSONObject("troop"));
    modified.put("skin", UserSyncData.getJSONObject("skin"));
    modified.put("pushFlags", UserSyncData.getJSONObject("pushFlags"));
    playerDataDelta.put("deleted", new JSONObject(true));
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    result.put("items", items);
    result.put("result", Integer.valueOf(0));
    return result;
  }

  @PostMapping(value = { "/receiveAllMail" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject receiveAllMail(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /mail/receiveAllMail");

    if (!ArknightsApplication.enableServer) {
      response.setStatus(400);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(400));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "server is close");
      return jSONObject;
    }

    JSONArray sysMailIdList = JsonBody.getJSONArray("sysMailIdList");

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
    JSONArray listMail = JSONArray.parseArray(((Account) Accounts.get(0)).getMails());

    JSONArray items = new JSONArray();
    for (int n = 0; n < sysMailIdList.size(); n++) {
      JSONArray mailItems;
      int mailId = sysMailIdList.getIntValue(n);
      List<Mail> mailList = mailDao.queryMailById(mailId);

      if (mailList.size() != 1) {
        mailItems = new JSONArray();
      } else {
        mailItems = JSONArray.parseArray(((Mail) mailList.get(0)).getItems());
      }

      for (int j = 0; j < mailItems.size(); j++) {

        String reward_id = mailItems.getJSONObject(j).getString("id");
        String reward_type = mailItems.getJSONObject(j).getString("type");
        int reward_count = mailItems.getJSONObject(j).getIntValue("count");

        admin.GM_GiveItem(UserSyncData, reward_id, reward_type, reward_count, items);
      }
    }
    int i;
    for (i = 0; i < sysMailIdList.size(); i++) {
      int mailId = sysMailIdList.getIntValue(i);
      for (int j = 0; j < listMail.size(); j++) {
        if (listMail.getJSONObject(j).getIntValue("mailId") == mailId) {
          listMail.getJSONObject(j).put("state", Integer.valueOf(1));
          UserSyncData.getJSONObject("pushFlags").put("hasGifts", Integer.valueOf(0));

          break;
        }
      }
    }
    for (i = 0; i < listMail.size(); i++) {
      if (listMail.getJSONObject(i).getIntValue("state") == 0) {
        UserSyncData.getJSONObject("pushFlags").put("hasGifts", Integer.valueOf(1));
        break;
      }
    }
    userDao.setMailsData(uid, listMail);
    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);

    JSONObject playerDataDelta = new JSONObject(true);
    JSONObject modified = new JSONObject(true);

    modified.put("status", UserSyncData.getJSONObject("status"));
    modified.put("inventory", UserSyncData.getJSONObject("inventory"));
    modified.put("troop", UserSyncData.getJSONObject("troop"));
    modified.put("skin", UserSyncData.getJSONObject("skin"));
    modified.put("pushFlags", UserSyncData.getJSONObject("pushFlags"));
    playerDataDelta.put("deleted", new JSONObject(true));
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    result.put("items", items);
    result.put("result", Integer.valueOf(0));
    return result;
  }
}