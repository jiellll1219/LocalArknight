package com.hypergryph.arknights.command;public class CommandManager extends CommandHandler {
  public CommandManager() {
     registerCommand((ICommand)new CommandHelp());
     registerCommand((ICommand)new CommandReload());
     registerCommand((ICommand)new CommandBan());
     registerCommand((ICommand)new CommandUnBan());
     registerCommand((ICommand)new CommandStop());
     registerCommand((ICommand)new CommandGive());
     registerCommand((ICommand)new CommandMail());
     registerCommand((ICommand)new CommandUpgrade());
     registerCommand((ICommand)new CommandUnLock());
     registerCommand((ICommand)new CommandActivity());
  }
}