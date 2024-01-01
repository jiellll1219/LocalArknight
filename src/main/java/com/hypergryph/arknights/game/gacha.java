package com.hypergryph.arknights.game;import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.file.IOTools;
import com.hypergryph.arknights.core.pojo.Account;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;@RestController
@RequestMapping({"/gacha"})
public class gacha
{
  @PostMapping({"/syncNormalGacha"})
  public JSONObject SyncNormalGacha(@RequestHeader("secret") String secret, HttpServletResponse response, HttpServletRequest request) {
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
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("recruit", UserSyncData.getJSONObject("recruit"));
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     return result;
  }  
  @PostMapping({"/normalGacha"})
  public JSONObject normalGacha(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /gacha/normalGacha");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
    } 
    
     String slotId = JsonBody.getString("slotId");
     JSONArray tagList = JsonBody.getJSONArray("tagList");    
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
    
     UserSyncData.getJSONObject("recruit").getJSONObject("normal").getJSONObject("slots").getJSONObject(String.valueOf(slotId)).put("state", Integer.valueOf(2));
    
     JSONArray selectTags = new JSONArray();
     for (int i = 0; i < tagList.size(); i++) {
       JSONObject selectTag = new JSONObject(true);
       selectTag.put("pick", Integer.valueOf(1));
       selectTag.put("tagId", Integer.valueOf(tagList.getIntValue(i)));
       selectTags.add(selectTag);
    } 
    
     UserSyncData.getJSONObject("recruit").getJSONObject("normal").getJSONObject("slots").getJSONObject(String.valueOf(slotId)).put("selectTags", selectTags);
     UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") - 1));
    
     userDao.setUserData(uid, UserSyncData);
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("recruit", UserSyncData.getJSONObject("recruit"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     return result;
  }  
  @PostMapping({"/finishNormalGacha"})
  public JSONObject finishNormalGacha(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /gacha/finishNormalGacha");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
    } 
    
     String slotId = JsonBody.getString("slotId");
    
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
    
     JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars");
     JSONObject buildingChars = UserSyncData.getJSONObject("building").getJSONObject("chars");
     JSONArray availCharInfo = ArknightsApplication.normalGachaData.getJSONObject("detailInfo").getJSONObject("availCharInfo").getJSONArray("perAvailList");    
     JSONArray randomRankArray = new JSONArray();
    
     for (int i = 0; i < availCharInfo.size(); i++) {
      
       int totalPercent = (int)(availCharInfo.getJSONObject(i).getFloat("totalPercent").floatValue() * 100.0F);
       int rarityRank = availCharInfo.getJSONObject(i).getIntValue("rarityRank");
      
       JSONObject randomRankObject = new JSONObject(true);
       randomRankObject.put("rarityRank", Integer.valueOf(rarityRank));
       randomRankObject.put("index", Integer.valueOf(i));      
       IntStream.range(0, totalPercent).forEach(n -> randomRankArray.add(randomRankObject));
    }     
     Collections.shuffle((List<?>)randomRankArray);    
     JSONObject randomRank = randomRankArray.getJSONObject((new Random()).nextInt(randomRankArray.size()));    
     JSONArray randomCharArray = availCharInfo.getJSONObject(randomRank.getIntValue("index")).getJSONArray("charIdList");    
     Collections.shuffle((List<?>)randomCharArray);    
     String randomCharId = randomCharArray.getString((new Random()).nextInt(randomCharArray.size()));
    
     int repeatCharId = 0;
    
     for (int j = 0; j < UserSyncData.getJSONObject("troop").getJSONObject("chars").size(); j++) {
       if (UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(j + 1)).getString("charId").equals(randomCharId)) {
         repeatCharId = j + 1;
        
        break;
      } 
    } 
     JSONArray itemGet = new JSONArray();
     int isNew = 0;
     int charinstId = repeatCharId;
     if (repeatCharId == 0) {      
       JSONObject char_data = new JSONObject(true);
      
       JSONArray skilsArray = ArknightsApplication.characterJson.getJSONObject(randomCharId).getJSONArray("skills");
       JSONArray skils = new JSONArray();
      
       for (int k = 0; k < skilsArray.size(); k++) {
         JSONObject new_skils = new JSONObject(true);
         new_skils.put("skillId", skilsArray.getJSONObject(k).getString("skillId"));
         new_skils.put("state", Integer.valueOf(0));
         new_skils.put("specializeLevel", Integer.valueOf(0));
         new_skils.put("completeUpgradeTime", Integer.valueOf(-1));
         if (skilsArray.getJSONObject(k).getJSONObject("unlockCond").getIntValue("phase") == 0) {
           new_skils.put("unlock", Integer.valueOf(1));
        } else {
           new_skils.put("unlock", Integer.valueOf(0));
        } 
         skils.add(new_skils);
      } 
      
       int instId = UserSyncData.getJSONObject("troop").getJSONObject("chars").size() + 1;
       charinstId = instId;
       char_data.put("instId", Integer.valueOf(instId));
       char_data.put("charId", randomCharId);
       char_data.put("favorPoint", Integer.valueOf(0));
       char_data.put("potentialRank", Integer.valueOf(0));
       char_data.put("mainSkillLvl", Integer.valueOf(1));
       char_data.put("skin", randomCharId + "#1");
       char_data.put("level", Integer.valueOf(1));
       char_data.put("exp", Integer.valueOf(0));
       char_data.put("evolvePhase", Integer.valueOf(0));
       char_data.put("gainTime", Long.valueOf((new Date()).getTime() / 1000L));
       char_data.put("skills", skils);
       char_data.put("equip", new JSONObject(true));
       char_data.put("voiceLan", ArknightsApplication.charwordTable.getJSONObject("charDefaultTypeDict").getString(randomCharId));
       if (skils == new JSONArray()) {
         char_data.put("defaultSkillIndex", Integer.valueOf(-1));
      } else {
         char_data.put("defaultSkillIndex", Integer.valueOf(0));
      } 
      
       String sub1 = randomCharId.substring(randomCharId.indexOf("_") + 1);
       String charName = sub1.substring(sub1.indexOf("_") + 1);
      
       if (ArknightsApplication.uniequipTable.containsKey("uniequip_001_" + charName)) {
         JSONObject equip = new JSONObject(true);
         JSONObject uniequip_001 = new JSONObject(true);
         uniequip_001.put("hide", Integer.valueOf(0));
         uniequip_001.put("locked", Integer.valueOf(0));
         uniequip_001.put("level", Integer.valueOf(1));
         JSONObject uniequip_002 = new JSONObject(true);
         uniequip_002.put("hide", Integer.valueOf(0));
         uniequip_002.put("locked", Integer.valueOf(0));
         uniequip_002.put("level", Integer.valueOf(1));
         equip.put("uniequip_001_" + charName, uniequip_001);
         equip.put("uniequip_002_" + charName, uniequip_002);
         char_data.put("equip", equip);
         char_data.put("currentEquip", "uniequip_001_" + charName);
      } else {
         char_data.put("currentEquip", null);
      } 
      
       UserSyncData.getJSONObject("troop").getJSONObject("chars").put(String.valueOf(instId), char_data);
      
       JSONObject charGroup = new JSONObject(true);
       charGroup.put("favorPoint", Integer.valueOf(0));
       UserSyncData.getJSONObject("troop").getJSONObject("charGroup").put(randomCharId, charGroup);
      
       JSONObject buildingChar = new JSONObject(true);
       buildingChar.put("charId", randomCharId);
       buildingChar.put("lastApAddTime", Long.valueOf((new Date()).getTime() / 1000L));
       buildingChar.put("ap", Integer.valueOf(8640000));
       buildingChar.put("roomSlotId", "");
       buildingChar.put("index", Integer.valueOf(-1));
       buildingChar.put("changeScale", Integer.valueOf(0));
       JSONObject bubble = new JSONObject(true);
       JSONObject normal = new JSONObject(true);
       normal.put("add", Integer.valueOf(-1));
       normal.put("ts", Integer.valueOf(0));
       bubble.put("normal", normal);
       JSONObject assist = new JSONObject(true);
       assist.put("add", Integer.valueOf(-1));
       assist.put("ts", Integer.valueOf(-1));
       bubble.put("assist", assist);
       buildingChar.put("bubble", bubble);
       buildingChar.put("workTime", Integer.valueOf(0));
      
       buildingChars.put(String.valueOf(instId), buildingChar);
       chars.put(String.valueOf(instId), char_data);
      
       JSONObject SHD = new JSONObject(true);
       SHD.put("type", "HGG_SHD");
       SHD.put("id", "4004");
       SHD.put("count", Integer.valueOf(1));
       itemGet.add(SHD);
      
       isNew = 1;
      
       UserSyncData.getJSONObject("status").put("hggShard", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("hggShard") + 1));
    }
    else {
      
       JSONObject repatChar = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(repeatCharId));
       int potentialRank = repatChar.getIntValue("potentialRank");
       int rarity = ArknightsApplication.characterJson.getJSONObject(randomCharId).getIntValue("rarity");
      
       String itemName = null;
       String itemType = null;
       String itemId = null;
      
       int itemCount = 0;
       if (rarity == 0) {
         itemName = "lggShard";
         itemType = "LGG_SHD";
         itemId = "4005";
         itemCount = 1;
      } 
       if (rarity == 1) {
         itemName = "lggShard";
         itemType = "LGG_SHD";
         itemId = "4005";
         itemCount = 1;
      } 
       if (rarity == 2) {
         itemName = "lggShard";
         itemType = "LGG_SHD";
         itemId = "4005";
         itemCount = 5;
      } 
       if (rarity == 3) {
         itemName = "lggShard";
         itemType = "LGG_SHD";
         itemId = "4005";
         itemCount = 30;
      } 
       if (rarity == 4) {
         itemName = "hggShard";
         itemType = "HGG_SHD";
         itemId = "4004";
         if (potentialRank != 5) {
           itemCount = 5;
        } else {
           itemCount = 8;
        } 
      } 
       if (rarity == 5) {
         itemName = "hggShard";
         itemType = "HGG_SHD";
         itemId = "4004";
         if (potentialRank != 5) {
           itemCount = 10;
        } else {
           itemCount = 15;
        } 
      } 
      
       JSONObject SHD = new JSONObject(true);
       SHD.put("type", itemType);
       SHD.put("id", itemId);
       SHD.put("count", Integer.valueOf(itemCount));
       itemGet.add(SHD);
      
       JSONObject potential = new JSONObject(true);
       potential.put("type", "MATERIAL");
       potential.put("id", "p_" + randomCharId);
       potential.put("count", Integer.valueOf(1));
       itemGet.add(potential);
      
       UserSyncData.getJSONObject("status").put(itemName, Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue(itemName) + itemCount));
      
       UserSyncData.getJSONObject("inventory").put("p_" + randomCharId, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("p_" + randomCharId) + 1));
      
       chars.put(String.valueOf(repeatCharId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(repeatCharId)));
    } 
     UserSyncData.getJSONObject("troop").put("chars", chars);    
     UserSyncData.getJSONObject("recruit").getJSONObject("normal").getJSONObject("slots").getJSONObject(String.valueOf(slotId)).put("state", Integer.valueOf(1));
     UserSyncData.getJSONObject("recruit").getJSONObject("normal").getJSONObject("slots").getJSONObject(String.valueOf(slotId)).put("selectTags", new JSONArray());
    
     userDao.setUserData(uid, UserSyncData);
    
     JSONObject charGet = new JSONObject(true);
     charGet.put("itemGet", itemGet);
     charGet.put("charId", randomCharId);
     charGet.put("charInstId", Integer.valueOf(charinstId));
     charGet.put("isNew", Integer.valueOf(isNew));
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("recruit", UserSyncData.getJSONObject("recruit"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     modified.put("troop", UserSyncData.getJSONObject("troop"));
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     result.put("charGet", charGet);
     return result;
  }  
  @PostMapping({"/getPoolDetail"})
  public JSONObject GetPoolDetail(@RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /gacha/getPoolDetail");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject result = new JSONObject(true);
       result.put("statusCode", Integer.valueOf(400));
       result.put("error", "Bad Request");
       result.put("message", "server is close");
       return result;
    }     
     String poolId = JsonBody.getString("poolId");    
     String PoolPath = System.getProperty("user.dir") + "/data/gacha/" + poolId + ".json";    //读取卡池json文件
     if (!(new File(PoolPath)).exists()) {
       JSONObject result = new JSONObject(true);
       JSONObject detailInfo = new JSONObject();
       JSONObject availCharInfo = new JSONObject();
       availCharInfo.put("perAvailList", new JSONArray());
       detailInfo.put("availCharInfo", availCharInfo);
       detailInfo.put("limitedChar", null);
       detailInfo.put("weightUpCharInfo", null);
       JSONArray gachaObjList = new JSONArray();
       JSONObject Text0 = new JSONObject();      
       JSONObject Text7 = new JSONObject();
       Text7.put("gachaObject", "TEXT");
       Text7.put("type", Integer.valueOf(7));
       Text7.put("param", poolId);
      
       JSONObject Text8 = new JSONObject();
       Text8.put("gachaObject", "TEXT");
       Text8.put("type", Integer.valueOf(5));
       Text8.put("param", "该卡池尚未实装，无法获取详细信息");      
       gachaObjList.add(Text7);
       gachaObjList.add(Text8);
       detailInfo.put("gachaObjList", gachaObjList);
       result.put("detailInfo", detailInfo);
       return result;
    }       
    // 读取卡池信息
     return IOTools.ReadJsonFile(PoolPath);
  }  
  @PostMapping({"/advancedGacha"})
  public JSONObject advancedGacha(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /gacha/advancedGacha");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject result = new JSONObject(true);
       result.put("statusCode", Integer.valueOf(400));
       result.put("error", "Bad Request");
       result.put("message", "server is close");
       return result;
    } 
    
     if (JsonBody.getString("poolId").equals("BOOT_0_1_1")) {
       return Gacha("gachaTicket", 380, secret, JsonBody);
    }
     return Gacha("gachaTicket", 600, secret, JsonBody);
  }  
  @PostMapping({"/tenAdvancedGacha"})
  public JSONObject tenAdvancedGacha(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /gacha/tenAdvancedGacha");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject result = new JSONObject(true);
       result.put("statusCode", Integer.valueOf(400));
       result.put("error", "Bad Request");
       result.put("message", "server is close");
       return result;
    } 
    
     if (JsonBody.getString("poolId").equals("BOOT_0_1_1")) {
       return Gacha("tenGachaTicket", 3800, secret, JsonBody);
    }
     return Gacha("tenGachaTicket", 6000, secret, JsonBody);
  }  
  public JSONObject Gacha(String type, int useDiamondShard, String secret, JSONObject JsonBody) {
     List<Account> Accounts = userDao.queryAccountBySecret(secret);
     if (Accounts.size() != 1) {
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("result", Integer.valueOf(2));
       jSONObject.put("error", "无法查询到此账户");
       return jSONObject;
    } 
    
     Long uid = Long.valueOf(((Account)Accounts.get(0)).getUid());
    
     JSONObject UserSyncData = JSONObject.parseObject(((Account)Accounts.get(0)).getUser());    
     String poolId = JsonBody.getString("poolId");
     String poolPath = System.getProperty("user.dir") + "/data/gacha/" + poolId + ".json";
    
     int useTkt = JsonBody.getIntValue("useTkt");
    
     if (!(new File(poolPath)).exists()) {
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("result", Integer.valueOf(1));
       jSONObject.put("errMsg", "该当前干员寻访无法使用，详情请关注官方公告");
       return jSONObject;
    } 
    
     JSONObject poolJson = IOTools.ReadJsonFile(poolPath);
    
     JSONArray gachaResultList = new JSONArray();
     JSONArray newChars = new JSONArray();
     JSONObject charGet = new JSONObject(true);
     JSONObject troop = new JSONObject(true);
     JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars");
    
     int usedimmond = 0;
     if (JsonBody.getString("poolId").equals("BOOT_0_1_1")) {
       usedimmond = useDiamondShard / 380;
    } else {
       usedimmond = useDiamondShard / 600;
    } 
    
     for (int count = 0; count < usedimmond; count++) {      
       if (useTkt == 1 || useTkt == 2) {
        
         if (UserSyncData.getJSONObject("status").getIntValue(type) <= 0) {
           JSONObject jSONObject = new JSONObject(true);
           jSONObject.put("result", Integer.valueOf(2));
           jSONObject.put("errMsg", "剩余寻访凭证不足");
           return jSONObject;
        }
      
      }
       else if (UserSyncData.getJSONObject("status").getIntValue("diamondShard") < useDiamondShard) {
         JSONObject jSONObject = new JSONObject(true);
         jSONObject.put("result", Integer.valueOf(3));
         jSONObject.put("errMsg", "剩余合成玉不足");
         return jSONObject;
      }       
       Boolean Minimum = Boolean.valueOf(false);
       String poolObjecName = null;
       JSONObject Pool = new JSONObject(true);
      
       if (JsonBody.getString("poolId").equals("BOOT_0_1_1")) {
         poolObjecName = "newbee";
        
         Pool = UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName);
         int cnt = Pool.getIntValue("cnt") - 1;        
         UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).put("cnt", Integer.valueOf(cnt));
         UserSyncData.getJSONObject("status").put("gachaCount", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gachaCount") + 1));
        
         if (cnt == 0) {
           UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).put("openFlag", Integer.valueOf(0));
        }
      } else {
         poolObjecName = "normal";        
         if (!UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).containsKey(poolId)) {
          
           JSONObject PoolJson = new JSONObject(true);
           PoolJson.put("cnt", Integer.valueOf(0));
           PoolJson.put("maxCnt", Integer.valueOf(10));
           PoolJson.put("rarity", Integer.valueOf(4));
           PoolJson.put("avail", Boolean.valueOf(true));
           UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).put(poolId, PoolJson);
        }         
         Pool = UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).getJSONObject(poolId);
         int cnt = Pool.getIntValue("cnt") + 1;        
         UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).getJSONObject(poolId).put("cnt", Integer.valueOf(cnt));
         UserSyncData.getJSONObject("status").put("gachaCount", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gachaCount") + 1));        
         if (cnt == 10 && Pool.getBoolean("avail").booleanValue()) {
           UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).getJSONObject(poolId).put("avail", Boolean.valueOf(false));
           Minimum = Boolean.valueOf(true);
        } 
      }       
       JSONArray availCharInfo = poolJson.getJSONObject("detailInfo").getJSONObject("availCharInfo").getJSONArray("perAvailList");
      
       JSONArray upCharInfo = poolJson.getJSONObject("detailInfo").getJSONObject("upCharInfo").getJSONArray("perCharList");      
       JSONArray randomRankArray = new JSONArray();
      
       for (int i = 0; i < availCharInfo.size(); i++) {
        
         int totalPercent = (int)(availCharInfo.getJSONObject(i).getFloat("totalPercent").floatValue() * 200.0F);
         int rarityRank = availCharInfo.getJSONObject(i).getIntValue("rarityRank");        
         if (rarityRank == 5) {
           totalPercent += (UserSyncData.getJSONObject("status").getIntValue("gachaCount") + 50) / 50 * 2;
        }        
         if (!Minimum.booleanValue() || 
           rarityRank >= Pool.getIntValue("rarity")) {          
           JSONObject randomRankObject = new JSONObject(true);
           randomRankObject.put("rarityRank", Integer.valueOf(rarityRank));
           randomRankObject.put("index", Integer.valueOf(i));          
           IntStream.range(0, totalPercent).forEach(n -> randomRankArray.add(randomRankObject));
        } 
      }       
       Collections.shuffle((List<?>)randomRankArray);      
       JSONObject randomRank = randomRankArray.getJSONObject((new Random()).nextInt(randomRankArray.size()));      
       if (!JsonBody.getString("poolId").equals("BOOT_0_1_1") && 
         randomRank.getIntValue("rarityRank") >= Pool.getIntValue("rarity")) {
         UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).getJSONObject(poolId).put("avail", Boolean.valueOf(false));
      }      
       if (randomRank.getIntValue("rarityRank") == 5) {
         UserSyncData.getJSONObject("status").put("gachaCount", Integer.valueOf(0));
      }      
       JSONArray randomCharArray = availCharInfo.getJSONObject(randomRank.getIntValue("index")).getJSONArray("charIdList");
      
       for (int j = 0; j < upCharInfo.size(); j++) {
        
         if (upCharInfo.getJSONObject(j).getIntValue("rarityRank") == randomRank.getIntValue("rarityRank")) {
          
           int percent = (int)(upCharInfo.getJSONObject(j).getFloat("percent").floatValue() * 100.0F) - 15;
          
           JSONArray upCharIdList = upCharInfo.getJSONObject(j).getJSONArray("charIdList");
          
           for (int n = 0; n < upCharIdList.size(); n++) {
             String charId = upCharIdList.getString(n);
             IntStream.range(0, percent).forEach(p -> randomCharArray.add(charId));
          } 
        } 
      }       
       Collections.shuffle((List<?>)randomCharArray);      
       String randomCharId = randomCharArray.getString((new Random()).nextInt(randomCharArray.size()));      
       int repeatCharId = 0;
      
       for (int k = 0; k < UserSyncData.getJSONObject("troop").getJSONObject("chars").size(); k++) {
         if (UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(k + 1)).getString("charId").equals(randomCharId)) {
           repeatCharId = k + 1;
          
          break;
        } 
      } 
       if (repeatCharId == 0) {
        
         JSONObject get_char = new JSONObject(true);        
         JSONObject char_data = new JSONObject(true);
         JSONArray skilsArray = ArknightsApplication.characterJson.getJSONObject(randomCharId).getJSONArray("skills");
         JSONArray skils = new JSONArray();
        
         for (int m = 0; m < skilsArray.size(); m++) {
           JSONObject new_skils = new JSONObject(true);
           new_skils.put("skillId", skilsArray.getJSONObject(m).getString("skillId"));
           new_skils.put("state", Integer.valueOf(0));
           new_skils.put("specializeLevel", Integer.valueOf(0));
           new_skils.put("completeUpgradeTime", Integer.valueOf(-1));
           if (skilsArray.getJSONObject(m).getJSONObject("unlockCond").getIntValue("phase") == 0) {
             new_skils.put("unlock", Integer.valueOf(1));
          } else {
             new_skils.put("unlock", Integer.valueOf(0));
          } 
           skils.add(new_skils);
        } 
        
         int instId = UserSyncData.getJSONObject("troop").getJSONObject("chars").size() + 1;
         char_data.put("instId", Integer.valueOf(instId));
         char_data.put("charId", randomCharId);
         char_data.put("favorPoint", Integer.valueOf(0));
         char_data.put("potentialRank", Integer.valueOf(0));
         char_data.put("mainSkillLvl", Integer.valueOf(1));
         char_data.put("skin", randomCharId + "#1");
         char_data.put("level", Integer.valueOf(1));
         char_data.put("exp", Integer.valueOf(0));
         char_data.put("evolvePhase", Integer.valueOf(0));
         char_data.put("gainTime", Long.valueOf((new Date()).getTime() / 1000L));
         char_data.put("skills", skils);
        
         char_data.put("voiceLan", ArknightsApplication.charwordTable.getJSONObject("charDefaultTypeDict").getString(randomCharId));
        
         if (skils == new JSONArray()) {
           char_data.put("defaultSkillIndex", Integer.valueOf(-1));
        } else {
           char_data.put("defaultSkillIndex", Integer.valueOf(0));
        }         
         String sub1 = randomCharId.substring(randomCharId.indexOf("_") + 1);
         String charName = sub1.substring(sub1.indexOf("_") + 1);
        
         if (ArknightsApplication.uniequipTable.containsKey("uniequip_001_" + charName)) {
           JSONObject equip = new JSONObject(true);
           JSONObject uniequip_001 = new JSONObject(true);
           uniequip_001.put("hide", Integer.valueOf(0));
           uniequip_001.put("locked", Integer.valueOf(0));
           uniequip_001.put("level", Integer.valueOf(1));
           JSONObject uniequip_002 = new JSONObject(true);
           uniequip_002.put("hide", Integer.valueOf(0));
           uniequip_002.put("locked", Integer.valueOf(0));
           uniequip_002.put("level", Integer.valueOf(1));
           equip.put("uniequip_001_" + charName, uniequip_001);
           equip.put("uniequip_002_" + charName, uniequip_002);
           char_data.put("equip", equip);
           char_data.put("currentEquip", "uniequip_001_" + charName);
        } else {
           char_data.put("currentEquip", null);
        } 
        
         UserSyncData.getJSONObject("troop").getJSONObject("chars").put(String.valueOf(instId), char_data);
        
         JSONObject charGroup = new JSONObject(true);
         charGroup.put("favorPoint", Integer.valueOf(0));
         UserSyncData.getJSONObject("troop").getJSONObject("charGroup").put(randomCharId, charGroup);
        
         JSONObject buildingChar = new JSONObject(true);
         buildingChar.put("charId", randomCharId);
         buildingChar.put("lastApAddTime", Long.valueOf((new Date()).getTime() / 1000L));
         buildingChar.put("ap", Integer.valueOf(8640000));
         buildingChar.put("roomSlotId", "");
         buildingChar.put("index", Integer.valueOf(-1));
         buildingChar.put("changeScale", Integer.valueOf(0));
         JSONObject bubble = new JSONObject(true);
         JSONObject normal = new JSONObject(true);
         normal.put("add", Integer.valueOf(-1));
         normal.put("ts", Integer.valueOf(0));
         bubble.put("normal", normal);
         JSONObject assist = new JSONObject(true);
         assist.put("add", Integer.valueOf(-1));
         assist.put("ts", Integer.valueOf(-1));
         bubble.put("assist", assist);
         buildingChar.put("bubble", bubble);
         buildingChar.put("workTime", Integer.valueOf(0));
        
         UserSyncData.getJSONObject("building").getJSONObject("chars").put(String.valueOf(instId), buildingChar);
        
         get_char.put("charInstId", Integer.valueOf(instId));
         get_char.put("charId", randomCharId);
         get_char.put("isNew", Integer.valueOf(1));
        
         JSONArray itemGet = new JSONArray();
        
         JSONObject new_itemGet_1 = new JSONObject(true);
         new_itemGet_1.put("type", "HGG_SHD");
         new_itemGet_1.put("id", "4004");
         new_itemGet_1.put("count", Integer.valueOf(1));
         itemGet.add(new_itemGet_1);
        
         UserSyncData.getJSONObject("status").put("hggShard", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("hggShard") + 1));
        
         get_char.put("itemGet", itemGet);
         UserSyncData.getJSONObject("inventory").put("p_" + randomCharId, Integer.valueOf(0));
         gachaResultList.add(get_char);
         newChars.add(get_char);
         charGet = get_char;
        
         JSONObject charinstId = new JSONObject(true);
         charinstId.put(String.valueOf(instId), char_data);
         chars.put(String.valueOf(instId), char_data);
         troop.put("chars", charinstId);
      }
      else {
        
         JSONObject get_char = new JSONObject(true);        
         get_char.put("charInstId", Integer.valueOf(repeatCharId));
         get_char.put("charId", randomCharId);
         get_char.put("isNew", Integer.valueOf(0));
        
         JSONObject repatChar = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(repeatCharId));
        
         int potentialRank = repatChar.getIntValue("potentialRank");
         int rarity = randomRank.getIntValue("rarityRank");
        
         String itemName = null;
         String itemType = null;
         String itemId = null;
         int itemCount = 0;
         if (rarity == 0) {
           itemName = "lggShard";
           itemType = "LGG_SHD";
           itemId = "4005";
           itemCount = 1;
        } 
         if (rarity == 1) {
           itemName = "lggShard";
           itemType = "LGG_SHD";
           itemId = "4005";
           itemCount = 1;
        } 
         if (rarity == 2) {
           itemName = "lggShard";
           itemType = "LGG_SHD";
           itemId = "4005";
           itemCount = 5;
        } 
         if (rarity == 3) {
           itemName = "lggShard";
           itemType = "LGG_SHD";
           itemId = "4005";
           itemCount = 30;
        } 
         if (rarity == 4) {
           itemName = "hggShard";
           itemType = "HGG_SHD";
           itemId = "4004";
           if (potentialRank != 5) {
             itemCount = 5;
          } else {
             itemCount = 8;
          } 
        } 
         if (rarity == 5) {
           itemName = "hggShard";
           itemType = "HGG_SHD";
           itemId = "4004";
           if (potentialRank != 5) {
             itemCount = 10;
          } else {
             itemCount = 15;
          } 
        } 
        
         JSONArray itemGet = new JSONArray();
         JSONObject new_itemGet_1 = new JSONObject(true);
         new_itemGet_1.put("type", itemType);
         new_itemGet_1.put("id", itemId);
         new_itemGet_1.put("count", Integer.valueOf(itemCount));
         itemGet.add(new_itemGet_1);
        
         UserSyncData.getJSONObject("status").put(itemName, Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue(itemName) + count));
        
         JSONObject new_itemGet_3 = new JSONObject(true);
         new_itemGet_3.put("type", "MATERIAL");
         new_itemGet_3.put("id", "p_" + randomCharId);
         new_itemGet_3.put("count", Integer.valueOf(1));
         itemGet.add(new_itemGet_3);
         get_char.put("itemGet", itemGet);
         UserSyncData.getJSONObject("inventory").put("p_" + randomCharId, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("p_" + randomCharId) + 1));
        
         gachaResultList.add(get_char);
         charGet = get_char;
        
         JSONObject charinstId = new JSONObject(true);
         charinstId.put(String.valueOf(repeatCharId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(repeatCharId)));
         chars.put(String.valueOf(repeatCharId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(repeatCharId)));
         troop.put("chars", charinstId);
      } 
    }     
     if (useTkt == 1 || useTkt == 2) {
      
       UserSyncData.getJSONObject("status").put(type, Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue(type) - 1));
    } else {
      
       UserSyncData.getJSONObject("status").put("diamondShard", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("diamondShard") - useDiamondShard));
    } 
    
     UserSyncData.getJSONObject("troop").put("chars", chars);
    
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
    
     modified.put("troop", UserSyncData.getJSONObject("troop"));
     modified.put("consumable", UserSyncData.getJSONObject("consumable"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     modified.put("gacha", UserSyncData.getJSONObject("gacha"));
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
    
     userDao.setUserData(uid, UserSyncData);
    
     JSONObject result = new JSONObject(true);
     result.put("result", Integer.valueOf(0));
     result.put("charGet", charGet);
     result.put("gachaResultList", gachaResultList);
     result.put("playerDataDelta", playerDataDelta);
     return result;
  }
}