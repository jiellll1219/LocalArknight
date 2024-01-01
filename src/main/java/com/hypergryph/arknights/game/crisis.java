 package com.hypergryph.arknights.game;
 
 import com.alibaba.fastjson.JSONObject;
 import com.hypergryph.arknights.ArknightsApplication;
 import com.hypergryph.arknights.core.dao.userDao;
 import com.hypergryph.arknights.core.decrypt.Utils;
 import com.hypergryph.arknights.core.pojo.Account;
 import java.util.Date;
 import java.util.List;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestHeader;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;
 
 
 
 
 
 
 
 
 @RestController
 @RequestMapping({"/crisis"})
 public class crisis
 {
   @RequestMapping({"/getInfo"})
   public JSONObject getInfo(HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /crisis/getInfo");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject result = new JSONObject(true);
       result.put("statusCode", Integer.valueOf(400));
       result.put("error", "Bad Request");
       result.put("message", "server is close");
       return result;
     } 
     
     ArknightsApplication.CrisisData.put("ts", Long.valueOf(ArknightsApplication.getTimestamp()));
     return ArknightsApplication.CrisisData;
   }
 
 
   
   @RequestMapping({"/battleStart"})
   public JSONObject battleStart(@RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /crisis/battleStart");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     String seed = JsonBody.getString("seed");
     JSONObject result = new JSONObject(true);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     result.put("result", Integer.valueOf(0));
     result.put("sign", Integer.valueOf(0));
     result.put("battleId", "f0e625c0-4c67-11ec-b5c8-f5807dfeda12");
     result.put("signStr", "char_215_mantic&0&1&1&" + seed);
     return result;
   }
 
 
   
   @RequestMapping({"/battleFinish"})
   public JSONObject battleFinish(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /crisis/battleFinish");
     
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
 
 
     
     JSONObject result = new JSONObject(true);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     result.put("result", Integer.valueOf(0));
     result.put("score", Integer.valueOf(18));
     result.put("ts", Long.valueOf((new Date()).getTime() / 1000L));
     
     JSONObject updateInfo = new JSONObject(true);
     updateInfo.put("after", Integer.valueOf(18));
     updateInfo.put("before", Integer.valueOf(0));
     result.put("updateInfo", updateInfo);
     return JSONObject.parseObject("{\"result\":0,\"score\":66,\"updateInfo\":{\"point\":{\"before\":-1,\"after\":2}},\"ts\":1637940848,\"playerDataDelta\":{\"modified\":{\"crisis\":{\"current\":\"rune_season_7_1\",\"lst\":1637940327,\"nst\":1637956800,\"map\":{\"rune_02-01\":{\"rank\":3,\"confirmed\":3},\"rune_01-01\":{\"rank\":3,\"confirmed\":3},\"rune_01-02\":{\"rank\":3,\"confirmed\":3},\"rune_01-03\":{\"rank\":3,\"confirmed\":3},\"rune_02-03\":{\"rank\":3,\"confirmed\":3},\"rune_02-04\":{\"rank\":3,\"confirmed\":3},\"rune_02-05\":{\"rank\":3,\"confirmed\":3},\"rune_02-02\":{\"rank\":3,\"confirmed\":3},\"rune_03-02\":{\"rank\":3,\"confirmed\":3},\"rune_03-01\":{\"rank\":3,\"confirmed\":3},\"rune_04-01\":{\"rank\":3,\"confirmed\":3},\"rune_05-02\":{\"rank\":3,\"confirmed\":3},\"rune_05-01\":{\"rank\":3,\"confirmed\":3},\"rune_06-02\":{\"rank\":3,\"confirmed\":3},\"rune_06-01\":{\"rank\":3,\"confirmed\":3},\"rune_07-01\":{\"rank\":3,\"confirmed\":3},\"rune_08-02\":{\"rank\":3,\"confirmed\":3},\"rune_08-01\":{\"rank\":0,\"confirmed\":0},\"rune_09-02\":{\"rank\":3,\"confirmed\":3}},\"shop\":{\"coin\":3882,\"info\":[{\"id\":\"rs_shop_4\",\"count\":5},{\"id\":\"rs_shop_3\",\"count\":5},{\"id\":\"rs_shop_2\",\"count\":5},{\"id\":\"rs_shop_1\",\"count\":2},{\"id\":\"rs_shop_29\",\"count\":3},{\"id\":\"rs_shop_20\",\"count\":14},{\"id\":\"good_rs_1_1_skin1\",\"count\":1},{\"id\":\"good_rs_1_1_furniture1\",\"count\":1},{\"id\":\"good_rs_1_1_furniture2\",\"count\":1},{\"id\":\"rs_1_shop_1\",\"count\":2},{\"id\":\"rs_1_shop_2\",\"count\":5},{\"id\":\"rs_1_shop_3\",\"count\":5},{\"id\":\"rs_1_shop_4\",\"count\":5},{\"id\":\"rs_1_shop_6\",\"count\":15},{\"id\":\"rs_1_shop_5\",\"count\":10},{\"id\":\"rs_1_shop_7\",\"count\":15},{\"id\":\"rs_1_shop_8\",\"count\":30},{\"id\":\"rs_1_shop_9\",\"count\":30},{\"id\":\"rs_1_shop_10\",\"count\":12},{\"id\":\"good_rs_2_1_furniture1\",\"count\":1},{\"id\":\"rs_2_shop_1\",\"count\":2},{\"id\":\"rs_2_shop_2\",\"count\":5},{\"id\":\"rs_2_shop_3\",\"count\":5},{\"id\":\"rs_2_shop_4\",\"count\":5},{\"id\":\"rs_2_shop_5\",\"count\":10},{\"id\":\"rs_2_shop_7\",\"count\":15},{\"id\":\"rs_2_shop_6\",\"count\":15},{\"id\":\"good_rs_3_1_skin1\",\"count\":1},{\"id\":\"good_rs_t6_pioneer\",\"count\":4},{\"id\":\"rs_3_shop_9\",\"count\":30},{\"id\":\"rs_3_shop_16\",\"count\":11},{\"id\":\"rs_3_shop_1\",\"count\":2},{\"id\":\"rs_3_shop_2\",\"count\":5},{\"id\":\"rs_3_shop_3\",\"count\":5},{\"id\":\"rs_3_shop_7\",\"count\":5},{\"id\":\"good_rs_t6_guard\",\"count\":4},{\"id\":\"rs_4_shop_6\",\"count\":15},{\"id\":\"rs_4_shop_7\",\"count\":15},{\"id\":\"rs_4_shop_1\",\"count\":2},{\"id\":\"rs_4_shop_3\",\"count\":5},{\"id\":\"rs_4_shop_2\",\"count\":5},{\"id\":\"rs_4_shop_5\",\"count\":5},{\"id\":\"rs_4_shop_4\",\"count\":3},{\"id\":\"good_rs_5_1_skin1\",\"count\":1},{\"id\":\"rs_5_shop_4\",\"count\":5},{\"id\":\"rs_5_shop_5\",\"count\":10},{\"id\":\"rs_5_shop_2\",\"count\":5},{\"id\":\"rs_5_shop_3\",\"count\":5},{\"id\":\"rs_5_shop_1\",\"count\":2},{\"id\":\"good_rs_t5_tank\",\"count\":4},{\"id\":\"rs_5_shop_7\",\"count\":15},{\"id\":\"rs_5_shop_6\",\"count\":15},{\"id\":\"rs_5_shop_11\",\"count\":10},{\"id\":\"rs_5_shop_12\",\"count\":10},{\"id\":\"good_rs_6_1_skin1\",\"count\":1},{\"id\":\"rs_6_shop_17\",\"count\":5},{\"id\":\"rs_6_shop_1\",\"count\":2},{\"id\":\"rs_6_shop_2\",\"count\":5},{\"id\":\"rs_6_shop_4\",\"count\":5},{\"id\":\"rs_6_shop_3\",\"count\":5},{\"id\":\"rs_6_shop_8\",\"count\":30},{\"id\":\"rs_6_shop_7\",\"count\":15},{\"id\":\"good_rs_7_1_skin2\",\"count\":1}],\"progressInfo\":{\"char_bibeak_progress\":{\"count\":1,\"order\":6},\"char_folivo_progress\":{\"count\":1,\"order\":6},\"char_tuye_progress\":{\"count\":0,\"order\":4}}},\"training\":{\"currentStage\":[\"tr_level_rune_05-01\",\"tr_level_rune_06-02\",\"tr_level_rune_06-01\",\"tr_level_rune_07-01\",\"tr_level_rune_08-02\",\"tr_level_rune_08-01\",\"tr_level_rune_09-02\"],\"stage\":{\"tr_level_rune_01-01\":{\"point\":7},\"tr_level_rune_01-02\":{\"point\":7},\"tr_level_rune_01-03\":{\"point\":5},\"tr_level_rune_02-01\":{\"point\":7},\"tr_level_rune_02-03\":{\"point\":-1},\"tr_level_rune_02-04\":{\"point\":3},\"tr_level_rune_02-05\":{\"point\":-1},\"tr_level_rune_02-02\":{\"point\":-1},\"tr_level_rune_03-02\":{\"point\":-1},\"tr_level_rune_03-01\":{\"point\":-1},\"tr_level_rune_04-01\":{\"point\":4},\"tr_level_rune_05-02\":{\"point\":4},\"tr_level_rune_05-01\":{\"point\":-1},\"tr_level_rune_06-02\":{\"point\":-1},\"tr_level_rune_06-01\":{\"point\":-1},\"tr_level_rune_07-01\":{\"point\":-1},\"tr_level_rune_08-02\":{\"point\":1},\"tr_level_rune_08-01\":{\"point\":-1},\"tr_level_rune_09-02\":{\"point\":66}},\"nst\":1638734400},\"season\":{\"rune_season_0_1\":{\"coin\":3,\"tCoin\":0,\"permanent\":{\"nst\":-1,\"rune\":{\"enemy_atk_1\":3,\"char_atk_1\":3,\"char_hp_1\":3,\"global_costrecovery_1\":3,\"global_squadnum_1\":3,\"global_forbidloc_1\":3,\"char_cost_guarddefender_1\":3,\"global_lifepoint_1\":3,\"enemy_def_1\":3,\"enemy_eagent_1\":3,\"enemy_hirman_2\":3,\"enemy_atk_2\":3,\"enemy_hp_2\":3,\"char_atk_2\":3,\"char_hp_2\":3,\"global_costrecovery_2\":3,\"global_squadnum_2\":3,\"enemy_hirman_3\":3,\"enemy_atk_3\":3,\"enemy_hp_3\":3,\"char_atk_3\":1,\"char_exclude_ranged_3\":3,\"enemy_bslime_1\":1,\"char_cost_sniperhealer_1\":1,\"enemy_bslime_2\":1,\"global_forbidloc_2\":1,\"char_exclude_sniperhealer_2\":1,\"enemy_bslime_3\":0,\"global_forbidloc_3\":0},\"point\":19,\"challenge\":{\"taskList\":{\"normalTask_1\":{\"fts\":1584434954,\"rts\":1584435093},\"normalTask_2\":{\"fts\":1584787592,\"rts\":1584787606},\"normalTask_3\":{\"fts\":1584434954,\"rts\":1584435092},\"normalTask_4\":{\"fts\":1584787944,\"rts\":1584787954},\"normalTask_5\":{\"fts\":1584787244,\"rts\":1584787254},\"normalTask_6\":{\"fts\":1584789178,\"rts\":1584789188},\"normalTask_7\":{\"fts\":-1,\"rts\":-1},\"normalTask_8\":{\"fts\":-1,\"rts\":-1}},\"topPoint\":19,\"pointList\":{\"1\":1584435100,\"2\":1584435101,\"3\":1584435102,\"4\":1584435104,\"5\":1584435105,\"6\":1584435107,\"7\":1584435108,\"8\":1584435110,\"9\":1584435111,\"10\":1584435113,\"11\":1584435130,\"12\":1584435132,\"13\":1584435133,\"14\":1584435135,\"15\":1584435136,\"16\":1584439034,\"17\":1584439035,\"18\":1584452888}}},\"temporary\":{\"schedule\":\"rg10\",\"nst\":1585339200,\"point\":-1,\"challenge\":{\"taskList\":{\"dailyTask_10\":{\"fts\":-1,\"rts\":-1}},\"topPoint\":-1,\"pointList\":{\"0\":-1,\"1\":-1,\"2\":-1,\"3\":-1,\"4\":-1,\"5\":-1,\"6\":-1,\"7\":-1,\"8\":-1}}},\"sInfo\":{\"assistCnt\":22,\"maxPnt\":20,\"chars\":[{\"charId\":\"char_202_demkni\",\"cnt\":19},{\"charId\":\"char_140_whitew\",\"cnt\":8},{\"charId\":\"char_017_huang\",\"cnt\":5}],\"history\":{}}},\"rune_season_1_1\":{\"coin\":2,\"tCoin\":0,\"permanent\":{\"nst\":-1,\"rune\":{\"global_lifepoint_1\":3,\"enemy_def_1\":3,\"char_debuff_1\":3,\"char_atk_1\":3,\"global_forbidloc_1\":3,\"char_def_1\":3,\"global_costrecovery_1\":3,\"global_squadnum_1\":3,\"enemy_ghost_1\":3,\"enemy_hp_1\":3,\"enemy_hp_2\":3,\"char_exclude_specialsniper_2\":3,\"char_exclude_guardcaster_2\":3,\"char_def_2\":3,\"char_atk_2\":3,\"global_squadnum_2\":3,\"enemy_ghost_2\":3,\"enemy_crowns_2\":3,\"char_atk_3\":1,\"enemy_hp_3\":1,\"enemy_crowns_3\":3,\"enemy_ghost_3\":3,\"char_exclude_supporter_1\":1,\"char_cdtime_1\":3,\"char_cdtime_2\":3,\"enemy_atk_2\":1,\"char_def_3\":1,\"global_costrecovery_3\":3},\"point\":21,\"challenge\":{\"taskList\":{\"normalTask_1\":{\"fts\":1591631107,\"rts\":1591631831},\"normalTask_2\":{\"fts\":1592153567,\"rts\":1592153583},\"normalTask_3\":{\"fts\":1591631107,\"rts\":1591631831},\"normalTask_4\":{\"fts\":1592153814,\"rts\":1592153829},\"normalTask_5\":{\"fts\":1592154103,\"rts\":1592154116},\"normalTask_6\":{\"fts\":1592154397,\"rts\":1592154411},\"normalTask_7\":{\"fts\":1592155169,\"rts\":1592155182},\"normalTask_8\":{\"fts\":1592155845,\"rts\":1592155863}},\"topPoint\":21,\"pointList\":{\"1\":1591631831,\"2\":1591631831,\"3\":1591631831,\"4\":1591631831,\"5\":1591631831,\"6\":1591631831,\"7\":1591631831,\"8\":1591631831,\"9\":1591631831,\"10\":1591631831,\"11\":1591631831,\"12\":1591631831,\"13\":1591631831,\"14\":1591631831,\"15\":1591631831,\"16\":1591631831,\"17\":1591631831,\"18\":1591631831}}},\"temporary\":{\"schedule\":\"rg13\",\"nst\":1592251200,\"point\":-1,\"challenge\":{\"taskList\":{\"dailyTask_13\":{\"fts\":-1,\"rts\":-1}},\"topPoint\":-1,\"pointList\":{\"0\":-1,\"1\":-1,\"2\":-1,\"3\":-1,\"4\":-1,\"5\":-1,\"6\":-1,\"7\":-1,\"8\":-1}}},\"sInfo\":{\"assistCnt\":24,\"maxPnt\":18,\"chars\":[{\"charId\":\"char_112_siege\",\"cnt\":13},{\"charId\":\"char_202_demkni\",\"cnt\":9},{\"charId\":\"char_140_whitew\",\"cnt\":2}],\"history\":{}}},\"rune_season_2_1\":{\"coin\":6,\"tCoin\":0,\"permanent\":{\"nst\":-1,\"rune\":{\"global_lifepoint_1\":3,\"enemy_def_1\":3,\"char_atk_1\":3,\"char_cost_sniperguard_1\":1,\"char_cost_casterspecial_1\":3,\"char_hp_1\":3,\"global_costrecovery_1\":3,\"global_skillrecovery_1\":1,\"enemy_atk_1\":3,\"enemy_reveng_1\":3,\"global_forbidloc_1\":3,\"global_squadnum_1\":3,\"global_costrecovery_2\":3,\"global_skillrecovery_2\":3,\"char_cost_sniperguard_2\":1,\"char_cost_casterspecial_2\":3,\"char_hp_2\":3,\"enemy_atk_2\":3,\"enemy_hp_2\":3,\"enemy_faust_atkmod_2\":1,\"enemy_faust_trapmod_2\":3,\"enemy_reveng_spdmod_2\":1,\"enemy_reveng_atkmod_2\":3,\"enemy_hp_3\":3,\"enemy_faust_atkmod_3\":1,\"enemy_faust_trapmod_3\":3,\"enemy_reveng_spdmod_3\":1,\"enemy_reveng_atkmod_3\":3,\"enemy_movespeed_1\":1,\"enemy_faust_1\":1,\"char_def_1\":3,\"global_pcharnum_2\":3,\"global_forbidloc_2\":3,\"char_atk_2\":3,\"char_atk_3\":0,\"enemy_atk_3\":3},\"point\":18,\"challenge\":{\"taskList\":{\"normalTask_1\":{\"fts\":1595947983,\"rts\":1595949272},\"normalTask_2\":{\"fts\":1595947983,\"rts\":1595949272},\"normalTask_3\":{\"fts\":1595949255,\"rts\":1595949272},\"normalTask_4\":{\"fts\":1596245704,\"rts\":1596245732},\"normalTask_5\":{\"fts\":1596718151,\"rts\":1596718980},\"normalTask_6\":{\"fts\":1596718444,\"rts\":1596718980},\"normalTask_7\":{\"fts\":1596718703,\"rts\":1596718980},\"normalTask_8\":{\"fts\":1596718968,\"rts\":1596718980}},\"topPoint\":18,\"pointList\":{\"1\":1595949272,\"2\":1595949272,\"3\":1595949272,\"4\":1595949272,\"5\":1595949272,\"6\":1595949272,\"7\":1595949272,\"8\":1595949272,\"9\":1595949272,\"10\":1595949272,\"11\":1595949272,\"12\":1595949272,\"13\":1595949272,\"14\":1595949272,\"15\":1595949272,\"16\":1595949272,\"17\":1595949272,\"18\":1595949272}}},\"temporary\":{\"schedule\":\"rg13\",\"nst\":1597089600,\"point\":-1,\"challenge\":{\"taskList\":{\"dailyTask_13\":{\"fts\":-1,\"rts\":-1}},\"topPoint\":-1,\"pointList\":{\"0\":-1,\"1\":-1,\"2\":-1,\"3\":-1,\"4\":-1,\"5\":-1,\"6\":-1,\"7\":-1,\"8\":-1}}},\"sInfo\":{\"assistCnt\":13,\"maxPnt\":21,\"chars\":[{\"charId\":\"char_202_demkni\",\"cnt\":7},{\"charId\":\"char_180_amgoat\",\"cnt\":4},{\"charId\":\"char_112_siege\",\"cnt\":2}],\"history\":{}}},\"rune_season_3_1\":{\"coin\":12,\"tCoin\":0,\"permanent\":{\"nst\":-1,\"rune\":{\"global_lifepoint_1\":3,\"enemy_def_1\":3,\"char_atkatkspeed_1\":3,\"char_cost_special_1\":3,\"char_hp_1\":3,\"enemy_atk_1\":3,\"enemy_dbskar_1\":3,\"global_forbidloc_1\":3,\"enemy_dmgswd_1\":3,\"enemy_dscout_1\":3,\"token_blower_1\":3,\"global_squadnum_1\":3,\"global_forbidloc_2\":3,\"char_atkatkspeed_2\":3,\"enemy_dbskar_2\":3,\"char_cost_special_2\":3,\"enemy_dmgswd_2\":3,\"enemy_dscout_2\":3,\"enemy_hp_2\":3,\"enemy_atk_2\":3,\"enemy_dscout_3\":3,\"enemy_hp_3\":3,\"enemy_dbskar_3\":3,\"char_atkatkspeed_3\":1,\"enemy_atk_3\":1,\"enemy_dmgswd_3\":3,\"enemy_hp_1\":3,\"enemy_dlancer_1\":1,\"token_blower_2\":3,\"char_hp_2\":1,\"enemy_dlancer_2\":1,\"char_hp_3\":1,\"global_forbidloc_3\":3},\"point\":18,\"challenge\":{\"taskList\":{\"normalTask_1\":{\"fts\":1605432549,\"rts\":1605433115},\"normalTask_2\":{\"fts\":1605432549,\"rts\":1605433115},\"normalTask_3\":{\"fts\":1605433612,\"rts\":1605434032},\"normalTask_4\":{\"fts\":1605449922,\"rts\":1605450232},\"normalTask_5\":{\"fts\":1605656995,\"rts\":1605657346},\"normalTask_6\":{\"fts\":1606090751,\"rts\":1606091135},\"normalTask_7\":{\"fts\":1606089936,\"rts\":1606090200},\"normalTask_8\":{\"fts\":1606091148,\"rts\":1606091544}},\"topPoint\":18,\"pointList\":{\"1\":1605433115,\"2\":1605433115,\"3\":1605433115,\"4\":1605433115,\"5\":1605433115,\"6\":1605433115,\"7\":1605433115,\"8\":1605433115,\"9\":1605433115,\"10\":1605433115,\"11\":1605433115,\"12\":1605433115,\"13\":1605434032,\"14\":1605434032,\"15\":1605434032,\"16\":1605434032,\"17\":1605434032,\"18\":1605434032}}},\"temporary\":{\"schedule\":\"rg13\",\"nst\":1606593600,\"point\":8,\"challenge\":{\"taskList\":{\"dailyTask_13\":{\"fts\":1606539854,\"rts\":1606540031}},\"topPoint\":8,\"pointList\":{\"0\":1606540031,\"1\":1606540031,\"2\":1606540031,\"3\":1606540031,\"4\":1606540031,\"5\":1606540031,\"6\":1606540031,\"7\":1606540031,\"8\":1606540031}}},\"sInfo\":{\"assistCnt\":15,\"maxPnt\":26,\"chars\":[{\"charId\":\"char_179_cgbird\",\"cnt\":9},{\"charId\":\"char_202_demkni\",\"cnt\":3},{\"charId\":\"char_171_bldsk\",\"cnt\":3}],\"history\":{}}},\"rune_season_4_1\":{\"coin\":16,\"tCoin\":0,\"permanent\":{\"nst\":-1,\"rune\":{\"global_lifepoint_1\":3,\"enemy_magicresistance_1\":3,\"trap_change_1\":3,\"char_atk_1\":3,\"char_hp_1\":3,\"global_costrecovery_1\":3,\"enemy_atk_1\":3,\"enemy_hp_1\":3,\"enemy_mdgint_1\":3,\"global_forbidloc_1\":3,\"global_squadnum_1\":3,\"char_attackspeed_1\":3,\"char_respawntime_1\":3,\"char_atk_2\":3,\"char_hp_2\":3,\"global_costrecovery_2\":3,\"enemy_atk_2\":3,\"enemy_bigbomdrock_2\":3,\"enemy_hp_2\":3,\"enemy_mdgint_amod_2\":3,\"enemy_mdgint_bmod_2\":3,\"global_forbidloc_2\":3,\"global_squadnum_2\":3,\"global_pcharnum_2\":1,\"char_hp_3\":1,\"enemy_bigbomdrock_3\":3,\"enemy_hp_3\":1,\"enemy_mdgint_amod_3\":3,\"enemy_mdgint_bmod_3\":3,\"enemy_movespeed_1\":3,\"char_cost_guardspecial_1\":1,\"char_cost_castersupporter_1\":1,\"enemy_bigbomdrock_1\":3,\"char_cost_guardspecial_2\":1,\"char_cost_castersupporter_2\":1,\"enemy_atk_3\":0,\"char_atk_3\":3},\"point\":18,\"challenge\":{\"taskList\":{\"normalTask_1\":{\"fts\":1611732262,\"rts\":1611732659},\"normalTask_2\":{\"fts\":1611048353,\"rts\":1611048751},\"normalTask_3\":{\"fts\":1611731854,\"rts\":1611732251},\"normalTask_4\":{\"fts\":1611048893,\"rts\":1611049266},\"normalTask_5\":{\"fts\":1611049290,\"rts\":1611049629},\"normalTask_6\":{\"fts\":1611048893,\"rts\":1611049266},\"normalTask_7\":{\"fts\":1611733198,\"rts\":1611733539},\"normalTask_8\":{\"fts\":1611733687,\"rts\":1611734188}},\"topPoint\":18,\"pointList\":{\"1\":1611048751,\"2\":1611048751,\"3\":1611048751,\"4\":1611048751,\"5\":1611048751,\"6\":1611048751,\"7\":1611048751,\"8\":1611048751,\"9\":1611048751,\"10\":1611048751,\"11\":1611048751,\"12\":1611048751,\"13\":1611049266,\"14\":1611049266,\"15\":1611049266,\"16\":1611049266,\"17\":1611049266,\"18\":1611049629}}},\"temporary\":{\"schedule\":\"rg13\",\"nst\":1612209600,\"point\":8,\"challenge\":{\"taskList\":{\"dailyTask_13\":{\"fts\":1612168981,\"rts\":1612169294}},\"topPoint\":8,\"pointList\":{\"0\":1612169294,\"1\":1612169294,\"2\":1612169294,\"3\":1612169294,\"4\":1612169294,\"5\":1612169294,\"6\":1612169294,\"7\":1612169294,\"8\":1612169294}}},\"sInfo\":{\"assistCnt\":5,\"maxPnt\":27,\"chars\":[{\"charId\":\"char_180_amgoat\",\"cnt\":4},{\"charId\":\"char_202_demkni\",\"cnt\":1}],\"history\":{}}},\"rune_season_5_1\":{\"coin\":12,\"tCoin\":0,\"permanent\":{\"nst\":-1,\"rune\":{\"global_lifepoint_1\":3,\"enemy_scorpn_1\":3,\"enemy_attackspeed_1\":3,\"char_atk_1\":3,\"char_cost_casterdefender_1\":1,\"char_cost_guardhealer_1\":1,\"char_defmagicresistance_1\":3,\"global_costrecovery_1\":3,\"enemy_dekght_1\":1,\"enemy_atk_1\":3,\"enemy_lfkght_1\":3,\"global_forbidloc_left_1\":3,\"global_squadnum_1\":3,\"global_forbidloc_right_1\":3,\"global_pcharnum_1\":3,\"char_hp_2\":3,\"char_atk_2\":3,\"char_exclude_guardhealer_2\":1,\"char_exclude_casterdefender_2\":3,\"char_defmagicresistance_2\":3,\"global_costrecovery_2\":3,\"enemy_lfkght_2\":3,\"enemy_atk_2\":3,\"enemy_dekght_amod_2\":1,\"enemy_dekght_bmod_2\":3,\"global_squadnum_2\":3,\"global_pcharnum_2\":1,\"enemy_atk_3\":3,\"char_atk_3\":1,\"char_defmagicresistance_3\":3,\"enemy_dekght_amod_3\":3,\"enemy_dekght_bmod_3\":3,\"enemy_hp_1\":3,\"global_forbidloc_2\":3,\"char_exclude_castersupporter_2\":3,\"enemy_hp_2\":1,\"global_costrecovery_3\":1,\"enemy_lfkght_3\":3,\"global_squadnum_3\":1},\"point\":25,\"challenge\":{\"taskList\":{\"normalTask_1\":{\"fts\":1621066322,\"rts\":1621066621},\"normalTask_2\":{\"fts\":1621071996,\"rts\":1621072253},\"normalTask_3\":{\"fts\":1621067806,\"rts\":1621068579},\"normalTask_4\":{\"fts\":1621515893,\"rts\":1621516105},\"normalTask_5\":{\"fts\":1621071996,\"rts\":1621072253},\"normalTask_6\":{\"fts\":1621516186,\"rts\":1621516402},\"normalTask_7\":{\"fts\":1621666406,\"rts\":1621666634},\"normalTask_8\":{\"fts\":1621666123,\"rts\":1621666362}},\"topPoint\":25,\"pointList\":{\"1\":1621066621,\"2\":1621066621,\"3\":1621066621,\"4\":1621066621,\"5\":1621066621,\"6\":1621066621,\"7\":1621066621,\"8\":1621066621,\"9\":1621066621,\"10\":1621068579,\"11\":1621068579,\"12\":1621068579,\"13\":1621068579,\"14\":1621068579,\"15\":1621068579,\"16\":1621068579,\"17\":1621068579,\"18\":1621068579}}},\"temporary\":{\"schedule\":\"rg13\",\"nst\":1622232000,\"point\":8,\"challenge\":{\"taskList\":{\"dailyTask_13\":{\"fts\":1622156197,\"rts\":1622156757}},\"topPoint\":8,\"pointList\":{\"0\":1622156757,\"1\":1622156757,\"2\":1622156757,\"3\":1622156757,\"4\":1622156757,\"5\":1622156757,\"6\":1622156757,\"7\":1622156757,\"8\":1622156757}}},\"sInfo\":{\"assistCnt\":11,\"maxPnt\":24,\"chars\":[{\"charId\":\"char_179_cgbird\",\"cnt\":7},{\"charId\":\"char_222_bpipe\",\"cnt\":3},{\"charId\":\"char_144_red\",\"cnt\":1}],\"history\":{}}},\"rune_season_6_1\":{\"coin\":16,\"tCoin\":0,\"permanent\":{\"nst\":-1,\"rune\":{\"global_lifepoint_1\":3,\"char_atk_1\":3,\"enemy_def_1\":3,\"token_storm_speed_1\":3,\"char_cost_guardspecial_1\":1,\"char_cost_castersupporter_1\":3,\"char_hp_1\":3,\"global_costrecovery_1\":3,\"enemy_atk_1\":3,\"enemy_cchmpn_1\":3,\"enemy_ccripr_1\":3,\"enemy_ccwitch_1\":3,\"global_squadnum_1\":3,\"global_pcharnum_1\":3,\"token_storm_direction_1\":3,\"char_cost_guardspecial_2\":1,\"char_cost_castersupporter_2\":3,\"char_hp_2\":3,\"global_costrecovery_2\":3,\"enemy_atk_2\":3,\"enemy_hp_2\":3,\"enemy_ccripr_2\":3,\"enemy_ccwitch_2\":3,\"enemy_cchmpn_amod_2\":3,\"enemy_cchmpn_bmod_2\":3,\"global_squadnum_2\":1,\"global_pcharnum_2\":3,\"char_hp_3\":3,\"enemy_hp_3\":3,\"enemy_cchmpn_amod_3\":1,\"enemy_cchmpn_bmod_3\":3,\"enemy_hp_1\":1,\"char_attackspeed_1\":1,\"char_attackspeed_2\":1,\"token_storm_damage_2\":1,\"token_storm_direction_2\":3,\"global_costrecovery_3\":3,\"enemy_atk_3\":3},\"point\":21,\"challenge\":{\"taskList\":{\"normalTask_1\":{\"fts\":1629967314,\"rts\":1629967707},\"normalTask_2\":{\"fts\":1629967314,\"rts\":1629967707},\"normalTask_3\":{\"fts\":1629968401,\"rts\":1629968776},\"normalTask_4\":{\"fts\":1630649814,\"rts\":1630731989},\"normalTask_5\":{\"fts\":1630645806,\"rts\":1630646141},\"normalTask_6\":{\"fts\":1630645806,\"rts\":1630646141},\"normalTask_7\":{\"fts\":1630650381,\"rts\":1630731989},\"normalTask_8\":{\"fts\":1630649573,\"rts\":1630731989}},\"topPoint\":21,\"pointList\":{\"1\":1629967707,\"2\":1629967707,\"3\":1629967707,\"4\":1629967707,\"5\":1629967707,\"6\":1629967707,\"7\":1629967707,\"8\":1629967707,\"9\":1629967707,\"10\":1629967707,\"11\":1629967707,\"12\":1629967707,\"13\":1629967707,\"14\":1629968777,\"15\":1629968777,\"16\":1629968777,\"17\":1629968777,\"18\":1629968777}}},\"temporary\":{\"schedule\":\"rg13\",\"nst\":1631131200,\"point\":8,\"challenge\":{\"taskList\":{\"dailyTask_13\":{\"fts\":1631058258,\"rts\":1631058424}},\"topPoint\":8,\"pointList\":{\"0\":1631058424,\"1\":1631058424,\"2\":1631058424,\"3\":1631058424,\"4\":1631058424,\"5\":1631058424,\"6\":1631058424,\"7\":1631058424,\"8\":1631058424}}},\"sInfo\":{\"assistCnt\":21,\"maxPnt\":22,\"chars\":[{\"charId\":\"char_1012_skadi2\",\"cnt\":9},{\"charId\":\"char_144_red\",\"cnt\":6},{\"charId\":\"char_293_thorns\",\"cnt\":3}],\"history\":{}}},\"rune_season_7_1\":{\"coin\":4,\"tCoin\":10,\"permanent\":{\"nst\":1638129600,\"rune\":{\"char_magicresistance_1\":3,\"global_forbidloc_1\":3,\"enemy_movespeed_1\":3,\"enemy_def_1\":3,\"char_attackspeed_1\":3,\"char_hp_1\":3,\"global_costrecovery_1\":3,\"enemy_atk_1\":3,\"enemy_attackspeed_1\":3,\"enemy_hp_1\":3,\"enemy_spslme_1\":3,\"enemy_xi_1\":3,\"global_squadnum_1\":1,\"global_pcharnum_1\":3,\"enemy_spmage_2\":1,\"char_attackspeed_2\":1,\"char_cost_attackers_2\":1,\"char_cost_supporters_2\":3,\"char_hp_2\":3,\"global_costrecovery_2\":1,\"enemy_atk_2\":3,\"enemy_hp_2\":3,\"enemy_spslme_amod_2\":1,\"enemy_spslme_bmod_2\":1,\"enemy_xi_amod_2\":1,\"enemy_xi_bmod_2\":3,\"global_squadnum_2\":1,\"global_pcharnum_2\":1,\"enemy_hp_3\":1,\"enemy_xi_amod_3\":1,\"enemy_xi_bmod_3\":1},\"point\":18,\"challenge\":{\"taskList\":{\"normalTask_1\":{\"fts\":1637568429,\"rts\":1637568684},\"normalTask_2\":{\"fts\":1637568429,\"rts\":1637568685},\"normalTask_3\":{\"fts\":-1,\"rts\":-1},\"normalTask_4\":{\"fts\":-1,\"rts\":-1},\"normalTask_5\":{\"fts\":-1,\"rts\":-1},\"normalTask_6\":{\"fts\":-1,\"rts\":-1}},\"topPoint\":18,\"pointList\":{\"1\":1637568685,\"2\":1637568685,\"3\":1637568685,\"4\":1637568685,\"5\":1637568685,\"6\":1637568685,\"7\":1637568685,\"8\":1637568685,\"9\":1637568685,\"10\":1637568685,\"11\":1637568685,\"12\":1637568685,\"13\":1637571331,\"14\":1637571331,\"15\":1637571331,\"16\":1637571331,\"17\":1637571331,\"18\":1637571331}}},\"temporary\":{\"schedule\":\"rg4\",\"nst\":1637956800,\"point\":8,\"challenge\":{\"taskList\":{\"dailyTask_4\":{\"fts\":1637922566,\"rts\":1637922767}},\"topPoint\":8,\"pointList\":{\"0\":1637922767,\"1\":1637922767,\"2\":1637922767,\"3\":1637922767,\"4\":1637922767,\"5\":1637922767,\"6\":1637922767,\"7\":1637922767,\"8\":1637922767}}},\"sInfo\":{\"assistCnt\":14,\"maxPnt\":18,\"chars\":[{\"charId\":\"char_1012_skadi2\",\"cnt\":7},{\"charId\":\"char_144_red\",\"cnt\":5},{\"charId\":\"char_222_bpipe\",\"cnt\":1}],\"history\":{}}}},\"box\":[]}},\"deleted\":{\"dexNav\":{\"enemy\":{\"stage\":[\"tr_level_rune_09-02\"]}}}}}");
   }
 }