package com.hypergryph.arknights.command;

import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommand;
import com.hypergryph.arknights.command.ICommandSender;
import java.util.List;

public interface ICommand extends Comparable<ICommand> {
  String getCommandName();

  String getCommandUsage(ICommandSender paramICommandSender);

  String getCommandDescription();

  String getCommandExample();

  String getCommandExampleUsage();

  List<String> getCommandAliases();

  void processCommand(ICommandSender paramICommandSender, String[] paramArrayOfString) throws CommandException;
}