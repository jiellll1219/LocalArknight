 package com.hypergryph.arknights.game;
 
 import com.alibaba.fastjson.JSONArray;
 import com.alibaba.fastjson.JSONObject;
 import com.hypergryph.arknights.ArknightsApplication;
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
 @RequestMapping({"/building"})
 public class building
 {
   @PostMapping(value = {"/sync"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject Sync(@RequestHeader("secret") String secret, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/sync");
     
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
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("building", UserSyncData.getJSONObject("building"));
     modified.put("event", UserSyncData.getJSONObject("event"));
     
     JSONObject result = new JSONObject(true);
     result.put("ts", Long.valueOf(ArknightsApplication.getTimestamp()));
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     return result;
   }
 
   
   @PostMapping(value = {"/getInfoShareVisitorsNum"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject getInfoShareVisitorsNum() {
     JSONObject result = new JSONObject(true);
     result.put("num", Integer.valueOf(0));
     return result;
   }
 
   
   @PostMapping(value = {"/getRecentVisitors"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject getRecentVisitors() {
     JSONObject result = new JSONObject(true);
     result.put("getRecentVisitors", new JSONArray());
     return result;
   }
 
 
   
   @PostMapping(value = {"/assignChar"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject assignChar(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/assignChar");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     String roomSlotId = JsonBody.getString("roomSlotId");
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
     
     JSONObject roomSlots = UserSyncData.getJSONObject("building").getJSONObject("roomSlots");
     
     for (Map.Entry entry : roomSlots.entrySet()) {
       JSONArray roomCharInstIds = roomSlots.getJSONObject(entry.getKey().toString()).getJSONArray("charInstIds");
       for (int i = 0; i < roomCharInstIds.size(); i++) {
         for (int n = 0; n < charInstIdList.size(); n++) {
           if (charInstIdList.get(n) == roomCharInstIds.get(i)) {
             roomCharInstIds.set(i, Integer.valueOf(-1));
           }
         } 
       } 
     } 
 
 
     
     UserSyncData.getJSONObject("building").getJSONObject("roomSlots").getJSONObject(roomSlotId).put("charInstIds", charInstIdList);
     
     if (roomSlotId.equals("slot_13")) {
       
       int trainer = charInstIdList.getIntValue(0);
       int trainee = charInstIdList.getIntValue(1);
       
       UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainee").put("charInstId", Integer.valueOf(trainee));
       UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainee").put("targetSkill", Integer.valueOf(-1));
       UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainee").put("speed", Integer.valueOf(1000));
       
       UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainer").put("charInstId", Integer.valueOf(trainer));
       
       if (trainee == -1) {
         UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainee").put("state", Integer.valueOf(0));
       } else {
         UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainee").put("state", Integer.valueOf(3));
       } 
       
       if (trainer == -1) {
         UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainer").put("state", Integer.valueOf(0));
       } else {
         UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainer").put("state", Integer.valueOf(3));
       } 
     } 
 
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("building", UserSyncData.getJSONObject("building"));
     modified.put("event", UserSyncData.getJSONObject("event"));
     
     JSONObject result = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     
     return result;
   }
 
 
   
   @PostMapping(value = {"/changeDiySolution"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject changeDiySolution(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/changeDiySolution");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     String roomSlotId = JsonBody.getString("roomSlotId");
     JSONObject solution = JsonBody.getJSONObject("solution");
 
 
 
 
     
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
     
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("DORMITORY").getJSONObject(roomSlotId).put("diySolution", solution);
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("building", UserSyncData.getJSONObject("building"));
     modified.put("event", UserSyncData.getJSONObject("event"));
     
     JSONObject result = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     
     return result;
   }
   
   public String getFormulaId(String formulaId) {
     if (formulaId.equals("1")) {
       return "2001";
     }
     if (formulaId.equals("2")) {
       return "2002";
     }
     if (formulaId.equals("3")) {
       return "2003";
     }
     if (formulaId.equals("4")) {
       return "3003";
     }
     if (formulaId.equals("5")) {
       return "3213";
     }
     if (formulaId.equals("6")) {
       return "3223";
     }
     if (formulaId.equals("7")) {
       return "3233";
     }
     if (formulaId.equals("8")) {
       return "3243";
     }
     if (formulaId.equals("9")) {
       return "3253";
     }
     if (formulaId.equals("10")) {
       return "3263";
     }
     if (formulaId.equals("11")) {
       return "3273";
     }
     if (formulaId.equals("12")) {
       return "3283";
     }
     if (formulaId.equals("13")) {
       return "3141";
     }
     if (formulaId.equals("14")) {
       return "3141";
     }
     return null;
   }
 
 
   
   @PostMapping(value = {"/changeManufactureSolution"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject changeManufactureSolution(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/changeManufactureSolution");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     String roomSlotId = JsonBody.getString("roomSlotId");
     String targetFormulaId = JsonBody.getString("targetFormulaId");
     int solutionCount = JsonBody.getIntValue("solutionCount");
 
 
 
 
     
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
     
     int outputSolutionCnt = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).getIntValue("outputSolutionCnt");
     String FormulaId = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).getString("formulaId");
     
     if (outputSolutionCnt != 0) {
       if (Integer.valueOf(FormulaId).intValue() >= 5 && Integer.valueOf(FormulaId).intValue() <= 12) {
         String itemId = null;
         if (Integer.valueOf(FormulaId).intValue() == 5) {
           itemId = "3212";
         }
         if (Integer.valueOf(FormulaId).intValue() == 6) {
           itemId = "3222";
         }
         if (Integer.valueOf(FormulaId).intValue() == 7) {
           itemId = "3232";
         }
         if (Integer.valueOf(FormulaId).intValue() == 8) {
           itemId = "3242";
         }
         if (Integer.valueOf(FormulaId).intValue() == 9) {
           itemId = "3252";
         }
         if (Integer.valueOf(FormulaId).intValue() == 10) {
           itemId = "3262";
         }
         if (Integer.valueOf(FormulaId).intValue() == 11) {
           itemId = "3272";
         }
         if (Integer.valueOf(FormulaId).intValue() == 12) {
           itemId = "3282";
         }
         UserSyncData.getJSONObject("inventory").put(getFormulaId(FormulaId), Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(getFormulaId(FormulaId)) + outputSolutionCnt));
         UserSyncData.getJSONObject("inventory").put(itemId, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(itemId) - 2 * outputSolutionCnt));
         UserSyncData.getJSONObject("inventory").put("32001", Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("32001") - 1 * outputSolutionCnt));
       } else if (Integer.valueOf(FormulaId).intValue() > 12) {
         String itemId = null;
         if (Integer.valueOf(FormulaId).intValue() == 13) {
           itemId = "30012";
           UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") - 1600 * outputSolutionCnt));
         } 
         if (Integer.valueOf(FormulaId).intValue() == 14) {
           itemId = "30062";
           UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") - 1000 * outputSolutionCnt));
         } 
         UserSyncData.getJSONObject("inventory").put(getFormulaId(FormulaId), Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(getFormulaId(FormulaId)) + outputSolutionCnt));
         UserSyncData.getJSONObject("inventory").put(itemId, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(itemId) - 2 * outputSolutionCnt));
       } else {
         UserSyncData.getJSONObject("inventory").put(getFormulaId(FormulaId), Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(getFormulaId(FormulaId)) + outputSolutionCnt));
       } 
     }
 
     
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("state", Integer.valueOf(1));
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("formulaId", targetFormulaId);
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("lastUpdateTime", Long.valueOf((new Date()).getTime() / 1000L));
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("completeWorkTime", Integer.valueOf(-1));
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("remainSolutionCnt", Integer.valueOf(0));
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("outputSolutionCnt", Integer.valueOf(solutionCount));
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("building", UserSyncData.getJSONObject("building"));
     modified.put("event", UserSyncData.getJSONObject("event"));
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     
     JSONObject result = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     
     return result;
   }
 
 
   
   @PostMapping(value = {"/settleManufacture"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject settleManufacture(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/settleManufacture");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     JSONArray roomSlotIdList = JsonBody.getJSONArray("roomSlotIdList");
 
 
 
 
     
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
 
     
     for (int i = 0; i < roomSlotIdList.size(); i++) {
       
       String roomSlotId = roomSlotIdList.getString(i);
       
       int outputSolutionCnt = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).getIntValue("outputSolutionCnt");
       String FormulaId = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).getString("formulaId");
       
       if (outputSolutionCnt != 0) {
         if (Integer.valueOf(FormulaId).intValue() >= 5 && Integer.valueOf(FormulaId).intValue() <= 12) {
           String itemId = null;
           if (Integer.valueOf(FormulaId).intValue() == 5) {
             itemId = "3212";
           }
           if (Integer.valueOf(FormulaId).intValue() == 6) {
             itemId = "3222";
           }
           if (Integer.valueOf(FormulaId).intValue() == 7) {
             itemId = "3232";
           }
           if (Integer.valueOf(FormulaId).intValue() == 8) {
             itemId = "3242";
           }
           if (Integer.valueOf(FormulaId).intValue() == 9) {
             itemId = "3252";
           }
           if (Integer.valueOf(FormulaId).intValue() == 10) {
             itemId = "3262";
           }
           if (Integer.valueOf(FormulaId).intValue() == 11) {
             itemId = "3272";
           }
           if (Integer.valueOf(FormulaId).intValue() == 12) {
             itemId = "3282";
           }
           UserSyncData.getJSONObject("inventory").put(getFormulaId(FormulaId), Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(getFormulaId(FormulaId)) + outputSolutionCnt));
           UserSyncData.getJSONObject("inventory").put(itemId, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(itemId) - 2 * outputSolutionCnt));
           UserSyncData.getJSONObject("inventory").put("32001", Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("32001") - 1 * outputSolutionCnt));
         } else if (Integer.valueOf(FormulaId).intValue() > 12) {
           String itemId = null;
           if (Integer.valueOf(FormulaId).intValue() == 13) {
             itemId = "30012";
             UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") - 1600 * outputSolutionCnt));
           } 
           if (Integer.valueOf(FormulaId).intValue() == 14) {
             itemId = "30062";
             UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") - 1000 * outputSolutionCnt));
           } 
           UserSyncData.getJSONObject("inventory").put(getFormulaId(FormulaId), Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(getFormulaId(FormulaId)) + outputSolutionCnt));
           UserSyncData.getJSONObject("inventory").put(itemId, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(itemId) - 2 * outputSolutionCnt));
         } else {
           UserSyncData.getJSONObject("inventory").put(getFormulaId(FormulaId), Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(getFormulaId(FormulaId)) + outputSolutionCnt));
         } 
       }
 
       
       UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("state", Integer.valueOf(0));
       UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("formulaId", "");
       UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("lastUpdateTime", Long.valueOf((new Date()).getTime() / 1000L));
       UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("completeWorkTime", Integer.valueOf(-1));
       UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("remainSolutionCnt", Integer.valueOf(0));
       UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("outputSolutionCnt", Integer.valueOf(0));
     } 
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("building", UserSyncData.getJSONObject("building"));
     modified.put("event", UserSyncData.getJSONObject("event"));
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     
     JSONObject result = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     
     return result;
   }
 
 
   
   @PostMapping(value = {"/workshopSynthesis"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject workshopSynthesis(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/workshopSynthesis");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     String formulaId = JsonBody.getString("formulaId");
     int workCount = JsonBody.getIntValue("times");
 
 
     
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
     
     JSONObject workshopFormulas = ArknightsApplication.buildingData.getJSONObject(formulaId);
     
     JSONArray costs = workshopFormulas.getJSONArray("costs");
     for (int i = 0; i < costs.size(); i++) {
       String itemId = costs.getJSONObject(i).getString("id");
       int itemCount = costs.getJSONObject(i).getIntValue("count");
       UserSyncData.getJSONObject("inventory").put(itemId, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(itemId) - itemCount * workCount));
     } 
     UserSyncData.getJSONObject("inventory").put(workshopFormulas.getString("itemId"), Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(workshopFormulas.getString("itemId")) + workshopFormulas.getIntValue("count") * workCount));
     UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") - workshopFormulas.getIntValue("goldCost") * workCount));
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("building", UserSyncData.getJSONObject("building"));
     modified.put("event", UserSyncData.getJSONObject("event"));
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     
     JSONObject result = new JSONObject(true);
     JSONObject results = new JSONObject(true);
     results.put("type", "MATERIAL");
     results.put("id", workshopFormulas.getString("itemId"));
     results.put("count", Integer.valueOf(workCount));
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     result.put("results", results);
     return result;
   }
 
 
   
   @PostMapping(value = {"/upgradeSpecialization"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject upgradeSpecialization(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/upgradeSpecialization");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     int skillIndex = JsonBody.getIntValue("skillIndex");
     
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
     
     int charInstId = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").getIntValue("charInstId");
     
     String charId = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(charInstId)).getString("charId");
     
     JSONArray levelUpCost = ArknightsApplication.characterJson.getJSONObject(charId).getJSONArray("skills").getJSONObject(skillIndex).getJSONArray("levelUpCostCond").getJSONObject(UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(charInstId)).getJSONArray("skills").getJSONObject(skillIndex).getIntValue("specializeLevel")).getJSONArray("levelUpCost");
     
     for (int i = 0; i < levelUpCost.size(); i++) {
       String id = levelUpCost.getJSONObject(i).getString("id");
       int count = levelUpCost.getJSONObject(i).getIntValue("count");
       UserSyncData.getJSONObject("inventory").put(id, Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue(id) - count));
     } 
     
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").put("state", Integer.valueOf(2));
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").put("targetSkill", Integer.valueOf(skillIndex));
     
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainer").put("state", Integer.valueOf(2));
     
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").put("lastUpdateTime", Long.valueOf((new Date()).getTime() / 1000L));
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject troop = new JSONObject(true);
     JSONObject chars = new JSONObject(true);
     
     chars.put(String.valueOf(charInstId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(charInstId)));
     
     troop.put("chars", chars);
     modified.put("building", UserSyncData.getJSONObject("building"));
     modified.put("event", UserSyncData.getJSONObject("event"));
     modified.put("troop", troop);
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     
     JSONObject result = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     
     return result;
   }
 
 
   
   @PostMapping(value = {"/completeUpgradeSpecialization"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject completeUpgradeSpecialization(@RequestHeader("secret") String secret, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/completeUpgradeSpecialization");
     
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
 
     
     int charInstId = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").getIntValue("charInstId");
     int targetSkill = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").getIntValue("targetSkill");
     
     int specializeLevel = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(charInstId)).getJSONArray("skills").getJSONObject(targetSkill).getIntValue("specializeLevel");
     
     UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(charInstId)).getJSONArray("skills").getJSONObject(targetSkill).put("specializeLevel", Integer.valueOf(specializeLevel + 1));
     
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").put("state", Integer.valueOf(3));
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").put("targetSkill", Integer.valueOf(-1));
     
     UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainer").put("state", Integer.valueOf(3));
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject troop = new JSONObject(true);
     JSONObject chars = new JSONObject(true);
     
     chars.put(String.valueOf(charInstId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(charInstId)));
     
     troop.put("chars", chars);
     modified.put("building", UserSyncData.getJSONObject("building"));
     modified.put("event", UserSyncData.getJSONObject("event"));
     modified.put("troop", troop);
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     
     JSONObject result = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     
     return result;
   }
 
 
   
   @PostMapping(value = {"/deliveryOrder"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject deliveryOrder(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/deliveryOrder");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     String slotId = JsonBody.getString("slotId");
     int orderId = JsonBody.getIntValue("orderId");
 
 
 
 
     
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
     
     if (slotId.equals("slot_24")) {
       UserSyncData.getJSONObject("inventory").put("3003", Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("3003") - 2));
       UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + 1000));
     } 
     if (slotId.equals("slot_14")) {
       UserSyncData.getJSONObject("inventory").put("3003", Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("3003") - 4));
       UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + 2000));
     } 
     if (slotId.equals("slot_5")) {
       UserSyncData.getJSONObject("inventory").put("3003", Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("3003") - 6));
       UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + 3000));
     } 
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("building", UserSyncData.getJSONObject("building"));
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     
     JSONObject result = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     
     return result;
   }
 
 
   
   @PostMapping(value = {"/deliveryBatchOrder"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject deliveryBatchOrder(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/deliveryBatchOrder");
     
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
     } 
     
     JSONArray slotList = JsonBody.getJSONArray("slotList");
 
 
     
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
     
     JSONObject delivered = new JSONObject(true);
     for (int i = 0; i < slotList.size(); i++) {
       String slotId = slotList.getString(i);
       int count = 0;
       if (slotId.equals("slot_24")) {
         count = 1000;
         UserSyncData.getJSONObject("inventory").put("3003", Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("3003") - 2));
         UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + count));
       } 
       if (slotId.equals("slot_14")) {
         count = 2000;
         UserSyncData.getJSONObject("inventory").put("3003", Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("3003") - 4));
         UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + count));
       } 
       if (slotId.equals("slot_5")) {
         count = 3000;
         UserSyncData.getJSONObject("inventory").put("3003", Integer.valueOf(UserSyncData.getJSONObject("inventory").getIntValue("3003") - 6));
         UserSyncData.getJSONObject("status").put("gold", Integer.valueOf(UserSyncData.getJSONObject("status").getIntValue("gold") + count));
       } 
       
       JSONArray itemGet = new JSONArray();
       JSONObject GOLD = new JSONObject(true);
       GOLD.put("type", "GOLD");
       GOLD.put("id", "4001");
       GOLD.put("count", Integer.valueOf(count));
       itemGet.add(GOLD);
       delivered.put(slotId, itemGet);
     } 
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     modified.put("inventory", UserSyncData.getJSONObject("inventory"));
     modified.put("status", UserSyncData.getJSONObject("status"));
     
     JSONObject result = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     playerDataDelta.put("deleted", new JSONObject(true));
     result.put("playerDataDelta", playerDataDelta);
     result.put("delivered", delivered);
     return result;
   }
 }