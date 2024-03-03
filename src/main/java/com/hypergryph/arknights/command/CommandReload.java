package com.hypergryph.arknights.command;

import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.command.CommandBase;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommandSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandReload
      extends CommandBase {
   private static final Logger LOGGER = LogManager.getLogger();

   public String getCommandName() {
      return "reload";
   }

   public String getCommandUsage(ICommandSender sender) {
      return "";
   }

   public String getCommandDescription() {
      return "重载配置文件";
   }

   public String getCommandExample() {
      return "/reload";
   }

   public String getCommandExampleUsage() {
      return "使用/reload重新载入配置文件";
   }

   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length >= 1) {
         ArknightsApplication.reloadServerConfig();

         return;
      }
      LOGGER.error("§6使用方式: §f/" + getCommandName() + " " + getCommandUsage(sender));
   }
}