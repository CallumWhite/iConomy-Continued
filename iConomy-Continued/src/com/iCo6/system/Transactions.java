package com.iCo6.system;

import com.iCo6.Constants.Nodes;
import com.iCo6.IO.Database;
import com.iCo6.IO.Database.Type;
import com.iCo6.IO.mini.Arguments;
import com.iCo6.IO.mini.Mini;
import com.iCo6.iConomy;
import com.iCo6.util.Common;

public class Transactions
{
  public static void insert(Transaction data)
  {
    if (!Constants.Nodes.Logging.getBoolean().booleanValue()) {
      return;
    }
    if (Common.matches(iConomy.Database.getType().toString(), new String[] { "inventorydb", "minidb", "orbdb" })) {
      Mini database = iConomy.Database.getTransactionDatabase();

      if (database == null) {
        return;
      }
      Arguments entry = new Arguments(Long.valueOf(data.time));
      entry.setValue("where", data.where);
      entry.setValue("from", data.from);
      entry.setValue("to", data.to);
      entry.setValue("from_balance", data.fromBalance);
      entry.setValue("to_balance", data.toBalance);
      entry.setValue("gain", data.gain);
      entry.setValue("loss", data.loss);
      entry.setValue("set", data.set);
      database.addIndex(entry);
      database.update();

      return;
    }
  }
}