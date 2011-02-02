//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

/**
 * 
 */
public interface ICommandHistory extends Iterable<Command> {
   public void push(Command command);
   public Command get(int index);
}
