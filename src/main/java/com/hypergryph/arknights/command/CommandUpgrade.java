 package com.hypergryph.arknights.command;
 
 import com.alibaba.fastjson.JSONObject;
 import com.hypergryph.arknights.ArknightsApplication;
 import com.hypergryph.arknights.command.CommandBase;
 import com.hypergryph.arknights.command.CommandException;
 import com.hypergryph.arknights.command.ICommandSender;
 import com.hypergryph.arknights.core.dao.userDao;
 import com.hypergryph.arknights.core.pojo.Account;
 import java.util.List;
 import java.util.Map;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 
 public class CommandUpgrade
   extends CommandBase {
   private static final Logger LOGGER = LogManager.getLogger();
 
   
   public String getCommandName() {
     return "upgrade";
   }
 
   
   public String getCommandUsage(ICommandSender sender) {
     return "[int]<玩家UID>";
   }
 
   
   public String getCommandDescription() {
     return "让某位玩家的所有干员升级至满级";
   }
 
   
   public String getCommandExample() {
     return "/upgrade 10000001";
   }
 
   
   public String getCommandExampleUsage() {
     return "把UID为10000001的玩家的所有干员升级至满级";
   }
 
   
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
     if (args.length >= 2) {
       
       int uid = 0;
 
       
       try {
         uid = Integer.parseInt(args[1]);
       } catch (Exception e) {
         LOGGER.error("使用方式: /" + getCommandName() + " " + getCommandUsage(sender));
         return;
       } 
       List<Account> user = userDao.queryAccountByUid(uid);
       
       if (user.size() != 1) {
         LOGGER.error("无法找到该玩家");
         return;
       } 
       JSONObject UserSyncData = JSONObject.parseObject(((Account)user.get(0)).getUser());
       
       JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars");
       for (Map.Entry entry : chars.entrySet()) {
         
         JSONObject userChar = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
         String charId = userChar.getString("charId");
         int charRarity = ArknightsApplication.characterJson.getJSONObject(charId).getIntValue("rarity");
         int level = 0;
         int evolvePhase = 0;
         userChar.put("favorPoint", Integer.valueOf(25570));
         
         if (charRarity != 0 && charRarity != 1) {
           userChar.put("mainSkillLvl", Integer.valueOf(7));
         }
         
         if (charRarity == 0 || charRarity == 1) {
           level = 30;
           evolvePhase = 0;
         } 
         if (charRarity == 2) {
           level = 55;
           evolvePhase = 1;
         } 
         if (charRarity == 3) {
           level = 70;
           evolvePhase = 2;
         } 
         if (charRarity == 4) {
           level = 80;
           evolvePhase = 2;
         } 
         if (charRarity == 5) {
           level = 90;
           evolvePhase = 2;
         } 
         userChar.put("level", Integer.valueOf(level));
         userChar.put("evolvePhase", Integer.valueOf(evolvePhase));
         
         for (int i = 0; i < userChar.getJSONArray("skills").size(); i++) {
           userChar.getJSONArray("skills").getJSONObject(i).put("unlock", Integer.valueOf(1));
           userChar.getJSONArray("skills").getJSONObject(i).put("specializeLevel", Integer.valueOf(3));
         } 
       } 
       
       userDao.setUserData(Long.valueOf(uid), UserSyncData);
       LOGGER.info("已把该玩家所有干员升至满级");
       return;
     } 
     LOGGER.error("使用方式: /" + getCommandName() + " " + getCommandUsage(sender));
   }
 }