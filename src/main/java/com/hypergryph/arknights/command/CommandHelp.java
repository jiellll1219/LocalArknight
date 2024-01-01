package com.hypergryph.arknights.command;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.command.CommandBase;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommand;
import com.hypergryph.arknights.command.ICommandSender;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;public class CommandHelp extends CommandBase {
   private static final Logger LOGGER = LogManager.getLogger();  
  public String getCommandName() {
     return "help";
  }  
  public String getCommandUsage(ICommandSender sender) {
     return "[string]<命令>";
  }  
  public String getCommandDescription() {
     return "打开帮助菜单";
  }  
  public String getCommandExample() {
     return "/help help";
  }  
  public String getCommandExampleUsage() {
     return "查看命令 help 的帮助";
  }  
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
     if (args.length == 1) {
      
       LOGGER.info("§e------------------- §f帮助菜单 §e-------------------");
       Map<String, ICommand> map1 = getCommands();
      
       for (Map.Entry<String, ICommand> entry : map1.entrySet()) {
         ICommand iCommand = map1.get(entry.getKey());
         LOGGER.info("§6/" + iCommand.getCommandName() + " §f" + iCommand.getCommandUsage(sender));
      } 
      
      return;
    } 
     Map<String, ICommand> map = getCommands();
     ICommand icommand = map.get(args[1]);
    
     if (icommand == null) {
       LOGGER.error("未知或不完整的命令 '" + args[1] + "'");
      return;
    } 
     LOGGER.info("§e------------------- §f命令帮助 §e-------------------");
     LOGGER.info("§6描述: §f" + icommand.getCommandDescription());
     LOGGER.info("§6使用方式: §f/" + icommand.getCommandName() + " " + icommand.getCommandUsage(sender));
     LOGGER.info("§6例子: §f" + icommand.getCommandExample());
     LOGGER.info("§6说明: §f" + icommand.getCommandExampleUsage());
  }
  
  protected Map<String, ICommand> getCommands() {
     return ArknightsApplication.ConsoleCommandManager.getCommands();
  }
}