package com.hypergryph.arknights.game;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hypergryph.arknights.admin;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.decrypt.Utils;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Iterator;
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
@RequestMapping({ "/rlv2" })
public class rlv2 {
  private static final Logger LOGGER = LogManager.getLogger();

  @PostMapping(value = { "/createGame" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject createGame(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/createGame");

    String theme = JsonBody.getString("theme");
    String predefinedId = JsonBody.getString("predefinedId");
    String mode = JsonBody.getString("mode");

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
    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");

    current.put("buff", new JSONObject());
    current.put("game", new JSONObject());
    current.put("inventory", new JSONObject());
    current.put("map", new JSONObject());
    current.put("player", new JSONObject());
    current.put("record", new JSONObject());
    current.put("troop", new JSONObject());

    current.getJSONObject("buff").put("tmpHP", Integer.valueOf(0));
    current.getJSONObject("buff").put("capsule", null);

    JSONObject support = new JSONObject();
    support.put("support", Boolean.valueOf(false));
    current.getJSONObject("game").put("outer", support);
    current.getJSONObject("game").put("mode", mode);
    current.getJSONObject("game").put("predefined", predefinedId);
    current.getJSONObject("game").put("theme", theme);
    current.getJSONObject("game").put("start", Long.valueOf((new Date()).getTime() / 1000L));

    current.getJSONObject("inventory").put("relic", new JSONObject());
    current.getJSONObject("inventory").put("recruit", new JSONObject());
    current.getJSONObject("inventory").put("trap", null);

    current.getJSONObject("map").put("zones", new JSONObject());

    current.getJSONObject("record").put("brief", null);

    current.getJSONObject("troop").put("chars", new JSONObject());

    current.getJSONObject("player").put("chgEnding", Boolean.valueOf(false));
    current.getJSONObject("player").put("trace", new JSONArray());
    current.getJSONObject("player").put("toEnding", "ro_ending_1");
    current.getJSONObject("player").put("state", "INIT");

    JSONObject cursor = new JSONObject();
    cursor.put("position", null);
    cursor.put("zone", Integer.valueOf(1));

    current.getJSONObject("player").put("property", new JSONObject());
    current.getJSONObject("player").getJSONObject("property").put("population", new JSONObject());
    current.getJSONObject("player").put("status", new JSONObject());
    current.getJSONObject("player").put("pending", new JSONArray());
    current.getJSONObject("player").getJSONObject("property").put("capacity", Integer.valueOf(6));
    current.getJSONObject("player").getJSONObject("property").put("conPerfectBattle", Integer.valueOf(0));
    current.getJSONObject("player").getJSONObject("property").put("level", Integer.valueOf(1));
    current.getJSONObject("player").getJSONObject("property").put("exp", Integer.valueOf(0));
    current.getJSONObject("player").getJSONObject("property").put("hp", Integer.valueOf(6));
    current.getJSONObject("player").getJSONObject("property").put("gold", Integer.valueOf(8));
    current.getJSONObject("player").getJSONObject("property").getJSONObject("population").put("cost",
        Integer.valueOf(0));
    current.getJSONObject("player").getJSONObject("property").getJSONObject("population").put("max",
        Integer.valueOf(6));

    current.getJSONObject("player").getJSONObject("status").put("bankPut", Integer.valueOf(0));

    current.getJSONObject("player").put("cursor", cursor);
    JSONObject buff = UserSyncData.getJSONObject("rlv2").getJSONObject("outer").getJSONObject(theme)
        .getJSONObject("buff");

    for (Map.Entry entry : buff.getJSONObject("unlocked").entrySet()) {

      JSONArray buffDisplayInfo = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
          .getJSONObject("developments").getJSONObject(entry.getKey().toString()).getJSONArray("buffDisplayInfo");

      for (int i = 0; i < buffDisplayInfo.size(); i++) {
        String displayType = buffDisplayInfo.getJSONObject(i).getString("displayType");
        String displayForm = buffDisplayInfo.getJSONObject(i).getString("displayForm");
        int displayNum = buffDisplayInfo.getJSONObject(i).getIntValue("displayNum");

        if (displayForm.equals("ABSOLUTE_VAL")) {
          if (displayType.equals("display_gold")) {
            current.getJSONObject("player").getJSONObject("property").put("gold", Integer
                .valueOf(current.getJSONObject("player").getJSONObject("property").getIntValue("gold") + displayNum));
          }

          if (displayType.equals("display_hp")) {
            current.getJSONObject("player").getJSONObject("property").put("hp", Integer
                .valueOf(current.getJSONObject("player").getJSONObject("property").getIntValue("hp") + displayNum));
          }

          if (displayType.equals("display_squad_capacity")) {
            current.getJSONObject("player").getJSONObject("property").put("capacity", Integer.valueOf(
                current.getJSONObject("player").getJSONObject("property").getIntValue("capacity") + displayNum));
          }

          if (displayType.equals("display_temp_hp")) {
            current.getJSONObject("buff").put("tmpHP",
                Integer.valueOf(current.getJSONObject("buff").getIntValue("tmpHP") + displayNum));
          }
        }
      }
    }

    JSONObject content = new JSONObject();
    JSONObject tmpObj = new JSONObject();

    JSONObject initModeRelic = new JSONObject();
    JSONArray items = new JSONArray();

    initModeRelic.put("step", JSONArray.parseArray("[1,4]"));

    JSONObject rlv2InitRelic = ArknightsApplication.roguelike.getJSONObject("rlv2").getJSONObject("initRelic");
    for (Map.Entry entry : rlv2InitRelic.entrySet()) {
      if (rlv2InitRelic.getBooleanValue(entry.getKey().toString())) {
        items.add(entry.getKey().toString());
      }
    }

    initModeRelic.put("items", items);

    tmpObj.put("initModeRelic", initModeRelic);

    content.put("content", tmpObj);
    content.put("index", "e_0");
    content.put("type", "GAME_INIT_MODE_RELIC");
    current.getJSONObject("player").getJSONArray("pending").add(content);
    content = new JSONObject();
    tmpObj = new JSONObject();

    JSONObject initRelic = new JSONObject();

    initRelic.put("step", JSONArray.parseArray("[2,4]"));
    initRelic.put("items", JSONObject.parseObject(
        "{\"0\":{\"id\":\"rogue_1_band_1\",\"count\":1},\"1\":{\"id\":\"rogue_1_band_2\",\"count\":1},\"2\":{\"id\":\"rogue_1_band_3\",\"count\":1},\"3\":{\"id\":\"rogue_1_band_4\",\"count\":1},\"4\":{\"id\":\"rogue_1_band_5\",\"count\":1},\"5\":{\"id\":\"rogue_1_band_6\",\"count\":1},\"6\":{\"id\":\"rogue_1_band_7\",\"count\":1},\"7\":{\"id\":\"rogue_1_band_8\",\"count\":1},\"8\":{\"id\":\"rogue_1_band_9\",\"count\":1},\"9\":{\"id\":\"rogue_1_band_10\",\"count\":1}}"));

    tmpObj.put("initRelic", initRelic);

    content.put("content", tmpObj);
    content.put("index", "e_1");
    content.put("type", "GAME_INIT_RELIC");
    current.getJSONObject("player").getJSONArray("pending").add(content);
    content = new JSONObject();
    tmpObj = new JSONObject();

    JSONObject initRecruitSet = new JSONObject();

    initRecruitSet.put("step", JSONArray.parseArray("[3,4]"));
    initRecruitSet.put("option",
        JSONArray.parseArray("[\"recruit_group_1\",\"recruit_group_2\",\"recruit_group_3\",\"recruit_group_random\"]"));

    tmpObj.put("initRecruitSet", initRecruitSet);

    content.put("content", tmpObj);
    content.put("index", "e_2");
    content.put("type", "GAME_INIT_RECRUIT_SET");
    current.getJSONObject("player").getJSONArray("pending").add(content);
    content = new JSONObject();
    tmpObj = new JSONObject();

    JSONObject initRecruit = new JSONObject();

    initRecruit.put("step", JSONArray.parseArray("[4,4]"));
    initRecruit.put("tickets", new JSONArray());

    tmpObj.put("initRecruit", initRecruit);

    content.put("content", tmpObj);
    content.put("index", "e_3");
    content.put("type", "GAME_INIT_RECRUIT");
    current.getJSONObject("player").getJSONArray("pending").add(content);
    if (mode.equals("MONTH_TEAM"))
      ;
    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    JSONObject jSONObject1 = new JSONObject();
    jSONObject1.put("current", UserSyncData.getJSONObject("rlv2").getJSONObject("current"));
    JSONObject outer = new JSONObject();
    JSONObject rogue_1 = new JSONObject();
    rogue_1.put("record",
        UserSyncData.getJSONObject("rlv2").getJSONObject("outer").getJSONObject(theme).getJSONObject("record"));
    outer.put(theme, rogue_1);
    jSONObject1.put("outer", outer);
    modified.put("rlv2", jSONObject1);
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/giveUpGame" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject giveUpGame(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/giveUpGame");

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

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");

    current.put("buff", null);
    current.put("game", null);
    current.put("inventory", null);
    current.put("map", null);
    current.put("player", null);
    current.put("record", null);
    current.put("troop", null);

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    JSONObject jSONObject1 = new JSONObject();
    jSONObject1.put("current", current);
    modified.put("rlv2", jSONObject1);
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/chooseInitialRelic" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject chooseInitialRelic(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/chooseInitialRelic");

    String select = String.valueOf(JsonBody.getIntValue("select") + 1);

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

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
    String theme = current.getJSONObject("game").getString("theme");
    JSONObject selectRelics = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
        .getJSONObject("relics").getJSONObject(theme + "_band_" + select);
    JSONObject roguelikeItems = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
        .getJSONObject("items");
    JSONObject r_0 = new JSONObject();
    r_0.put("count", Integer.valueOf(1));
    r_0.put("id", theme + "_band_" + select);
    r_0.put("index", "r_0");
    r_0.put("ts", Long.valueOf((new Date()).getTime() / 1000L));
    current.getJSONObject("inventory").getJSONObject("relic").put("r_0", r_0);
    JSONArray buffs = selectRelics.getJSONArray("buffs");

    JSONObject property = current.getJSONObject("player").getJSONObject("property");

    for (int i = 0; i < buffs.size(); i++) {
      String key = buffs.getJSONObject(i).getString("key");
      JSONArray blackboard = buffs.getJSONObject(i).getJSONArray("blackboard");
      if (key.equals("level_life_point_add")) {
        current.getJSONObject("buff").put("tmpHP", Integer.valueOf(
            current.getJSONObject("buff").getIntValue("tmpHP") + blackboard.getJSONObject(0).getIntValue("value")));
      }
      if (key.equals("immediate_reward")) {

        String valueStr = blackboard.getJSONObject(0).getString("valueStr");
        String type = roguelikeItems.getJSONObject(valueStr).getString("type");
        int count = blackboard.getJSONObject(1).getIntValue("value");

        if (type.equals("HP")) {
          property.put("hp", Integer.valueOf(property.getIntValue("hp") + count));
        }
        if (type.equals("GOLD")) {
          property.put("gold", Integer.valueOf(property.getIntValue("gold") + count));
        }
        if (type.equals("SQUAD_CAPACITY")) {
          property.put("capacity", Integer.valueOf(property.getIntValue("capacity") + count));
        }
        if (type.equals("POPULATION")) {
          property.getJSONObject("population").put("max",
              Integer.valueOf(property.getJSONObject("population").getIntValue("max") + count));
        }
      }
      if (key.equals("item_cover_set")) {
        String valueStr = blackboard.getJSONObject(0).getString("valueStr");
        String type = roguelikeItems.getJSONObject(valueStr).getString("type");
        int count = blackboard.getJSONObject(1).getIntValue("value");

        if (type.equals("HP")) {
          property.put("hp", Integer.valueOf(count));
        }
      }
    }
    current.getJSONObject("player").getJSONArray("pending").remove(0);

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    JSONObject jSONObject1 = new JSONObject();
    jSONObject1.put("current", current);
    modified.put("rlv2", jSONObject1);
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/chooseInitialRecruitSet" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject chooseInitialRecruitSet(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/chooseInitialRecruitSet");

    String select = JsonBody.getString("select");

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

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");

    if (select.equals("recruit_group_1")) {
      current.getJSONObject("inventory").put("recruit", JSONObject.parseObject(
          "{\"t_1\":{\"index\":\"t_1\",\"id\":\"rogue_1_recruit_ticket_pioneer\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641721789,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_2\":{\"index\":\"t_2\",\"id\":\"rogue_1_recruit_ticket_pioneer\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641721789,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_3\":{\"index\":\"t_3\",\"id\":\"rogue_1_recruit_ticket_special\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641721789,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false}}"));
    }
    if (select.equals("recruit_group_2")) {
      current.getJSONObject("inventory").put("recruit", JSONObject.parseObject(
          "{\"t_1\":{\"index\":\"t_1\",\"id\":\"rogue_1_recruit_ticket_tank\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722549,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_2\":{\"index\":\"t_2\",\"id\":\"rogue_1_recruit_ticket_caster\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722549,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_3\":{\"index\":\"t_3\",\"id\":\"rogue_1_recruit_ticket_sniper\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722549,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false}}"));
    }
    if (select.equals("recruit_group_3")) {
      current.getJSONObject("inventory").put("recruit", JSONObject.parseObject(
          "{\"t_1\":{\"index\":\"t_1\",\"id\":\"rogue_1_recruit_ticket_warrior\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722628,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_2\":{\"index\":\"t_2\",\"id\":\"rogue_1_recruit_ticket_support\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722628,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_3\":{\"index\":\"t_3\",\"id\":\"rogue_1_recruit_ticket_medic\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722628,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false}}"));
    }
    if (select.equals("recruit_group_random")) {
      current.getJSONObject("inventory").put("recruit", JSONObject.parseObject(
          "{\"t_1\":{\"index\":\"t_1\",\"id\":\"rogue_1_recruit_ticket_medic_sp\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722698,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_2\":{\"index\":\"t_2\",\"id\":\"rogue_1_recruit_ticket_all\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722698,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_3\":{\"index\":\"t_3\",\"id\":\"rogue_1_recruit_ticket_medic\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722698,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false}}"));
    }
    current.getJSONObject("player").getJSONArray("pending").remove(0);

    current.getJSONObject("player").getJSONArray("pending").getJSONObject(0).getJSONObject("content")
        .getJSONObject("initRecruit").put("tickets", JSONArray.parseArray("[\"t_1\",\"t_2\",\"t_3\"]"));

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    JSONObject jSONObject1 = new JSONObject();
    jSONObject1.put("current", current);
    modified.put("rlv2", jSONObject1);
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/activeRecruitTicket" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject activeRecruitTicket(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/activeRecruitTicket");

    String id = JsonBody.getString("id");

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

    JSONArray list = new JSONArray();

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
    String theme = current.getJSONObject("game").getString("theme");
    JSONObject recruitTickets = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
        .getJSONObject("recruitTickets");
    current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).put("state", Integer.valueOf(1));

    String ticketId = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).getString("id");

    JSONObject tempChar = JSONObject.parseObject(
        "{\"instId\":\"0\",\"charId\":\"temp\",\"type\":\"THIRD_LOW\",\"evolvePhase\":1,\"level\":55,\"exp\":0,\"favorPoint\":25570,\"potentialRank\":0,\"mainSkillLvl\":7,\"skills\":[],\"defaultSkillIndex\":0,\"skin\":\"temp\",\"upgradeLimited\":false,\"upgradePhase\":0,\"isUpgrade\":false,\"population\":0}");

    JSONObject ticket = recruitTickets.getJSONObject(ticketId);

    JSONArray professionList = ticket.getJSONArray("professionList");
    JSONArray rarityList = ticket.getJSONArray("rarityList");
    JSONArray extraFreeRarity = ticket.getJSONArray("extraFreeRarity");
    JSONArray extraCharIds = ticket.getJSONArray("extraCharIds");
    JSONArray extraFreeList = new JSONArray();
    for (int i = 0; i < extraCharIds.size(); i++) {
      tempChar.put("instId", Integer.valueOf(list.size() + 1));
      tempChar.put("charId", extraCharIds.getString(i));
      tempChar.put("skin", extraCharIds.getString(i) + "#1");
      list.add(JSONObject.parseObject(JSONObject.toJSONString(tempChar,
          new SerializerFeature[] { SerializerFeature.DisableCircularReferenceDetect })));
    }

    String relicsId = current.getJSONObject("inventory").getJSONObject("relic").getJSONObject("r_0").getString("id");
    JSONObject selectRelics = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
        .getJSONObject("relics").getJSONObject(relicsId);
    JSONArray relicsBuffs = selectRelics.getJSONArray("buffs");

    JSONArray dynamicUpdateList = new JSONArray();
    int j;
    for (j = 0; j < relicsBuffs.size(); j++) {
      if (relicsBuffs.getJSONObject(j).getString("key").equals("dynamic_update")) {
        String band = relicsBuffs.getJSONObject(j).getJSONArray("blackboard").getJSONObject(0).getString("valueStr")
            .substring("recruit_upgrade_".length()).toUpperCase(Locale.ROOT);
        dynamicUpdateList.add(band);
      }
    }

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject originalChar = JSONObject.parseObject(entry.getValue().toString());
      JSONObject userChar = JSONObject.parseObject(entry.getValue().toString());
      String charId = userChar.getString("charId");
      String charProfession = ArknightsApplication.characterJson.getJSONObject(charId).getString("profession");

      if (professionList.contains(charProfession)) {

        int charRarity = ArknightsApplication.characterJson.getJSONObject(charId).getIntValue("rarity");

        if (rarityList.contains(Integer.valueOf(charRarity))) {

          int charPopulation = 0;

          if (originalChar.getIntValue("evolvePhase") != 0 &&
              originalChar.getIntValue("evolvePhase") == 2) {
            userChar.put("evolvePhase", Integer.valueOf(1));
          }

          if (userChar.getIntValue("evolvePhase") == 1) {
            if (userChar.getJSONArray("skills").size() == 1) {
              userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
            }
            if (userChar.getJSONArray("skills").size() == 2) {
              userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
              userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", Integer.valueOf(0));
            }
            if (userChar.getJSONArray("skills").size() == 3) {
              userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
              userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", Integer.valueOf(0));
              userChar.getJSONArray("skills").getJSONObject(2).put("specializeLevel", Integer.valueOf(0));
              userChar.getJSONArray("skills").getJSONObject(2).put("unlock", Integer.valueOf(0));
            }
          }

          if (charRarity == 3) {
            charPopulation = 2;
            if (originalChar.getIntValue("level") > 60) {
              userChar.put("level", Integer.valueOf(60));
            }
          }
          if (charRarity == 4) {
            charPopulation = 3;
            if (originalChar.getIntValue("level") > 70) {
              userChar.put("level", Integer.valueOf(70));
            }
          }
          if (charRarity == 5) {
            charPopulation = 6;
            if (originalChar.getIntValue("level") > 80) {
              userChar.put("level", Integer.valueOf(80));
            }
          }

          userChar.put("isUpgrade", Boolean.valueOf(false));
          userChar.put("upgradePhase", Integer.valueOf(0));
          userChar.put("upgradeLimited", Boolean.valueOf(true));
          if (charRarity >= 3) {
            userChar.put("upgradeLimited", Boolean.valueOf(false));
            if (dynamicUpdateList.contains(charProfession)) {
              userChar.put("upgradeLimited", Boolean.valueOf(true));

              if (originalChar.getIntValue("evolvePhase") != 0 &&
                  originalChar.getIntValue("evolvePhase") == 2) {
                userChar.put("evolvePhase", Integer.valueOf(2));
              }
              userChar.put("skills", originalChar.getJSONArray("skills"));
              if (userChar.getIntValue("evolvePhase") == 1) {
                if (userChar.getJSONArray("skills").size() == 1) {
                  userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                }
                if (userChar.getJSONArray("skills").size() == 2) {
                  userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                  userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", Integer.valueOf(0));
                }
                if (userChar.getJSONArray("skills").size() == 3) {
                  userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                  userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", Integer.valueOf(0));
                  userChar.getJSONArray("skills").getJSONObject(2).put("specializeLevel", Integer.valueOf(0));
                  userChar.getJSONArray("skills").getJSONObject(2).put("unlock", Integer.valueOf(0));
                }
              }
              userChar.put("level", Integer.valueOf(originalChar.getIntValue("level")));
              userChar.put("upgradePhase", Integer.valueOf(originalChar.getIntValue("evolvePhase")));
            }
          }

          userChar.put("rarity", Integer.valueOf(charRarity));
          userChar.put("originalId", originalChar.getString("instId"));
          userChar.put("profession", charProfession);
          userChar.put("instId", Integer.valueOf(list.size() + 1));
          userChar.put("population", Integer.valueOf(charPopulation));
          userChar.put("type", "NORMAL");

          Boolean upgrade = Boolean.valueOf(false);
          for (Map.Entry Entry : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject troopChar = JSONObject.parseObject(Entry.getValue().toString());

            if (troopChar.getString("charId").equals(charId)) {

              if (!troopChar.getBooleanValue("upgradeLimited")) {

                if (charRarity == 3) {
                  charPopulation = 1;
                }
                if (charRarity == 4) {
                  charPopulation = 2;
                }
                if (charRarity == 5) {
                  charPopulation = 3;
                }

                if (originalChar.getIntValue("evolvePhase") != 0 &&
                    originalChar.getIntValue("evolvePhase") == 2) {
                  userChar.put("evolvePhase", Integer.valueOf(2));
                }

                userChar.put("isUpgrade", Boolean.valueOf(true));
                userChar.put("upgradeLimited", Boolean.valueOf(true));
                userChar.put("population", Integer.valueOf(charPopulation));
                userChar.put("skills", originalChar.getJSONArray("skills"));
                userChar.put("level", Integer.valueOf(originalChar.getIntValue("level")));
                userChar.put("upgradePhase", Integer.valueOf(originalChar.getIntValue("evolvePhase")));
                continue;
              }
              upgrade = Boolean.valueOf(true);
            }
          }
          if (!upgrade.booleanValue()) {
            list.add(userChar);
          }
        }
      }
    }

    for (j = 0; j < list.size(); j++) {
      if (extraFreeRarity.contains(Integer.valueOf(list.getJSONObject(j).getIntValue("rarity")))) {
        extraFreeList.add(Integer.valueOf(j));
      }
    }
    Collections.shuffle((List<?>) extraFreeList);

    if (extraFreeList.size() != 0) {

      JSONObject userChar = list.getJSONObject(extraFreeList.getIntValue(0));
      int charRarity = userChar.getIntValue("rarity");

      userChar.put("population", Integer.valueOf(0));
      if (charRarity == 3) {
        userChar.put("level", Integer.valueOf(60));
      }
      if (charRarity == 4) {
        userChar.put("level", Integer.valueOf(70));
      }
      if (charRarity == 5) {
        userChar.put("level", Integer.valueOf(80));
      }
      userChar.put("potentialRank", Integer.valueOf(5));
      userChar.put("mainSkillLvl", Integer.valueOf(7));
      userChar.put("favorPoint", Integer.valueOf(25570));
      userChar.put("evolvePhase", Integer.valueOf(1));
      userChar.put("type", "FREE");
    }

    current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).put("list", list);
    JSONObject pending = new JSONObject();
    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    JSONObject jSONObject1 = new JSONObject();
    jSONObject1.put("current", current);
    modified.put("rlv2", jSONObject1);
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/recruitChar" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject recruitChar(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/recruitChar");

    String ticketIndex = JsonBody.getString("ticketIndex");
    int optionId = JsonBody.getIntValue("optionId");

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

    JSONArray chars = new JSONArray();
    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
    String theme = current.getJSONObject("game").getString("theme");

    JSONObject ticket = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(ticketIndex);

    JSONObject optChar = ticket.getJSONArray("list").getJSONObject(optionId - 1);

    if (optChar.getBooleanValue("isUpgrade")) {
      for (Map.Entry Entry : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
        JSONObject troopChar = JSONObject.parseObject(Entry.getValue().toString());
        if (troopChar.getString("charId").equals(optChar.getString("charId"))) {
          optChar.put("instId", Integer.valueOf(troopChar.getIntValue("instId")));
          current.getJSONObject("troop").getJSONObject("chars").put(troopChar.getString("instId"), optChar);
          break;
        }
      }
    } else {
      optChar.put("instId", Integer.valueOf(current.getJSONObject("troop").getJSONObject("chars").size() + 1));
      current.getJSONObject("troop").getJSONObject("chars").put(optChar.getString("instId"), optChar);
    }

    chars.add(optChar);

    ticket.put("list", new JSONArray());
    ticket.put("result", JSONObject.parseObject(JSONObject.toJSONString(optChar,
        new SerializerFeature[] { SerializerFeature.DisableCircularReferenceDetect })));
    ticket.put("state", Integer.valueOf(2));

    current.getJSONObject("player").getJSONObject("property").getJSONObject("population").put("cost",
        Integer.valueOf(
            current.getJSONObject("player").getJSONObject("property").getJSONObject("population").getIntValue("cost")
                + optChar.getIntValue("population")));

    JSONArray pending = current.getJSONObject("player").getJSONArray("pending");
    JSONArray newPending = new JSONArray();
    Iterator<Object> iterator = pending.iterator();

    while (iterator.hasNext()) {
      JSONObject event = (JSONObject) iterator.next();

      if (event.getString("type").equals("RECRUIT")) {
        if (event.getJSONObject("content").getJSONObject("recruit").getString("ticket").equals(ticketIndex)) {
          int pendingIndex = event.getJSONObject("content").getJSONObject("recruit").getIntValue("pendingIndex");
          int rewardsIndex = event.getJSONObject("content").getJSONObject("recruit").getIntValue("rewardsIndex");
          if (pending.getJSONObject(pendingIndex).getString("type").equals("BATTLE_REWARD")) {
            JSONObject battleReward = pending.getJSONObject(pendingIndex).getJSONObject("content")
                .getJSONObject("battleReward");
            if (battleReward.getJSONArray("rewards").getJSONObject(rewardsIndex).getBooleanValue("isRelic")) {
              JSONObject relic = new JSONObject();
              String relicIndex = "r_" + current.getJSONObject("inventory").getJSONObject("relic").size() + '\001';
              relic.put("count", Integer.valueOf(1));
              relic.put("id", battleReward.getJSONArray("rewards").getJSONObject(rewardsIndex).getString("relicId"));
              relic.put("index", relicIndex);
              relic.put("ts", Long.valueOf((new Date()).getTime() / 1000L));
              current.getJSONObject("inventory").getJSONObject("relic").put(relicIndex, relic);
            }
          }
          continue;
        }
        current.getJSONObject("inventory").getJSONObject("recruit")
            .remove(event.getJSONObject("content").getJSONObject("recruit").getString("ticket"));
        continue;
      }
      newPending.add(event);
    }
    current.getJSONObject("player").put("pending", newPending);
    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    JSONObject jSONObject1 = new JSONObject();
    jSONObject1.put("current", current);
    modified.put("rlv2", jSONObject1);
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    result.put("chars", chars);
    return result;
  }

  @PostMapping(value = { "/closeRecruitTicket" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject closeRecruitTicket(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/closeRecruitTicket");

    String ticketIndex = JsonBody.getString("id");

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

    JSONArray chars = new JSONArray();
    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());
    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
    String theme = current.getJSONObject("game").getString("theme");
    JSONObject ticket = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(ticketIndex);
    JSONArray pending = current.getJSONObject("player").getJSONArray("pending");

    ticket.put("state", Integer.valueOf(2));
    ticket.put("result", null);
    ticket.put("list", new JSONArray());

    for (int i = 0; i < pending.size(); i++) {
      if (pending.getJSONObject(i).getString("type").equals("RECRUIT") &&
          pending.getJSONObject(i).getJSONObject("content").getJSONObject("recruit").getString("ticket")
              .equals(ticketIndex)) {
        pending.remove(i);
      }
    }
    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    JSONObject jSONObject1 = new JSONObject();
    jSONObject1.put("current", current);
    modified.put("rlv2", jSONObject1);
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    result.put("chars", chars);
    return result;
  }

  @PostMapping(value = { "/finishEvent" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject finishEvent(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/finishEvent");
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

    JSONArray chars = new JSONArray();

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");

    for (int n = 0; n < current.getJSONObject("player").getJSONArray("pending").size(); n++) {

      if (current.getJSONObject("player").getJSONArray("pending").size() == 1 &&
          current.getJSONObject("player").getJSONArray("pending").getJSONObject(n).getString("type")
              .equals("GAME_INIT_RECRUIT")) {
        current.getJSONObject("player").getJSONObject("cursor").put("zone", Integer.valueOf(1));
        current.getJSONObject("player").put("pending", new JSONArray());
        current.getJSONObject("player").put("state", "WAIT_MOVE");

        current.getJSONObject("map").put("zones",
            ArknightsApplication.roguelike.getJSONObject("rlv2").getJSONObject("customMap").getJSONObject("zones"));
      }
      if (current.getJSONObject("player").getJSONArray("pending").size() != 0 &&
          current.getJSONObject("player").getJSONArray("pending").getJSONObject(n).getString("type")
              .equals("GAME_INIT_MODE_RELIC")) {

        String theme = current.getJSONObject("game").getString("theme");
        JSONObject property = current.getJSONObject("player").getJSONObject("property");
        JSONObject roguelikeItems = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
            .getJSONObject("items");

        JSONArray items = current.getJSONObject("player").getJSONArray("pending").getJSONObject(n)
            .getJSONObject("content").getJSONObject("initModeRelic").getJSONArray("items");

        for (int i = 0; i < items.size(); i++) {

          Boolean UPGRADE = Boolean.valueOf(false);

          String itemId = items.getString(i);
          if (ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("relics")
              .containsKey(itemId)) {
            JSONObject selectRelics = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
                .getJSONObject("relics").getJSONObject(itemId);

            JSONArray buffs = selectRelics.getJSONArray("buffs");

            for (int m = 0; m < buffs.size(); m++) {
              String key = buffs.getJSONObject(m).getString("key");
              JSONArray blackboard = buffs.getJSONObject(m).getJSONArray("blackboard");
              if (key.equals("level_life_point_add")) {
                current.getJSONObject("buff").put("tmpHP",
                    Integer.valueOf(current.getJSONObject("buff").getIntValue("tmpHP")
                        + blackboard.getJSONObject(0).getIntValue("value")));
              }
              if (key.equals("immediate_reward")) {

                String valueStr = blackboard.getJSONObject(0).getString("valueStr");
                String buffType = roguelikeItems.getJSONObject(valueStr).getString("type");
                int count = blackboard.getJSONObject(1).getIntValue("value");

                if (buffType.equals("HP")) {
                  property.put("hp", Integer.valueOf(property.getIntValue("hp") + count));
                }
                if (buffType.equals("GOLD")) {
                  property.put("gold", Integer.valueOf(property.getIntValue("gold") + count));
                }
                if (buffType.equals("SQUAD_CAPACITY")) {
                  property.put("capacity", Integer.valueOf(property.getIntValue("capacity") + count));
                }
                if (buffType.equals("POPULATION")) {
                  property.getJSONObject("population").put("max",
                      Integer.valueOf(property.getJSONObject("population").getIntValue("max") + count));
                }
                if (buffType.equals("UPGRADE_TICKET")) {
                  UPGRADE = Boolean.valueOf(true);

                  int recruitIndex = current.getJSONObject("inventory").getJSONObject("recruit").size() + 1;
                  String id = "t_" + recruitIndex;
                  JSONObject recruit = new JSONObject();
                  recruit.put("from", "buff");
                  recruit.put("id", valueStr);
                  recruit.put("index", "t_" + recruitIndex);
                  recruit.put("mustExtra", Integer.valueOf(0));
                  recruit.put("needAssist", Boolean.valueOf(false));
                  recruit.put("state", Integer.valueOf(0));
                  recruit.put("result", null);
                  recruit.put("ts", Long.valueOf((new Date()).getTime() / 1000L));
                  current.getJSONObject("inventory").getJSONObject("recruit").put("t_" + recruitIndex, recruit);

                  JSONArray list = new JSONArray();

                  JSONObject upgradeTickets = ArknightsApplication.roguelikeTable.getJSONObject("details")
                      .getJSONObject(theme).getJSONObject("upgradeTickets");
                  current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).put("state",
                      Integer.valueOf(1));

                  String ticketId = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id)
                      .getString("id");

                  JSONObject ticket = upgradeTickets.getJSONObject(ticketId);

                  JSONArray professionList = ticket.getJSONArray("professionList");
                  JSONArray rarityList = ticket.getJSONArray("rarityList");

                  for (Map.Entry Entry : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
                    JSONObject troopChar = JSONObject.parseObject(Entry.getValue().toString());
                    String profession = troopChar.getString("profession");
                    int rarity = troopChar.getIntValue("rarity");

                    if (professionList.contains(profession) &&
                        rarityList.contains(Integer.valueOf(rarity)) &&
                        !troopChar.getBooleanValue("upgradeLimited")) {

                      JSONObject originalChar = UserSyncData.getJSONObject("troop").getJSONObject("chars")
                          .getJSONObject(troopChar.getString("originalId"));

                      if (originalChar.getIntValue("evolvePhase") != 0 &&
                          originalChar.getIntValue("evolvePhase") == 2) {
                        troopChar.put("evolvePhase", Integer.valueOf(2));
                      }

                      troopChar.put("isUpgrade", Boolean.valueOf(true));
                      troopChar.put("upgradeLimited", Boolean.valueOf(true));
                      troopChar.put("population", Integer.valueOf(0));
                      troopChar.put("skills", originalChar.getJSONArray("skills"));
                      troopChar.put("level", Integer.valueOf(originalChar.getIntValue("level")));
                      troopChar.put("upgradePhase", Integer.valueOf(originalChar.getIntValue("evolvePhase")));

                      list.add(troopChar);
                    }
                  }
                  recruit.put("list", list);

                  JSONObject RECRUIT = new JSONObject();
                  JSONObject content = new JSONObject();

                  JSONObject recruitTicket = new JSONObject();
                  recruitTicket.put("ticket", id);
                  recruitTicket.put("pendingIndex", Integer.valueOf(0));
                  recruitTicket.put("rewardsIndex", Integer.valueOf(0));
                  recruitTicket.put("isRelic", Boolean.valueOf(true));
                  recruitTicket.put("relicId", itemId);

                  content.put("recruit", recruitTicket);
                  RECRUIT.put("content", content);
                  RECRUIT.put("index", "e_" + current.getJSONObject("player").getJSONArray("pending").size() + '\001');
                  RECRUIT.put("type", "RECRUIT");
                  current.getJSONObject("player").getJSONArray("pending").add(0, RECRUIT);
                }

                if (!UPGRADE.booleanValue()) {
                  JSONObject relic = new JSONObject();
                  String relicIndex = "r_" + current.getJSONObject("inventory").getJSONObject("relic").size() + '\001';
                  relic.put("count", Integer.valueOf(1));
                  relic.put("id", itemId);
                  relic.put("index", relicIndex);
                  relic.put("ts", Long.valueOf((new Date()).getTime() / 1000L));
                  current.getJSONObject("inventory").getJSONObject("relic").put(relicIndex, relic);
                }
              }
            }
          }
        }
        current.getJSONObject("player").getJSONArray("pending").remove(0);
      }
    }
    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    JSONObject jSONObject1 = new JSONObject();
    jSONObject1.put("current", current);
    modified.put("rlv2", jSONObject1);
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    result.put("chars", chars);
    return result;
  }

  @PostMapping(value = { "/normal/unlockBuff" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject unlockBuff(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/normal/unlockBuff");

    String theme = JsonBody.getString("theme");
    String buff = JsonBody.getString("buff");

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

    JSONObject outer = UserSyncData.getJSONObject("rlv2").getJSONObject("outer").getJSONObject(theme);

    JSONObject outBuff = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
        .getJSONObject("developments").getJSONObject(buff);

    outer.getJSONObject("buff").put("pointCost",
        Integer.valueOf(outer.getJSONObject("buff").getIntValue("pointCost") + outBuff.getIntValue("tokenCost")));

    if (outer.getJSONObject("buff").getIntValue("pointCost") > outer.getJSONObject("buff").getIntValue("pointOwned")) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("msg", "error");
      return jSONObject;
    }

    outer.getJSONObject("buff").getJSONObject("unlocked").put(buff, Integer.valueOf(1));

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    JSONObject jSONObject1 = new JSONObject();
    JSONObject tmp = new JSONObject();
    JSONObject tmp2 = new JSONObject();
    tmp2.put("buff", outer.getJSONObject("buff"));
    tmp.put(theme, tmp2);
    jSONObject1.put("outer", tmp);
    modified.put("rlv2", jSONObject1);
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/moveAndBattleStart" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject moveAndBattleStart(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/moveAndBattleStart");

    JSONObject moveTo = JsonBody.getJSONObject("to");

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

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");

    current.getJSONObject("player").put("pending", new JSONArray());
    current.getJSONObject("player").put("state", "PENDING");
    current.getJSONObject("player").getJSONObject("cursor").put("position", moveTo);
    current.getJSONObject("player").getJSONArray("trace").add(current.getJSONObject("player").getJSONObject("cursor"));

    JSONObject tmp = new JSONObject();
    JSONObject content = new JSONObject();
    JSONObject battle = new JSONObject();

    battle.put("chestCnt", Integer.valueOf(10));
    battle.put("goldTrapCnt", Integer.valueOf(10));
    battle.put("state", Integer.valueOf(1));
    battle.put("tmpChar", new JSONArray());

    JSONArray unKeepBuff = new JSONArray();
    JSONArray customBuff = ArknightsApplication.roguelike.getJSONObject("rlv2").getJSONArray("customRelic");
    for (int i = 0; i < customBuff.size(); i++) {
      if (customBuff.getJSONObject(i).getBooleanValue("enableRelic")) {
        JSONArray buffs = customBuff.getJSONObject(i).getJSONArray("buffs");
        for (int n = 0; n < buffs.size(); n++) {
          unKeepBuff.add(buffs.getJSONObject(n));
        }
      }
    }

    battle.put("unKeepBuff", unKeepBuff);

    content.put("battle", battle);

    tmp.put("content", content);
    tmp.put("index", "e_" + current.getJSONObject("player").getJSONArray("pending").size() + '\001');
    tmp.put("type", "BATTLE");

    current.getJSONObject("player").getJSONArray("pending").add(tmp);

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/battleFinish" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject battleFinish(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/battleFinish");

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

    JSONObject BattleData = Utils.BattleData_decrypt(JsonBody.getString("data"),
        UserSyncData.getJSONObject("pushFlags").getString("status"));

    int completeState = BattleData.getIntValue("completeState");

    if (ArknightsApplication.serverConfig.getJSONObject("battle").getBooleanValue("debug")) {
      completeState = 3;
    }
    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");

    current.getJSONObject("player").put("pending", new JSONArray());

    if (completeState == 1) {

      JSONObject jSONObject1 = new JSONObject();

      jSONObject1.put("detailStr", "");
      jSONObject1.put("popReport", Boolean.valueOf(false));
      jSONObject1.put("success", Integer.valueOf(0));
      jSONObject1.put("result", JSONObject.parseObject(
          "{\"brief\":{\"level\":4,\"over\":true,\"success\":0,\"ending\":\"\",\"theme\":\"rogue_1\",\"mode\":\"EASY\",\"predefined\":null,\"band\":\"rogue_1_band_3\",\"startTs\":1642943654,\"endTs\":1642945001,\"endZoneId\":\"zone_3\",\"endProperty\":{\"hp\":0,\"gold\":16,\"populationCost\":19,\"populationMax\":29}},\"record\":{\"cntZone\":3,\"cntBattleNormal\":5,\"cntBattleElite\":1,\"cntBattleBoss\":0,\"cntArrivedNode\":13,\"cntRecruitChar\":8,\"cntUpgradeChar\":2,\"cntKillEnemy\":217,\"cntShopBuy\":2,\"cntPerfectBattle\":6,\"cntProtectBox\":4,\"cntRecruitFree\":0,\"cntRecruitAssist\":2,\"cntRecruitNpc\":3,\"cntRecruitProfession\":{\"SNIPER\":1,\"CASTER\":1,\"PIONEER\":2,\"TANK\":1,\"WARRIOR\":2,\"MEDIC\":1},\"troopChars\":[{\"charId\":\"char_1013_chen2\",\"type\":\"ASSIST\",\"upgradePhase\":1,\"evolvePhase\":2,\"level\":90},{\"charId\":\"char_328_cammou\",\"type\":\"ASSIST\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":60},{\"charId\":\"char_504_rguard\",\"type\":\"THIRD_LOW\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_201_moeshd\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":70},{\"charId\":\"char_504_rguard\",\"type\":\"THIRD_LOW\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_143_ghost\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":70},{\"charId\":\"char_208_melan\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_510_amedic\",\"type\":\"THIRD\",\"upgradePhase\":1,\"evolvePhase\":2,\"level\":80}],\"cntArrivedNodeType\":{\"BATTLE_NORMAL\":4,\"INCIDENT\":4,\"SHOP\":1,\"BATTLE_ELITE\":2,\"TREASURE\":1,\"REST\":1},\"relicList\":[\"rogue_1_relic_a01\",\"rogue_1_relic_r09\",\"rogue_1_relic_q02\",\"rogue_1_relic_a45\",\"rogue_1_relic_a11\"],\"capsuleList\":[\"rogue_1_capsule_3\",\"rogue_1_capsule_7\",\"rogue_1_capsule_8\"],\"activeToolList\":[],\"zones\":[{\"index\":1,\"zoneId\":\"zone_1\",\"variation\":[]},{\"index\":2,\"zoneId\":\"zone_2\",\"variation\":[]},{\"index\":3,\"zoneId\":\"zone_3\",\"variation\":[]}]}}"));

      JSONObject jSONObject2 = new JSONObject();
      jSONObject2.put("content", jSONObject1);
      jSONObject2.put("index", "e_" + current.getJSONObject("player").getJSONArray("pending").size() + '\001');
      jSONObject2.put("type", "GAME_SETTLE");

      current.getJSONObject("player").getJSONArray("pending").add(jSONObject2);
    }

    JSONObject content = new JSONObject();
    JSONObject battleReward = new JSONObject();

    JSONObject earn = new JSONObject();
    earn.put("damage", Integer.valueOf(0));
    earn.put("exp", Integer.valueOf(0));
    earn.put("hp", Integer.valueOf(0));
    earn.put("populationMax", Integer.valueOf(4));
    earn.put("squadCapacity", Integer.valueOf(1));

    current.getJSONObject("player").getJSONObject("property").put("capacity",
        Integer.valueOf(current.getJSONObject("player").getJSONObject("property").getIntValue("capacity") + 1));

    current.getJSONObject("player").getJSONObject("property").getJSONObject("population").put("max", Integer.valueOf(
        current.getJSONObject("player").getJSONObject("property").getJSONObject("population").getIntValue("max") + 4));

    JSONArray items = new JSONArray();
    JSONObject item = new JSONObject();

    item.put("count", Integer.valueOf(1));
    item.put("id", "rogue_1_recruit_ticket_all");
    item.put("sub", Integer.valueOf(0));
    items.add(item);

    JSONArray rewards = new JSONArray();
    JSONObject reward = new JSONObject();

    reward.put("done", Integer.valueOf(0));
    reward.put("index", Integer.valueOf(0));
    reward.put("items", items);
    rewards.add(reward);

    battleReward.put("earn", earn);
    battleReward.put("rewards", rewards);
    battleReward.put("show", Integer.valueOf(1));
    content.put("battleReward", battleReward);
    JSONObject event = new JSONObject();
    event.put("content", content);
    event.put("index", "e_" + current.getJSONObject("player").getJSONArray("pending").size() + '\001');
    event.put("type", "BATTLE_REWARD");

    current.getJSONObject("player").getJSONArray("pending").add(event);

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/chooseBattleReward" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject chooseBattleReward(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/chooseBattleReward");

    int index = JsonBody.getIntValue("index");
    int sub = JsonBody.getIntValue("sub");

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

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
    String theme = current.getJSONObject("game").getString("theme");
    JSONArray pending = current.getJSONObject("player").getJSONArray("pending");
    JSONObject property = current.getJSONObject("player").getJSONObject("property");
    JSONObject roguelikeItems = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
        .getJSONObject("items");

    JSONObject BATTLE_REWARD = new JSONObject();
    JSONArray items = new JSONArray();
    int pendingIndex = 0;
    int rewardsIndex = 0;

    for (int i = 0; i < pending.size(); i++) {
      if (pending.getJSONObject(i).getString("type").equals("BATTLE_REWARD")) {
        BATTLE_REWARD = pending.getJSONObject(i).getJSONObject("content").getJSONObject("battleReward");
        pendingIndex = i;
      }
    }

    JSONObject reward = new JSONObject();
    int j;
    for (j = 0; j < BATTLE_REWARD.getJSONArray("rewards").size(); j++) {

      int rewardDone = BATTLE_REWARD.getJSONArray("rewards").getJSONObject(j).getIntValue("done");
      int rewardIndex = BATTLE_REWARD.getJSONArray("rewards").getJSONObject(j).getIntValue("index");

      if (rewardIndex == index) {
        reward = BATTLE_REWARD.getJSONArray("rewards").getJSONObject(j);
        rewardsIndex = j;
        if (rewardDone == 0) {
          items = reward.getJSONArray("items");
        }
      }
    }

    for (j = 0; j < items.size(); j++) {

      int itemCount = items.getJSONObject(j).getIntValue("count");
      int itemSub = items.getJSONObject(j).getIntValue("sub");
      String itemId = items.getJSONObject(j).getString("id");

      String type = roguelikeItems.getJSONObject(itemId).getString("type");

      reward.put("done", Integer.valueOf(1));

      if (type.equals("HP")) {
        property.put("hp", Integer.valueOf(property.getIntValue("hp") + itemCount));
      }

      if (type.equals("GOLD")) {
        property.put("gold", Integer.valueOf(property.getIntValue("gold") + itemCount));
      }

      if (type.equals("SQUAD_CAPACITY")) {
        property.put("capacity", Integer.valueOf(property.getIntValue("capacity") + itemCount));
      }

      if (type.equals("POPULATION")) {
        property.getJSONObject("population").put("max",
            Integer.valueOf(property.getJSONObject("population").getIntValue("max") + itemCount));
      }

      if (type.equals("EXP")) {
        property.put("exp", Integer.valueOf(property.getIntValue("exp") + itemCount));
      }

      if (type.equals("RELIC")) {

        Boolean UPGRADE = Boolean.valueOf(false);

        JSONObject selectRelics = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
            .getJSONObject("relics").getJSONObject(itemId);

        JSONArray buffs = selectRelics.getJSONArray("buffs");

        for (int m = 0; m < buffs.size(); m++) {
          String key = buffs.getJSONObject(m).getString("key");
          JSONArray blackboard = buffs.getJSONObject(m).getJSONArray("blackboard");
          if (key.equals("level_life_point_add")) {
            current.getJSONObject("buff").put("tmpHP", Integer.valueOf(
                current.getJSONObject("buff").getIntValue("tmpHP") + blackboard.getJSONObject(0).getIntValue("value")));
          }
          if (key.equals("immediate_reward")) {

            String valueStr = blackboard.getJSONObject(0).getString("valueStr");
            String buffType = roguelikeItems.getJSONObject(valueStr).getString("type");
            int count = blackboard.getJSONObject(1).getIntValue("value");

            if (buffType.equals("HP")) {
              property.put("hp", Integer.valueOf(property.getIntValue("hp") + count));
            }
            if (buffType.equals("GOLD")) {
              property.put("gold", Integer.valueOf(property.getIntValue("gold") + count));
            }
            if (buffType.equals("SQUAD_CAPACITY")) {
              property.put("capacity", Integer.valueOf(property.getIntValue("capacity") + count));
            }
            if (buffType.equals("POPULATION")) {
              property.getJSONObject("population").put("max",
                  Integer.valueOf(property.getJSONObject("population").getIntValue("max") + count));
            }
            if (buffType.equals("UPGRADE_TICKET")) {
              UPGRADE = Boolean.valueOf(true);

              int recruitIndex = current.getJSONObject("inventory").getJSONObject("recruit").size() + 1;
              String id = "t_" + recruitIndex;
              JSONObject recruit = new JSONObject();
              recruit.put("from", "buff");
              recruit.put("id", valueStr);
              recruit.put("index", "t_" + recruitIndex);
              recruit.put("mustExtra", Integer.valueOf(0));
              recruit.put("needAssist", Boolean.valueOf(false));
              recruit.put("state", Integer.valueOf(0));
              recruit.put("result", null);
              recruit.put("ts", Long.valueOf((new Date()).getTime() / 1000L));
              current.getJSONObject("inventory").getJSONObject("recruit").put("t_" + recruitIndex, recruit);

              JSONArray list = new JSONArray();

              JSONObject upgradeTickets = ArknightsApplication.roguelikeTable.getJSONObject("details")
                  .getJSONObject(theme).getJSONObject("upgradeTickets");
              current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).put("state",
                  Integer.valueOf(1));

              String ticketId = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id)
                  .getString("id");

              JSONObject ticket = upgradeTickets.getJSONObject(ticketId);

              JSONArray professionList = ticket.getJSONArray("professionList");
              JSONArray rarityList = ticket.getJSONArray("rarityList");

              for (Map.Entry Entry : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
                JSONObject troopChar = JSONObject.parseObject(Entry.getValue().toString());
                String profession = troopChar.getString("profession");
                int rarity = troopChar.getIntValue("rarity");

                if (professionList.contains(profession) &&
                    rarityList.contains(Integer.valueOf(rarity)) &&
                    !troopChar.getBooleanValue("upgradeLimited")) {

                  JSONObject originalChar = UserSyncData.getJSONObject("troop").getJSONObject("chars")
                      .getJSONObject(troopChar.getString("originalId"));

                  if (originalChar.getIntValue("evolvePhase") != 0 &&
                      originalChar.getIntValue("evolvePhase") == 2) {
                    troopChar.put("evolvePhase", Integer.valueOf(2));
                  }

                  troopChar.put("isUpgrade", Boolean.valueOf(true));
                  troopChar.put("upgradeLimited", Boolean.valueOf(true));
                  troopChar.put("population", Integer.valueOf(0));
                  troopChar.put("skills", originalChar.getJSONArray("skills"));
                  troopChar.put("level", Integer.valueOf(originalChar.getIntValue("level")));
                  troopChar.put("upgradePhase", Integer.valueOf(originalChar.getIntValue("evolvePhase")));

                  list.add(troopChar);
                }
              }
              recruit.put("list", list);

              JSONObject RECRUIT = new JSONObject();
              JSONObject content = new JSONObject();

              JSONObject recruitTicket = new JSONObject();
              recruitTicket.put("ticket", id);
              recruitTicket.put("pendingIndex", Integer.valueOf(pendingIndex));
              recruitTicket.put("rewardsIndex", Integer.valueOf(rewardsIndex));
              recruitTicket.put("isRelic", Boolean.valueOf(true));
              recruitTicket.put("relicId", itemId);

              content.put("recruit", recruitTicket);
              RECRUIT.put("content", content);
              RECRUIT.put("index", "e_" + current.getJSONObject("player").getJSONArray("pending").size() + '\001');
              RECRUIT.put("type", "RECRUIT");
              current.getJSONObject("player").getJSONArray("pending").add(0, RECRUIT);
            }

            if (!UPGRADE.booleanValue()) {
              JSONObject relic = new JSONObject();
              String relicIndex = "r_" + current.getJSONObject("inventory").getJSONObject("relic").size() + '\001';
              relic.put("count", Integer.valueOf(itemCount));
              relic.put("id", itemId);
              relic.put("index", relicIndex);
              relic.put("ts", Long.valueOf((new Date()).getTime() / 1000L));
              current.getJSONObject("inventory").getJSONObject("relic").put(relicIndex, relic);
            }
          }
        }
      }
      if (type.equals("UPGRADE_TICKET")) {

        int relicIndex = current.getJSONObject("inventory").getJSONObject("recruit").size() + 1;
        String id = "t_" + relicIndex;
        JSONObject relic = new JSONObject();
        relic.put("from", "buff");
        relic.put("id", itemId);
        relic.put("index", "t_" + relicIndex);
        relic.put("mustExtra", Integer.valueOf(0));
        relic.put("needAssist", Boolean.valueOf(false));
        relic.put("state", Integer.valueOf(0));
        relic.put("result", null);
        relic.put("ts", Long.valueOf((new Date()).getTime() / 1000L));
        current.getJSONObject("inventory").getJSONObject("recruit").put("t_" + relicIndex, relic);

        JSONArray list = new JSONArray();

        JSONObject upgradeTickets = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
            .getJSONObject("upgradeTickets");
        current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).put("state", Integer.valueOf(1));

        String ticketId = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).getString("id");

        JSONObject ticket = upgradeTickets.getJSONObject(ticketId);

        JSONArray professionList = ticket.getJSONArray("professionList");
        JSONArray rarityList = ticket.getJSONArray("rarityList");

        for (Map.Entry Entry : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
          JSONObject troopChar = JSONObject.parseObject(Entry.getValue().toString());
          String profession = troopChar.getString("profession");
          int rarity = troopChar.getIntValue("rarity");

          if (professionList.contains(profession) &&
              rarityList.contains(Integer.valueOf(rarity)) &&
              !troopChar.getBooleanValue("upgradeLimited")) {

            JSONObject originalChar = UserSyncData.getJSONObject("troop").getJSONObject("chars")
                .getJSONObject(troopChar.getString("originalId"));

            if (originalChar.getIntValue("evolvePhase") != 0 &&
                originalChar.getIntValue("evolvePhase") == 2) {
              troopChar.put("evolvePhase", Integer.valueOf(2));
            }

            troopChar.put("isUpgrade", Boolean.valueOf(true));
            troopChar.put("upgradeLimited", Boolean.valueOf(true));
            troopChar.put("population", Integer.valueOf(0));
            troopChar.put("skills", originalChar.getJSONArray("skills"));
            if (troopChar.getIntValue("evolvePhase") == 1) {
              if (troopChar.getJSONArray("skills").size() == 1) {
                troopChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
              }
              if (troopChar.getJSONArray("skills").size() == 2) {
                troopChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                troopChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", Integer.valueOf(0));
              }
              if (troopChar.getJSONArray("skills").size() == 3) {
                troopChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                troopChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", Integer.valueOf(0));
                troopChar.getJSONArray("skills").getJSONObject(2).put("specializeLevel", Integer.valueOf(0));
                troopChar.getJSONArray("skills").getJSONObject(2).put("unlock", Integer.valueOf(0));
              }
            }
            troopChar.put("level", Integer.valueOf(originalChar.getIntValue("level")));
            troopChar.put("upgradePhase", Integer.valueOf(originalChar.getIntValue("evolvePhase")));

            list.add(troopChar);
          }
        }
        relic.put("list", list);

        JSONObject RECRUIT = new JSONObject();
        JSONObject content = new JSONObject();

        JSONObject recruit = new JSONObject();
        recruit.put("ticket", id);
        recruit.put("pendingIndex", Integer.valueOf(pendingIndex));
        recruit.put("rewardsIndex", Integer.valueOf(rewardsIndex));
        recruit.put("isRelic", Boolean.valueOf(false));
        recruit.put("relicId", null);

        content.put("recruit", recruit);
        RECRUIT.put("content", content);
        RECRUIT.put("index", "e_" + current.getJSONObject("player").getJSONArray("pending").size() + '\001');
        RECRUIT.put("type", "RECRUIT");
        current.getJSONObject("player").getJSONArray("pending").add(0, RECRUIT);
      }
      if (type.equals("RECRUIT_TICKET")) {

        int relicIndex = current.getJSONObject("inventory").getJSONObject("recruit").size() + 1;
        String id = "t_" + relicIndex;
        JSONObject relic = new JSONObject();
        relic.put("from", "battle");
        relic.put("id", itemId);
        relic.put("index", "t_" + relicIndex);
        relic.put("mustExtra", Integer.valueOf(0));
        relic.put("needAssist", Boolean.valueOf(false));
        relic.put("state", Integer.valueOf(0));
        relic.put("result", null);
        relic.put("ts", Long.valueOf((new Date()).getTime() / 1000L));
        current.getJSONObject("inventory").getJSONObject("recruit").put("t_" + relicIndex, relic);

        JSONArray list = new JSONArray();

        JSONObject recruitTickets = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
            .getJSONObject("recruitTickets");
        current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).put("state", Integer.valueOf(1));

        String ticketId = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).getString("id");

        JSONObject tempChar = JSONObject.parseObject(
            "{\"instId\":\"0\",\"charId\":\"temp\",\"type\":\"THIRD_LOW\",\"evolvePhase\":1,\"level\":55,\"exp\":0,\"favorPoint\":25570,\"potentialRank\":0,\"mainSkillLvl\":7,\"skills\":[],\"defaultSkillIndex\":0,\"skin\":\"temp\",\"upgradeLimited\":false,\"upgradePhase\":0,\"isUpgrade\":false,\"population\":0}");

        JSONObject ticket = recruitTickets.getJSONObject(ticketId);

        JSONArray professionList = ticket.getJSONArray("professionList");
        JSONArray rarityList = ticket.getJSONArray("rarityList");
        JSONArray extraFreeRarity = ticket.getJSONArray("extraFreeRarity");
        JSONArray extraCharIds = ticket.getJSONArray("extraCharIds");
        JSONArray extraFreeList = new JSONArray();
        for (int m = 0; m < extraCharIds.size(); m++) {
          tempChar.put("instId", Integer.valueOf(m));
          tempChar.put("charId", extraCharIds.getString(m));
          tempChar.put("skin", extraCharIds.getString(m) + "#1");
          list.add(JSONObject.parseObject(JSONObject.toJSONString(tempChar,
              new SerializerFeature[] { SerializerFeature.DisableCircularReferenceDetect })));
        }

        String relicsId = current.getJSONObject("inventory").getJSONObject("relic").getJSONObject("r_0")
            .getString("id");
        JSONObject selectRelics = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
            .getJSONObject("relics").getJSONObject(relicsId);
        JSONArray relicsBuffs = selectRelics.getJSONArray("buffs");

        JSONArray dynamicUpdateList = new JSONArray();
        int k;
        for (k = 0; k < relicsBuffs.size(); k++) {
          if (relicsBuffs.getJSONObject(k).getString("key").equals("dynamic_update")) {
            String band = relicsBuffs.getJSONObject(k).getJSONArray("blackboard").getJSONObject(0).getString("valueStr")
                .substring("recruit_upgrade_".length()).toUpperCase(Locale.ROOT);
            dynamicUpdateList.add(band);
          }
        }

        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
          JSONObject originalChar = JSONObject.parseObject(entry.getValue().toString());
          JSONObject userChar = JSONObject.parseObject(entry.getValue().toString());
          String charId = userChar.getString("charId");
          String charProfession = ArknightsApplication.characterJson.getJSONObject(charId).getString("profession");

          if (professionList.contains(charProfession)) {

            int charRarity = ArknightsApplication.characterJson.getJSONObject(charId).getIntValue("rarity");

            if (rarityList.contains(Integer.valueOf(charRarity))) {

              int charPopulation = 0;

              if (originalChar.getIntValue("evolvePhase") != 0 &&
                  originalChar.getIntValue("evolvePhase") == 2) {
                userChar.put("evolvePhase", Integer.valueOf(1));
              }

              if (userChar.getIntValue("evolvePhase") == 1) {
                if (userChar.getJSONArray("skills").size() == 1) {
                  userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                }
                if (userChar.getJSONArray("skills").size() == 2) {
                  userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                  userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", Integer.valueOf(0));
                }
                if (userChar.getJSONArray("skills").size() == 3) {
                  userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                  userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", Integer.valueOf(0));
                  userChar.getJSONArray("skills").getJSONObject(2).put("specializeLevel", Integer.valueOf(0));
                  userChar.getJSONArray("skills").getJSONObject(2).put("unlock", Integer.valueOf(0));
                }
              }

              if (charRarity == 3) {
                charPopulation = 2;
                if (originalChar.getIntValue("level") > 60) {
                  userChar.put("level", Integer.valueOf(60));
                }
              }
              if (charRarity == 4) {
                charPopulation = 3;
                if (originalChar.getIntValue("level") > 70) {
                  userChar.put("level", Integer.valueOf(70));
                }
              }
              if (charRarity == 5) {
                charPopulation = 6;
                if (originalChar.getIntValue("level") > 80) {
                  userChar.put("level", Integer.valueOf(80));
                }
              }

              userChar.put("isUpgrade", Boolean.valueOf(false));
              userChar.put("upgradePhase", Integer.valueOf(0));
              userChar.put("upgradeLimited", Boolean.valueOf(true));
              if (charRarity >= 3) {
                userChar.put("upgradeLimited", Boolean.valueOf(false));
                if (dynamicUpdateList.contains(charProfession)) {
                  userChar.put("upgradeLimited", Boolean.valueOf(true));

                  if (originalChar.getIntValue("evolvePhase") != 0 &&
                      originalChar.getIntValue("evolvePhase") == 2) {
                    userChar.put("evolvePhase", Integer.valueOf(2));
                  }
                  userChar.put("skills", originalChar.getJSONArray("skills"));
                  if (userChar.getIntValue("evolvePhase") == 1) {
                    if (userChar.getJSONArray("skills").size() == 1) {
                      userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                    }
                    if (userChar.getJSONArray("skills").size() == 2) {
                      userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                      userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", Integer.valueOf(0));
                    }
                    if (userChar.getJSONArray("skills").size() == 3) {
                      userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", Integer.valueOf(0));
                      userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", Integer.valueOf(0));
                      userChar.getJSONArray("skills").getJSONObject(2).put("specializeLevel", Integer.valueOf(0));
                      userChar.getJSONArray("skills").getJSONObject(2).put("unlock", Integer.valueOf(0));
                    }
                  }
                  userChar.put("level", Integer.valueOf(originalChar.getIntValue("level")));
                  userChar.put("upgradePhase", Integer.valueOf(originalChar.getIntValue("evolvePhase")));
                }
              }

              userChar.put("rarity", Integer.valueOf(charRarity));
              userChar.put("originalId", originalChar.getString("instId"));
              userChar.put("profession", charProfession);
              userChar.put("instId", Integer.valueOf(list.size()));
              userChar.put("population", Integer.valueOf(charPopulation));
              userChar.put("type", "NORMAL");

              Boolean upgrade = Boolean.valueOf(false);
              for (Map.Entry Entry : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
                JSONObject troopChar = JSONObject.parseObject(Entry.getValue().toString());

                if (troopChar.getString("charId").equals(charId)) {

                  if (!troopChar.getBooleanValue("upgradeLimited")) {

                    if (charRarity == 3) {
                      charPopulation = 1;
                    }
                    if (charRarity == 4) {
                      charPopulation = 2;
                    }
                    if (charRarity == 5) {
                      charPopulation = 3;
                    }

                    if (originalChar.getIntValue("evolvePhase") != 0 &&
                        originalChar.getIntValue("evolvePhase") == 2) {
                      userChar.put("evolvePhase", Integer.valueOf(2));
                    }

                    userChar.put("isUpgrade", Boolean.valueOf(true));
                    userChar.put("upgradeLimited", Boolean.valueOf(true));
                    userChar.put("population", Integer.valueOf(charPopulation));
                    userChar.put("skills", originalChar.getJSONArray("skills"));
                    userChar.put("level", Integer.valueOf(originalChar.getIntValue("level")));
                    userChar.put("upgradePhase", Integer.valueOf(originalChar.getIntValue("evolvePhase")));
                    continue;
                  }
                  upgrade = Boolean.valueOf(true);
                }
              }
              if (!upgrade.booleanValue()) {
                list.add(userChar);
              }
            }
          }
        }

        for (k = 0; k < list.size(); k++) {
          if (extraFreeRarity.contains(Integer.valueOf(list.getJSONObject(k).getIntValue("rarity")))) {
            extraFreeList.add(Integer.valueOf(k));
          }
        }
        Collections.shuffle((List<?>) extraFreeList);

        if (extraFreeList.size() != 0) {

          JSONObject userChar = list.getJSONObject(extraFreeList.getIntValue(0));
          int charRarity = userChar.getIntValue("rarity");

          userChar.put("population", Integer.valueOf(0));
          if (charRarity == 3) {
            userChar.put("level", Integer.valueOf(60));
          }
          if (charRarity == 4) {
            userChar.put("level", Integer.valueOf(70));
          }
          if (charRarity == 5) {
            userChar.put("level", Integer.valueOf(80));
          }
          userChar.put("potentialRank", Integer.valueOf(5));
          userChar.put("mainSkillLvl", Integer.valueOf(7));
          userChar.put("favorPoint", Integer.valueOf(25570));
          userChar.put("evolvePhase", Integer.valueOf(1));
          userChar.put("type", "FREE");
        }

        relic.put("list", list);

        JSONObject RECRUIT = new JSONObject();
        JSONObject content = new JSONObject();

        JSONObject recruit = new JSONObject();
        recruit.put("ticket", id);
        recruit.put("pendingIndex", Integer.valueOf(pendingIndex));
        recruit.put("rewardsIndex", Integer.valueOf(rewardsIndex));
        recruit.put("isRelic", Boolean.valueOf(false));
        recruit.put("relicId", null);

        content.put("recruit", recruit);
        RECRUIT.put("content", content);
        RECRUIT.put("index", "e_" + current.getJSONObject("player").getJSONArray("pending").size() + '\001');
        RECRUIT.put("type", "RECRUIT");
        current.getJSONObject("player").getJSONArray("pending").add(0, RECRUIT);
      }
    }

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/finishBattleReward" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject finishBattleReward(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/finishBattleReward");

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

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");

    current.getJSONObject("player").put("pending", new JSONArray());
    current.getJSONObject("player").put("state", "WAIT_MOVE");

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/gameSettle" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject gameSettle(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/gameSettle");

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

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");

    current.put("buff", null);
    current.put("game", null);
    current.put("inventory", null);
    current.put("map", null);
    current.put("player", null);
    current.put("record", null);
    current.put("troop", null);

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    result.put("outer", JSONObject.parseObject(
        "{\"mission\":{\"before\":[{\"type\":\"A\",\"tmpl\":\"CerternItem\",\"id\":\"rogue_1_task_25\",\"state\":1,\"target\":6,\"value\":6},{\"type\":\"B\",\"tmpl\":\"PassNodeType\",\"id\":\"rogue_1_task_13\",\"state\":1,\"target\":24,\"value\":24},{\"type\":\"C\",\"tmpl\":\"UsePopulation\",\"id\":\"rogue_1_task_11\",\"state\":1,\"target\":75,\"value\":76},{\"type\":\"C\",\"tmpl\":\"KillEnemy\",\"id\":\"rogue_1_task_3\",\"state\":1,\"target\":500,\"value\":641}],\"after\":[{\"type\":\"A\",\"tmpl\":\"CerternItem\",\"id\":\"rogue_1_task_25\",\"state\":1,\"target\":6,\"value\":6},{\"type\":\"B\",\"tmpl\":\"PassNodeType\",\"id\":\"rogue_1_task_13\",\"state\":1,\"target\":24,\"value\":24},{\"type\":\"C\",\"tmpl\":\"UsePopulation\",\"id\":\"rogue_1_task_11\",\"state\":1,\"target\":75,\"value\":76},{\"type\":\"C\",\"tmpl\":\"KillEnemy\",\"id\":\"rogue_1_task_3\",\"state\":1,\"target\":500,\"value\":641}]},\"missionBp\":{\"cnt\":0,\"from\":7594,\"to\":7594},\"relicBp\":{\"cnt\":0,\"from\":7594,\"to\":7594},\"relicUnlock\":[],\"gp\":0}"));
    result.put("game", JSONObject.parseObject(
        "{\"brief\":{\"level\":4,\"over\":true,\"success\":0,\"ending\":\"\",\"theme\":\"rogue_1\",\"mode\":\"EASY\",\"predefined\":null,\"band\":\"rogue_1_band_3\",\"startTs\":1642943654,\"endTs\":1642945001,\"endZoneId\":\"zone_3\",\"endProperty\":{\"hp\":0,\"gold\":16,\"populationCost\":19,\"populationMax\":29}},\"record\":{\"cntZone\":3,\"cntBattleNormal\":5,\"cntBattleElite\":1,\"cntBattleBoss\":0,\"cntArrivedNode\":13,\"cntRecruitChar\":8,\"cntUpgradeChar\":2,\"cntKillEnemy\":217,\"cntShopBuy\":2,\"cntPerfectBattle\":6,\"cntProtectBox\":4,\"cntRecruitFree\":0,\"cntRecruitAssist\":2,\"cntRecruitNpc\":3,\"cntRecruitProfession\":{\"SNIPER\":1,\"CASTER\":1,\"PIONEER\":2,\"TANK\":1,\"WARRIOR\":2,\"MEDIC\":1},\"troopChars\":[{\"charId\":\"char_1013_chen2\",\"type\":\"ASSIST\",\"upgradePhase\":1,\"evolvePhase\":2,\"level\":90},{\"charId\":\"char_328_cammou\",\"type\":\"ASSIST\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":60},{\"charId\":\"char_504_rguard\",\"type\":\"THIRD_LOW\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_201_moeshd\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":70},{\"charId\":\"char_504_rguard\",\"type\":\"THIRD_LOW\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_143_ghost\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":70},{\"charId\":\"char_208_melan\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_510_amedic\",\"type\":\"THIRD\",\"upgradePhase\":1,\"evolvePhase\":2,\"level\":80}],\"cntArrivedNodeType\":{\"BATTLE_NORMAL\":4,\"INCIDENT\":4,\"SHOP\":1,\"BATTLE_ELITE\":2,\"TREASURE\":1,\"REST\":1},\"relicList\":[\"rogue_1_relic_a01\",\"rogue_1_relic_r09\",\"rogue_1_relic_q02\",\"rogue_1_relic_a45\",\"rogue_1_relic_a11\"],\"capsuleList\":[\"rogue_1_capsule_3\",\"rogue_1_capsule_7\",\"rogue_1_capsule_8\"],\"activeToolList\":[],\"zones\":[{\"index\":1,\"zoneId\":\"zone_1\",\"variation\":[]},{\"index\":2,\"zoneId\":\"zone_2\",\"variation\":[]},{\"index\":3,\"zoneId\":\"zone_3\",\"variation\":[]}]},\"score\":{\"detail\":[[2,80],[13,13],[5,50],[1,20],[0,0],[7,35],[7,14]],\"scoreFactor\":0.5,\"score\":106,\"buff\":1.08,\"bp\":{\"cnt\":114,\"from\":7480,\"to\":7594},\"gp\":11}}"));
    return result;
  }

  @PostMapping(value = { "/moveTo" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject moveTo(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/gameSettle");

    JSONObject moveTo = JsonBody.getJSONObject("to");

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

    JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
    JSONObject zones = current.getJSONObject("map").getJSONObject("zones");

    String zoneIndex = current.getJSONObject("player").getJSONObject("cursor").getString("zone");

    JSONObject nodes = zones.getJSONObject(zoneIndex).getJSONObject("nodes");

    for (Map.Entry entry : nodes.entrySet()) {
      if (nodes.getJSONObject(entry.getKey().toString()).getJSONObject("pos") == moveTo)
        ;
    }
    current.getJSONObject("player").getJSONObject("cursor").put("position", moveTo);
    current.getJSONObject("player").getJSONArray("trace").add(current.getJSONObject("player").getJSONObject("cursor"));

    JSONObject pending = new JSONObject();
    JSONObject content = new JSONObject();

    JSONObject scene = new JSONObject();
    content.put("scene", scene);

    pending.put("content", content);
    pending.put("index", "e_" + current.getJSONObject("player").getJSONArray("pending").size() + '\001');
    pending.put("type", "SCENE");

    current.getJSONObject("player").getJSONArray("pending").add(pending);
    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    JSONObject playerDataDelta = new JSONObject(true);
    playerDataDelta.put("deleted", new JSONObject(true));
    JSONObject modified = new JSONObject(true);
    modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    return result;
  }

  @PostMapping(value = { "/battlePass/getReward" }, produces = { "application/json;charset=UTF-8" })
  public JSONObject getReward(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
      HttpServletResponse response, HttpServletRequest request) {
    String clientIp = ArknightsApplication.getIpAddr(request);
    LOGGER.info("[/" + clientIp + "] /rlv2/battlePass/getReward");

    String theme = JsonBody.getString("theme");
    JSONArray rewards = JsonBody.getJSONArray("rewards");

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
    JSONObject bp = UserSyncData.getJSONObject("rlv2").getJSONObject("outer").getJSONObject(theme).getJSONObject("bp");

    JSONArray milestones = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme)
        .getJSONArray("milestones");

    JSONArray items = new JSONArray();

    Boolean isChar = Boolean.valueOf(false);
    for (int i = 0; i < rewards.size(); i++) {
      String bp_level = rewards.getString(i);
      int index = Integer.valueOf(bp_level.substring(9)).intValue() - 1;

      String itemID = milestones.getJSONObject(index).getString("itemID");
      String itemType = milestones.getJSONObject(index).getString("itemType");
      int itemCount = milestones.getJSONObject(index).getIntValue("itemCount");

      if (itemType.equals("CHAR")) {
        isChar = Boolean.valueOf(true);
      }
      admin.GM_GiveItem(UserSyncData, itemID, itemType, itemCount, items);

      if (!ArknightsApplication.serverConfig.getJSONObject("roguelike").getBooleanValue("unlimitedMilestones")) {
        bp.getJSONObject("reward").put(bp_level, Integer.valueOf(1));
      }
    }

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);

    JSONObject playerDataDelta = new JSONObject(true);
    JSONObject modified = new JSONObject(true);
    if (!ArknightsApplication.serverConfig.getJSONObject("roguelike").getBooleanValue("unlimitedMilestones")) {
      modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
    }
    modified.put("status", UserSyncData.getJSONObject("status"));
    modified.put("inventory", UserSyncData.getJSONObject("inventory"));
    if (isChar.booleanValue()) {
      modified.put("troop", UserSyncData.getJSONObject("troop"));
    }
    playerDataDelta.put("deleted", new JSONObject(true));
    playerDataDelta.put("modified", modified);
    result.put("playerDataDelta", playerDataDelta);
    result.put("items", items);
    result.put("result", Integer.valueOf(0));
    return result;
  }
}
