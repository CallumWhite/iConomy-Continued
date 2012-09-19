package com.iCo6.system.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AccountCreation extends Event
{
  private final String account;
  private boolean cancelled = false;

  private static final HandlerList handlers = new HandlerList();

  public AccountCreation(String account)
  {
    this.account = account;
  }

  public String getAccountName() {
    return this.account;
  }

  public boolean isCancelled() {
    return this.cancelled;
  }

  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public HandlerList getHandlers()
  {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}