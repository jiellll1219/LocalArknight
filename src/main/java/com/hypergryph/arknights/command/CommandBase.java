package com.hypergryph.arknights.command;

import com.hypergryph.arknights.command.ICommand;
import java.util.Collections;
import java.util.List;

public abstract class CommandBase implements ICommand {
   public List<String> getCommandAliases() {
      return Collections.emptyList();
   }

   public int compareTo(ICommand p_compareTo_1_) {
      return getCommandName().compareTo(p_compareTo_1_.getCommandName());
   }
}