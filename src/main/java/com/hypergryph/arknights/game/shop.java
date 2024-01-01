 package com.hypergryph.arknights.game;
 
 import com.alibaba.fastjson.JSONArray;
 import com.alibaba.fastjson.JSONObject;
 import com.hypergryph.arknights.ArknightsApplication;
 import com.hypergryph.arknights.admin;
 import com.hypergryph.arknights.core.dao.userDao;
 import com.hypergryph.arknights.core.pojo.Account;
 import java.util.Date;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestHeader;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;
 
 
 @RestController
 @RequestMapping({"/shop"})
 public class shop
 {
   @PostMapping(value = {"/getSkinGoodList"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject getSkinGoodList(@RequestBody JSONObject JsonBody) {
     JSONArray charIdList = JsonBody.getJSONArray("charIdList");
     
     JSONArray goodList = new JSONArray();
     if (charIdList.size() == 0) {
       return ArknightsApplication.skinGoodList;
     }
     for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)ArknightsApplication.skinTable.entrySet()) {
       String skinId = entry.getKey();
       if (skinId.indexOf(charIdList.getString(0)) != -1 && 
         skinId.indexOf("@") != -1) {
         
         JSONObject SkinData = ArknightsApplication.skinTable.getJSONObject(skinId);
         JSONObject SkinGood = new JSONObject(true);
         SkinGood.put("charId", SkinData.getString("charId"));
         SkinGood.put("skinId", SkinData.getString("skinId"));
         SkinGood.put("goodId", "SS_" + SkinData.getString("skinId"));
         SkinGood.put("slotId", Integer.valueOf(SkinData.getJSONObject("displaySkin").getIntValue("sortId")));
         SkinGood.put("skinName", SkinData.getJSONObject("displaySkin").getString("skinName"));
         SkinGood.put("discount", Integer.valueOf(0));
         SkinGood.put("originPrice", Integer.valueOf(18));
         SkinGood.put("price", Integer.valueOf(18));
         SkinGood.put("startDateTime", Integer.valueOf(-1));
         SkinGood.put("endDateTime", Integer.valueOf(-1));
         SkinGood.put("desc1", null);
         SkinGood.put("desc2", null);
         SkinGood.put("currencyUnit", "DIAMOND");
         
         goodList.add(SkinGood);
       } 
     } 
 
 
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("modified", new JSONObject(true));
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     result.put("goodList", goodList);
     return result;
   }
 
 
   
   @PostMapping(value = {"/buySkinGood"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject buySkinGood(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /shop/buySkinGood");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     String goodId = JsonBody.getString("goodId");
 
 
     
     List<Account> Accounts = userDao.queryAccountBySecret(secret);
     if (Accounts.size() != 1) {
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("result", Integer.valueOf(2));
       jSONObject.put("error", "无法查询到此账户");
       return jSONObject;
     } 
     
     Long uid = Long.valueOf(((Account)Accounts.get(0)).getUid());
     
     if (((Account)Accounts.get(0)).getBan() == 1L) {
       response.setStatus(500);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(403));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "error");
       return jSONObject;
     } 
 
     
     JSONObject UserSyncData = JSONObject.parseObject(((Account)Accounts.get(0)).getUser());
     
     UserSyncData.getJSONObject("skin").getJSONObject("characterSkins").put(goodId.substring(3), Integer.valueOf(1));
     UserSyncData.getJSONObject("skin").getJSONObject("skinTs").put(goodId.substring(3), Long.valueOf((new Date()).getTime() / 1000L));
     UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") - 18));
     UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") - 18));
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject status = new JSONObject(true);
     status.put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond")));
     status.put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond")));
     modified.put("skin", UserSyncData.getJSONObject("skin"));
     modified.put("status", status);
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("result", Integer.valueOf(0));
     return result;
   }
 
 
 
   
   @PostMapping(value = {"/buyLowGood"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject buyLowGood(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /shop/buyLowGood");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     String goodId = JsonBody.getString("goodId");
     int count = JsonBody.getIntValue("count");
 
 
     
     List<Account> Accounts = userDao.queryAccountBySecret(secret);
     if (Accounts.size() != 1) {
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("result", Integer.valueOf(2));
       jSONObject.put("error", "无法查询到此账户");
       return jSONObject;
     } 
     
     Long uid = Long.valueOf(((Account)Accounts.get(0)).getUid());
     
     if (((Account)Accounts.get(0)).getBan() == 1L) {
       response.setStatus(500);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(403));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "error");
       return jSONObject;
     } 
 
     
     JSONObject UserSyncData = JSONObject.parseObject(((Account)Accounts.get(0)).getUser());
     
     JSONArray items = new JSONArray();
     
     for (int i = 0; i < ArknightsApplication.LowGoodList.getJSONArray("goodList").size(); i++) {
       JSONObject lowGood = ArknightsApplication.LowGoodList.getJSONArray("goodList").getJSONObject(i);
       if (lowGood.getString("goodId").equals(goodId)) {
         UserSyncData.getJSONObject("status").put("lggShard", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("lggShard") - lowGood.getIntValue("price") * count));
         
         String reward_id = lowGood.getJSONObject("item").getString("id");
         String reward_type = lowGood.getJSONObject("item").getString("type");
         int reward_count = lowGood.getJSONObject("item").getIntValue("count") * count;
         admin.GM_GiveItem(UserSyncData, reward_id, reward_type, reward_count, items);
         
         break;
       } 
     } 
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("skin", UserSyncData.getJSONObject("skin"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     modified.put("shop", UserSyncData.getJSONObject("shop"));
     modified.put("troop", UserSyncData.getJSONObject("troop"));
     modified.put("skin", UserSyncData.getJSONObject("skin"));
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("items", items);
     result.put("result", Integer.valueOf(0));
     return result;
   }
 
 
   
   @PostMapping(value = {"/buyHighGood"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject buyHighGood(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /shop/buyHighGood");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     String goodId = JsonBody.getString("goodId");
     int count = JsonBody.getIntValue("count");
 
 
     
     List<Account> Accounts = userDao.queryAccountBySecret(secret);
     if (Accounts.size() != 1) {
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("result", Integer.valueOf(2));
       jSONObject.put("error", "无法查询到此账户");
       return jSONObject;
     } 
     
     Long uid = Long.valueOf(((Account)Accounts.get(0)).getUid());
     
     if (((Account)Accounts.get(0)).getBan() == 1L) {
       response.setStatus(500);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(403));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "error");
       return jSONObject;
     } 
 
     
     JSONObject UserSyncData = JSONObject.parseObject(((Account)Accounts.get(0)).getUser());
     
     JSONArray items = new JSONArray();
     
     for (int i = 0; i < ArknightsApplication.HighGoodList.getJSONArray("goodList").size(); i++) {
       JSONObject HighGood = ArknightsApplication.HighGoodList.getJSONArray("goodList").getJSONObject(i);
       if (HighGood.getString("goodId").equals(goodId)) {
         UserSyncData.getJSONObject("status").put("hggShard", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("hggShard") - HighGood.getIntValue("price") * count));
         
         String reward_id = HighGood.getJSONObject("item").getString("id");
         String reward_type = HighGood.getJSONObject("item").getString("type");
         int reward_count = HighGood.getJSONObject("item").getIntValue("count") * count;
         admin.GM_GiveItem(UserSyncData, reward_id, reward_type, reward_count, items);
         
         break;
       } 
     } 
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("skin", UserSyncData.getJSONObject("skin"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     modified.put("shop", UserSyncData.getJSONObject("shop"));
     modified.put("troop", UserSyncData.getJSONObject("troop"));
     modified.put("skin", UserSyncData.getJSONObject("skin"));
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("items", items);
     result.put("result", Integer.valueOf(0));
     return result;
   }
 
 
   
   @PostMapping(value = {"/buyExtraGood"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject buyExtraGood(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /shop/buyExtraGood");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     String goodId = JsonBody.getString("goodId");
     int count = JsonBody.getIntValue("count");
 
 
     
     List<Account> Accounts = userDao.queryAccountBySecret(secret);
     if (Accounts.size() != 1) {
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("result", Integer.valueOf(2));
       jSONObject.put("error", "无法查询到此账户");
       return jSONObject;
     } 
     
     Long uid = Long.valueOf(((Account)Accounts.get(0)).getUid());
     
     if (((Account)Accounts.get(0)).getBan() == 1L) {
       response.setStatus(500);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(403));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "error");
       return jSONObject;
     } 
 
     
     JSONObject UserSyncData = JSONObject.parseObject(((Account)Accounts.get(0)).getUser());
     
     JSONArray items = new JSONArray();
     
     for (int i = 0; i < ArknightsApplication.ExtraGoodList.getJSONArray("goodList").size(); i++) {
       JSONObject ExtraGood = ArknightsApplication.ExtraGoodList.getJSONArray("goodList").getJSONObject(i);
       if (ExtraGood.getString("goodId").equals(goodId)) {
         UserSyncData.getJSONObject("inventory").put("4006", Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("4006") - ExtraGood.getIntValue("price") * count));
         
         String reward_id = ExtraGood.getJSONObject("item").getString("id");
         String reward_type = ExtraGood.getJSONObject("item").getString("type");
         int reward_count = ExtraGood.getJSONObject("item").getIntValue("count") * count;
         admin.GM_GiveItem(UserSyncData, reward_id, reward_type, reward_count, items);
         
         break;
       } 
     } 
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("skin", UserSyncData.getJSONObject("skin"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     modified.put("shop", UserSyncData.getJSONObject("shop"));
     modified.put("troop", UserSyncData.getJSONObject("troop"));
     modified.put("skin", UserSyncData.getJSONObject("skin"));
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("items", items);
     result.put("result", Integer.valueOf(0));
     return result;
   }
 
 
 
   
   @PostMapping(value = {"/decomposePotentialItem"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject decomposePotentialItem(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /shop/decomposePotentialItem");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     JSONArray charInstIdList = JsonBody.getJSONArray("charInstIdList");
 
 
     
     List<Account> Accounts = userDao.queryAccountBySecret(secret);
     if (Accounts.size() != 1) {
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("result", Integer.valueOf(2));
       jSONObject.put("error", "无法查询到此账户");
       return jSONObject;
     } 
     
     Long uid = Long.valueOf(((Account)Accounts.get(0)).getUid());
     
     if (((Account)Accounts.get(0)).getBan() == 1L) {
       response.setStatus(500);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(403));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "error");
       return jSONObject;
     } 
 
     
     JSONObject UserSyncData = JSONObject.parseObject(((Account)Accounts.get(0)).getUser());
     
     JSONArray itemGet = new JSONArray();
     
     for (int i = 0; i < charInstIdList.size(); i++) {
       int lggShard = UserSyncData.getJSONObject("status").getIntValue("lggShard");
       int hggShard = UserSyncData.getJSONObject("status").getIntValue("hggShard");
       JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(charInstIdList.get(i)));
       String CharId = chars.getString("charId");
       int pcount = UserSyncData.getJSONObject("inventory").getIntValue("p_" + CharId);
       UserSyncData.getJSONObject("inventory").put("p_" + CharId, Integer.valueOf(0));
       int rarity = ArknightsApplication.characterJson.getJSONObject(CharId).getIntValue("rarity");
       JSONObject item = new JSONObject(true);
       if (rarity == 0) {
         item.put("type", "LGG_SHD");
         item.put("id", "4005");
         item.put("count", Integer.valueOf(pcount * 1));
         itemGet.add(item);
         UserSyncData.getJSONObject("status").put("lggShard", Integer.valueOf(lggShard + pcount * 1));
       } else if (rarity == 1) {
         item.put("type", "LGG_SHD");
         item.put("id", "4005");
         item.put("count", Integer.valueOf(pcount * 1));
         itemGet.add(item);
         UserSyncData.getJSONObject("status").put("lggShard", Integer.valueOf(lggShard + pcount * 1));
       } else if (rarity == 2) {
         item.put("type", "LGG_SHD");
         item.put("id", "4005");
         item.put("count", Integer.valueOf(pcount * 5));
         itemGet.add(item);
         UserSyncData.getJSONObject("status").put("lggShard", Integer.valueOf(lggShard + pcount * 5));
       } else if (rarity == 3) {
         item.put("type", "HGG_SHD");
         item.put("id", "4004");
         item.put("count", Integer.valueOf(pcount * 1));
         itemGet.add(item);
         UserSyncData.getJSONObject("status").put("hggShard", Integer.valueOf(hggShard + pcount * 1));
       } else if (rarity == 4) {
         item.put("type", "HGG_SHD");
         item.put("id", "4004");
         item.put("count", Integer.valueOf(pcount * 5));
         itemGet.add(item);
         UserSyncData.getJSONObject("status").put("hggShard", Integer.valueOf(hggShard + pcount * 5));
       } else if (rarity == 5) {
         item.put("type", "HGG_SHD");
         item.put("id", "4004");
         item.put("count", Integer.valueOf(pcount * 10));
         itemGet.add(item);
         UserSyncData.getJSONObject("status").put("hggShard", Integer.valueOf(hggShard + pcount * 10));
       } 
     } 
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject status = new JSONObject(true);
     status.put("lggShard", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("lggShard")));
     status.put("hggShard", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("hggShard")));
     modified.put("status", status);
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("items", itemGet);
     result.put("playerDataDelta", playerDataDelta);
     result.put("result", Integer.valueOf(0));
     return result;
   }
 
 
   
   @RequestMapping({"/getGoodPurchaseState"})
   public JSONObject getGoodPurchaseState() {
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("modified", new JSONObject(true));
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     result.put("result", new JSONObject(true));
     return result;
   }
 
   
   @RequestMapping({"/getCashGoodList"})
   public JSONObject getCashGoodList() {
     return ArknightsApplication.CashGoodList;
   }
 
   
   @RequestMapping({"/getGPGoodList"})
   public JSONObject getGPGoodList() {
     return ArknightsApplication.GPGoodList;
   }
 
   
   @RequestMapping({"/getLowGoodList"})
   public JSONObject getLowGoodList() {
     return ArknightsApplication.LowGoodList;
   }
 
   
   @RequestMapping({"/getHighGoodList"})
   public JSONObject getHighGoodList() {
     return ArknightsApplication.HighGoodList;
   }
 
   
   @RequestMapping({"/getExtraGoodList"})
   public JSONObject getExtraGoodList() {
     return ArknightsApplication.ExtraGoodList;
   }
 
   
   @RequestMapping({"/getLMTGSGoodList"})
   public JSONObject getLMTGSGoodList() {
     return ArknightsApplication.LMTGSGoodList;
   }
 
   
   @RequestMapping({"/getEPGSGoodList"})
   public JSONObject getEPGSGoodList() {
     return ArknightsApplication.EPGSGoodList;
   }
 
   
   @RequestMapping({"/getRepGoodList"})
   public JSONObject getRepGoodList() {
     return ArknightsApplication.RepGoodList;
   }
 
   
   @RequestMapping({"/getFurniGoodList"})
   public JSONObject getFurniGoodList() {
     return ArknightsApplication.FurniGoodList;
   }
 
   
   @RequestMapping({"/getSocialGoodList"})
   public JSONObject getSocialGoodList() {
     return ArknightsApplication.SocialGoodList;
   }
 }