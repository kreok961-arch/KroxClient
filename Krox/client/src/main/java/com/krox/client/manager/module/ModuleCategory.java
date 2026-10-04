package com.krox.client.manager.module;

public enum ModuleCategory {
   HUD("HUD"),
   COMBAT("Combat"),
   RENDER("Visuals"),
   PLAYER("Player"),
   WORLD("QoL"),
   PERFORMANCE("Performance"),
   MOBILE("Mobile"),
   ACCOUNT("Account"),
   MISC("Misc"),
   COSMETICS("Cosmetics"),
   WAYPOINTS("Waypoints"),
   CLIENT("Client");

   private final String displayName;

   private ModuleCategory(String displayName) {
      this.displayName = displayName;
   }

   public String getDisplayName() {
      return this.displayName;
   }
}
