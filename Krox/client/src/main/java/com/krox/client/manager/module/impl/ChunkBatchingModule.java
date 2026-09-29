package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class ChunkBatchingModule extends Module {
   public ChunkBatchingModule() {
      super("chunk_batching", "Chunk Batching", "Reduces render state changes by batching chunks.", ModuleCategory.PERFORMANCE);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new NumberSetting(this, "batchSize", "Batch Size", 16, 4, 64, 1, "Batch Size"));
      this.register(new BooleanSetting(this, "sortByMaterial", "Sort By Material", true, "Sort By Material"));
   }
   // TODO(D-42): chunk section batching is decided by ChunkBuilder's
   // section sorter during the build; a module tick runs after the fact.
}
