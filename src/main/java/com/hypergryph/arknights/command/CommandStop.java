package com.hypergryph.arknights.command;

import com.hypergryph.arknights.command.CommandBase;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommandSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandStop extends CommandBase {
   private static final Logger LOGGER = LogManager.getLogger();

   public String getCommandName() {
      return "stop";
   }

   public String getCommandUsage(ICommandSender sender) {
      return "";
   }

   public String getCommandDescription() {
      return "关闭服务器";
   }

   public String getCommandExample() {
      return "/stop";
   }

   public String getCommandExampleUsage() {
      return "使用/stop停止服务器";
   }

   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      LOGGER.warn("正在关闭服务器...");
      System.exit(0);
   }
}