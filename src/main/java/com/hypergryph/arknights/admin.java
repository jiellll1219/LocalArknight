package com.hypergryph.arknights;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/admin" })
public class admin {
  public static void GM_GiveItem(JSONObject UserSyncData, String reward_id, String reward_type, int reward_count,
      JSONArray items) {
    JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars");
    JSONObject troop = new JSONObject(true);

    if (reward_type.equals("CHAR")) {
      JSONObject item = new JSONObject(true);

      String randomCharId = reward_id;
      int repeatCharId = 0;

      for (int j = 0; j < UserSyncData.getJSONObject("troop").getJSONObject("chars").size(); j++) {
        if (UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(j + 1))
            .getString("charId").equals(randomCharId)) {
          repeatCharId = j + 1;
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
        char_data.put("voiceLan",
            ArknightsApplication.charwordTable.getJSONObject("charDefaultTypeDict").getString(randomCharId));
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

        UserSyncData.getJSONObject("troop").put("curCharInstId", Integer.valueOf(instId + 1));
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

        UserSyncData.getJSONObject("status").put("hggShard",
            Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("hggShard") + 1));

        get_char.put("itemGet", itemGet);
        UserSyncData.getJSONObject("inventory").put("p_" + randomCharId, Integer.valueOf(0));

        JSONObject charGet = get_char;

        JSONObject charinstId = new JSONObject(true);
        charinstId.put(String.valueOf(instId), char_data);
        chars.put(String.valueOf(instId), char_data);
        troop.put("chars", charinstId);

        item.put("id", randomCharId);
        item.put("type", reward_type);
        item.put("charGet", charGet);
        items.add(item);
      } else {

        JSONObject get_char = new JSONObject(true);

        get_char.put("charInstId", Integer.valueOf(repeatCharId));
        get_char.put("charId", randomCharId);
        get_char.put("isNew", Integer.valueOf(0));

        JSONObject repatChar = UserSyncData.getJSONObject("troop").getJSONObject("chars")
            .getJSONObject(String.valueOf(repeatCharId));

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
        UserSyncData.getJSONObject("status").put(itemName,
            Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue(itemName) + itemCount));

        JSONObject new_itemGet_3 = new JSONObject(true);
        new_itemGet_3.put("type", "MATERIAL");
        new_itemGet_3.put("id", "p_" + randomCharId);
        new_itemGet_3.put("count", Integer.valueOf(1));
        itemGet.add(new_itemGet_3);
        get_char.put("itemGet", itemGet);
        UserSyncData.getJSONObject("inventory").put("p_" + randomCharId,
            Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("p_" + randomCharId) + 1));

        JSONObject charGet = get_char;

        JSONObject charinstId = new JSONObject(true);
        charinstId.put(String.valueOf(repeatCharId),
            UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(repeatCharId)));
        chars.put(String.valueOf(repeatCharId),
            UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(repeatCharId)));
        troop.put("chars", charinstId);

        item.put("type", reward_type);
        item.put("id", randomCharId);
        item.put("charGet", charGet);
        items.add(item);
      }
    }

    if (reward_type.equals("HGG_SHD")) {
      UserSyncData.getJSONObject("status").put("practiceTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("hggShard") + reward_count));
    }
    if (reward_type.equals("LGG_SHD")) {
      UserSyncData.getJSONObject("status").put("practiceTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("lggShard") + reward_count));
    }
    if (reward_type.equals("MATERIAL")) {
      UserSyncData.getJSONObject("inventory").put(reward_id,
          Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
    }
    if (reward_type.equals("CARD_EXP")) {
      UserSyncData.getJSONObject("inventory").put(reward_id,
          Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
    }
    if (reward_type.equals("SOCIAL_PT")) {
      UserSyncData.getJSONObject("status").put("socialPoint",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("socialPoint") + reward_count));
    }
    if (reward_type.equals("AP_GAMEPLAY")) {
      UserSyncData.getJSONObject("status").put("ap",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + reward_count));
    }
    if (reward_type.equals("AP_ITEM")) {
      if (reward_id.contains("60")) {
        UserSyncData.getJSONObject("status").put("ap",
            Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + 60));
      } else if (reward_id.contains("200")) {
        UserSyncData.getJSONObject("status").put("ap",
            Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + 200));
      } else {
        UserSyncData.getJSONObject("status").put("ap",
            Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("ap") + 100));
      }
    }
    if (reward_type.equals("TKT_TRY")) {
      UserSyncData.getJSONObject("status").put("practiceTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("practiceTicket") + reward_count));
    }
    if (reward_type.equals("DIAMOND")) {
      UserSyncData.getJSONObject("status").put("androidDiamond",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
      UserSyncData.getJSONObject("status").put("iosDiamond",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
    }
    if (reward_type.equals("DIAMOND_SHD")) {
      UserSyncData.getJSONObject("status").put("diamondShard",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("diamondShard") + reward_count));
    }
    if (reward_type.equals("GOLD")) {
      UserSyncData.getJSONObject("status").put("gold",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
    }
    if (reward_type.equals("TKT_RECRUIT")) {
      UserSyncData.getJSONObject("status").put("recruitLicense",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
    }
    if (reward_type.equals("TKT_INST_FIN")) {
      UserSyncData.getJSONObject("status").put("instantFinishTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("instantFinishTicket") + reward_count));
    }
    if (reward_type.equals("TKT_GACHA_PRSV")) {
      UserSyncData.getJSONObject("inventory").put(reward_id,
          Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
    }
    if (reward_type.equals("RENAMING_CARD")) {
      UserSyncData.getJSONObject("inventory").put(reward_id,
          Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
    }
    if (reward_type.equals("RETRO_COIN")) {
      UserSyncData.getJSONObject("inventory").put(reward_id,
          Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
    }
    if (reward_type.equals("AP_SUPPLY")) {
      UserSyncData.getJSONObject("inventory").put(reward_id,
          Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
    }
    if (reward_type.equals("TKT_GACHA_10")) {
      UserSyncData.getJSONObject("status").put("tenGachaTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("tenGachaTicket") + reward_count));
    }
    if (reward_type.equals("TKT_GACHA")) {
      UserSyncData.getJSONObject("status").put("gachaTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gachaTicket") + reward_count));
    }
    if (reward_type.indexOf("VOUCHER") != -1) {
      if (!UserSyncData.getJSONObject("consumable").containsKey(reward_id)) {
        JSONObject consumables = new JSONObject(true);
        JSONObject consumable = new JSONObject(true);
        consumable.put("ts", Integer.valueOf(-1));
        consumable.put("count", Integer.valueOf(0));
        consumables.put("0", consumable);
        UserSyncData.getJSONObject("consumable").put(reward_id, consumables);
      }
      UserSyncData.getJSONObject("consumable").getJSONObject(reward_id).getJSONObject("0").put("count",
          Integer.valueOf(
              UserSyncData.getJSONObject("consumable").getJSONObject(reward_id).getJSONObject("0").getIntValue("count")
                  + reward_count));
    }
    if (reward_type.equals("CHAR_SKIN")) {
      UserSyncData.getJSONObject("skin").getJSONObject("characterSkins").put(reward_id, Integer.valueOf(1));
      UserSyncData.getJSONObject("skin").getJSONObject("skinTs").put(reward_id,
          Long.valueOf((new Date()).getTime() / 1000L));
    }

    if (!reward_type.equals("CHAR")) {
      JSONObject item = new JSONObject(true);
      item.put("id", reward_id);
      item.put("type", reward_type);
      item.put("count", Integer.valueOf(reward_count));
      items.add(item);
    }
  }

  @RequestMapping({ "/send/character" })
  public JSONObject character(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid,
      @RequestParam String charId) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> user = userDao.queryAccountByUid(uid.longValue());

    if (user.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) user.get(0)).getUser());

    JSONArray items = new JSONArray();
    GM_GiveItem(UserSyncData, charId, "CHAR", 1, items);

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(200));
    result.put("items", items);
    return result;
  }

  @RequestMapping({ "/send/item" })
  public JSONObject item(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid, @RequestParam String itemType,
      @RequestParam String itemId, @RequestParam int count) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> user = userDao.queryAccountByUid(uid.longValue());

    if (user.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) user.get(0)).getUser());

    JSONArray items = new JSONArray();
    GM_GiveItem(UserSyncData, itemId, itemType, count, items);

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(200));
    result.put("items", items);
    return result;
  }

  @RequestMapping({ "/charBuild/changeLevel" })
  public JSONObject level(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId,
      @RequestParam int value) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(entry.getKey().toString());
      if (charData.getString("charId").equals(charId)) {

        charData.put("level", Integer.valueOf(value));
        UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);

        userDao.setUserData(uid, UserSyncData);

        JSONObject jSONObject = new JSONObject(true);
        jSONObject.put("code", Integer.valueOf(200));
        jSONObject.put("msg", "已改变 " + charId + " 的等级为" + value);
        return jSONObject;
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(404));
    result.put("msg", "该玩家尚未拥有 " + charId);
    return result;
  }

  @RequestMapping({ "/charBuild/changeFavorPoint" })
  public JSONObject favorPoint(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid,
      @RequestParam String charId, @RequestParam int value) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(entry.getKey().toString());
      if (charData.getString("charId").equals(charId)) {

        charData.put("favorPoint", Integer.valueOf(value));
        UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);

        userDao.setUserData(uid, UserSyncData);

        JSONObject jSONObject = new JSONObject(true);
        jSONObject.put("code", Integer.valueOf(200));
        jSONObject.put("msg", "已改变 " + charId + " 的信赖为" + value);
        return jSONObject;
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(404));
    result.put("msg", "该玩家尚未拥有 " + charId);
    return result;
  }

  @RequestMapping({ "/charBuild/changePotentialRank" })
  public JSONObject PotentialRank(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid,
      @RequestParam String charId, @RequestParam int value) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(entry.getKey().toString());
      if (charData.getString("charId").equals(charId)) {

        charData.put("potentialRank", Integer.valueOf(value));
        UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);

        userDao.setUserData(uid, UserSyncData);

        JSONObject jSONObject = new JSONObject(true);
        jSONObject.put("code", Integer.valueOf(200));
        jSONObject.put("msg", "已改变 " + charId + " 的潜能为 " + value);
        return jSONObject;
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(404));
    result.put("msg", "该玩家尚未拥有 " + charId);
    return result;
  }

  @RequestMapping({ "/charBuild/changeMainSkillLvl" })
  public JSONObject mainSkillLvl(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid,
      @RequestParam String charId, @RequestParam int value) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(entry.getKey().toString());
      if (charData.getString("charId").equals(charId)) {

        charData.put("mainSkillLvl", Integer.valueOf(value));
        UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);

        userDao.setUserData(uid, UserSyncData);

        JSONObject jSONObject = new JSONObject(true);
        jSONObject.put("code", Integer.valueOf(200));
        jSONObject.put("msg", "已改变 " + charId + " 的技能等级为 " + value);
        return jSONObject;
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(404));
    result.put("msg", "该玩家尚未拥有 " + charId);
    return result;
  }

  @RequestMapping({ "/charBuild/changeExp" })
  public JSONObject Exp(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId,
      @RequestParam int value) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(entry.getKey().toString());
      if (charData.getString("charId").equals(charId)) {

        charData.put("exp", Integer.valueOf(value));
        UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);

        userDao.setUserData(uid, UserSyncData);

        JSONObject jSONObject = new JSONObject(true);
        jSONObject.put("code", Integer.valueOf(200));
        jSONObject.put("msg", "已改变 " + charId + " 的经验为 " + value);
        return jSONObject;
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(404));
    result.put("msg", "该玩家尚未拥有 " + charId);
    return result;
  }

  @RequestMapping({ "/charBuild/changeEvolvePhase" })
  public JSONObject evolvePhase(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid,
      @RequestParam String charId, @RequestParam int value) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(entry.getKey().toString());
      if (charData.getString("charId").equals(charId)) {

        charData.put("evolvePhase", Integer.valueOf(value));
        UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);

        userDao.setUserData(uid, UserSyncData);

        JSONObject jSONObject = new JSONObject(true);
        jSONObject.put("code", Integer.valueOf(200));
        jSONObject.put("msg", "已改变 " + charId + " 的精英化等级为 " + value);
        return jSONObject;
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(404));
    result.put("msg", "该玩家尚未拥有 " + charId);
    return result;
  }

  @RequestMapping({ "/charBuild/changeDefaultSkillIndex" })
  public JSONObject defaultSkillIndex(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid,
      @RequestParam String charId, @RequestParam int value) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(entry.getKey().toString());
      if (charData.getString("charId").equals(charId)) {

        charData.put("defaultSkillIndex", Integer.valueOf(value));
        UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);

        userDao.setUserData(uid, UserSyncData);

        JSONObject jSONObject = new JSONObject(true);
        jSONObject.put("code", Integer.valueOf(200));
        jSONObject.put("msg", "已改变 " + charId + " 的默认技能为 " + value);
        return jSONObject;
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(404));
    result.put("msg", "该玩家尚未拥有 " + charId);
    return result;
  }

  @RequestMapping({ "/charBuild/changeSkin" })
  public JSONObject skin(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId,
      @RequestParam String value) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(entry.getKey().toString());
      if (charData.getString("charId").equals(charId)) {

        charData.put("skin", value);
        UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);

        userDao.setUserData(uid, UserSyncData);

        JSONObject jSONObject = new JSONObject(true);
        jSONObject.put("code", Integer.valueOf(200));
        jSONObject.put("msg", "已改变 " + charId + " 的默认皮肤为 " + value);
        return jSONObject;
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(404));
    result.put("msg", "该玩家尚未拥有 " + charId);
    return result;
  }

  @RequestMapping({ "/charBuild/unlockAllSkills" })
  public JSONObject unlockAllSkills(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid,
      @RequestParam String charId) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(entry.getKey().toString());
      if (charData.getString("charId").equals(charId)) {

        for (int i = 0; i < charData.getJSONArray("skills").size(); i++) {
          charData.getJSONArray("skills").getJSONObject(i).put("unlock", Integer.valueOf(1));
        }

        UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);

        userDao.setUserData(uid, UserSyncData);

        JSONObject jSONObject = new JSONObject(true);
        jSONObject.put("code", Integer.valueOf(200));
        jSONObject.put("msg", "已解锁 " + charId + " 的所有技能");
        return jSONObject;
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(404));
    result.put("msg", "该玩家尚未拥有 " + charId);
    return result;
  }

  @RequestMapping({ "/charBuild/changeSpecializeLevel" })
  public JSONObject UpSpecializeLevel(@RequestHeader("GMKey") String GMKey, @RequestParam Long uid,
      @RequestParam String charId, @RequestParam int value) {
    if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(401));
      jSONObject.put("msg", "Unauthorized");
      return jSONObject;
    }

    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    if (Accounts.size() != 1) {
      JSONObject jSONObject = new JSONObject(true);
      jSONObject.put("code", Integer.valueOf(404));
      jSONObject.put("msg", "无法找到该玩家的存档");
      return jSONObject;
    }

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
      JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(entry.getKey().toString());
      if (charData.getString("charId").equals(charId)) {

        for (int i = 0; i < charData.getJSONArray("skills").size(); i++) {
          charData.getJSONArray("skills").getJSONObject(i).put("specializeLevel", Integer.valueOf(value));
        }

        UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);

        userDao.setUserData(uid, UserSyncData);

        JSONObject jSONObject = new JSONObject(true);
        jSONObject.put("code", Integer.valueOf(200));
        jSONObject.put("msg", "已把 " + charId + " 的所有技能专精提升至 " + value);
        return jSONObject;
      }
    }

    JSONObject result = new JSONObject(true);
    result.put("code", Integer.valueOf(404));
    result.put("msg", "该玩家尚未拥有 " + charId);
    return result;
  }
}