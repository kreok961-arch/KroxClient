package com.krox.client.manager.module;

import com.krox.client.KroxClient;
import com.krox.client.manager.config.ConfigManager;

public abstract class Setting {
   protected final String id;
   protected final String name;
   protected final String description;
   protected final transient Module owner;

   protected Setting(Module owner, String id, String name, String description) {
      this.owner = owner;
      this.id = id;
      this.name = name;
      this.description = description;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public Module getOwner() {
      return this.owner;
   }

   public abstract void save();

   public abstract void load();

   protected ConfigManager cfg() {
      // Null before KroxClient is constructed, e.g. a self-check running outside the
      // game. ConfigManager is pure in-memory state once reached, so falling back to
      // a bare one is safe; the alternative is an NPE out of every load()/save().
      if (KroxClient.get() == null) {
         return new ConfigManager();
      }

      return KroxClient.get().getClientManager().config();
   }

   protected String cfgKey() {
      return "modules." + this.owner.getId() + "." + this.id;
   }
}
