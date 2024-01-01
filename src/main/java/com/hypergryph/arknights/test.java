 package com.hypergryph.arknights;
 
 import com.alibaba.fastjson.JSON;
 import com.alibaba.fastjson.JSONArray;
 import com.alibaba.fastjson.JSONObject;
 import com.alibaba.fastjson.serializer.SerializerFeature;
 import com.hypergryph.arknights.ArknightsApplication;
 import com.hypergryph.arknights.core.dao.userDao;
 import com.hypergryph.arknights.core.file.IOTools;
 import com.hypergryph.arknights.core.pojo.Account;
 import java.util.List;
 import java.util.Map;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 @RestController
 @RequestMapping({"/test"})
 public class test
 {
   @RequestMapping({"/set/map"})
   public String setMap() {
     long uid = 10000001L;
     
     List<Account> Accounts = userDao.queryAccountByUid(uid);
     
     JSONObject UserSyncData = JSONObject.parseObject(((Account)Accounts.get(0)).getUser());
     
     JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
     
     current.getJSONObject("map").put("zones", IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/map.json"));
     
     userDao.setUserData(Long.valueOf(uid), UserSyncData);
     
     return "ok";
   }
 
 
 
   
   @RequestMapping({"/query"})
   public String query(@RequestParam String name) {
     ArknightsApplication.LOGGER.info(Integer.valueOf(userDao.queryNickName(name).size()));
     
     return "ok";
   }
 
 
 
   
   @RequestMapping({"/set"})
   public String set(@RequestParam String str) {
     return "ok";
   }
 
   
   @RequestMapping({"/itemtype"})
   public JSONArray itemtype() {
     JSONObject itemtype = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/excel/item_table.json").getJSONObject("items");
     JSONArray type = new JSONArray();
     for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)itemtype.entrySet()) {
       String itemType = itemtype.getJSONObject(entry.getKey()).getString("itemType");
       if (!type.contains(itemType)) {
         type.add(itemType);
       }
     } 
     return type;
   }
 
   
   @RequestMapping({"/sub"})
   public String sub() {
     String charId = "char_179_cgbird";
     String sub1 = charId.substring(charId.indexOf("_") + 1);
     String charName = sub1.substring(sub1.indexOf("_") + 1);
     
     return charName;
   }
 
   
   @RequestMapping({"/pwd"})
   public String pwd(@RequestParam String GMKey) {
     return GMKey;
   }
   
   @RequestMapping({"/roguelike"})
   public String roguelike() {
     JSONObject jSONObject = new JSONObject();
     
     for (Map.Entry entry : ArknightsApplication.stageTable.entrySet()) {
       JSONObject stageData = ArknightsApplication.stageTable.getJSONObject(entry.getKey().toString());
       
       JSONObject stage = new JSONObject();
       stage.put("id", stageData.getString("stageId"));
       stage.put("linkedStageId", "");
       stage.put("levelId", stageData.getString("levelId"));
       stage.put("code", "ISW-" + stageData.getString("code"));
       stage.put("name", stageData.getString("name"));
       stage.put("loadingPicId", "loading_PCS");
       stage.put("isBoss", Integer.valueOf(0));
       stage.put("isElite", Integer.valueOf(0));
       stage.put("difficulty", stageData.getString("difficulty"));
       stage.put("enlargeId", null);
       stage.put("capsulePool", "pool_capsule_default");
       stage.put("capsuleProb", Double.valueOf(1.0D));
       stage.put("vutresProb", new JSONArray());
       stage.put("description", stageData.getString("description"));
       stage.put("eliteDesc", stageData.getString("description"));
       if (stageData.getString("difficulty").equals("NORMAL")) {
         stage.put("eliteDesc", null);
       }
       if (stageData.getString("difficulty").equals("FOUR_STAR")) {
         stage.put("isElite", Integer.valueOf(1));
       }
       if (stageData.getBooleanValue("bossMark")) {
         stage.put("isBoss", Integer.valueOf(1));
       }
       jSONObject.put(entry.getKey().toString(), stage);
     } 
     
     return JSON.toJSONString(jSONObject, new SerializerFeature[] { SerializerFeature.WriteMapNullValue });
   }
 
 
 
   
   @RequestMapping({"/stage"})
   public JSONObject stage() {
     JSONArray jSONArray = new JSONArray();
     JSONObject a = new JSONObject(true);
     JSONObject b = new JSONObject(true);
     a.put("MainStage", new JSONObject(true));
     for (Map.Entry entry : ArknightsApplication.stageTable.entrySet()) {
       JSONObject stageData = ArknightsApplication.stageTable.getJSONObject(entry.getKey().toString());
       
       if (stageData.getString("stageId").indexOf("act15side") != -1) {
         JSONObject data = new JSONObject(true);
         data.put("next", null);
         data.put("star", null);
         data.put("sub", null);
         data.put("hard", null);
         
         a.getJSONObject("MainStage").put(stageData.getString("stageId"), data);
       } 
     } 
     
     b.put("stage", new JSONObject(true));
     for (Map.Entry entry : ArknightsApplication.stageTable.entrySet()) {
       JSONObject stageData = ArknightsApplication.stageTable.getJSONObject(entry.getKey().toString());
       
       if (stageData.getString("stageId").indexOf("act15side") != -1) {
         JSONObject data = new JSONObject(true);
         data.put("stageId", stageData.getString("stageId"));
         data.put("completeTimes", Integer.valueOf(1));
         data.put("startTimes", Integer.valueOf(1));
         data.put("practiceTimes", Integer.valueOf(0));
         data.put("state", Integer.valueOf(3));
         data.put("hasBattleReplay", Integer.valueOf(0));
         data.put("noCostCnt", Integer.valueOf(0));
         
         b.getJSONObject("stage").put(stageData.getString("stageId"), data);
       } 
     } 
     
     System.out.println(JSON.toJSONString(a, new SerializerFeature[] { SerializerFeature.WriteMapNullValue }));
     System.out.println(JSON.toJSONString(b, new SerializerFeature[] { SerializerFeature.WriteMapNullValue }));
     JSONObject stage = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/battle/stage.json");
     
     return stage;
   }
 }