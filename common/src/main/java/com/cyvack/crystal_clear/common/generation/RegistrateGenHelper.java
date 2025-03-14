package com.cyvack.crystal_clear.common.generation;

import com.cyvack.crystal_clear.RegistrationGenPlatform;
import com.cyvack.crystal_clear.common.CrystalClear;
import com.cyvack.crystal_clear.common.content.blocks.GlassCasing;
import com.cyvack.crystal_clear.common.content.blocks.GlassEncasedCogwheel;
import com.cyvack.crystal_clear.common.content.blocks.GlassEncasedShaft;
import com.cyvack.crystal_clear.common.creative_tab.ItemVisibilityHandler;
import com.cyvack.crystal_clear.common.glass_ct_behaviours.GlassEncasedCTBehaviour;
import com.cyvack.crystal_clear.common.glass_ct_behaviours.GlassEncasedCogCTBehaviour;
import com.cyvack.crystal_clear.common.registry.CCCBlocks;
import com.cyvack.crystal_clear.common.registry.CCSpriteShifts;
import com.cyvack.crystal_clear.common.util.CasingHolder;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.MetalScaffoldingBlock;
import com.simibubi.create.content.decoration.MetalScaffoldingCTBehaviour;
import com.simibubi.create.content.decoration.palettes.AllPaletteBlocks;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.createmod.catnip.data.Couple;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class RegistrateGenHelper {

    //builders//
    public static <T extends AbstractRegistrate<?>> BlockEntry<GlassCasing> glassCasing(T reg, CasingHolder holder, boolean clear) {

        String name = holder.name();
        String newName = !clear ? name + "_glass_casing" : name + "_clear_glass_casing";
        CTSpriteShiftEntry ctEntry = CCSpriteShifts.GLASS_CASING_SHIFTS.get(name).get(clear);

        return reg.block(newName, GlassCasing::new)
                .initialProperties(() -> Blocks.GLASS)
                .properties(p -> p.sound(SoundType.GLASS))
                .properties(BlockBehaviour.Properties::noOcclusion)
                .properties(RegistrateGenHelper::glassProperties)
                .addLayer(() -> RenderType::cutout)
                .blockstate((c, p) -> p.simpleBlock(c.get()))
                .onRegister(connectedTextures(() -> new SimpleCTBehaviour(ctEntry)))
                .tag(AllTags.AllBlockTags.CASING.tag)
                .recipe((c, p) ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, c.get())
                                .requires(clear ? CCCBlocks.GLASS_CASINGS.getCasing(holder.name()) /*iffy, but works TODO: change this*/ : holder.casing().get())
                                .requires(AllPaletteBlocks.FRAMED_GLASS)
                                .unlockedBy("has_valve", RegistrateRecipeProvider.has(AllTags.AllItemTags.CASING.tag))
                                .save(p, CrystalClear.path("crafting/glass_casing/" + c.getName())))
                .item()
                .tag(AllTags.AllItemTags.CASING.tag)
                .build()
                .register();
    }

    public static <T extends AbstractRegistrate<T>> BlockEntry<MetalScaffoldingBlock> glassScaffolding(T reg, String modId, String casing, boolean clear, CTSpriteShiftEntry side, CTSpriteShiftEntry innerSide, Supplier<ItemLike> scaffolding, NonNullFunction<BlockBehaviour.Properties, MetalScaffoldingBlock> factory) {
        String name = casing.concat(clear ? "_clear" : "").concat("_glass_scaffolding");
        CTSpriteShiftEntry mainShift = CCSpriteShifts.GLASS_CASING_SHIFTS.get(casing).get(clear);

        return reg.block(name, factory)
                .initialProperties(() -> Blocks.SCAFFOLDING)
                .properties(p -> p.sound(SoundType.COPPER))
                .addLayer(() -> RenderType::cutout)
                .onRegister(connectedTextures(() -> new MetalScaffoldingCTBehaviour(side, innerSide, mainShift)))
                .transform(pickaxeOnly())
                .tag(BlockTags.CLIMBABLE)
                .recipe((c, p) ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, c.get())
                                .requires(scaffolding.get())
                                .requires(AllPaletteBlocks.FRAMED_GLASS)
                                .unlockedBy("has_valve", RegistrateRecipeProvider.has(AllTags.AllItemTags.CASING.tag))
                                .save(p, CrystalClear.path("crafting/glass_scaffolding/" + c.getName())))
                .transform(RegistrationGenPlatform.glassScaffolding(modId, casing, clear, getGlassScaffoldingHolder(modId, casing, clear)))
                .register();
    }

    private static IDArrayHolder<ResourceLocation> getGlassScaffoldingHolder(String modId, String casing, boolean clear) {
        IDArrayHolder<ResourceLocation> holder = new IDArrayHolder<>(new HashMap<>());

        holder.add("main", new ResourceLocation(modId, "block/scaffold/block"));
        holder.add("casing", new ResourceLocation(modId, "block/".concat(casing).concat(clear ? "_clear" : "").concat("_glass_casing")));

        String scaffoldingFolder = "block/scaffold/";
        if (casing.contentEquals("andesite") || casing.contentEquals("brass") || casing.contentEquals("copper")) {
            holder.add("top", Create.asResource("block/funnel/" + casing + "_funnel_frame"));
            holder.add("inside", Create.asResource(scaffoldingFolder + casing + "_scaffold_inside"));
            holder.add("side", Create.asResource(scaffoldingFolder + casing + "_scaffold"));
            holder.add("particle", Create.asResource(scaffoldingFolder + casing + "_scaffold"));
        } else {
            holder.add("top", new ResourceLocation(modId, scaffoldingFolder + casing + "_frame"));
            holder.add("inside", new ResourceLocation(modId, scaffoldingFolder + casing + "_scaffold_inside"));
            holder.add("side", new ResourceLocation(modId, scaffoldingFolder + casing + "_scaffold"));
            holder.add("particle", new ResourceLocation(modId, scaffoldingFolder + casing + "_scaffold"));
        }

        return holder;
    }

    public static <T extends AbstractRegistrate<T>> BlockEntry<GlassEncasedShaft> glassEncasedShaft(T reg, String modId, String casing, Boolean clear, NonNullFunction<BlockBehaviour.Properties, GlassEncasedShaft> factory) {
        String newName = casing + (!clear ? "_glass_encased_shaft" : "_clear_glass_encased_shaft");
        CTSpriteShiftEntry ctEntry = CCSpriteShifts.GLASS_CASING_SHIFTS.get(casing).get(clear);

        return reg.block(newName, factory)
                .properties(BlockBehaviour.Properties::noOcclusion)
                .properties(RegistrateGenHelper::glassProperties)
                //.transform(CStress.setNoImpact())
                .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0f))
                .loot((p, lb) -> p.dropOther(lb, AllBlocks.SHAFT))
                .addLayer(() -> RenderType::cutout)
                .onRegister(CreateRegistrate.connectedTextures(() -> new GlassEncasedCTBehaviour(ctEntry)))
                .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, ctEntry,
                        (state, face) -> true /*state.getBlock() instanceof GlassEncasedShaft && face.getAxis() != state.getValue(GlassEncasedShaft.AXIS)*/)))
                .initialProperties(() -> Blocks.GLASS)
                .transform(pickaxeOnly())
                .transform(RegistrationGenPlatform.glassEncasedshaft(modId, casing, clear))
                .transform(RegistrationGenPlatform.addVariant(Create.asResource("shaft"), AllBlocks.SHAFT))
                .transform(ItemVisibilityHandler.makeInvisible())
                .register();
    }

    //Glass Encased Cogwheels
    //Entry
    public static <T extends AbstractRegistrate<T>> BlockEntry<GlassEncasedCogwheel> glassEncasedCogwheel(T reg, String modId, String casingType, Boolean large, Boolean clear, NonNullFunction<BlockBehaviour.Properties, GlassEncasedCogwheel> factory) {
        CTSpriteShiftEntry mainShift = CCSpriteShifts.GLASS_CASING_SHIFTS.get(casingType).get(clear);
        String name = clear ? casingType + "_clear" : casingType;

        return !large ?
                //small cog
                reg.block(name + "_glass_encased_cogwheel", factory)
                        .loot((p, lb) -> p.dropOther(lb, AllBlocks.COGWHEEL))
                        .transform(glassEncasedSmallCogwheel(modId, casingType, clear, () -> mainShift))
                        .onRegister(CreateRegistrate.connectedTextures(() -> getCoupledCTBehaviour(mainShift, casingType)))
                        .register() :
                //Large Cog
                reg.block(name + "_glass_encased_large_cogwheel", factory)
                        .loot((p, lb) -> p.dropOther(lb, AllBlocks.LARGE_COGWHEEL))
                        .transform(glassEncasedLargeCogwheel(modId, casingType, clear,
                                () -> mainShift))
                        .register();
    }

    @NotNull
    private static GlassEncasedCogCTBehaviour getCoupledCTBehaviour(CTSpriteShiftEntry mainShift, String casingType) {
        CTSpriteShiftEntry side;
        CTSpriteShiftEntry otherSide;

        side = CCSpriteShifts.vertical("encased_cogwheels/" + casingType + "_encased_cogwheel_side");
        otherSide = CCSpriteShifts.horizontal("encased_cogwheels/" + casingType + "_encased_cogwheel_side");

        return new GlassEncasedCogCTBehaviour(mainShift, Couple.create(side, otherSide));
    }

    //Builders
    private static <B extends GlassEncasedCogwheel, P> NonNullUnaryOperator<BlockBuilder<B, P>> glassEncasedSmallCogwheel(
            String modid, String casing, Boolean clear, Supplier<CTSpriteShiftEntry> casingShift) {
        return b -> glassEncasedCogwheelBase(b, modid, casing, clear, casingShift, false);
    }

    private static <B extends GlassEncasedCogwheel, P> NonNullUnaryOperator<BlockBuilder<B, P>> glassEncasedLargeCogwheel(
            String modid, String casing, Boolean clear, Supplier<CTSpriteShiftEntry> casingShift) {
        return b -> glassEncasedCogwheelBase(b, modid, casing, clear, casingShift, true)
                .onRegister(CreateRegistrate.connectedTextures(() -> new GlassEncasedCogCTBehaviour(casingShift.get())));
    }

    //----------------------------//

    //Glass Encased Cogwheel Base
    private static <B extends GlassEncasedCogwheel, P> BlockBuilder<B, P> glassEncasedCogwheelBase(BlockBuilder<B, P> b, String modId,
                                                                                                   String casing, Boolean clear, Supplier<CTSpriteShiftEntry> casingShift, boolean large) {
        String name = clear ? casing + "_clear_glass" : casing + "_glass";
        String blockFolder = "encased_cogwheel";

        BlockEntry<CogWheelBlock> encasable = large ? AllBlocks.LARGE_COGWHEEL : AllBlocks.COGWHEEL;
        ResourceLocation loc = Create.asResource(large ? "large_cogwheel" : "cogwheel");

        return b.properties(BlockBehaviour.Properties::noOcclusion)
                .properties(RegistrateGenHelper::glassProperties)
                //.transform(CStress.setNoImpact())
                .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 0f))
                .addLayer(() -> RenderType::cutout)
                .initialProperties(() -> Blocks.GLASS)
                .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, casingShift.get(),
                        (state, f) -> state.getBlock() instanceof GlassEncasedCogwheel && f.getAxis() == state.getValue(GlassEncasedCogwheel.AXIS)
                                && !state.getValue(f.getAxisDirection() == Direction.AxisDirection.POSITIVE ? GlassEncasedCogwheel.TOP_SHAFT
                                : GlassEncasedCogwheel.BOTTOM_SHAFT))))
                .transform(RegistrationGenPlatform.glassEncasedCog(modId, casing, large, name, blockFolder))
                .transform(RegistrationGenPlatform.addVariant(loc, encasable))
                .transform(ItemVisibilityHandler.makeInvisible());
    }

    public static ResourceLocation getBacking(String modid, String casing) {
        ResourceLocation texture;

        if (casing.equals("andesite"))
            texture = new ResourceLocation("block/stripped_spruce_log_top");
        else if (casing.equals("brass"))
            texture = new ResourceLocation("block/stripped_dark_oak_log_top");
        else
            texture = new ResourceLocation(modid, "block/" + casing + "_backing");

        return texture;
    }

    public static ResourceLocation getSiding(String modid, String casing, boolean large) {
        ResourceLocation texture;

        texture = new ResourceLocation(modid, "block/encased_cogwheels/" + (large ? "large_" : "") + casing + "_encased_cogwheel_side");

        return texture;
    }

    //util
    public static ResourceLocation getOpening(String modid, String casing) {
        ResourceLocation texture;

        if (casing.equals("andesite"))
            texture = Create.asResource("block/gearbox");
        else if (casing.equals("brass"))
            texture = Create.asResource("block/brass_gearbox");
        else
            texture = new ResourceLocation(modid, "block/" + casing + "_gearbox");

        return texture;
    }

    private static BlockBehaviour.Properties glassProperties(BlockBehaviour.Properties p) {
        return p.isValidSpawn(($, $$, $$$, $$$$) -> false)
                .isRedstoneConductor(($, $$, $$$) -> false)
                .isSuffocating(($, $$, $$$) -> false)
                .isViewBlocking(($, $$, $$$) -> false);
    }

    public record IDArrayHolder<T>(Map<String, T> backing) {
        public T get(String key) {
            return backing.get(key);
        }

        public IDArrayHolder<T> add(String key, T val) {
            backing.put(key, val);
            return this;
        }
    }
}
