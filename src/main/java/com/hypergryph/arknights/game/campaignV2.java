package com.hypergryph.arknights.game;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.decrypt.Utils;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/campaignV2" })
public class campaignV2 {
  @PostMapping(value = { "/battleStart" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject BattleStart(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /campaignV2/battleStart");

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
      UserSyncData.getJSONObject("status").put("practiceTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("practiceTicket") - 1));
      UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("practiceTimes",
          Integer.valueOf(1));
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

    if (UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId)
        .getIntValue("noCostCnt") == 1) {
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

  @PostMapping(value = { "/battleFinish" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject BattleFinish(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    ArknightsApplication.LOGGER.info("[/" + clientIp + "] /campaignV2/battleFinish");

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

    JSONObject BattleData = Utils.BattleData_decrypt(JsonBody.getString("data"),
        UserSyncData.getJSONObject("pushFlags").getString("status"));

    String stageId = BattleData.getString("battleId");

    int DropRate = ArknightsApplication.serverConfig.getJSONObject("battle").getIntValue("dropRate");

    int killCnt = BattleData.getIntValue("killCnt");
    int completeState = BattleData.getIntValue("completeState");

    if (ArknightsApplication.serverConfig.getJSONObject("battle").getBooleanValue("debug")) {
      killCnt = 400;
    }

    JSONArray firstRewards = new JSONArray();
    JSONObject diamondShard = new JSONObject(true);
    int count = 0;
    int apFailReturn = 0;

    if (killCnt < 100) {
      count = 0;
      apFailReturn = 25;
    } else if (killCnt < 200) {
      count = 80;
      apFailReturn = 17;
    } else if (killCnt < 250) {
      count = 155;
      apFailReturn = 10;
    } else if (killCnt < 300) {
      count = 200;
      apFailReturn = 7;
    } else if (killCnt < 325) {
      count = 235;
      apFailReturn = 5;
    } else if (killCnt < 350) {
      count = 275;
      apFailReturn = 3;
    } else if (killCnt < 375) {
      count = 300;
      apFailReturn = 2;
    } else if (killCnt < 400) {
      count = 330;
      apFailReturn = 1;
    } else if (killCnt == 400) {
      count = 365;
      apFailReturn = 0;
    }

    int nowTime = (int) ((new Date()).getTime() / 1000L);

    int addAp = (nowTime - UserSyncData.getJSONObject("status").getIntValue("lastApAddTime")) / 360;

    if (UserSyncData.getJSONObject("status").getIntValue("ap") < UserSyncData.getJSONObject("status")
        .getIntValue("maxAp")) {
      if (UserSyncData.getJSONObject("status").getIntValue("ap") + addAp >= UserSyncData.getJSONObject("status")
          .getIntValue("maxAp")) {
        UserSyncData.getJSONObject("status").put("ap",
            Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("maxAp")));
        UserSyncData.getJSONObject("status").put("lastApAddTime", Integer.valueOf(nowTime));
      } else if (addAp != 0) {
        UserSyncData.getJSONObject("status").put("ap",
            Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + addAp));
        UserSyncData.getJSONObject("status").put("lastApAddTime", Integer.valueOf(nowTime));
      }
    }

    UserSyncData.getJSONObject("status").put("ap",
        Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap")
            - ArknightsApplication.stageTable.getJSONObject(stageId).getIntValue("apCost")));

    if (apFailReturn != 0) {
      UserSyncData.getJSONObject("status").put("ap",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + apFailReturn));
      UserSyncData.getJSONObject("status").put("lastApAddTime", Integer.valueOf(nowTime));
    }

    diamondShard.put("count", Integer.valueOf(count * DropRate));
    diamondShard.put("id", "4003");
    diamondShard.put("type", "DIAMOND_SHD");

    firstRewards.add(diamondShard);

    UserSyncData.getJSONObject("status").put("diamondShard",
        Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("diamondShard") + count * DropRate));

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    JSONObject modified = new JSONObject(true);
    modified.put("status", UserSyncData.getJSONObject("status"));
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    result.put("rewards", firstRewards);
    result.put("apFailReturn", Integer.valueOf(apFailReturn));
    result.put("result", Integer.valueOf(0));
    return result;
  }
}