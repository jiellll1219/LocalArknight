package com.hypergryph.arknights.command;

import com.hypergryph.arknights.command.CommandBase;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommandSender;
import com.hypergryph.arknights.core.dao.userDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBan
      extends CommandBase {
   private static final Logger LOGGER = LogManager.getLogger();

   public String getCommandName() {
      return "ban";
   }

   public String getCommandUsage(ICommandSender sender) {
      return "[int]<玩家UID>";
   }

   public String getCommandDescription() {
      return "禁止某位玩家进入服务器";
   }

   public String getCommandExample() {
      return "/ban 1";
   }

   public String getCommandExampleUsage() {
      return "封禁UID为1的玩家";
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
         if (userDao.setBanStatus(uid, 1) != 1) {
            LOGGER.error("封禁失败");
            return;
         }
         LOGGER.info("已封禁该玩家");
         return;
      }
      LOGGER.error("使用方式: /" + getCommandName() + " " + getCommandUsage(sender));
   }
}