package com.cyvack.crystal_clear.common.registry;

import com.cyvack.crystal_clear.common.CCCRegistrate;
import com.cyvack.crystal_clear.common.CrystalClear;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CCCBlockEntities {
    public static CCCRegistrate REGISTRATE = CrystalClear.getRegistrate();

    public static final BlockEntityEntry<KineticBlockEntity> GLASS_ENCASED_SHAFT = REGISTRATE
            .blockEntity("glass_encased_shaft", KineticBlockEntity::new)
            .visual(() -> ShaftVisual::new, false)
            .validBlocks(CCCBlocks.GLASS_ENCASED_SHAFTS.toArray())
            .validBlocks(CCCBlocks.CLEAR_GLASS_ENCASED_SHAFTS.toArray())
            .renderer(()-> ShaftRenderer::new)
            .register();

    //Glass Encased Small Cogs
    public static final BlockEntityEntry<SimpleKineticBlockEntity> GLASS_ENCASED_COG = REGISTRATE
            .blockEntity("glass_encased_cog", SimpleKineticBlockEntity::new)
            .visual(()-> EncasedCogVisual::small, false)
            .validBlocks(CCCBlocks.SMALL_GLASS_ENCASED_COGWHEELS.toArray())
            .validBlocks(CCCBlocks.SMALL_CLEAR_GLASS_ENCASED_COGWHEELS.toArray())
            .renderer(()-> EncasedCogRenderer::small)
            .register();

    //Glass Encased Large Cogs
    public static final BlockEntityEntry<SimpleKineticBlockEntity> GLASS_ENCASED_LARGE_COG = REGISTRATE
            .blockEntity("glass_encased_large_cog", SimpleKineticBlockEntity::new)
            .visual(()-> EncasedCogVisual::large, false)
            .validBlocks(CCCBlocks.LARGE_GLASS_ENCASED_COGWHEELS.toArray())
            .validBlocks(CCCBlocks.LARGE_CLEAR_GLASS_ENCASED_COGWHEELS.toArray())
            .renderer(()-> EncasedCogRenderer::large)
            .register();

    public static void register() {

    }
}
