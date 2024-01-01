 package com.hypergryph.arknights.game;
 
 import com.alibaba.fastjson.JSONArray;
 import com.alibaba.fastjson.JSONObject;
 import com.hypergryph.arknights.ArknightsApplication;
 import com.hypergryph.arknights.core.dao.userDao;
 import com.hypergryph.arknights.core.pojo.Account;
 import com.hypergryph.arknights.core.pojo.SearchUidList;
 import com.hypergryph.arknights.core.pojo.UserInfo;
 import java.util.List;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestHeader;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;
 
 
 
 
 
 
 @RestController
 @RequestMapping({"/social"})
 public class social
 {
   public JSONObject teamV2 = JSONObject.parseObject("{\"abyssal\":0,\"action4\":0,\"blacksteel\":0,\"bolivar\":0,\"chiave\":0,\"columbia\":0,\"egir\":0,\"followers\":0,\"glasgow\":0,\"higashi\":0,\"iberia\":0,\"karlan\":0,\"kazimierz\":0,\"kjerag\":0,\"laterano\":0,\"lee\":0,\"leithanien\":0,\"lgd\":0,\"lungmen\":0,\"minos\":0,\"penguin\":0,\"reserve1\":0,\"reserve4\":0,\"reserve6\":0,\"rhine\":0,\"rhodes\":0,\"rim\":0,\"sami\":0,\"sargon\":0,\"siesta\":0,\"siracusa\":0,\"student\":0,\"sui\":0,\"sweep\":0,\"ursus\":0,\"victoria\":0,\"yan\":0}\n");
 
   
   @PostMapping(value = {"/setAssistCharList"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject setAssistCharList(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
     JSONArray assistCharList = JsonBody.getJSONArray("assistCharList");
     
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
     
     JSONObject assistChar = new JSONObject();
     JSONObject UserSyncData = JSONObject.parseObject(((Account)Accounts.get(0)).getUser());
     
     UserSyncData.getJSONObject("social").put("assistCharList", assistCharList);
     
     userDao.setUserData(uid, UserSyncData);
     
     for (int i = 0; i < assistCharList.size(); i++) {
       if (assistCharList.getJSONObject(i) != null) {
         JSONObject charInfo = assistCharList.getJSONObject(i);
         String charInstId = charInfo.getString("charInstId");
         String charId = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(charInstId).getString("charId");
         charInfo.put("charId", charId);
         String profession = ArknightsApplication.characterJson.getJSONObject(charId).getString("profession");
         
         if (!assistChar.containsKey(profession)) assistChar.put(profession, new JSONArray());
         
         assistChar.getJSONArray(profession).add(charInfo);
       } 
     } 
     
     userDao.setAssistCharListData(uid, assistChar);
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("deleted", new JSONObject(true));
     JSONObject modified = new JSONObject(true);
     modified.put("social", UserSyncData.getJSONObject("social"));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     return result;
   }
 
 
 
   
   @PostMapping(value = {"/getSortListInfo"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject getSortListInfo(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
     int type = JsonBody.getIntValue("type");
     
     List<Account> Accounts = userDao.queryAccountBySecret(secret);
     if (Accounts.size() != 1) {
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("result", Integer.valueOf(2));
       jSONObject.put("error", "无法查询到此账户");
       return jSONObject;
     } 
     
     Long uid = Long.valueOf(((Account)Accounts.get(0)).getUid());
     
     JSONArray resultList = new JSONArray();
     
     if (type == 0) {
       
       String nickNumber = JsonBody.getJSONObject("param").getString("nickNumber");
       String nickName = JsonBody.getJSONObject("param").getString("nickName");
       
       List<SearchUidList> search = userDao.searchPlayer("%" + nickName + "%", "%" + nickNumber + "%");
       
       for (int i = 0; i < search.size(); i++) {
         if (((SearchUidList)search.get(i)).getUid() != uid.longValue()) {
           JSONObject FriendInfo = new JSONObject(true);
           FriendInfo.put("level", Long.valueOf(((SearchUidList)search.get(i)).getLevel()));
           FriendInfo.put("uid", Long.valueOf(((SearchUidList)search.get(i)).getUid()));
           resultList.add(FriendInfo);
         } 
       } 
     } 
     
     if (type == 1) {
       JSONArray FriendList = JSONObject.parseObject(((Account)Accounts.get(0)).getFriend()).getJSONArray("list");
       
       for (int i = 0; i < FriendList.size(); i++) {
         int FriendUid = FriendList.getJSONObject(i).getIntValue("uid");
         List<UserInfo> userInfo = userDao.queryUserInfo(FriendUid);
         JSONObject userStatus = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getStatus());
         JSONObject FriendInfo = new JSONObject(true);
         FriendInfo.put("level", Integer.valueOf(userStatus.getIntValue("level")));
         FriendInfo.put("infoShare", Integer.valueOf(0));
         FriendInfo.put("uid", Integer.valueOf(FriendUid));
         resultList.add(FriendInfo);
       } 
     } 
     
     if (type == 2) {
       
       JSONArray FriendRequest = JSONObject.parseObject(((Account)Accounts.get(0)).getFriend()).getJSONArray("request");
       
       resultList = FriendRequest;
     } 
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("deleted", new JSONObject(true));
     JSONObject modified = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("result", resultList);
     return result;
   }
 
 
 
   
   @PostMapping(value = {"/getFriendList"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject getFriendList(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
     JSONArray idList = JsonBody.getJSONArray("idList");
     
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
     
     JSONArray friends = new JSONArray();
     
     JSONArray board = new JSONArray();
     
     JSONObject medalBoard = new JSONObject(true);
     medalBoard.put("type", "EMPTY");
     medalBoard.put("template", null);
     medalBoard.put("custom", null);
     
     JSONArray friendAlias = new JSONArray();
     
     for (int i = 0; i < idList.size(); i++) {
       
       Long FriendUid = Long.valueOf(idList.getLongValue(i));
       
       List<UserInfo> userInfo = userDao.queryUserInfo(FriendUid.longValue());
       
       JSONArray userAssistCharList = JSONArray.parseArray(((UserInfo)userInfo.get(0)).getSocialAssistCharList());
       JSONObject userStatus = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getStatus());
       JSONObject chars = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getChars());
       JSONObject UserFriend = JSONObject.parseObject(((Account)Accounts.get(0)).getFriend());
       
       JSONObject FriendInfo = new JSONObject(true);
       JSONArray assistCharList = new JSONArray();
       
       for (int n = 0; n < userAssistCharList.size(); n++) {
         if (userAssistCharList.getJSONObject(n) != null) {
           String charInstId = String.valueOf(userAssistCharList.getJSONObject(n).getIntValue("charInstId"));
           JSONObject chardata = chars.getJSONObject(charInstId);
           chardata.put("skillIndex", Integer.valueOf(userAssistCharList.getJSONObject(n).getIntValue("skillIndex")));
           assistCharList.add(chardata);
         } else {
           assistCharList.add(null);
         } 
       } 
       FriendInfo.put("assistCharList", assistCharList);
       FriendInfo.put("avatarId", Integer.valueOf(userStatus.getIntValue("avatarId")));
       FriendInfo.put("uid", FriendUid);
       FriendInfo.put("board", board);
       FriendInfo.put("medalBoard", medalBoard);
       FriendInfo.put("charCnt", Integer.valueOf(chars.size()));
       FriendInfo.put("friendNumLimit", Integer.valueOf(50));
       FriendInfo.put("furnCnt", Integer.valueOf(0));
       FriendInfo.put("infoShare", Integer.valueOf(0));
       FriendInfo.put("lastOnlineTime", Integer.valueOf(userStatus.getIntValue("lastOnlineTs")));
       FriendInfo.put("level", Integer.valueOf(userStatus.getIntValue("level")));
       FriendInfo.put("mainStageProgress", userStatus.getString("mainStageProgress"));
       FriendInfo.put("nickName", userStatus.getString("nickName"));
       FriendInfo.put("nickNumber", userStatus.getString("nickNumber"));
       FriendInfo.put("avatar", userStatus.getJSONObject("avatar"));
       FriendInfo.put("resume", userStatus.getString("resume"));
       FriendInfo.put("recentVisited", Integer.valueOf(0));
       FriendInfo.put("registerTs", Integer.valueOf(userStatus.getIntValue("registerTs")));
       FriendInfo.put("secretary", userStatus.getString("secretary"));
       FriendInfo.put("secretarySkinId", userStatus.getString("secretarySkinId"));
       FriendInfo.put("serverName", "泰拉");
       FriendInfo.put("teamV2", this.teamV2);
       
       friends.add(FriendInfo);
       
       JSONArray FriendList = UserFriend.getJSONArray("list");
       
       for (int m = 0; m < FriendList.size(); m++) {
         if (FriendList.getJSONObject(m).getIntValue("uid") == FriendUid.longValue()) {
           friendAlias.add(FriendList.getJSONObject(m).getString("alias"));
         }
       } 
     } 
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("deleted", new JSONObject(true));
     JSONObject modified = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("friends", friends);
     result.put("resultIdList", idList);
     result.put("friendAlias", friendAlias);
     return result;
   }
 
 
 
   
   @PostMapping(value = {"/searchPlayer"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject searchPlayer(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
     JSONArray idList = JsonBody.getJSONArray("idList");
     
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
     
     Long uid = Long.valueOf(((Account)Accounts.get(0)).getUid());
     
     JSONArray friends = new JSONArray();
     
     JSONObject medalBoard = new JSONObject(true);
     medalBoard.put("type", "EMPTY");
     medalBoard.put("template", null);
     medalBoard.put("custom", null);
     
     JSONArray friendStatusList = new JSONArray();
     for (int i = 0; i < idList.size(); i++) {
       long FriendUid = idList.getLongValue(i);
       
       List<UserInfo> userInfo = userDao.queryUserInfo(FriendUid);
       
       JSONArray userAssistCharList = JSONArray.parseArray(((UserInfo)userInfo.get(0)).getSocialAssistCharList());
       JSONObject userStatus = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getStatus());
       JSONObject chars = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getChars());
       JSONObject UserFriend = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getFriend());
       
       JSONObject FriendInfo = new JSONObject(true);
       JSONArray assistCharList = new JSONArray();
       
       for (int n = 0; n < userAssistCharList.size(); n++) {
         if (userAssistCharList.getJSONObject(n) != null) {
           String charInstId = String.valueOf(userAssistCharList.getJSONObject(n).getIntValue("charInstId"));
           JSONObject chardata = chars.getJSONObject(charInstId);
           chardata.put("skillIndex", Integer.valueOf(userAssistCharList.getJSONObject(n).getIntValue("skillIndex")));
           assistCharList.add(chardata);
         } else {
           assistCharList.add(null);
         } 
       } 
       FriendInfo.put("assistCharList", assistCharList);
       FriendInfo.put("avatarId", Integer.valueOf(userStatus.getIntValue("avatarId")));
       FriendInfo.put("uid", Long.valueOf(FriendUid));
       FriendInfo.put("friendNumLimit", Integer.valueOf(999));
       FriendInfo.put("medalBoard", medalBoard);
       FriendInfo.put("lastOnlineTime", Integer.valueOf(userStatus.getIntValue("lastOnlineTs")));
       FriendInfo.put("level", Integer.valueOf(userStatus.getIntValue("level")));
       FriendInfo.put("nickName", userStatus.getString("nickName"));
       FriendInfo.put("nickNumber", userStatus.getString("nickNumber"));
       FriendInfo.put("avatar", userStatus.getJSONObject("avatar"));
       FriendInfo.put("resume", userStatus.getString("resume"));
       FriendInfo.put("serverName", "泰拉");
       
       friends.add(FriendInfo);
 
       
       JSONArray FriendRequest = UserFriend.getJSONArray("request");
       JSONArray FriendList = UserFriend.getJSONArray("list");
       
       Boolean isSet = Boolean.valueOf(false); int j;
       for (j = 0; j < FriendList.size(); j++) {
         if (FriendList.getJSONObject(j).getIntValue("uid") == uid.longValue()) {
           friendStatusList.add(Integer.valueOf(2));
           isSet = Boolean.valueOf(true);
         } 
       } 
       for (j = 0; j < FriendRequest.size(); j++) {
         if (FriendRequest.getJSONObject(j).getIntValue("uid") == uid.longValue()) {
           friendStatusList.add(Integer.valueOf(1));
           isSet = Boolean.valueOf(true);
         } 
       } 
       if (!isSet.booleanValue()) {
         friendStatusList.add(Integer.valueOf(0));
       }
     } 
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("deleted", new JSONObject(true));
     JSONObject modified = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("players", friends);
     result.put("resultIdList", idList);
     result.put("friendStatusList", friendStatusList);
     return result;
   }
 
 
   
   @PostMapping(value = {"/getFriendRequestList"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject getFriendRequestList(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
     JSONArray idList = JsonBody.getJSONArray("idList");
     
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
     
     JSONArray friends = new JSONArray();
     
     JSONArray board = new JSONArray();
     
     JSONObject medalBoard = new JSONObject(true);
     medalBoard.put("type", "EMPTY");
     medalBoard.put("template", null);
     medalBoard.put("custom", null);
     
     for (int i = 0; i < idList.size(); i++) {
       long FriendUid = idList.getIntValue(i);
       List<UserInfo> userInfo = userDao.queryUserInfo(FriendUid);
       
       JSONArray userAssistCharList = JSONArray.parseArray(((UserInfo)userInfo.get(0)).getSocialAssistCharList());
       JSONObject userStatus = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getStatus());
       JSONObject chars = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getChars());
       JSONObject UserFriend = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getFriend());
       
       JSONObject FriendInfo = new JSONObject(true);
       JSONArray assistCharList = new JSONArray();
       
       for (int n = 0; n < userAssistCharList.size(); n++) {
         if (userAssistCharList.getJSONObject(n) != null) {
           String charInstId = userAssistCharList.getJSONObject(n).getString("charInstId");
           JSONObject chardata = chars.getJSONObject(charInstId);
           chardata.put("skillIndex", Integer.valueOf(userAssistCharList.getJSONObject(n).getIntValue("skillIndex")));
           assistCharList.add(chardata);
         } else {
           assistCharList.add(null);
         } 
       } 
       FriendInfo.put("assistCharList", assistCharList);
       FriendInfo.put("avatarId", Integer.valueOf(userStatus.getIntValue("avatarId")));
       FriendInfo.put("uid", Long.valueOf(FriendUid));
       FriendInfo.put("board", board);
       FriendInfo.put("medalBoard", medalBoard);
       FriendInfo.put("charCnt", Integer.valueOf(chars.size()));
       FriendInfo.put("friendNumLimit", Integer.valueOf(50));
       FriendInfo.put("furnCnt", Integer.valueOf(0));
       FriendInfo.put("infoShare", Integer.valueOf(0));
       FriendInfo.put("lastOnlineTime", Integer.valueOf(userStatus.getIntValue("lastOnlineTs")));
       FriendInfo.put("level", Integer.valueOf(userStatus.getIntValue("level")));
       FriendInfo.put("mainStageProgress", userStatus.getString("mainStageProgress"));
       FriendInfo.put("nickName", userStatus.getString("nickName"));
       FriendInfo.put("nickNumber", userStatus.getString("nickNumber"));
       FriendInfo.put("avatar", userStatus.getJSONObject("avatar"));
       FriendInfo.put("resume", userStatus.getString("resume"));
       FriendInfo.put("recentVisited", Integer.valueOf(0));
       FriendInfo.put("registerTs", Integer.valueOf(userStatus.getIntValue("registerTs")));
       FriendInfo.put("secretary", userStatus.getString("secretary"));
       FriendInfo.put("secretarySkinId", userStatus.getString("secretarySkinId"));
       FriendInfo.put("serverName", "泰拉");
       FriendInfo.put("teamV2", this.teamV2);
       
       friends.add(FriendInfo);
     } 
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("deleted", new JSONObject(true));
     JSONObject modified = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("requestList", friends);
     result.put("resultIdList", idList);
     return result;
   }
 
 
 
   
   @PostMapping(value = {"/processFriendRequest"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject processFriendRequest(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
     int action = JsonBody.getIntValue("action");
     long friendId = JsonBody.getIntValue("friendId");
     
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
     
     JSONObject FriendJson = JSONObject.parseObject(((Account)Accounts.get(0)).getFriend());
     JSONArray FriendRequest = FriendJson.getJSONArray("request");
     JSONArray FriendList = FriendJson.getJSONArray("list");
     
     for (int i = 0; i < FriendRequest.size(); i++) {
       if (FriendRequest.getJSONObject(i).getIntValue("uid") == friendId) {
         
         FriendRequest.remove(i);
         FriendJson.put("request", FriendRequest);
         
         userDao.setFriendData(uid, FriendJson);
         
         if (action == 1) {
           JSONObject Friend = new JSONObject(true);
           Friend.put("uid", Long.valueOf(friendId));
           Friend.put("alias", null);
           FriendList.add(Friend);
           
           FriendJson.put("list", FriendList);
           userDao.setFriendData(uid, FriendJson);
         } 
       } 
     } 
     
     if (action == 1) {
       
       List<UserInfo> userInfo = userDao.queryUserInfo(friendId);
       
       JSONObject FJson = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getFriend());
       JSONArray FList = FJson.getJSONArray("list");
       
       JSONObject Friend = new JSONObject(true);
       Friend.put("uid", uid);
       Friend.put("alias", null);
       FList.add(Friend);
       
       FJson.put("list", FList);
       userDao.setFriendData(Long.valueOf(friendId), FJson);
     } 
     
     if (FriendRequest.size() == 0) {
       UserSyncData.getJSONObject("pushFlags").put("hasFriendRequest", Integer.valueOf(0));
     }
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("deleted", new JSONObject(true));
     JSONObject modified = new JSONObject(true);
     modified.put("pushFlags", UserSyncData.getJSONObject("pushFlags"));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("friendNum", Integer.valueOf(FriendList.size()));
     result.put("result", Integer.valueOf(0));
     return result;
   }
 
 
 
   
   @PostMapping(value = {"/sendFriendRequest"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject sendFriendRequest(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
     int afterBattle = JsonBody.getIntValue("afterBattle");
     long friendId = JsonBody.getIntValue("friendId");
     
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
     
     List<UserInfo> userInfo = userDao.queryUserInfo(friendId);
     
     JSONObject FriendJson = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getFriend());
     JSONArray FriendRequest = FriendJson.getJSONArray("request");
     JSONArray FriendList = FriendJson.getJSONArray("list");
     int i;
     for (i = 0; i < FriendList.size(); i++) {
       if (FriendList.getJSONObject(i).getIntValue("uid") == uid.longValue()) {
         JSONObject jSONObject = new JSONObject(true);
         jSONObject.put("result", Integer.valueOf(2));
         jSONObject.put("error", "已添加该好友");
         return jSONObject;
       } 
     } 
     
     for (i = 0; i < FriendRequest.size(); i++) {
       if (FriendRequest.getJSONObject(i).getIntValue("uid") == uid.longValue()) {
         JSONObject jSONObject = new JSONObject(true);
         jSONObject.put("result", Integer.valueOf(2));
         jSONObject.put("error", "已对该博士进行过好友申请");
         return jSONObject;
       } 
     } 
     
     JSONObject Request = new JSONObject(true);
     Request.put("uid", uid);
     FriendRequest.add(Request);
     
     FriendJson.put("request", FriendRequest);
     userDao.setFriendData(Long.valueOf(friendId), FriendJson);
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("deleted", new JSONObject(true));
     JSONObject modified = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("result", Integer.valueOf(0));
     return result;
   }
 
 
 
   
   @PostMapping(value = {"/setFriendAlias"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject setFriendAlias(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
     String alias = JsonBody.getString("alias");
     int friendId = JsonBody.getIntValue("friendId");
     
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
     
     List<UserInfo> userInfo = userDao.queryUserInfo(uid.longValue());
     
     JSONObject FriendJson = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getFriend());
     JSONArray FriendList = FriendJson.getJSONArray("list");
     
     for (int i = 0; i < FriendList.size(); i++) {
       if (FriendList.getJSONObject(i).getIntValue("uid") == friendId) {
         FriendList.getJSONObject(i).put("alias", alias);
       }
     } 
     
     FriendJson.put("list", FriendList);
     userDao.setFriendData(uid, FriendJson);
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("deleted", new JSONObject(true));
     JSONObject modified = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("result", Integer.valueOf(0));
     return result;
   }
 
 
 
   
   @PostMapping(value = {"/deleteFriend"}, produces = {"application/json;charset=UTF-8"})
   public JSONObject deleteFriend(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
     long friendId = JsonBody.getIntValue("friendId");
     
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
     
     List<UserInfo> userInfo = userDao.queryUserInfo(uid.longValue());
     
     JSONObject FriendJson = JSONObject.parseObject(((UserInfo)userInfo.get(0)).getFriend());
     JSONArray FriendList = FriendJson.getJSONArray("list");
     
     for (int i = 0; i < FriendList.size(); i++) {
       if (FriendList.getJSONObject(i).getIntValue("uid") == friendId) {
         FriendList.remove(i);
       }
     } 
     
     FriendJson.put("list", FriendList);
     userDao.setFriendData(uid, FriendJson);
     
     List<UserInfo> UserFriend = userDao.queryUserInfo(friendId);
     
     FriendJson = JSONObject.parseObject(((UserInfo)UserFriend.get(0)).getFriend());
     FriendList = FriendJson.getJSONArray("list");
     
     for (int j = 0; j < FriendList.size(); j++) {
       if (FriendList.getJSONObject(j).getIntValue("uid") == uid.longValue()) {
         FriendList.remove(j);
       }
     } 
     
     FriendJson.put("list", FriendList);
     userDao.setFriendData(Long.valueOf(friendId), FriendJson);
     
     userDao.setUserData(uid, UserSyncData);
     
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("deleted", new JSONObject(true));
     JSONObject modified = new JSONObject(true);
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     result.put("result", Integer.valueOf(0));
     return result;
   }
 }