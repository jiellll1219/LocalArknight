package com.hypergryph.arknights.command;import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.command.CommandBase;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommandSender;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;public class CommandUnLock
  extends CommandBase
{
   private static final Logger LOGGER = LogManager.getLogger();  
  public String getCommandName() {
     return "unlock";
  }  
  public String getCommandUsage(ICommandSender sender) {
     return "[int]<玩家UID> [string]<关卡ID>";
  }  
  public String getCommandDescription() {
     return "解锁某位玩家的关卡";
  }  
  public String getCommandExample() {
     return "/unlock 10000001 main_03-08";
  }  
  public String getCommandExampleUsage() {
     return "为UID为10000001的玩家解锁 3-8 关卡 详细信息请查看 data/excel/stage_table.json";
  }  
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
     if (args.length >= 3) {
      
       int uid = 0;
       String stageId = "";      
      try {
         uid = Integer.parseInt(args[1]);
         stageId = args[2];
       } catch (Exception e) {
         LOGGER.error("使用方式: /" + getCommandName() + " " + getCommandUsage(sender));
        
        return;
      } 
       List<Account> user = userDao.queryAccountByUid(uid);
      
       if (user.size() != 1) {
         LOGGER.error("无法找到该玩家");
        
        return;
      } 
       if (!ArknightsApplication.stageTable.containsKey(stageId)) {
         LOGGER.error("未知的关卡ID，请检查并修改后重试");
        
        return;
      } 
       JSONObject UserSyncData = JSONObject.parseObject(((Account)user.get(0)).getUser());
      
       JSONObject stageInfo = new JSONObject();
       stageInfo.put("stageId", stageId);
       stageInfo.put("completeTimes", Integer.valueOf(1));
       stageInfo.put("startTimes", Integer.valueOf(1));
       stageInfo.put("practiceTimes", Integer.valueOf(1));
       stageInfo.put("state", Integer.valueOf(3));
       stageInfo.put("hasBattleReplay", Integer.valueOf(0));
       stageInfo.put("noCostCnt", Integer.valueOf(0));
      
       UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(stageId, stageInfo);
      
       userDao.setUserData(Long.valueOf(uid), UserSyncData);
       LOGGER.info("已为该玩家解锁 " + stageId);
      return;
    } 
     LOGGER.error("使用方式: /" + getCommandName() + " " + getCommandUsage(sender));
  }
}