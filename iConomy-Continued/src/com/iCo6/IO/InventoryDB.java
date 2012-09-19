package com.iCo6.IO;

import com.iCo6.Constants.Nodes;
import com.iCo6.iConomy;
import com.iCo6.util.nbt.ByteTag;
import com.iCo6.util.nbt.CompoundTag;
import com.iCo6.util.nbt.ListTag;
import com.iCo6.util.nbt.NBTInputStream;
import com.iCo6.util.nbt.NBTOutputStream;
import com.iCo6.util.nbt.ShortTag;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryDB
{
  private File dataDir;

  public InventoryDB()
  {
    this.dataDir = new File(((World)iConomy.Server.getWorlds().get(0)).getName(), "players");
  }

  public List<String> getAllPlayers() {
    ArrayList result = new ArrayList();

    for (String file : this.dataDir.list()) {
      if (file.endsWith(".dat"))
        result.add(file.substring(0, file.length() - 4));
    }
    return result;
  }

  public boolean dataExists(String name) {
    return new File(this.dataDir, name + ".dat").exists();
  }

  public void setBalance(String name, double balance) {
    if ((iConomy.Server.getPlayer(name) != null) && (iConomy.Server.getPlayer(name).isOnline()))
    {
      ItemStack[] stacks = (ItemStack[])iConomy.Server.getPlayer(name).getInventory().getContents().clone();
      setBalance(stacks, balance);

      iConomy.Server.getPlayer(name).getInventory().setContents(stacks);
      iConomy.Server.getPlayer(name).updateInventory();
      return;
    }

    if (!dataExists(name)) {
      return;
    }
    ItemStack[] stacks = readInventory(name);
    if (stacks != null) {
      setBalance(stacks, balance);
      writeInventory(name, stacks);
    }
  }

  public double getBalance(String name)
  {
    if ((iConomy.Server.getPlayer(name) != null) && (iConomy.Server.getPlayer(name).isOnline())) {
      return getBalance(iConomy.Server.getPlayer(name).getInventory().getContents());
    }
    ItemStack[] stacks = readInventory(name);
    if (stacks != null) {
      return getBalance(stacks);
    }
    return Constants.Nodes.Balance.getDouble().doubleValue();
  }

  private ItemStack[] readInventory(String name)
  {
    try
    {
      NBTInputStream in = new NBTInputStream(new FileInputStream(new File(this.dataDir, name + ".dat")));
      CompoundTag tag = (CompoundTag)in.readTag();
      in.close();

      ListTag inventory = (ListTag)tag.getValue().get("Inventory");

      ItemStack[] stacks = new ItemStack[40];
      for (int i = 0; i < inventory.getValue().size(); i++) {
        CompoundTag item = (CompoundTag)inventory.getValue().get(i);
        byte count = ((ByteTag)item.getValue().get("Count")).getValue().byteValue();
        byte slot = ((ByteTag)item.getValue().get("Slot")).getValue().byteValue();
        short damage = ((ShortTag)item.getValue().get("Damage")).getValue().shortValue();
        short id = ((ShortTag)item.getValue().get("id")).getValue().shortValue();

        stacks[slot] = new ItemStack(id, count, damage);
      }
      return stacks;
    } catch (IOException ex) {
      iConomy.Server.getLogger().log(Level.WARNING, "[iCo/InvDB] error reading inventory {0}: {1}", new Object[] { name, ex.getMessage() });
    }return null;
  }

  private void writeInventory(String name, ItemStack[] stacks)
  {
    try {
      NBTInputStream in = new NBTInputStream(new FileInputStream(new File(this.dataDir, name + ".dat")));
      CompoundTag tag = (CompoundTag)in.readTag();
      in.close();

      ArrayList tagList = new ArrayList();

      for (int i = 0; i < stacks.length; i++) {
        if (stacks[i] == null)
          continue;
        ByteTag count = new ByteTag("Count", (byte)stacks[i].getAmount());
        ByteTag slot = new ByteTag("Slot", (byte)i);
        ShortTag damage = new ShortTag("Damage", stacks[i].getDurability());
        ShortTag id = new ShortTag("id", (short)stacks[i].getTypeId());

        HashMap tagMap = new HashMap();
        tagMap.put("Count", count);
        tagMap.put("Slot", slot);
        tagMap.put("Damage", damage);
        tagMap.put("id", id);

        tagList.add(new CompoundTag("", tagMap));
      }

      ListTag inventory = new ListTag("Inventory", CompoundTag.class, tagList);

      HashMap tagCompound = new HashMap(tag.getValue());
      tagCompound.put("Inventory", inventory);
      tag = new CompoundTag("Player", tagCompound);

      NBTOutputStream out = new NBTOutputStream(new FileOutputStream(new File(this.dataDir, name + ".dat")));
      out.writeTag(tag);
      out.close();
    } catch (IOException ex) {
      iConomy.Server.getLogger().log(Level.WARNING, "[iCo/InvDB] error writing inventory {0}: {1}", new Object[] { name, ex.getMessage() });
    }
  }

  private void setBalance(ItemStack[] contents, double balance) {
    int major = Constants.Nodes.DatabaseMajorItem.getInteger().intValue();
    int minor = Constants.Nodes.DatabaseMinorItem.getInteger().intValue();

    for (int i = 0; i < contents.length; i++) {
      ItemStack item = contents[i];

      if ((item == null) || (
        (item.getTypeId() != major) && (item.getTypeId() != minor))) continue;
      contents[i] = null;
    }

    for (int i = 0; i < contents.length; i++) {
      if (contents[i] == null) {
        if (balance >= 1.0D) {
          int add = (int)balance;

          if (add > Material.getMaterial(major).getMaxStackSize()) {
            add = Material.getMaterial(major).getMaxStackSize();
          }
          contents[i] = new ItemStack(major, add);
          balance -= add;
        } else if (balance >= 0.01D) {
          int add = (int)(roundTwoDecimals(balance) * 100.0D);

          if (add > Material.getMaterial(minor).getMaxStackSize()) {
            add = Material.getMaterial(minor).getMaxStackSize();
          }
          contents[i] = new ItemStack(minor, add);
          balance -= 0.01D * add;
        } else {
          balance = 0.0D;
          break;
        }
      }

    }

    if (balance > 0.0D)
      throw new RuntimeException("Unable to set balance, inventory is overfull");
  }

  private double getBalance(ItemStack[] contents)
  {
    double balance = 0.0D;
    int major = Constants.Nodes.DatabaseMajorItem.getInteger().intValue();
    int minor = Constants.Nodes.DatabaseMinorItem.getInteger().intValue();

    for (ItemStack item : contents) {
      if (item != null)
        if (item.getTypeId() == major)
          balance += item.getAmount();
        else if (item.getTypeId() == minor)
          balance += 0.01D * item.getAmount();
    }
    return balance;
  }

  private double roundTwoDecimals(double d) {
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    return Double.valueOf(twoDForm.format(d)).doubleValue();
  }
}