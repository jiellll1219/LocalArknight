package com.hypergryph.arknights.command;
import com.hypergryph.arknights.command.ICommand;
import com.hypergryph.arknights.command.ICommandSender;
import java.util.List;
import java.util.Map;public interface ICommandManager {
  static int executeCommand(ICommandSender sender, String rawCommand) {
     return 0;
  }
  
  List<ICommand> getPossibleCommands(ICommandSender paramICommandSender);
  
  Map<String, ICommand> getCommands();
}