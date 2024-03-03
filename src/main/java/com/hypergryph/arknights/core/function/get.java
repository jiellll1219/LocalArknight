package com.hypergryph.arknights.core.function;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.Date;
import java.util.List;

public class get {
  public static Boolean GM_ItemGet(Long uid, int count, String itemType, String itemId) {
    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    if (itemType.equals("TKT_TRY")) {
      UserSyncData.getJSONObject("status").put("practiceTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("practiceTicket") + count));
    }
    if (itemType.equals("DIAMOND")) {
      UserSyncData.getJSONObject("status").put("androidDiamond",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + count));
      UserSyncData.getJSONObject("status").put("iosDiamond",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + count));
    }
    if (itemType.equals("DIAMOND_SHD")) {
      UserSyncData.getJSONObject("status").put("diamondShard",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("diamondShard") + count));
    }
    if (itemType.equals("GOLD")) {
      UserSyncData.getJSONObject("status").put("gold",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + count));
    }
    if (itemType.equals("TKT_RECRUIT")) {
      UserSyncData.getJSONObject("status").put("recruitLicense",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + count));
    }
    if (itemType.equals("TKT_INST_FIN")) {
      UserSyncData.getJSONObject("status").put("instantFinishTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("instantFinishTicket") + count));
    }
    if (itemType.equals("LGG_SHD")) {
      UserSyncData.getJSONObject("status").put("lggShard",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("lggShard") + count));
    }
    if (itemType.equals("HGG_SHD")) {
      UserSyncData.getJSONObject("status").put("hggShard",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("hggShard") + count));
    }
    if (itemType.equals("TKT_GACHA")) {
      UserSyncData.getJSONObject("status").put("gachaTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gachaTicket") + count));
    }
    if (itemType.equals("TKT_GACHA_10")) {
      UserSyncData.getJSONObject("status").put("tenGachaTicket",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("tenGachaTicket") + count));
    }
    if (itemType.equals("VOUCHER_PICK")) {
      if (!UserSyncData.getJSONObject("consumable").containsKey(itemId)) {
        JSONObject consumables = new JSONObject(true);
        JSONObject item = new JSONObject(true);
        item.put("ts", Integer.valueOf(-1));
        item.put("count", Integer.valueOf(0));
        consumables.put("0", item);
        UserSyncData.getJSONObject("consumable").put(itemId, consumables);
      }
      UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").put("count",
          Integer.valueOf(
              UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").getIntValue("count")
                  + count));
    }
    if (itemType.equals("VOUCHER_ELITE_II_5")) {
      if (!UserSyncData.getJSONObject("consumable").containsKey(itemId)) {
        JSONObject consumables = new JSONObject(true);
        JSONObject item = new JSONObject(true);
        item.put("ts", Integer.valueOf(-1));
        item.put("count", Integer.valueOf(0));
        consumables.put("0", item);
        UserSyncData.getJSONObject("consumable").put(itemId, consumables);
      }
      UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").put("count",
          Integer.valueOf(
              UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").getIntValue("count")
                  + count));
    }
    if (itemType.equals("VOUCHER_SKIN")) {
      if (!UserSyncData.getJSONObject("consumable").containsKey(itemId)) {
        JSONObject consumables = new JSONObject(true);
        JSONObject item = new JSONObject(true);
        item.put("ts", Integer.valueOf(-1));
        item.put("count", Integer.valueOf(0));
        consumables.put("0", item);
        UserSyncData.getJSONObject("consumable").put(itemId, consumables);
      }
      UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").put("count",
          Integer.valueOf(
              UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").getIntValue("count")
                  + count));
    }
    if (itemType.equals("VOUCHER_CGACHA")) {
      if (!UserSyncData.getJSONObject("consumable").containsKey(itemId)) {
        JSONObject consumables = new JSONObject(true);
        JSONObject item = new JSONObject(true);
        item.put("ts", Integer.valueOf(-1));
        item.put("count", Integer.valueOf(0));
        consumables.put("0", item);
        UserSyncData.getJSONObject("consumable").put(itemId, consumables);
      }
      UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").put("count",
          Integer.valueOf(
              UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").getIntValue("count")
                  + count));
    }
    if (itemType.equals("VOUCHER_MGACHA")) {
      if (!UserSyncData.getJSONObject("consumable").containsKey(itemId)) {
        JSONObject consumables = new JSONObject(true);
        JSONObject item = new JSONObject(true);
        item.put("ts", Integer.valueOf(-1));
        item.put("count", Integer.valueOf(0));
        consumables.put("0", item);
        UserSyncData.getJSONObject("consumable").put(itemId, consumables);
      }
      UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").put("count",
          Integer.valueOf(
              UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").getIntValue("count")
                  + count));
    }
    if (itemType.equals("ACTIVITY_ITEM")) {
      if (!UserSyncData.getJSONObject("consumable").containsKey(itemId)) {
        JSONObject consumables = new JSONObject(true);
        JSONObject item = new JSONObject(true);
        item.put("ts", Integer.valueOf(-1));
        item.put("count", Integer.valueOf(0));
        consumables.put("0", item);
        UserSyncData.getJSONObject("consumable").put(itemId, consumables);
      }
      UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").put("count",
          Integer.valueOf(
              UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").getIntValue("count")
                  + count));
    }
    if (itemType.equals("AP_SUPPLY")) {
      if (!UserSyncData.getJSONObject("consumable").containsKey(itemId)) {
        JSONObject consumables = new JSONObject(true);
        JSONObject item = new JSONObject(true);
        item.put("ts", Integer.valueOf(-1));
        item.put("count", Integer.valueOf(0));
        consumables.put("0", item);
        UserSyncData.getJSONObject("consumable").put(itemId, consumables);
      }
      UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").put("count",
          Integer.valueOf(
              UserSyncData.getJSONObject("consumable").getJSONObject(itemId).getJSONObject("0").getIntValue("count")
                  + count));
    }
    userDao.setUserData(uid, UserSyncData);

    return Boolean.valueOf(true);
  }

  public static JSONObject GM_CharGet(Long uid, String charId) {
    List<Account> Accounts = userDao.queryAccountByUid(uid.longValue());

    JSONObject UserSyncData = JSONObject.parseObject(((Account) Accounts.get(0)).getUser());

    JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars");
    JSONObject buildingChars = UserSyncData.getJSONObject("building").getJSONObject("chars");
    JSONObject voiceLangDict = ArknightsApplication.charwordTable.getJSONObject("voiceLangDict");

    int repeatCharId = 0;

    for (int i = 0; i < UserSyncData.getJSONObject("troop").getJSONObject("chars").size(); i++) {
      if (UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(i + 1))
          .getString("charId").equals(charId)) {
        repeatCharId = i + 1;

        break;
      }
    }
    JSONArray itemGet = new JSONArray();
    int isNew = 0;
    int charinstId = repeatCharId;
    if (repeatCharId == 0) {

      JSONObject char_data = new JSONObject(true);

      JSONArray skilsArray = ArknightsApplication.characterJson.getJSONObject(charId).getJSONArray("skills");
      JSONArray skils = new JSONArray();

      for (int j = 0; j < skilsArray.size(); j++) {
        JSONObject new_skils = new JSONObject(true);
        new_skils.put("skillId", skilsArray.getJSONObject(j).getString("skillId"));
        new_skils.put("state", Integer.valueOf(0));
        new_skils.put("specializeLevel", Integer.valueOf(0));
        new_skils.put("completeUpgradeTime", Integer.valueOf(-1));
        if (skilsArray.getJSONObject(j).getJSONObject("unlockCond").getIntValue("phase") == 0) {
          new_skils.put("unlock", Integer.valueOf(1));
        } else {
          new_skils.put("unlock", Integer.valueOf(0));
        }
        skils.add(new_skils);
      }

      int instId = UserSyncData.getJSONObject("troop").getJSONObject("chars").size() + 1;
      charinstId = instId;
      char_data.put("instId", Integer.valueOf(instId));
      char_data.put("charId", charId);
      char_data.put("favorPoint", Integer.valueOf(0));
      char_data.put("potentialRank", Integer.valueOf(0));
      char_data.put("mainSkillLvl", Integer.valueOf(1));
      char_data.put("skin", charId + "#1");
      char_data.put("level", Integer.valueOf(1));
      char_data.put("exp", Integer.valueOf(0));
      char_data.put("evolvePhase", Integer.valueOf(0));
      char_data.put("gainTime", Long.valueOf((new Date()).getTime() / 1000L));
      char_data.put("skills", skils);
      char_data.put("equip", new JSONObject(true));

      char_data.put("voiceLan",
          ArknightsApplication.charwordTable.getJSONObject("charDefaultTypeDict").getString(charId));

      if (skils == new JSONArray()) {
        char_data.put("defaultSkillIndex", Integer.valueOf(-1));
      } else {
        char_data.put("defaultSkillIndex", Integer.valueOf(0));
      }

      String sub1 = charId.substring(charId.indexOf("_") + 1);
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
      UserSyncData.getJSONObject("troop").getJSONObject("charGroup").put(charId, charGroup);

      JSONObject buildingChar = new JSONObject(true);
      buildingChar.put("charId", charId);
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

      UserSyncData.getJSONObject("status").put("hggShard",
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("hggShard") + 1));
    } else {

      JSONObject repatChar = UserSyncData.getJSONObject("troop").getJSONObject("chars")
          .getJSONObject(String.valueOf(repeatCharId));
      int potentialRank = repatChar.getIntValue("potentialRank");
      int rarity = ArknightsApplication.characterJson.getJSONObject(charId).getIntValue("rarity");

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
      potential.put("id", "p_" + charId);
      potential.put("count", Integer.valueOf(1));
      itemGet.add(potential);

      UserSyncData.getJSONObject("status").put(itemName,
          Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue(itemName) + itemCount));

      UserSyncData.getJSONObject("inventory").put("p_" + charId,
          Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("p_" + charId) + 1));

      chars.put(String.valueOf(repeatCharId),
          UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(repeatCharId)));
    }
    UserSyncData.getJSONObject("troop").put("chars", chars);

    userDao.setUserData(uid, UserSyncData);

    JSONObject result = new JSONObject(true);
    result.put("itemGet", itemGet);
    result.put("charId", charId);
    result.put("charInstId", Integer.valueOf(charinstId));
    result.put("isNew", Integer.valueOf(isNew));
    return result;
  }

  public static void GM_FuncGet() {
  }
}