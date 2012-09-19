package com.iCo6.system;

import java.util.Comparator;

class MoneyComparator
  implements Comparator<Account>
{
  public int compare(Account a, Account b)
  {
    return (int)(b.getHoldings().getBalance().doubleValue() - a.getHoldings().getBalance().doubleValue());
  }
}