package com.netstal.tools.nubs.launcher.domain;

public interface ICommandHistory extends Iterable<Command> {
   public void push(Command command);
}
