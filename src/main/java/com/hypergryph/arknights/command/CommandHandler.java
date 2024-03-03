package com.hypergryph.arknights.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommand;
import com.hypergryph.arknights.command.ICommandManager;
import com.hypergryph.arknights.command.ICommandSender;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandHandler implements ICommandManager {
   private static final Logger logger = LogManager.getLogger();
   private final Map<String, ICommand> commandMap = Maps.newHashMap();
   private final Set<ICommand> commandSet = Sets.newHashSet();

   public List<ICommand> getPossibleCommands(ICommandSender sender) {
      List<ICommand> list = Lists.newArrayList();

      for (ICommand icommand : this.commandSet) {
         list.add(icommand);
      }

      return list;
   }

   public ICommand registerCommand(ICommand command) {
      this.commandMap.put(command.getCommandName(), command);
      this.commandSet.add(command);

      for (String s : command.getCommandAliases()) {
         ICommand icommand = this.commandMap.get(s);

         if (icommand == null || !icommand.getCommandName().equals(s)) {
            this.commandMap.put(s, command);
         }
      }

      return command;
   }

   public int executeCommand(ICommandSender sender, String rawCommand) {
      rawCommand = rawCommand.trim();

      String[] astring = rawCommand.split(" ");
      String s = astring[0];

      ICommand icommand = this.commandMap.get(s);

      if (icommand == null) {
         logger.error("未知或不完整的命令 '" + s + "'");
         return 0;
      }

      try {
         icommand.processCommand(sender, astring);
      } catch (CommandException e) {
         e.printStackTrace();
      }
      return 0;
   }

   public Map<String, ICommand> getCommands() {
      return this.commandMap;
   }
}