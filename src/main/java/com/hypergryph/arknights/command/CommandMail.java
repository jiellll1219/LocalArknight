 package com.hypergryph.arknights.command;
 
 import com.alibaba.fastjson.JSONArray;
 import com.alibaba.fastjson.JSONObject;
 import com.hypergryph.arknights.ArknightsApplication;
 import com.hypergryph.arknights.command.CommandBase;
 import com.hypergryph.arknights.command.CommandException;
 import com.hypergryph.arknights.command.ICommandSender;
 import com.hypergryph.arknights.core.dao.mailDao;
 import com.hypergryph.arknights.core.dao.userDao;
 import com.hypergryph.arknights.core.pojo.Account;
 import com.hypergryph.arknights.core.pojo.Mail;
 import java.util.Arrays;
 import java.util.List;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 
 public class CommandMail
   extends CommandBase
 {
   private static final Logger LOGGER = LogManager.getLogger();
 
   
   public String getCommandName() {
     return "mail";
   }
 
   
   public String getCommandUsage(ICommandSender sender) {
     return "[string]<邮件名|list> [string]<子命令>";
   }
 
   
   public String getCommandDescription() {
     return "处理邮件";
   }
 
   
   public String getCommandExample() {
     return "/mail 测试 create";
   }
 
   
   public String getCommandExampleUsage() {
     return "创造邮件，其名为\"测试\"";
   }
 
   
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
     if (args.length == 1) {
       
       LOGGER.error("需要指定邮件名或使用 /mail list");
     } else if (args.length == 2) {
       if (args[1].equals("list")) {
         mailDao.queryMails().forEach(mail -> LOGGER.info("ID: " + mail.getId() + ", 邮件名: " + mail.getName() + ", 主题: " + mail.getSubject()));
         
         return;
       } 
       
       LOGGER.error("可用的子命令: create<创建邮件> setFrom<设置来源> setSubject<设置主题> setContent<设置内容> items<物品管理> info<查看信息> send<发送邮件>");
     } else {
       
       List<Mail> mailList = mailDao.queryMailByName(args[1]);
       String[] subCommandArgs = Arrays.<String, String>copyOfRange(args, 3, args.length, String[].class);
       
       switch (args[2]) {
         case "create":
           processCreateCommand(sender, args[1], mailList, subCommandArgs);
           return;
         case "setFrom":
           processSetFromCommand(sender, args[1], mailList, subCommandArgs);
           return;
         case "setSubject":
           processSetSubjectCommand(sender, args[1], mailList, subCommandArgs);
           return;
         case "setContent":
           processSetContentCommand(sender, args[1], mailList, subCommandArgs);
           return;
         case "items":
           processItemsCommand(sender, args[1], mailList, subCommandArgs);
           return;
         case "send":
           processSendCommand(sender, args[1], mailList, subCommandArgs);
           return;
         case "info":
           processInfoCommand(sender, args[1], mailList, subCommandArgs);
           return;
       } 
       LOGGER.error("可用的子命令: create<创建邮件> setFrom<设置来源> setSubject<设置主题> setContent<设置内容> items<物品管理> send<发送邮件>");
     } 
   }
 
 
 
   
   private void processInfoCommand(ICommandSender sender, String arg, List<Mail> mailList, String[] subCommandArgs) {
     if (mailList.size() != 1) {
       LOGGER.error("指定的邮件名不存在");
       return;
     } 
     Mail mail = mailList.get(0);
     LOGGER.info("ID: " + mail.getId() + ", 邮件名: " + mail.getName());
     LOGGER.info("主题: " + mail.getSubject() + ", 来自: " + ArknightsApplication.characterJson
         .getJSONObject(mail.getFrom()).getString("name"));
     LOGGER.info("正文: ");
     LOGGER.info(mail.getContent());
     LOGGER.info("物品: ");
     JSONArray.parseArray(mail.getItems()).forEach(obj -> {
           if (!(obj instanceof JSONObject)) {
             return;
           }
           JSONObject item = (JSONObject)obj;
           if (item.getString("type").equals("CHAR")) {
             LOGGER.info(ArknightsApplication.characterJson.getJSONObject(item.getString("id")).getString("name") + " * " + item.getIntValue("count"));
           } else {
             LOGGER.info(ArknightsApplication.itemTable.getJSONObject(item.getString("id")).getString("name") + " * " + item.getIntValue("count"));
           } 
         });
   }
 
   
   private void processCreateCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
     if (mailList.size() != 0) {
       LOGGER.error("指定的邮件名已存在");
       return;
     } 
     int result = mailDao.createMail(name);
     if (result == 1) { LOGGER.info("创建成功"); }
     else { LOGGER.error("出了点意外，为什么捏？"); }
   
   }
   private void processSetFromCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
     if (mailList.size() != 1) {
       LOGGER.error("指定的邮件名不存在");
       return;
     } 
     if (args.length == 0) {
       LOGGER.error("用法: /mail " + name + " setFrom [string]<来源>");
       return;
     } 
     String characterId = null;
     for (String id : ArknightsApplication.characterJson.keySet()) {
       JSONObject character = ArknightsApplication.characterJson.getJSONObject(id);
       if (character.getString("name").equals(args[0]) || id.equals(args[0]))
         characterId = id; 
     } 
     if (characterId == null) {
       characterId = "none";
       LOGGER.info("未查找到此干员，默认企鹅物流");
     } 
     int result = mailDao.setMailFrom(((Mail)mailList.get(0)).getId(), characterId);
     if (result == 1) { LOGGER.info("修改成功"); }
     else { LOGGER.error("出了点意外，为什么捏？"); }
   
   }
   private void processSetSubjectCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
     if (mailList.size() != 1) {
       LOGGER.error("指定的邮件名不存在");
       return;
     } 
     if (args.length == 0) {
       LOGGER.error("用法: /mail " + name + " setSubject [string]<主题>");
       return;
     } 
     int result = mailDao.setMailSubject(((Mail)mailList.get(0)).getId(), joinToString(args, " "));
     if (result == 1) { LOGGER.info("修改成功"); }
     else { LOGGER.error("出了点意外，为什么捏？"); }
   
   }
   private void processSetContentCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
     if (mailList.size() != 1) {
       LOGGER.error("指定的邮件名不存在");
       return;
     } 
     if (args.length == 0) {
       LOGGER.error("用法: /mail " + name + " setConent [string]<内容>");
       return;
     } 
     int result = mailDao.setMailContent(((Mail)mailList.get(0)).getId(), joinToString(args, " "));
     if (result == 1) { LOGGER.info("修改成功"); }
     else { LOGGER.error("出了点意外，为什么捏？"); }
   
   }
   private void processItemsCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
     if (mailList.size() != 1) {
       LOGGER.error("指定的邮件名不存在");
       return;
     } 
     Mail mail = mailList.get(0);
     if (args.length == 0)
     { LOGGER.error("可用的二级子命令: list<查看物品列表> add<添加物品> del<删除物品>"); }
     else if (args[0].equals("list"))
     { JSONArray.parseArray(mail.getItems()).forEach(obj -> {
             if (!(obj instanceof JSONObject)) {
               return;
             }
             JSONObject item = (JSONObject)obj;
             if (item.getString("type").equals("CHAR")) {
               LOGGER.info(ArknightsApplication.characterJson.getJSONObject(item.getString("id")).getString("name") + " * " + item.getIntValue("count"));
             } else {
               LOGGER.info(ArknightsApplication.itemTable.getJSONObject(item.getString("id")).getString("name") + " * " + item.getIntValue("count"));
             } 
           }); }
     else if (args[0].equals("add"))
     { if (args.length == 1) {
         LOGGER.error("用法: /mail " + name + " items add [String]<物品|干员> [int]<数量>");
         return;
       } 
       int itemCount = 0;
       try {
         itemCount = Integer.parseInt(args[2]);
       } catch (Exception e) {
         LOGGER.error("解析数量时出错, 原文: " + args[2]);
         return;
       } 
       if (itemCount <= 0 || itemCount > 9999999) {
         LOGGER.error("数量范围应在1-9999999");
         return;
       } 
       String itemId = null, itemType = null;
       for (String id : ArknightsApplication.itemTable.keySet()) {
         if (ArknightsApplication.itemTable.getJSONObject(id).getString("name").equals(args[1]) || id.equals(args[1])) {
           itemId = id;
           itemType = ArknightsApplication.itemTable.getJSONObject(id).getString("itemType");
         } 
       } 
       for (String id : ArknightsApplication.characterJson.keySet()) {
         if (ArknightsApplication.characterJson.getJSONObject(id).getString("name").equals(args[1]) || id.equals(args[1])) {
           itemId = id;
           itemType = "CHAR";
         } 
       } 
       if (itemId == null) {
         LOGGER.error("未查找到此物品或干员");
         return;
       } 
       JSONArray items = JSONArray.parseArray(mail.getItems());
       JSONObject item = new JSONObject(true);
       item.put("id", itemId);
       item.put("type", itemType);
       item.put("count", Integer.valueOf(itemCount));
       items.add(item);
       int result = mailDao.setMailItems(mail.getId(), items);
       if (result == 1) { LOGGER.info("修改成功"); }
       else { LOGGER.error("出了点意外，为什么捏？"); }  }
     else if (args[0].equals("del"))
     { if (args.length == 1) {
         LOGGER.error("用法: /mail " + name + " items del [int]<物品下标>");
         return;
       } 
       int index = -1;
       try {
         index = Integer.parseInt(args[1]);
       } catch (Exception e) {
         LOGGER.error("解析下标时出错, 原文: " + args[2]);
         return;
       } 
       JSONArray items = JSONArray.parseArray(mail.getItems());
       if (index < 0 || index >= items.size()) {
         LOGGER.error("下标越界");
         return;
       } 
       items.remove(index);
       int result = mailDao.setMailItems(mail.getId(), items);
       if (result == 1) { LOGGER.info("修改成功"); }
       else { LOGGER.error("出了点意外，为什么捏？"); }
        }
     else { LOGGER.error("可用的二级子命令: list<查看物品列表> add<添加物品> del<删除物品>"); }
   
   }
   
   private void processSendCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
     if (mailList.size() != 1) {
       LOGGER.error("指定的邮件名不存在");
       return;
     } 
     if (args.length < 2) {
       LOGGER.error("用法: /mail " + name + " send [string]<玩家UID|*> [int]<过期时长(天)>");
       
       return;
     } 
     int expireTime = 0;
     try {
       expireTime = Integer.parseInt(args[1]);
     } catch (Exception e) {
       LOGGER.error("解析过期时长时出错, 原文: " + args[1]);
       return;
     } 
     Long createAt = Long.valueOf(ArknightsApplication.getTimestamp());
     Long expireAt = Long.valueOf(createAt.longValue() + 86400L * expireTime);
     
     Mail mail = mailList.get(0);
     
     JSONObject mailObject = new JSONObject(true);
     mailObject.put("mailId", Integer.valueOf(mail.getId()));
     mailObject.put("createAt", createAt);
     mailObject.put("expireAt", expireAt);
     mailObject.put("state", Integer.valueOf(0));
     mailObject.put("type", Integer.valueOf(1));
     mailObject.put("hasItem", Integer.valueOf(1));
     
     if (!args[0].equals("*")) {
 
       
       long UID = 0L;
       try {
         UID = Long.parseLong(args[0]);
       } catch (Exception e) {
         LOGGER.error("解析 UID 时出错, 原文: " + args[0]);
         return;
       } 
       List<Account> acounts = userDao.queryAccountByUid(UID);
       if (acounts.size() != 1) {
         LOGGER.error("无法找到该玩家");
         return;
       } 
       Account account = acounts.get(0);
       JSONArray mailBox = JSONArray.parseArray(account.getMails());
       if (!addMail(mailBox, mailObject)) {
         LOGGER.error("玩家已拥有此邮件");
         return;
       } 
       int result = userDao.setMailsData(Long.valueOf(account.getUid()), mailBox);
       if (result == 1) { LOGGER.info("修改成功"); }
       else { LOGGER.error("出了点意外，为什么捏？"); }
     
     } 
   }
   private static String joinToString(String[] args, String seperate) {
     if (args.length == 0) return ""; 
     StringBuilder builder = new StringBuilder(args[0]);
     for (int i = 1; i < args.length; ) { builder.append(seperate).append(args[i]); i++; }
      return builder.toString();
   }
   
   private static boolean addMail(JSONArray mailList, JSONObject mail) {
     for (Object object : mailList) {
       if (((JSONObject)object).getIntValue("mailId") == mail.getIntValue("mailId")) {
         return false;
       }
     } 
     mailList.add(mail);
     return true;
   }
 }