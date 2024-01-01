package com.hypergryph.arknights.game;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.decrypt.Utils;
import com.hypergryph.arknights.core.pojo.Account;
import com.hypergryph.arknights.core.pojo.SearchAssistCharList;
import com.hypergryph.arknights.core.pojo.UserInfo;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;@RestController
@RequestMapping({"/quest"})
public class quest {
  @PostMapping(value = {"/battleStart"}, produces = {"application/json;charset=UTF-8"})
  public JSONObject BattleStart(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /quest/battleStart");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
    } 
    
     String stageId = JsonBody.getString("stageId");
     int isReplay = JsonBody.getIntValue("isReplay");
     int startTs = JsonBody.getIntValue("startTs");
     int usePracticeTicket = JsonBody.getIntValue("usePracticeTicket");    
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
     JSONObject stage_table = ArknightsApplication.stageTable.getJSONObject(stageId);
    
     if (!UserSyncData.getJSONObject("dungeon").getJSONObject("stages").containsKey(stageId)) {
       JSONObject stagesData = new JSONObject(true);
       stagesData.put("completeTimes", Integer.valueOf(0));
       stagesData.put("hasBattleReplay", Integer.valueOf(0));
       stagesData.put("noCostCnt", Integer.valueOf(1));
       stagesData.put("practiceTimes", Integer.valueOf(0));
       stagesData.put("stageId", stageId);
       stagesData.put("startTimes", Integer.valueOf(0));
       stagesData.put("state", Integer.valueOf(0));
      
       UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(stageId, stagesData);
    } 
    
     if (usePracticeTicket == 1) {
       UserSyncData.getJSONObject("status").put("practiceTicket", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("practiceTicket") - 1));
       UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("practiceTimes", Integer.valueOf(1));
    } 
    
     userDao.setUserData(uid, UserSyncData);
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject dungeon = new JSONObject(true);
     JSONObject stages = new JSONObject(true);
    
     stages.put(stageId, UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId));
    
     dungeon.put("stages", stages);
     modified.put("dungeon", dungeon);
     modified.put("status", UserSyncData.getJSONObject("status"));
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("result", Integer.valueOf(0));
     result.put("battleId", stageId);
    
     result.put("isApProtect", Integer.valueOf(0));
     result.put("apFailReturn", Integer.valueOf(stage_table.getIntValue("apFailReturn")));
    
     if (UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).getIntValue("noCostCnt") == 1) {
       result.put("isApProtect", Integer.valueOf(1));
       result.put("apFailReturn", Integer.valueOf(stage_table.getIntValue("apCost")));
    } 
    
     if (stage_table.getIntValue("apCost") == 0) {
       result.put("isApProtect", Integer.valueOf(0));
       result.put("apFailReturn", Integer.valueOf(0));
    } 
    
     if (usePracticeTicket == 1) {
       result.put("isApProtect", Integer.valueOf(0));
       result.put("apFailReturn", Integer.valueOf(0));
    } 
    
     return result;
  }  
  @PostMapping(value = {"/battleFinish"}, produces = {"application/json;charset=UTF-8"})
  public JSONObject BattleFinish(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /quest/battleFinish");
    
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
    
     JSONObject BattleData = Utils.BattleData_decrypt(JsonBody.getString("data"), UserSyncData.getJSONObject("pushFlags").getString("status"));
    
     String stageId = BattleData.getString("battleId");
    
     JSONObject stage_table = ArknightsApplication.stageTable.getJSONObject(stageId);
     JSONObject stageClear = new JSONObject();
    
     if (ArknightsApplication.mainStage.containsKey(stageId)) {
       stageClear = ArknightsApplication.mainStage.getJSONObject(stageId);
    } else {
       stageClear.put("next", null);
       stageClear.put("star", null);
       stageClear.put("sub", null);
       stageClear.put("hard", null);
    }     
     if (UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).getIntValue("practiceTimes") == 1) {
      
       if (UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).getIntValue("state") == 0) {
         UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("state", Integer.valueOf(1));
      }
       UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("practiceTimes", Integer.valueOf(0));
      
       userDao.setUserData(uid, UserSyncData);
      
       JSONObject jSONObject1 = new JSONObject(true);
       JSONObject jSONObject2 = new JSONObject(true);
       JSONObject jSONObject3 = new JSONObject(true);
       JSONObject jSONObject4 = new JSONObject(true);
       JSONObject jSONObject5 = new JSONObject(true);
       jSONObject5.put(stageId, UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId));
       jSONObject4.put("stages", jSONObject5);
      
       jSONObject3.put("status", UserSyncData.getJSONObject("status"));
       jSONObject3.put("dungeon", jSONObject4);
       jSONObject2.put("deleted", new JSONObject(true));
       jSONObject2.put("modified", jSONObject3);
       jSONObject1.put("playerDataDelta", jSONObject2);
       jSONObject1.put("result", Integer.valueOf(0));
       return jSONObject1;
    } 
    
     JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars");
     JSONObject troop = new JSONObject(true);
    
     JSONObject result = new JSONObject(true);    
     int DropRate = ArknightsApplication.serverConfig.getJSONObject("battle").getIntValue("dropRate");
    
     int completeState = BattleData.getIntValue("completeState");
    
     if (ArknightsApplication.serverConfig.getJSONObject("battle").getBooleanValue("debug")) {
       completeState = 3;
    }
    
     int apCost = stage_table.getIntValue("apCost");
     int expGain = stage_table.getIntValue("expGain");
     int goldGain = stage_table.getIntValue("goldGain");
    
     result.put("goldScale", Integer.valueOf(1));
     result.put("expScale", Integer.valueOf(1));    
     if (completeState == 3) {
       expGain = (int)(expGain * 1.2D);
       goldGain = (int)(goldGain * 1.2D);
       result.put("goldScale", Double.valueOf(1.2D));
       result.put("expScale", Double.valueOf(1.2D));
    }     
     int nowTime = (int)((new Date()).getTime() / 1000L);
    
     int addAp = (nowTime - UserSyncData.getJSONObject("status").getIntValue("lastApAddTime")) / 360;
    
     if (UserSyncData.getJSONObject("status").getIntValue("ap") < UserSyncData.getJSONObject("status").getIntValue("maxAp")) {
       if (UserSyncData.getJSONObject("status").getIntValue("ap") + addAp >= UserSyncData.getJSONObject("status").getIntValue("maxAp")) {
         UserSyncData.getJSONObject("status").put("ap", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("maxAp")));
         UserSyncData.getJSONObject("status").put("lastApAddTime", Integer.valueOf(nowTime));
      }
       else if (addAp != 0) {
         UserSyncData.getJSONObject("status").put("ap", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + addAp));
         UserSyncData.getJSONObject("status").put("lastApAddTime", Integer.valueOf(nowTime));
      } 
    }    
     UserSyncData.getJSONObject("status").put("ap", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") - apCost));    
     if (completeState == 1) {
      
       int apFailReturn = stage_table.getIntValue("apFailReturn");
      
       if (UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).getIntValue("noCostCnt") == 1) {
         apFailReturn = stage_table.getIntValue("apCost");
         UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("noCostCnt", Integer.valueOf(0));
      } 
      
       nowTime = (int)((new Date()).getTime() / 1000L);
      
       addAp = (UserSyncData.getJSONObject("status").getIntValue("lastApAddTime") - nowTime) / 360;
      
       if (UserSyncData.getJSONObject("status").getIntValue("ap") < UserSyncData.getJSONObject("status").getIntValue("maxAp")) {
         if (UserSyncData.getJSONObject("status").getIntValue("ap") + addAp >= UserSyncData.getJSONObject("status").getIntValue("maxAp")) {
           UserSyncData.getJSONObject("status").put("ap", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("maxAp")));
           UserSyncData.getJSONObject("status").put("lastApAddTime", Integer.valueOf(nowTime));
        } else {
           UserSyncData.getJSONObject("status").put("ap", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + addAp));
           UserSyncData.getJSONObject("status").put("lastApAddTime", Integer.valueOf(nowTime));
        } 
      }
      
       UserSyncData.getJSONObject("status").put("ap", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + apFailReturn));
       UserSyncData.getJSONObject("status").put("lastApAddTime", Integer.valueOf(nowTime));
      
       userDao.setUserData(uid, UserSyncData);
      
       JSONObject jSONObject1 = new JSONObject(true);
       JSONObject jSONObject2 = new JSONObject(true);
       result.put("additionalRewards", new JSONArray());
       result.put("alert", new JSONArray());
       result.put("firstRewards", new JSONArray());
       result.put("furnitureRewards", new JSONArray());
       result.put("unlockStages", new JSONArray());
       result.put("unusualRewards", new JSONArray());
       result.put("rewards", new JSONArray());
       result.put("expScale", Integer.valueOf(0));
       result.put("goldScale", Integer.valueOf(0));
       result.put("apFailReturn", Integer.valueOf(apFailReturn));
      
       jSONObject2.put("status", UserSyncData.getJSONObject("status"));
      
       JSONObject jSONObject3 = new JSONObject(true);
       JSONObject jSONObject4 = new JSONObject(true);
       jSONObject4.put(stageId, UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId));
       jSONObject3.put("stages", jSONObject4);
       jSONObject2.put("dungeon", jSONObject3);
       jSONObject1.put("deleted", new JSONObject(true));
       jSONObject1.put("modified", jSONObject2);
       result.put("playerDataDelta", jSONObject1);
       result.put("result", Integer.valueOf(0));
       return result;
    } 
    
     if (UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).getIntValue("state") == 0) {
       UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("state", Integer.valueOf(1));
    }
    
     JSONObject stages_data = UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId);    
     JSONArray unlockStages = new JSONArray();
     JSONArray unlockStagesObject = new JSONArray();
     JSONArray additionalRewards = new JSONArray();
     JSONArray unusualRewards = new JSONArray();
     JSONArray furnitureRewards = new JSONArray();
     JSONArray firstRewards = new JSONArray();
     JSONArray rewards = new JSONArray();
    
     result.put("result", Integer.valueOf(0));
     result.put("alert", new JSONArray());
     result.put("suggestFriend", Boolean.valueOf(false));
     result.put("apFailReturn", Integer.valueOf(0));
    
     Boolean FirstClear = Boolean.valueOf(false);
     if (stages_data.getIntValue("state") != 3 && 
       completeState == 3) {
       FirstClear = Boolean.valueOf(true);
    }    
     if (stages_data.getIntValue("state") == 3 && 
       completeState == 4) {
       FirstClear = Boolean.valueOf(true);
    }    
     if (stages_data.getIntValue("state") == 1 && (
       completeState == 3 || completeState == 2)) {      
       if (stageId.equals("main_08-16")) {
         for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
           JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
           String charId = charData.getString("charId");
           if (charId.equals("char_002_amiya")) {
            
             JSONArray amiya_skills = charData.getJSONArray("skills");
             String amiya_skin = charData.getString("skin");
             int amiya_defaultSkillIndex = charData.getIntValue("defaultSkillIndex");
            
             charData.put("skin", null);
             charData.put("defaultSkillIndex", Integer.valueOf(-1));
             charData.put("skills", new JSONArray());
            
             charData.put("currentTmpl", "char_1001_amiya2");
             JSONObject tmpl = new JSONObject(true);
            
             JSONObject amiya = new JSONObject(true);
             amiya.put("skinId", amiya_skin);
             amiya.put("defaultSkillIndex", Integer.valueOf(amiya_defaultSkillIndex));
             amiya.put("skills", amiya_skills);
             amiya.put("currentEquip", null);
             amiya.put("equip", new JSONObject(true));
            
             tmpl.put("char_002_amiya", amiya);
            
             JSONArray sword_amiya_skills = new JSONArray();
            
             JSONObject skchr_amiya2_1 = new JSONObject(true);
             skchr_amiya2_1.put("skillId", "skchr_amiya2_1");
             skchr_amiya2_1.put("unlock", Integer.valueOf(1));
             skchr_amiya2_1.put("state", Integer.valueOf(0));
             skchr_amiya2_1.put("specializeLevel", Integer.valueOf(0));
             skchr_amiya2_1.put("completeUpgradeTime", Integer.valueOf(-1));
            
             sword_amiya_skills.add(skchr_amiya2_1);
            
             JSONObject skchr_amiya2_2 = new JSONObject(true);
             skchr_amiya2_2.put("skillId", "skchr_amiya2_1");
             skchr_amiya2_2.put("unlock", Integer.valueOf(1));
             skchr_amiya2_2.put("state", Integer.valueOf(0));
             skchr_amiya2_2.put("specializeLevel", Integer.valueOf(0));
             skchr_amiya2_2.put("completeUpgradeTime", Integer.valueOf(-1));
            
             sword_amiya_skills.add(skchr_amiya2_2);
            
             JSONObject sword_amiya = new JSONObject(true);
             sword_amiya.put("skinId", "char_1001_amiya2#2");
             sword_amiya.put("defaultSkillIndex", Integer.valueOf(0));
             sword_amiya.put("skills", sword_amiya_skills);
             sword_amiya.put("currentEquip", null);
             sword_amiya.put("equip", new JSONObject(true));
            
             tmpl.put("char_1001_amiya2", sword_amiya);
            
             charData.put("tmpl", tmpl);
            
             JSONObject charinstId = new JSONObject(true);
             charinstId.put(entry.getKey().toString(), charData);
             troop.put("chars", charinstId);
            
             UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            
            break;
          } 
        } 
      }
      
       if (stageClear.getString("next") != null) {
         String next = stageClear.getString("next");
         JSONObject unlockStage = new JSONObject(true);
         unlockStage.put("hasBattleReplay", Integer.valueOf(0));
         unlockStage.put("noCostCnt", Integer.valueOf(1));
         unlockStage.put("practiceTimes", Integer.valueOf(0));
         unlockStage.put("completeTimes", Integer.valueOf(0));
         unlockStage.put("state", Integer.valueOf(0));
         unlockStage.put("stageId", next);
         unlockStage.put("startTimes", Integer.valueOf(0));
         UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(next, unlockStage);
        
         if (stage_table.getString("stageType").equals("MAIN") || stage_table.getString("stageType").equals("SUB")) {
           UserSyncData.getJSONObject("status").put("mainStageProgress", next);
        }
         unlockStages.add(next);
         unlockStagesObject.add(unlockStage);
      } 
      
       if (stageClear.getString("sub") != null) {
         String sub = stageClear.getString("sub");
         JSONObject sub_unlockStage = new JSONObject(true);
         sub_unlockStage.put("hasBattleReplay", Integer.valueOf(0));
         sub_unlockStage.put("noCostCnt", Integer.valueOf(1));
         sub_unlockStage.put("practiceTimes", Integer.valueOf(0));
         sub_unlockStage.put("completeTimes", Integer.valueOf(0));
         sub_unlockStage.put("state", Integer.valueOf(0));
         sub_unlockStage.put("stageId", sub);
         sub_unlockStage.put("startTimes", Integer.valueOf(0));
        
         UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(sub, sub_unlockStage);
         unlockStages.add(sub);
         unlockStagesObject.add(sub_unlockStage);
      } 
      
       if (completeState == 3) {
         if (stageClear.getString("star") != null) {
           String star = stageClear.getString("star");
           JSONObject star_unlockStage = new JSONObject(true);
           star_unlockStage.put("hasBattleReplay", Integer.valueOf(0));
           star_unlockStage.put("noCostCnt", Integer.valueOf(0));
           star_unlockStage.put("practiceTimes", Integer.valueOf(0));
           star_unlockStage.put("completeTimes", Integer.valueOf(0));
           star_unlockStage.put("state", Integer.valueOf(0));
           star_unlockStage.put("stageId", star);
           star_unlockStage.put("startTimes", Integer.valueOf(0));
          
           UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(star, star_unlockStage);
           unlockStages.add(star);
           unlockStagesObject.add(star_unlockStage);
        } 
        
         if (stageClear.getString("hard") != null) {
           String hard = stageClear.getString("hard");
           JSONObject hard_unlockStage = new JSONObject(true);
           hard_unlockStage.put("hasBattleReplay", Integer.valueOf(0));
           hard_unlockStage.put("noCostCnt", Integer.valueOf(0));
           hard_unlockStage.put("practiceTimes", Integer.valueOf(0));
           hard_unlockStage.put("completeTimes", Integer.valueOf(0));
           hard_unlockStage.put("state", Integer.valueOf(0));
           hard_unlockStage.put("stageId", hard);
           hard_unlockStage.put("startTimes", Integer.valueOf(0));
          
           UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(hard, hard_unlockStage);
           unlockStages.add(hard);
           unlockStagesObject.add(hard_unlockStage);
        } 
      } 
      
       result.put("unlockStages", unlockStages);
    }     
     if (FirstClear.booleanValue() == true) {      
       JSONArray jSONArray = stage_table.getJSONObject("stageDropInfo").getJSONArray("displayDetailRewards");
      
       for (int k = 0; k < jSONArray.size(); k++) {
         int dropType = jSONArray.getJSONObject(k).getIntValue("dropType");
         int reward_count = 1 * DropRate;
         String reward_id = jSONArray.getJSONObject(k).getString("id");
         String reward_type = jSONArray.getJSONObject(k).getString("type");
        
         if (dropType == 1 || dropType == 8)
        {
           if (reward_type.equals("CHAR")) {
             JSONObject charGet = new JSONObject(true);
             String randomCharId = reward_id;
             int repeatCharId = 0;
            
             for (int n = 0; n < UserSyncData.getJSONObject("troop").getJSONObject("chars").size(); n++) {
               if (UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(n + 1)).getString("charId").equals(randomCharId)) {
                 repeatCharId = n + 1;
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
              
               charGet = get_char;
              
               JSONObject charinstId = new JSONObject(true);
               charinstId.put(String.valueOf(instId), char_data);
               chars.put(String.valueOf(instId), char_data);
               troop.put("chars", charinstId);
            } else {
              
               JSONObject get_char = new JSONObject(true);              
               get_char.put("charInstId", Integer.valueOf(repeatCharId));
               get_char.put("charId", randomCharId);
               get_char.put("isNew", Integer.valueOf(0));
              
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
              
               JSONArray itemGet = new JSONArray();
               JSONObject new_itemGet_1 = new JSONObject(true);
               new_itemGet_1.put("type", itemType);
               new_itemGet_1.put("id", itemId);
               new_itemGet_1.put("count", Integer.valueOf(itemCount));
               itemGet.add(new_itemGet_1);
               UserSyncData.getJSONObject("status").put(itemName, Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue(itemName) + itemCount));
              
               JSONObject new_itemGet_3 = new JSONObject(true);
               new_itemGet_3.put("type", "MATERIAL");
               new_itemGet_3.put("id", "p_" + randomCharId);
               new_itemGet_3.put("count", Integer.valueOf(1));
               itemGet.add(new_itemGet_3);
               get_char.put("itemGet", itemGet);
               UserSyncData.getJSONObject("inventory").put("p_" + randomCharId, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("p_" + randomCharId) + 1));
              
               charGet = get_char;
              
               JSONObject charinstId = new JSONObject(true);
               charinstId.put(String.valueOf(repeatCharId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(repeatCharId)));
               chars.put(String.valueOf(repeatCharId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(repeatCharId)));
               troop.put("chars", charinstId);
            } 
            
             JSONObject first_reward = new JSONObject(true);
             first_reward.put("count", Integer.valueOf(1));
             first_reward.put("id", reward_id);
             first_reward.put("type", reward_type);
             first_reward.put("charGet", charGet);
             firstRewards.add(first_reward);
          } else {
            
             if (reward_type.equals("MATERIAL")) {
               UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
            }
             if (reward_type.equals("CARD_EXP")) {
               UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
            }
             if (reward_type.equals("DIAMOND")) {
               UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
               UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
            } 
             if (reward_type.equals("GOLD")) {
               UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
            }
             if (reward_type.equals("TKT_RECRUIT")) {
               UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
            }
             if (reward_type.equals("FURN")) {
              
               if (!UserSyncData.getJSONObject("building").getJSONObject("furniture").containsKey(reward_id)) {
                 JSONObject furniture = new JSONObject(true);
                 furniture.put("count", Integer.valueOf(1));
                 furniture.put("inUse", Integer.valueOf(0));
                 UserSyncData.getJSONObject("building").getJSONObject("furniture").put(reward_id, furniture);
              } 
               UserSyncData.getJSONObject("building").getJSONObject("furniture").getJSONObject(reward_id).put("count", Integer.valueOf(UserSyncData.getJSONObject("building").getJSONObject("furniture").getJSONObject(reward_id).getIntValue("count") + 1));
            } 
             JSONObject first_reward = new JSONObject(true);
             first_reward.put("count", Integer.valueOf(reward_count));
             first_reward.put("id", reward_id);
             first_reward.put("type", reward_type);
             firstRewards.add(first_reward);
          } 
        }
      } 
    }     
     result.put("firstRewards", firstRewards);
    
     if (UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).getIntValue("state") != 3) {
       UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("state", Integer.valueOf(completeState));
    }
    
     if (completeState == 4) {
       UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("state", Integer.valueOf(completeState));
    }
    
     UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("completeTime", Integer.valueOf(BattleData.getJSONObject("battleData").getIntValue("completeTime")));
    
     JSONArray playerExpMap = JSON.parseArray("[500,800,1240,1320,1400,1480,1560,1640,1720,1800,1880,1960,2040,2120,2200,2280,2360,2440,2520,2600,2680,2760,2840,2920,3000,3080,3160,3240,3350,3460,3570,3680,3790,3900,4200,4500,4800,5100,5400,5700,6000,6300,6600,6900,7200,7500,7800,8100,8400,8700,9000,9500,10000,10500,11000,11500,12000,12500,13000,13500,14000,14500,15000,15500,16000,17000,18000,19000,20000,21000,22000,23000,24000,25000,26000,27000,28000,29000,30000,31000,32000,33000,34000,35000,36000,37000,38000,39000,40000,41000,42000,43000,44000,45000,46000,47000,48000,49000,50000,51000,52000,54000,56000,58000,60000,62000,64000,66000,68000,70000,73000,76000,79000,82000,85000,88000,91000,94000,97000,100000]");
     JSONArray playerApMap = JSON.parseArray("[82,84,86,88,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,120,120,120,120,121,121,121,121,121,122,122,122,122,122,123,123,123,123,123,124,124,124,124,124,125,125,125,125,125,126,126,126,126,126,127,127,127,127,127,128,128,128,128,128,129,129,129,129,129,130,130,130,130,130,130,130,130,130,130,130,130,130,130,130,130,131,131,131,131,132,132,132,132,133,133,133,133,134,134,134,134,135,135,135,135]");
     int gold = UserSyncData.getJSONObject("status").getIntValue("gold");
     int exp = UserSyncData.getJSONObject("status").getIntValue("exp");
     int level = UserSyncData.getJSONObject("status").getIntValue("level");    
     if (goldGain != 0) {
       UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(gold + goldGain));
      
       JSONObject rewards_gold = new JSONObject(true);
       rewards_gold.put("count", Integer.valueOf(goldGain));
       rewards_gold.put("id", Integer.valueOf(4001));
       rewards_gold.put("type", "GOLD");
       rewards.add(rewards_gold);
    }     
     if (level < 120 && 
       expGain != 0) {
       UserSyncData.getJSONObject("status").put("exp", Integer.valueOf(exp + expGain));
       for (int k = 0; k < playerExpMap.size(); k++) {
         if (level == k + 1) {
           if (Integer.valueOf(playerExpMap.get(k).toString()).intValue() - UserSyncData.getJSONObject("status").getIntValue("exp") <= 0) {
             if (k + 2 == 120) {
               UserSyncData.getJSONObject("status").put("level", Integer.valueOf(120));
               UserSyncData.getJSONObject("status").put("exp", Integer.valueOf(0));
               UserSyncData.getJSONObject("status").put("maxAp", playerApMap.get(k + 1));
               UserSyncData.getJSONObject("status").put("ap", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + UserSyncData.getJSONObject("status").getIntValue("maxAp")));
            } else {
               UserSyncData.getJSONObject("status").put("level", Integer.valueOf(k + 2));
               UserSyncData.getJSONObject("status").put("exp", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("exp") - Integer.valueOf(playerExpMap.get(k).toString()).intValue()));
               UserSyncData.getJSONObject("status").put("maxAp", playerApMap.get(k + 1));
               UserSyncData.getJSONObject("status").put("ap", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + UserSyncData.getJSONObject("status").getIntValue("maxAp")));
            } 
             UserSyncData.getJSONObject("status").put("lastApAddTime", Long.valueOf((new Date()).getTime() / 1000L));
          }           
          break;
        } 
      } 
    }     
     JSONArray displayDetailRewards = stage_table.getJSONObject("stageDropInfo").getJSONArray("displayDetailRewards");
    
     for (int i = 0; i < displayDetailRewards.size(); i++) {
      
       int occPercent = displayDetailRewards.getJSONObject(i).getIntValue("occPercent");
       int dropType = displayDetailRewards.getJSONObject(i).getIntValue("dropType");
       int reward_count = 1 * DropRate;
      
       String reward_id = displayDetailRewards.getJSONObject(i).getString("id");
       String reward_type = displayDetailRewards.getJSONObject(i).getString("type");
      
       int reward_rarity = 0;
       int Percent = 0;
       int addPercent = 0;
      
       if (completeState == 3 && 
         !reward_type.equals("FURN") && !reward_type.equals("CHAR")) {
         reward_rarity = ArknightsApplication.itemTable.getJSONObject(reward_id).getIntValue("rarity");
         if (reward_rarity == 0) {
          
           JSONArray dropArray = new JSONArray();
          
           IntStream.range(0, 70).forEach(n -> dropArray.add(Integer.valueOf(0)));
           IntStream.range(0, 20).forEach(n -> dropArray.add(Integer.valueOf(1)));
           IntStream.range(0, 10).forEach(n -> dropArray.add(Integer.valueOf(2)));
          
           Collections.shuffle((List<?>)dropArray);
          
           int random = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
          
           reward_count += random;
           Percent = 10;
           addPercent = 0;
        } 
         if (reward_rarity == 1) {
          
           JSONArray dropArray = new JSONArray();
          
           IntStream.range(0, 70).forEach(n -> dropArray.add(Integer.valueOf(0)));
           IntStream.range(0, 10).forEach(n -> dropArray.add(Integer.valueOf(1)));
           IntStream.range(0, 5).forEach(n -> dropArray.add(Integer.valueOf(2)));
          
           Collections.shuffle((List<?>)dropArray);
          
           int random = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
          
           reward_count += random;
          
           Percent = 5;
           addPercent = 0;
        } 
         if (reward_rarity == 2) {
           Percent = 0;
           addPercent = 110;
        } 
         if (reward_rarity == 3) {
           Percent = 0;
           addPercent = 120;
        } 
         if (reward_rarity == 4) {
           Percent = 0;
           addPercent = 130;
        } 
      }       
       if (completeState == 2 && 
         !reward_type.equals("FURN") && !reward_type.equals("CHAR")) {
         reward_rarity = ArknightsApplication.itemTable.getJSONObject(reward_id).getIntValue("rarity");
         if (reward_rarity == 0) {
          
           JSONArray dropArray = new JSONArray();
          
           IntStream.range(0, 90 + Percent).forEach(n -> dropArray.add(Integer.valueOf(0)));
           IntStream.range(0, 12 + Percent).forEach(n -> dropArray.add(Integer.valueOf(1)));
           IntStream.range(0, 8 + addPercent).forEach(n -> dropArray.add(Integer.valueOf(2)));
          
           Collections.shuffle((List<?>)dropArray);
          
           int random = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
          
           reward_count += random;
           Percent = 0;
           addPercent = 0;
        } 
         if (reward_rarity == 1) {
          
           JSONArray dropArray = new JSONArray();
          
           IntStream.range(0, 110 + Percent).forEach(n -> dropArray.add(Integer.valueOf(0)));
           IntStream.range(0, 8 + Percent).forEach(n -> dropArray.add(Integer.valueOf(1)));
           IntStream.range(0, 2 + addPercent).forEach(n -> dropArray.add(Integer.valueOf(2)));
          
           Collections.shuffle((List<?>)dropArray);
          
           int random = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
          
           reward_count += random;
          
           Percent = 0;
           addPercent = 0;
        } 
         if (reward_rarity == 2) {
           Percent = 0;
           addPercent = 120;
        } 
         if (reward_rarity == 3) {
           Percent = 0;
           addPercent = 140;
        } 
         if (reward_rarity == 4) {
           Percent = 0;
           addPercent = 160;
        } 
      }       
       if (occPercent == 0 && dropType == 2) {
        
         if (reward_type.equals("MATERIAL")) {          
           if (stageId.equals("wk_toxic_1")) {
             if (completeState == 3) {
               reward_count = 4;
            } else {
               reward_count = 3;
            } 
          }
          
           if (stageId.equals("wk_toxic_2")) {
             if (completeState == 3) {
               reward_count = 7;
            } else {
               reward_count = 3;
            } 
          }
          
           if (stageId.equals("wk_toxic_3")) {
             if (completeState == 3) {
               reward_count = 11;
            } else {
               reward_count = 6;
            } 
          }
          
           if (stageId.equals("wk_toxic_4")) {
             if (completeState == 3) {
               reward_count = 15;
            } else {
               reward_count = 7;
            } 
          }
          
           if (stageId.equals("wk_toxic_5")) {
             if (completeState == 3) {
               reward_count = 21;
            } else {
               reward_count = 8;
            } 
          }          
           if (stageId.equals("wk_fly_1")) {
             if (completeState == 3) {
               reward_count = 3;
            } else {
               reward_count = 1;
            } 
          }
          
           if (stageId.equals("wk_fly_2")) {
             if (completeState == 3) {
               reward_count = 5;
            } else {
               reward_count = 3;
            } 
          }
          
           if (stageId.equals("wk_fly_3")) {
             if (completeState == 3) {
              
               if (reward_rarity == 1) {
                 reward_count = 1;
              }
               if (reward_rarity == 2) {
                 reward_count = 3;
              }
            } else {
               if (reward_rarity == 1) {
                 reward_count = 1;
              }
               if (reward_rarity == 2) {
                 reward_count = 1;
              }
            } 
          }
          
           if (stageId.equals("wk_fly_4")) {
             if (completeState == 3) {
               if (reward_rarity == 1) {
                 reward_count = 1;
              }
               if (reward_rarity == 2) {
                 reward_count = 1;
              }
               if (reward_rarity == 3) {
                 reward_count = 2;
              }
            } else {
               if (reward_rarity == 1) {
                 reward_count = 1;
              }
               if (reward_rarity == 2) {
                 reward_count = 1;
              }
               if (reward_rarity == 3) {
                 reward_count = 1;
              }
            } 
          }
          
           if (stageId.equals("wk_fly_5")) {
             if (completeState == 3) {
               if (reward_rarity == 1) {
                 reward_count = 1;
              }
               if (reward_rarity == 2) {
                 reward_count = 2;
              }
               if (reward_rarity == 3) {
                 reward_count = 3;
              }
            } else {
               if (reward_rarity == 1) {
                 reward_count = 1;
              }
               if (reward_rarity == 2) {
                 reward_count = 1;
              }
               if (reward_rarity == 3) {
                 reward_count = 2;
              }
            } 
          }          
           UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
        } 
         if (reward_type.equals("CARD_EXP")) {
           UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
        }
         if (reward_type.equals("DIAMOND")) {
           UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
           UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
        } 
        
         if (reward_type.equals("TKT_RECRUIT")) {
           UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
        }        
         if (reward_type.equals("GOLD")) {
          
           if (stageId.equals("main_01-01")) {
             if (completeState == 3) {
               reward_count = 660;
            } else {
               reward_count = 550;
            } 
          }
          
           if (stageId.equals("main_02-07")) {
             if (completeState == 3) {
               reward_count = 1500;
            } else {
               reward_count = 1250;
            } 
          }
          
           if (stageId.equals("main_03-06")) {
             if (completeState == 3) {
               reward_count = 2040;
            } else {
               reward_count = 1700;
            } 
          }
          
           if (stageId.equals("main_04-01")) {
             if (completeState == 3) {
               reward_count = 2700;
            } else {
               reward_count = 2250;
            } 
          }
          
           if (stageId.equals("main_06-01")) {
             if (completeState == 3) {
               reward_count = 1216;
            } else {
               reward_count = 1013;
            } 
          }
          
           if (stageId.equals("main_07-02")) {
             if (completeState == 3) {
               reward_count = 1216;
            } else {
               reward_count = 1013;
            } 
          }
          
           if (stageId.equals("main_08-01")) {
             if (completeState == 3) {
               reward_count = 2700;
            } else {
               reward_count = 2250;
            } 
          }
          
           if (stageId.equals("main_08-04")) {
             if (completeState == 3) {
               reward_count = 1216;
            } else {
               reward_count = 1013;
            } 
          }
          
           if (stageId.equals("main_09-01")) {
             if (completeState == 3) {
               reward_count = 2700;
            } else {
               reward_count = 2250;
            } 
          }
          
           if (stageId.equals("main_09-02")) {
             if (completeState == 3) {
               reward_count = 1216;
            } else {
               reward_count = 1013;
            } 
          }
          
           if (stageId.equals("sub_02-02")) {
             if (completeState == 3) {
               reward_count = 1020;
            } else {
               reward_count = 850;
            } 
          }
          
           if (stageId.equals("sub_04-2-3")) {
             if (completeState == 3) {
               reward_count = 3480;
            } else {
               reward_count = 2900;
            } 
          }
          
           if (stageId.equals("sub_05-1-2")) {
             if (completeState == 3) {
               reward_count = 2700;
            } else {
               reward_count = 2250;
            } 
          }
          
           if (stageId.equals("sub_05-2-1")) {
             if (completeState == 3) {
               reward_count = 1216;
            } else {
               reward_count = 1013;
            } 
          }
          
           if (stageId.equals("sub_05-3-1")) {
             if (completeState == 3) {
               reward_count = 1216;
            } else {
               reward_count = 1013;
            } 
          }
          
           if (stageId.equals("sub_06-1-2")) {
             if (completeState == 3) {
               reward_count = 1216;
            } else {
               reward_count = 1013;
            } 
          }
          
           if (stageId.equals("sub_06-2-2")) {
             if (completeState == 3) {
               reward_count = 2700;
            } else {
               reward_count = 2250;
            } 
          }
          
           if (stageId.equals("sub_07-1-1")) {
             if (completeState == 3) {
               reward_count = 2700;
            } else {
               reward_count = 2250;
            } 
          }
          
           if (stageId.equals("sub_07-1-2")) {
             if (completeState == 3) {
               reward_count = 1216;
            } else {
               reward_count = 1013;
            } 
          }
          
           if (stageId.equals("wk_melee_1")) {
             if (completeState == 3) {
               reward_count = 1700;
            } else {
               reward_count = 1416;
            } 
          }
          
           if (stageId.equals("wk_melee_2")) {
             if (completeState == 3) {
               reward_count = 2800;
            } else {
               reward_count = 2333;
            } 
          }
          
           if (stageId.equals("wk_melee_3")) {
             if (completeState == 3) {
               reward_count = 4100;
            } else {
               reward_count = 3416;
            } 
          }
          
           if (stageId.equals("wk_melee_4")) {
             if (completeState == 3) {
               reward_count = 5700;
            } else {
               reward_count = 4750;
            } 
          }
          
           if (stageId.equals("wk_melee_5")) {
             if (completeState == 3) {
               reward_count = 7500;
            } else {
               reward_count = 6250;
            } 
          }
          
           UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
        } 
        
         JSONObject normal_reward = new JSONObject(true);
         normal_reward.put("count", Integer.valueOf(reward_count));
         normal_reward.put("id", reward_id);
         normal_reward.put("type", reward_type);
         rewards.add(normal_reward);
      }       
       if (occPercent == 1 && dropType == 2) {
        
         JSONArray dropArray = new JSONArray();
        
         IntStream.range(0, 80 + Percent).forEach(n -> dropArray.add(Integer.valueOf(1)));
         IntStream.range(0, 20 + addPercent).forEach(n -> dropArray.add(Integer.valueOf(0)));
        
         Collections.shuffle((List<?>)dropArray);
        
         int cur = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
        
         if (cur == 1) {
           if (reward_type.equals("MATERIAL")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("CARD_EXP")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("DIAMOND")) {
             UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
             UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
          } 
           if (reward_type.equals("GOLD")) {
             UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
          }
           if (reward_type.equals("TKT_RECRUIT")) {
             UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
          }
           JSONObject normal_reward = new JSONObject(true);
           normal_reward.put("count", Integer.valueOf(reward_count));
           normal_reward.put("id", reward_id);
           normal_reward.put("type", reward_type);
           rewards.add(normal_reward);
        } 
      }       
       if (occPercent == 2 && dropType == 2) {
        
         if (stageId.indexOf("pro_") != -1) {
          
           JSONArray jSONArray = new JSONArray();
           JSONArray dropArray = new JSONArray();
          
           IntStream.range(0, 5).forEach(n -> dropArray.add(Integer.valueOf(1)));
           IntStream.range(0, 5).forEach(n -> dropArray.add(Integer.valueOf(0)));
          
           Collections.shuffle((List<?>)jSONArray);
          
           int k = jSONArray.getIntValue((new Random()).nextInt(jSONArray.size()));
          
           reward_id = displayDetailRewards.getJSONObject(k).getString("id");
           reward_type = displayDetailRewards.getJSONObject(k).getString("type");
          
           if (reward_type.equals("MATERIAL")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
          
           JSONObject normal_reward = new JSONObject(true);
           normal_reward.put("count", Integer.valueOf(reward_count));
           normal_reward.put("id", reward_id);
           normal_reward.put("type", reward_type);
           rewards.add(normal_reward);
          
          break;
        } 
        
         JSONArray dropArray = new JSONArray();
        
         IntStream.range(0, 50 + Percent).forEach(n -> dropArray.add(Integer.valueOf(1)));
         IntStream.range(0, 50 + addPercent).forEach(n -> dropArray.add(Integer.valueOf(0)));
        
         Collections.shuffle((List<?>)dropArray);
        
         int cur = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
        
         if (cur == 1) {
           if (reward_type.equals("MATERIAL")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("CARD_EXP")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("DIAMOND")) {
             UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
             UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
          } 
           if (reward_type.equals("GOLD")) {
             UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
          }
           if (reward_type.equals("TKT_RECRUIT")) {
             UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
          }
          
           JSONObject normal_reward = new JSONObject(true);
           normal_reward.put("count", Integer.valueOf(reward_count));
           normal_reward.put("id", reward_id);
           normal_reward.put("type", reward_type);
           rewards.add(normal_reward);
        } 
      }       
       if (occPercent == 3 && dropType == 2) {
        
         JSONArray dropArray = new JSONArray();
        
         IntStream.range(0, 15 + Percent).forEach(n -> dropArray.add(Integer.valueOf(1)));
         IntStream.range(0, 90 + addPercent).forEach(n -> dropArray.add(Integer.valueOf(0)));
        
         Collections.shuffle((List<?>)dropArray);
        
         int cur = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
        
         if (cur == 1) {
           if (reward_type.equals("MATERIAL")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("CARD_EXP")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("DIAMOND")) {
             UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
             UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
          } 
           if (reward_type.equals("GOLD")) {
             UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
          }
           if (reward_type.equals("TKT_RECRUIT")) {
             UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
          }
           if (reward_type.equals("FURN")) {
            
             if (!UserSyncData.getJSONObject("building").getJSONObject("furniture").containsKey(reward_id)) {
               JSONObject furniture = new JSONObject(true);
               furniture.put("count", Integer.valueOf(1));
               furniture.put("inUse", Integer.valueOf(0));
               UserSyncData.getJSONObject("building").getJSONObject("furniture").put(reward_id, furniture);
            } 
             UserSyncData.getJSONObject("building").getJSONObject("furniture").getJSONObject(reward_id).put("count", Integer.valueOf(UserSyncData.getJSONObject("building").getJSONObject("furniture").getJSONObject(reward_id).getIntValue("count") + 1));
          } 
           JSONObject normal_reward = new JSONObject(true);
           normal_reward.put("count", Integer.valueOf(reward_count));
           normal_reward.put("id", reward_id);
           normal_reward.put("type", reward_type);
          
           if (!reward_type.equals("FURN")) {
             rewards.add(normal_reward);
          } else {
             furnitureRewards.add(normal_reward);
          } 
        } 
      }       
       if (occPercent == 4 && dropType == 2) {
        
         JSONArray dropArray = new JSONArray();
        
         IntStream.range(0, 10 + Percent).forEach(n -> dropArray.add(Integer.valueOf(1)));
         IntStream.range(0, 90 + addPercent).forEach(n -> dropArray.add(Integer.valueOf(0)));
        
         Collections.shuffle((List<?>)dropArray);
        
         int cur = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
        
         if (cur == 1) {
           if (reward_type.equals("MATERIAL")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("CARD_EXP")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("DIAMOND")) {
             UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
             UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
          } 
           if (reward_type.equals("GOLD")) {
             UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
          }
           if (reward_type.equals("TKT_RECRUIT")) {
             UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
          }
           if (reward_type.equals("FURN")) {
            
             if (!UserSyncData.getJSONObject("building").getJSONObject("furniture").containsKey(reward_id)) {
               JSONObject furniture = new JSONObject(true);
               furniture.put("count", Integer.valueOf(1));
               furniture.put("inUse", Integer.valueOf(0));
               UserSyncData.getJSONObject("building").getJSONObject("furniture").put(reward_id, furniture);
            } 
             UserSyncData.getJSONObject("building").getJSONObject("furniture").getJSONObject(reward_id).put("count", Integer.valueOf(UserSyncData.getJSONObject("building").getJSONObject("furniture").getJSONObject(reward_id).getIntValue("count") + 1));
          } 
           JSONObject normal_reward = new JSONObject(true);
           normal_reward.put("count", Integer.valueOf(reward_count));
           normal_reward.put("id", reward_id);
           normal_reward.put("type", reward_type);
          
           if (!reward_type.equals("FURN")) {
             rewards.add(normal_reward);
          } else {
             furnitureRewards.add(normal_reward);
          } 
        } 
      }       
       if (occPercent == 0 && dropType == 3) {
        
         if (reward_type.equals("MATERIAL")) {
           UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
        }
         if (reward_type.equals("CARD_EXP")) {
           UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
        }
         if (reward_type.equals("DIAMOND")) {
           UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
           UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
        } 
         if (reward_type.equals("GOLD")) {
           UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
        }
         if (reward_type.equals("TKT_RECRUIT")) {
           UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
        }
         JSONObject normal_reward = new JSONObject(true);
         normal_reward.put("count", Integer.valueOf(reward_count));
         normal_reward.put("id", reward_id);
         normal_reward.put("type", reward_type);
         unusualRewards.add(normal_reward);
      }       
       if (occPercent == 3 && dropType == 3) {
        
         JSONArray dropArray = new JSONArray();
        
         IntStream.range(0, 5 + Percent).forEach(n -> dropArray.add(Integer.valueOf(1)));
         IntStream.range(0, 95 + addPercent).forEach(n -> dropArray.add(Integer.valueOf(0)));
        
         Collections.shuffle((List<?>)dropArray);
        
         int cur = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
        
         if (cur == 1) {
           if (reward_type.equals("MATERIAL")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("CARD_EXP")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("DIAMOND")) {
             UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
             UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
          } 
           if (reward_type.equals("GOLD")) {
             UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
          }
           if (reward_type.equals("TKT_RECRUIT")) {
             UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
          }
           JSONObject normal_reward = new JSONObject(true);
           normal_reward.put("count", Integer.valueOf(reward_count));
           normal_reward.put("id", reward_id);
           normal_reward.put("type", reward_type);
           unusualRewards.add(normal_reward);
        } 
      }       
       if (occPercent == 4 && dropType == 3) {
        
         JSONArray dropArray = new JSONArray();
        
         IntStream.range(0, 5 + Percent).forEach(n -> dropArray.add(Integer.valueOf(1)));
         IntStream.range(0, 95 + addPercent).forEach(n -> dropArray.add(Integer.valueOf(0)));
        
         Collections.shuffle((List<?>)dropArray);
        
         int cur = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
        
         if (cur == 1) {
           if (reward_type.equals("MATERIAL")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("CARD_EXP")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("DIAMOND")) {
             UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
             UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
          } 
           if (reward_type.equals("GOLD")) {
             UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
          }
           if (reward_type.equals("TKT_RECRUIT")) {
             UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
          }
           JSONObject normal_reward = new JSONObject(true);
           normal_reward.put("count", Integer.valueOf(reward_count));
           normal_reward.put("id", reward_id);
           normal_reward.put("type", reward_type);
           unusualRewards.add(normal_reward);
        } 
      }       
       if (occPercent == 3 && dropType == 4) {
        
         JSONArray dropArray = new JSONArray();
        
         IntStream.range(0, 5 + Percent).forEach(n -> dropArray.add(Integer.valueOf(1)));
         IntStream.range(0, 95 + addPercent).forEach(n -> dropArray.add(Integer.valueOf(0)));
        
         Collections.shuffle((List<?>)dropArray);
        
         int cur = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
        
         if (cur == 1) {
           if (reward_type.equals("MATERIAL")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("CARD_EXP")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("DIAMOND")) {
             UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
             UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
          } 
           if (reward_type.equals("GOLD")) {
             UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
          }
           if (reward_type.equals("TKT_RECRUIT")) {
             UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
          }
           JSONObject normal_reward = new JSONObject(true);
           normal_reward.put("count", Integer.valueOf(reward_count));
           normal_reward.put("id", reward_id);
           normal_reward.put("type", reward_type);
           additionalRewards.add(normal_reward);
        } 
      }       
       if (occPercent == 4 && dropType == 4) {
        
         JSONArray dropArray = new JSONArray();
        
         IntStream.range(0, 25 + Percent).forEach(n -> dropArray.add(Integer.valueOf(1)));
         IntStream.range(0, 75 + addPercent).forEach(n -> dropArray.add(Integer.valueOf(0)));
        
         Collections.shuffle((List<?>)dropArray);
        
         int cur = dropArray.getIntValue((new Random()).nextInt(dropArray.size()));
        
         if (cur == 1) {
           if (reward_type.equals("MATERIAL")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("CARD_EXP")) {
             UserSyncData.getJSONObject("inventory").put(reward_id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
          }
           if (reward_type.equals("DIAMOND")) {
             UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
             UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
          } 
           if (reward_type.equals("GOLD")) {
             UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
          }
           if (reward_type.equals("TKT_RECRUIT")) {
             UserSyncData.getJSONObject("status").put("recruitLicense", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
          }
           JSONObject normal_reward = new JSONObject(true);
           normal_reward.put("count", Integer.valueOf(reward_count));
           normal_reward.put("id", reward_id);
           normal_reward.put("type", reward_type);
           additionalRewards.add(normal_reward);
        } 
      } 
    }     
     result.put("rewards", rewards);
     result.put("additionalRewards", additionalRewards);
     result.put("unusualRewards", unusualRewards);
     result.put("furnitureRewards", furnitureRewards);    
     int completeFavor = stage_table.getIntValue("completeFavor");
     int passFavor = stage_table.getIntValue("passFavor");
    
     JSONObject charList = BattleData.getJSONObject("battleData").getJSONObject("stats").getJSONObject("charList");
    
     for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)charList.entrySet()) {
       String instId = entry.getKey();
       if (UserSyncData.getJSONObject("troop").getJSONObject("chars").containsKey(instId)) {
         JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(instId);
         String charId = charData.getString("charId");
         int charFavor = charData.getIntValue("favorPoint");
        
         if (completeState == 3 || completeState == 4) {
           charData.put("favorPoint", Integer.valueOf(charFavor + completeFavor));
           if (UserSyncData.getJSONObject("troop").getJSONObject("charGroup").containsKey(charId))
             UserSyncData.getJSONObject("troop").getJSONObject("charGroup").getJSONObject(charId).put("favorPoint", Integer.valueOf(charFavor + completeFavor)); 
          continue;
        } 
         charData.put("favorPoint", Integer.valueOf(charFavor + passFavor));
         if (UserSyncData.getJSONObject("troop").getJSONObject("charGroup").containsKey(charId)) {
           UserSyncData.getJSONObject("troop").getJSONObject("charGroup").getJSONObject(charId).put("favorPoint", Integer.valueOf(charFavor + passFavor));
        }
      } 
    }     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject dungeon = new JSONObject(true);
     JSONObject stages = new JSONObject(true);
    
     for (int j = 0; j < unlockStagesObject.size(); j++) {
       String unlock_stageId = unlockStagesObject.getJSONObject(j).getString("stageId");
       stages.put(unlock_stageId, UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(unlock_stageId));
    } 
     stages.put(stageId, UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId));
    
     dungeon.put("stages", stages);
     modified.put("dungeon", dungeon);
     modified.put("status", UserSyncData.getJSONObject("status"));
     modified.put("troop", troop);
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
    
     result.put("playerDataDelta", playerDataDelta);
    
     userDao.setUserData(uid, UserSyncData);
    
     return result;
  }  
  @PostMapping(value = {"/squadFormation"}, produces = {"application/json;charset=UTF-8"})
  public JSONObject SquadFormation(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /quest/squadFormation");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
    } 
    
     String squadId = JsonBody.getString("squadId");    
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
    
     UserSyncData.getJSONObject("troop").getJSONObject("squads").getJSONObject(squadId).put("slots", JsonBody.getJSONArray("slots"));
    
     userDao.setUserData(uid, UserSyncData);
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject troop = new JSONObject(true);
     JSONObject squads = new JSONObject(true);
     JSONObject squad = UserSyncData.getJSONObject("troop").getJSONObject("squads").getJSONObject(squadId);
     squads.put(squadId, squad);
     troop.put("squads", squads);
     modified.put("troop", troop);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     return result;
  }  
  @PostMapping(value = {"/saveBattleReplay"}, produces = {"application/json;charset=UTF-8"})
  public JSONObject SaveBattleReplay(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /quest/saveBattleReplay");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
    } 
    
     JSONObject BattleData = Utils.BattleReplay_decrypt(JsonBody.getString("battleReplay"));    
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
     String stageId = BattleData.getJSONObject("journal").getJSONObject("metadata").getString("stageId");
     JSONObject stages_data = UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId);
    
     stages_data.put("hasBattleReplay", Integer.valueOf(1));
     stages_data.put("battleReplay", JsonBody.getString("battleReplay"));
    
     userDao.setUserData(uid, UserSyncData);
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject dungeon = new JSONObject(true);
     JSONObject stages = new JSONObject(true);
     stages.put(stageId, UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId));
    
     dungeon.put("stages", stages);
     modified.put("dungeon", dungeon);
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     return result;
  }  
  @PostMapping(value = {"/getBattleReplay"}, produces = {"application/json;charset=UTF-8"})
  public JSONObject GetBattleReplay(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /quest/getBattleReplay");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
    } 
    
     String stageId = JsonBody.getString("stageId");    
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
    
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("battleReplay", UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).getString("battleReplay"));
     return result;
  }  
  @PostMapping(value = {"/changeSquadName"}, produces = {"application/json;charset=UTF-8"})
  public JSONObject ChangeSquadName(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /quest/changeSquadName");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
    } 
    
     String squadId = JsonBody.getString("squadId");
     String name = JsonBody.getString("name");    
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
    
     UserSyncData.getJSONObject("troop").getJSONObject("squads").getJSONObject(squadId).put("name", name);
    
     userDao.setUserData(uid, UserSyncData);
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject troop = new JSONObject(true);
     JSONObject squads = new JSONObject(true);
     JSONObject squad = UserSyncData.getJSONObject("troop").getJSONObject("squads").getJSONObject(squadId);
     squads.put(squadId, squad);
     troop.put("squads", squads);
     modified.put("troop", troop);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     return result;
  }  
  @PostMapping(value = {"/getAssistList"}, produces = {"application/json;charset=UTF-8"})
  public JSONObject getAssistList(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /quest/getAssistList");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
    } 
    
     String profession = JsonBody.getString("profession");
    
     List<Account> Accounts = userDao.queryAccountBySecret(secret);
     if (Accounts.size() != 1) {
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("result", Integer.valueOf(2));
       jSONObject.put("error", "无法查询到此账户");
       return jSONObject;
    } 
    
     if (((Account)Accounts.get(0)).getBan() == 1L) {
       response.setStatus(500);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(403));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "error");
       return jSONObject;
    } 
    
     JSONArray assistCharArray = new JSONArray();
     JSONArray assistList = new JSONArray();
    
     long uid = ((Account)Accounts.get(0)).getUid();
     JSONArray FriendList = JSONObject.parseObject(((Account)Accounts.get(0)).getFriend()).getJSONArray("list");
    
     JSONArray FriendArray = new JSONArray();
    
     Collections.shuffle((List<?>)FriendList);
     for (int i = 0; i < FriendList.size(); i++) {
      
       if (assistList.size() == 6) {
        break;
      }
      
       long friendUid = FriendList.getJSONObject(i).getLongValue("uid");
       String friendAlias = FriendList.getJSONObject(i).getString("alias");
      
       FriendArray.add(Long.valueOf(friendUid));
      
       List<UserInfo> userInfo = userDao.queryUserInfo(friendUid);
      
       JSONArray userSocialAssistCharList = JSONArray.parseArray(((UserInfo)userInfo.get(0)).getSocialAssistCharList());
       JSONObject userAssistCharList = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getAssistCharList());
       JSONObject userStatus = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getStatus());
       JSONObject chars = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getChars());
      
       if (userAssistCharList.containsKey(profession)) {
        
         JSONArray charList = userAssistCharList.getJSONArray(profession);
         Collections.shuffle((List<?>)charList);
         JSONObject assistCharData = charList.getJSONObject(0);
        
         String charId = assistCharData.getString("charId");
         String charInstId = assistCharData.getString("charInstId");
        
         if (!assistCharArray.contains(charId)) {
           assistCharArray.add(charId);
          
           JSONArray assistCharList = new JSONArray();
          
           JSONObject assistInfo = new JSONObject();
           assistInfo.put("aliasName", friendAlias);
           assistInfo.put("avatarId", Integer.valueOf(userStatus.getIntValue("avatarId")));
           assistInfo.put("avatar", userStatus.getJSONObject("avatar"));
           assistInfo.put("canRequestFriend", Boolean.valueOf(false));
           assistInfo.put("isFriend", Boolean.valueOf(true));
           assistInfo.put("lastOnlineTime", Integer.valueOf(userStatus.getIntValue("lastOnlineTs")));
           assistInfo.put("level", Integer.valueOf(userStatus.getIntValue("level")));
           assistInfo.put("nickName", userStatus.getString("nickName"));
           assistInfo.put("nickNumber", userStatus.getString("nickNumber"));
           assistInfo.put("uid", Long.valueOf(friendUid));
           assistInfo.put("powerScore", Integer.valueOf(140));
          
           for (int m = 0; m < userSocialAssistCharList.size(); m++) {
             if (userSocialAssistCharList.getJSONObject(m) != null) {
               JSONObject charData = chars.getJSONObject(userSocialAssistCharList.getJSONObject(m).getString("charInstId"));
               charData.put("skillIndex", Integer.valueOf(userSocialAssistCharList.getJSONObject(m).getIntValue("skillIndex")));
               assistCharList.add(charData);
               if (userSocialAssistCharList.getJSONObject(m).getString("charInstId").equals(charInstId)) {
                 assistInfo.put("assistSlotIndex", Integer.valueOf(m));
              }
            } 
          } 
           assistInfo.put("assistCharList", assistCharList);
           assistList.add(assistInfo);
        } 
      } 
    } 
    
     List<SearchAssistCharList> searchAssist = userDao.SearchAssistCharList("$." + profession);
    int j;
     for (j = 0; j < searchAssist.size(); j++) {
       if (((SearchAssistCharList)searchAssist.get(j)).getUid() == uid) {
         ((SearchAssistCharList)searchAssist.get(j)).setUid(-1L);
      }
       if (FriendArray.contains(Long.valueOf(((SearchAssistCharList)searchAssist.get(j)).getUid()))) {
         ((SearchAssistCharList)searchAssist.get(j)).setUid(-1L);
      }
    } 
    
     Collections.shuffle(searchAssist);
    
     for (j = 0; j < searchAssist.size(); j++) {
      
       long friendUid = ((SearchAssistCharList)searchAssist.get(j)).getUid();
      
       if (friendUid != -1L) {
        
         if (assistList.size() == 9) {
          break;
        }
        
         JSONArray userSocialAssistCharList = JSONArray.parseArray(((SearchAssistCharList)searchAssist.get(j)).getSocialAssistCharList());
         JSONArray charList = JSONArray.parseArray(((SearchAssistCharList)searchAssist.get(j)).getAssistCharList());
         JSONObject userStatus = JSONObject.parseObject(((SearchAssistCharList)searchAssist.get(j)).getStatus());
         JSONObject chars = JSONObject.parseObject(((SearchAssistCharList)searchAssist.get(j)).getChars());
        
         Collections.shuffle((List<?>)charList);
         JSONObject assistCharData = charList.getJSONObject(0);
        
         String charId = assistCharData.getString("charId");
         String charInstId = assistCharData.getString("charInstId");
        
         if (!assistCharArray.contains(charId)) {
           assistCharArray.add(charId);
          
           JSONArray assistCharList = new JSONArray();
          
           JSONObject assistInfo = new JSONObject();
           assistInfo.put("aliasName", "");
           assistInfo.put("avatarId", Integer.valueOf(userStatus.getIntValue("avatarId")));
           assistInfo.put("avatar", userStatus.getJSONObject("avatar"));
           assistInfo.put("canRequestFriend", Boolean.valueOf(true));
           assistInfo.put("isFriend", Boolean.valueOf(false));
           assistInfo.put("lastOnlineTime", Integer.valueOf(userStatus.getIntValue("lastOnlineTs")));
           assistInfo.put("level", Integer.valueOf(userStatus.getIntValue("level")));
           assistInfo.put("nickName", userStatus.getString("nickName"));
           assistInfo.put("nickNumber", userStatus.getString("nickNumber"));
           assistInfo.put("uid", Long.valueOf(friendUid));
           assistInfo.put("powerScore", Integer.valueOf(140));
          
           for (int m = 0; m < userSocialAssistCharList.size(); m++) {
             if (userSocialAssistCharList.getJSONObject(m) != null) {
               JSONObject charData = chars.getJSONObject(userSocialAssistCharList.getJSONObject(m).getString("charInstId"));
               charData.put("skillIndex", Integer.valueOf(userSocialAssistCharList.getJSONObject(m).getIntValue("skillIndex")));
               assistCharList.add(charData);
               if (userSocialAssistCharList.getJSONObject(m).getString("charInstId").equals(charInstId)) {
                 assistInfo.put("assistSlotIndex", Integer.valueOf(m));
              }
            } 
          } 
           assistInfo.put("assistCharList", assistCharList);
           assistList.add(assistInfo);
        } 
      } 
    } 
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     result.put("allowAskTs", Integer.valueOf(1636483552));
     result.put("assistList", assistList);
     return result;
  }  
  @PostMapping(value = {"/finishStoryStage"}, produces = {"application/json;charset=UTF-8"})
  public JSONObject finishStoryStage(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /quest/finishStoryStage");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
    } 
    
     String stageId = JsonBody.getString("stageId");    
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
    
     int stage_state = UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).getIntValue("state");
    
     JSONObject stageClear = ArknightsApplication.mainStage.getJSONObject(stageId);
    
     JSONArray rewards = new JSONArray();
     JSONArray unlockStages = new JSONArray();
     JSONArray unlockStagesObject = new JSONArray();
    
     JSONArray alert = new JSONArray();    
     int DropRate = ArknightsApplication.serverConfig.getJSONObject("battle").getIntValue("dropRate");
    
     if (stage_state != 3) {
       UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("state", Integer.valueOf(3));      
       if (stageClear.getString("next") != null) {
         String next = stageClear.getString("next");
         JSONObject unlockStage = new JSONObject(true);
         unlockStage.put("hasBattleReplay", Integer.valueOf(0));
         unlockStage.put("noCostCnt", Integer.valueOf(0));
         unlockStage.put("practiceTimes", Integer.valueOf(0));
         unlockStage.put("completeTimes", Integer.valueOf(0));
         unlockStage.put("state", Integer.valueOf(0));
         unlockStage.put("stageId", next);
         unlockStage.put("startTimes", Integer.valueOf(0));
         UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(next, unlockStage);
        
         unlockStages.add(next);
         unlockStagesObject.add(unlockStage);
      } 
      
       if (stageClear.getString("sub") != null) {
         String sub = stageClear.getString("sub");
         JSONObject sub_unlockStage = new JSONObject(true);
         sub_unlockStage.put("hasBattleReplay", Integer.valueOf(0));
         sub_unlockStage.put("noCostCnt", Integer.valueOf(0));
         sub_unlockStage.put("practiceTimes", Integer.valueOf(0));
         sub_unlockStage.put("completeTimes", Integer.valueOf(0));
         sub_unlockStage.put("state", Integer.valueOf(0));
         sub_unlockStage.put("stageId", sub);
         sub_unlockStage.put("startTimes", Integer.valueOf(0));
        
         UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(sub, sub_unlockStage);
         unlockStages.add(sub);
         unlockStagesObject.add(sub_unlockStage);
      } 
      
       if (stageClear.getString("star") != null) {
         String star = stageClear.getString("star");
         JSONObject star_unlockStage = new JSONObject(true);
         star_unlockStage.put("hasBattleReplay", Integer.valueOf(0));
         star_unlockStage.put("noCostCnt", Integer.valueOf(0));
         star_unlockStage.put("practiceTimes", Integer.valueOf(0));
         star_unlockStage.put("completeTimes", Integer.valueOf(0));
         star_unlockStage.put("state", Integer.valueOf(0));
         star_unlockStage.put("stageId", star);
         star_unlockStage.put("startTimes", Integer.valueOf(0));
        
         UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(star, star_unlockStage);
         unlockStages.add(star);
         unlockStagesObject.add(star_unlockStage);
      } 
      
       if (stageClear.getString("hard") != null) {
         String hard = stageClear.getString("hard");
         JSONObject hard_unlockStage = new JSONObject(true);
         hard_unlockStage.put("hasBattleReplay", Integer.valueOf(0));
         hard_unlockStage.put("noCostCnt", Integer.valueOf(0));
         hard_unlockStage.put("practiceTimes", Integer.valueOf(0));
         hard_unlockStage.put("completeTimes", Integer.valueOf(0));
         hard_unlockStage.put("state", Integer.valueOf(0));
         hard_unlockStage.put("stageId", hard);
         hard_unlockStage.put("startTimes", Integer.valueOf(0));
        
         UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(hard, hard_unlockStage);
         unlockStages.add(hard);
         unlockStagesObject.add(hard_unlockStage);
      } 
      
       JSONObject reward = new JSONObject(true);
      
       reward.put("type", "DIAMOND");
       reward.put("id", "4002");
       reward.put("count", Integer.valueOf(1 * DropRate));
      
       rewards.add(reward);
      
       UserSyncData.getJSONObject("status").put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + 1 * DropRate));
       UserSyncData.getJSONObject("status").put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + 1 * DropRate));
    } 
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject status = new JSONObject(true);
     status.put("androidDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond")));
     status.put("iosDiamond", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond")));
    
     JSONObject dungeon = new JSONObject(true);
     JSONObject stages = new JSONObject(true);
    
     for (int i = 0; i < unlockStagesObject.size(); i++) {
       String unlock_stageId = unlockStagesObject.getJSONObject(i).getString("stageId");
       stages.put(unlock_stageId, UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(unlock_stageId));
    } 
     stages.put(stageId, UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId));
    
     dungeon.put("stages", stages);
    
     modified.put("status", status);
     modified.put("dungeon", dungeon);
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("rewards", rewards);
     result.put("unlockStages", unlockStages);
     result.put("alert", alert);
     result.put("result", Integer.valueOf(0));
    
     userDao.setUserData(uid, UserSyncData);
    
     return result;
  }
}