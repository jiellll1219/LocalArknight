package com.hypergryph.arknights;

import com.hypergryph.arknights.ArknightsApplication;
import net.minecrell.terminalconsole.SimpleTerminalConsole;

public class console
    extends SimpleTerminalConsole {
  protected boolean isRunning() {
    return true;
  }

  protected void runCommand(String s) {
    ArknightsApplication.ConsoleCommandManager.executeCommand(ArknightsApplication.Sender, s);
  }

  protected void shutdown() {
  }
}