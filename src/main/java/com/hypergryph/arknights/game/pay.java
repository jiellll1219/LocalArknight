package com.hypergryph.arknights.game;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.admin;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/pay" })
public class pay {
  @PostMapping(value = { "/getUnconfirmedOrderIdList" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject getUnconfirmedOrderIdList() {
    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("modified", new JSONObject(true));
    playerDataDelta.put("deleted", new JSONObject(true));
    result.put("playerDataDelta", playerDataDelta);
    result.put("orderIdList", new JSONArray());
    return result;
  }

  @PostMapping(value = { "/createOrder" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject createOrder(@RequestBody JSONObject json) {
    if (!ArknightsApplication.serverConfig.getJSONObject("shop").getBooleanValue("enableDiamondShop")) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("result", Integer.valueOf(114514));
      return jSONObject;
    }

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("modified", new JSONObject(true));
    playerDataDelta.put("deleted", new JSONObject(true));
    result.put("playerDataDelta", playerDataDelta);
    result.put("result", Integer.valueOf(0));
    result.put("orderId", json.getString("goodId"));
    result.put("orderIdList", new JSONArray());
    return result;
  }

  @PostMapping(value = { "/createOrderAlipay2" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject createOrderAlipay(@RequestBody JSONObject json, HttpServletResponse response) {
    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("modified", new JSONObject(true));
    playerDataDelta.put("deleted", new JSONObject(true));
    result.put("playerDataDelta", playerDataDelta);
    result.put("result", Integer.valueOf(0));
    result.put("orderId", json.getString("goodId"));
    result.put("price", Integer.valueOf(600));
    result.put("qs", "");
    result.put("pagePay", Boolean.valueOf(true));
    result.put("returnUrl", "");
    return result;
  }

  @PostMapping(value = { "/confirmOrderAlipay" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject confirmOrderAlipay() {
    JSONObject result = new JSONObject(true);
    result.put("status", Integer.valueOf(0));
    return result;
  }

  @PostMapping(value = { "/confirmOrder" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject confirmOrder(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response) {
    if (!ArknightsApplication.enableServer) {
      response.setStatus(400);
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("statusCode", Integer.valueOf(400));
      jSONObject.put("error", "Bad Request");
      jSONObject.put("message", "server is close");
      return jSONObject;
    }

    String goodId = JsonBody.getString("orderId");

    JSONArray items = new JSONArray();
    JSONObject receiveItems = new JSONObject(true);

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

    if (goodId.indexOf("CS") != -1) {

      JSONObject CashGood = new JSONObject();
      for (int i = 0; i < ArknightsApplication.CashGoodList.getJSONArray("goodList").size(); i++) {
        if (ArknightsApplication.CashGoodList.getJSONArray("goodList").getJSONObject(i).getString("goodId")
            .equals(goodId)) {
          CashGood = ArknightsApplication.CashGoodList.getJSONArray("goodList").getJSONObject(i);

          break;
        }
      }
      Boolean doubleCash = Boolean.valueOf(true);
      JSONArray info = UserSyncData.getJSONObject("shop").getJSONObject("CASH").getJSONArray("info");

      int diamondNum = 0;
      for (int j = 0; j < info.size(); j++) {
        String id = info.getJSONObject(j).getString("id");
        if (id.equals(goodId)) {
          doubleCash = Boolean.valueOf(false);

          break;
        }
      }
      if (doubleCash.booleanValue()) {
        JSONObject CS = new JSONObject(true);
        CS.put("id", goodId);
        CS.put("count", Integer.valueOf(1));
        info.add(CS);

        diamondNum = CashGood.getIntValue("doubleCount");
      } else {

        diamondNum = CashGood.getIntValue("diamondNum") + CashGood.getIntValue("plusNum");
      }

      UserSyncData.getJSONObject("status").put("androidDiamond",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + diamondNum));
      UserSyncData.getJSONObject("status").put("iosDiamond",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + diamondNum));

      JSONObject item = new JSONObject(true);
      item.put("id", "4002");
      item.put("type", "DIAMOND");
      item.put("count", Integer.valueOf(diamondNum));
      items.add(item);
    }

    if (goodId.indexOf("GP_") != -1) {

      JSONArray GPItems = new JSONArray();

      if (goodId.indexOf("GP_gW") != -1) {
        GPItems = ArknightsApplication.GPGoodList.getJSONObject("weeklyGroup").getJSONObject("packages")
            .getJSONObject(goodId).getJSONArray("items");
      }

      if (goodId.indexOf("GP_gM") != -1) {
        GPItems = ArknightsApplication.GPGoodList.getJSONObject("monthlyGroup").getJSONObject("packages")
            .getJSONObject(goodId).getJSONArray("items");
      }

      if (goodId.indexOf("GP_Once") != -1) {
        for (int j = 0; j < ArknightsApplication.GPGoodList.getJSONArray("oneTimeGP").size(); j++) {
          if (ArknightsApplication.GPGoodList.getJSONArray("oneTimeGP").getJSONObject(j).getString("goodId")
              .equals(goodId)) {
            GPItems = ArknightsApplication.GPGoodList.getJSONArray("oneTimeGP").getJSONObject(j).getJSONArray("items");

            break;
          }
        }
      }
      for (int i = 0; i < GPItems.size(); i++) {
        String reward_id = GPItems.getJSONObject(i).getString("id");
        String reward_type = GPItems.getJSONObject(i).getString("type");
        int reward_count = GPItems.getJSONObject(i).getIntValue("count");

        admin.GM_GiveItem(UserSyncData, reward_id, reward_type, reward_count, items);
      }
    }

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    JSONObject modified = new JSONObject(true);

    receiveItems.put("items", items);

    modified.put("status", UserSyncData.getJSONObject("status"));
    modified.put("shop", UserSyncData.getJSONObject("shop"));
    modified.put("troop", UserSyncData.getJSONObject("troop"));
    modified.put("skin", UserSyncData.getJSONObject("skin"));
    modified.put("inventory", UserSyncData.getJSONObject("inventory"));
    playerDataDelta.put("modified", modified);
    playerDataDelta.put("deleted", new JSONObject(true));
    result.put("playerDataDelta", playerDataDelta);
    result.put("result", Integer.valueOf(0));
    result.put("receiveItems", receiveItems);
    return result;
  }
}