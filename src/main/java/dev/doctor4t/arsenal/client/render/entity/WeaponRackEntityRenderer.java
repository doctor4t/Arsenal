package dev.doctor4t.arsenal.client.render.entity;

import dev.doctor4t.arsenal.Arsenal;
import dev.doctor4t.arsenal.entity.WeaponRackEntity;
import dev.doctor4t.arsenal.index.ArsenalTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

@Environment(EnvType.CLIENT)
public class WeaponRackEntityRenderer<T extends WeaponRackEntity> extends EntityRenderer<T> {
    public static final ModelIdentifier MODEL = new ModelIdentifier(Arsenal.MOD_ID, "weapon_rack", "");
    private final ItemRenderer itemRenderer;
    private final BlockRenderManager blockRenderManager;

    public WeaponRackEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderManager = context.getBlockRenderManager();
    }

    protected int getBlockLight(T itemFrameEntity, BlockPos blockPos) {
        return itemFrameEntity.getType() == EntityType.GLOW_ITEM_FRAME
                ? Math.max(5, super.getBlockLight(itemFrameEntity, blockPos))
                : super.getBlockLight(itemFrameEntity, blockPos);
    }

    public void render(T itemFrameEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(itemFrameEntity, f, g, matrices, vertexConsumerProvider, i);
        matrices.push();
        Direction direction = itemFrameEntity.getHorizontalFacing();
        Vec3d vec3d = this.getPositionOffset(itemFrameEntity, g);
        matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        double d = 0.46875;
        matrices.translate((double) direction.getOffsetX() * d, (double) direction.getOffsetY() * d, (double) direction.getOffsetZ() * d);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(itemFrameEntity.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - itemFrameEntity.getYaw()));
        boolean bl = itemFrameEntity.isInvisible();
        ItemStack itemStack = itemFrameEntity.getHeldItemStack();

        int rotation = itemFrameEntity.getRotation();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rotation * 360.0F / 8.0F));

        if (!bl) {
            BakedModelManager bakedModelManager = this.blockRenderManager.getModels().getModelManager();
            matrices.push();
            matrices.translate(-0.5F, -0.5F, -0.5F);
            this.blockRenderManager
                    .getModelRenderer()
                    .render(
                            matrices.peek(),
                            vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntityCutout()),
                            null,
                            bakedModelManager.getModel(MODEL),
                            1.0F,
                            1.0F,
                            1.0F,
                            i,
                            OverlayTexture.DEFAULT_UV
                    );
            matrices.pop();
        }

        if (!itemStack.isEmpty()) {
            float zRot = 135f;
            float scale = .85f;
            if (itemStack.isIn(ArsenalTags.BIG_WEAPONS)) {
                scale = 1.6f;
            }
            if (itemStack.isIn(ArsenalTags.RANGED_WEAPONS)) {
                zRot = 45f;
            }
            if (itemStack.isIn(ArsenalTags.SHIELDS)) {
                scale = 1.8f;
                zRot = 0f;
            }
            if (itemStack.isIn(ArsenalTags.TRIDENTS)) {
                zRot = -45f;
            }

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(zRot));

            float offset = MathHelper.hashCode(itemFrameEntity.getBlockX(), itemFrameEntity.getBlockY(), itemFrameEntity.getBlockZ()) * 0.00000000000000001f; // offset to avoid z fighting
            if (bl) {
                matrices.translate(0.0F + offset, 0.0F + offset, 0.4375F + offset);
            } else {
                matrices.translate(0.0F + offset, 0.0F + offset, 0.3f + offset);
            }

            int light = this.getLight(itemFrameEntity, LightmapTextureManager.MAX_LIGHT_COORDINATE, i);

            matrices.scale(scale, scale, scale);

            this.itemRenderer
                    .renderItem(
                            itemStack,
                            ModelTransformationMode.FIXED,
                            light,
                            OverlayTexture.DEFAULT_UV,
                            matrices,
                            vertexConsumerProvider,
                            itemFrameEntity.getWorld(),
                            itemFrameEntity.getId()
                    );
        }

        matrices.pop();
    }

    private int getLight(T itemFrame, int glowLight, int regularLight) {
        return itemFrame.getType() == EntityType.GLOW_ITEM_FRAME ? glowLight : regularLight;
    }

    public Vec3d getPositionOffset(T itemFrameEntity, float f) {
        return new Vec3d(
                (float) itemFrameEntity.getHorizontalFacing().getOffsetX() * 0.3F,
                -0.25,
                (float) itemFrameEntity.getHorizontalFacing().getOffsetZ() * 0.3F
        );
    }

    public Identifier getTexture(T itemFrameEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    protected boolean hasLabel(T itemFrameEntity) {
        return false;
    }
}
