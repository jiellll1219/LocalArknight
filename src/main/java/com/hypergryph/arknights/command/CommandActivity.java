 package com.hypergryph.arknights.command;
 
 import com.alibaba.fastjson.JSONArray;
 import com.alibaba.fastjson.JSONObject;
 import com.hypergryph.arknights.ArknightsApplication;
 import com.hypergryph.arknights.command.CommandBase;
 import com.hypergryph.arknights.command.CommandException;
 import com.hypergryph.arknights.command.ICommandSender;
 import com.hypergryph.arknights.core.dao.userDao;
 import com.hypergryph.arknights.core.pojo.Account;
 import java.util.Date;
 import java.util.List;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 
 
 
 public class CommandActivity
   extends CommandBase
 {
   private static final Logger LOGGER = LogManager.getLogger();
 
   
   public String getCommandName() {
     return "activity";
   }
 
   
   public String getCommandUsage(ICommandSender sender) {
     return "[int]<玩家UID> [string]<活动ID>";
   }
 
   
   public String getCommandDescription() {
     return "解锁某位玩家的活动";
   }
 
   
   public String getCommandExample() {
     return "/unlock 10000001 act10mini";
   }
 
   
   public String getCommandExampleUsage() {
     return "为UID为10000001的玩家解锁 act10mini 活动 详细信息请查看 data/unlockActivity.json";
   }
 
   
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
     if (args.length >= 3) {
       
       int uid = 0;
       String activityId = "";
 
       
       try {
         uid = Integer.parseInt(args[1]);
         activityId = args[2];
       } catch (Exception e) {
         LOGGER.error("使用方式: /" + getCommandName() + " " + getCommandUsage(sender));
         
         return;
       } 
       List<Account> user = userDao.queryAccountByUid(uid);
       
       if (user.size() != 1) {
         LOGGER.error("无法找到该玩家");
         
         return;
       } 
       if (!ArknightsApplication.unlockActivity.containsKey(activityId)) {
         LOGGER.error("未知的活动ID，请检查并修改后重试");
         
         return;
       } 
       JSONObject UserSyncData = JSONObject.parseObject(((Account)user.get(0)).getUser());
       
       JSONObject activity = ArknightsApplication.unlockActivity.getJSONObject(activityId);
       String activityType = activity.getString("type");
       
       for (int i = 0; i < activity.getJSONObject("activity").getJSONArray("stage").size(); i++) {
         String stageId = activity.getJSONObject("activity").getJSONArray("stage").getString(i);
         
         JSONObject stageInfo = new JSONObject();
         stageInfo.put("stageId", stageId);
         stageInfo.put("completeTimes", Integer.valueOf(1));
         stageInfo.put("startTimes", Integer.valueOf(1));
         stageInfo.put("practiceTimes", Integer.valueOf(1));
         stageInfo.put("state", Integer.valueOf(3));
         stageInfo.put("hasBattleReplay", Integer.valueOf(0));
         stageInfo.put("noCostCnt", Integer.valueOf(0));
         
         UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(stageId, stageInfo);
       } 
       
       JSONObject activityStory = new JSONObject();
       JSONArray stories = new JSONArray();
       
       for (int j = 0; j < activity.getJSONObject("activity").getJSONArray("storyreview").size(); j++) {
         String storyReviewId = activity.getJSONObject("activity").getJSONArray("storyreview").getString(j);
         
         JSONObject storyReview = new JSONObject();
         storyReview.put("id", storyReviewId);
         storyReview.put("uts", Long.valueOf((new Date()).getTime() / 1000L));
         storyReview.put("rc", Integer.valueOf(0));
         
         stories.add(storyReview);
       } 
       
       activityStory.put("rts", Long.valueOf((new Date()).getTime() / 1000L));
       activityStory.put("stories", stories);
       
       if (!UserSyncData.getJSONObject("activity").containsKey("activityType")) {
         UserSyncData.getJSONObject("activity").put(activityType, new JSONObject());
       }
       
       UserSyncData.getJSONObject("activity").getJSONObject(activityType).put(activityId, activity.getJSONObject("activity").getJSONObject(activityType).getJSONObject(activityId));
       UserSyncData.getJSONObject("storyreview").getJSONObject("groups").put(activityId, activityStory);
       
       userDao.setUserData(Long.valueOf(uid), UserSyncData);
       LOGGER.info("已为该玩家解锁 " + activityId);
       return;
     } 
     LOGGER.error("使用方式: /" + getCommandName() + " " + getCommandUsage(sender));
   }
 }