package com.gildedgames.aether.client.renderer.accessory;

import com.gildedgames.aether.client.registry.AetherModelLayers;
import com.gildedgames.aether.client.renderer.accessory.model.GlovesModel;
import com.gildedgames.aether.common.item.accessories.gloves.GlovesItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class GlovesRenderer implements ICurioRenderer
{
    private final GlovesModel glovesModel;
    private final GlovesModel glovesModelSlim;
    private final GlovesModel glovesArmModel;
    private final GlovesModel glovesArmModelSlim;
    private final GlovesModel glovesSleeveModel;
    private final GlovesModel glovesSleeveModelSlim;

    public GlovesRenderer() {
        this.glovesModel = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES));
        this.glovesModelSlim = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_SLIM));
        this.glovesArmModel = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_ARM));
        this.glovesArmModelSlim = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_ARM_SLIM));
        this.glovesSleeveModel = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_SLEEVE));
        this.glovesSleeveModelSlim = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_SLEEVE_SLIM));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        GlovesItem glovesItem = (GlovesItem) stack.getItem();
        GlovesModel model;
        VertexConsumer vertexBuilder;

        if (!(renderLayerParent.getModel() instanceof PlayerModel<?> playerModel)) {
            model = this.glovesModel;
            vertexBuilder = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(glovesItem.getGlovesTexture()), false, stack.isEnchanted());
        } else {
            model = playerModel.slim ? this.glovesModelSlim : this.glovesModel;
            vertexBuilder = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(playerModel.slim ? glovesItem.getGlovesSlimTexture() : glovesItem.getGlovesTexture()), false, stack.isEnchanted());
        }
        ICurioRenderer.followBodyRotations(slotContext.entity(), model);

        float red = glovesItem.getColors(stack).getLeft();
        float green = glovesItem.getColors(stack).getMiddle();
        float blue = glovesItem.getColors(stack).getRight();

        model.renderToBuffer(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }

    public void renderFirstPerson(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, AbstractClientPlayer player, HumanoidArm arm) {
        GlovesModel model;

        boolean isSlim = player.getModelName().equals("slim");
        boolean isSleeve = false; //TODO: toggle.
        if (!isSlim) {
            model = !isSleeve ? this.glovesArmModel : this.glovesSleeveModel;
        } else {
            model = !isSleeve ? this.glovesArmModelSlim : this.glovesSleeveModelSlim;
        }

        model.setAllVisible(false);
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        GlovesItem glovesItem = (GlovesItem) stack.getItem();
        VertexConsumer vertexBuilder = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(!isSlim ? glovesItem.getGlovesTexture() : glovesItem.getGlovesSlimTexture()), false, stack.isEnchanted());

        float red = glovesItem.getColors(stack).getLeft();
        float green = glovesItem.getColors(stack).getMiddle();
        float blue = glovesItem.getColors(stack).getRight();

        if (arm == HumanoidArm.RIGHT) {
            this.renderGlove(model.rightArm, poseStack, combinedLight, vertexBuilder, red, green, blue);
        } else if (arm == HumanoidArm.LEFT) {
            this.renderGlove(model.leftArm, poseStack, combinedLight, vertexBuilder, red, green, blue);
        }
    }

    private void renderGlove(ModelPart gloveArm, PoseStack poseStack, int combinedLight, VertexConsumer vertexBuilder, float red, float green, float blue) {
        gloveArm.visible = true;
        gloveArm.xRot = 0.0F;
        gloveArm.render(poseStack, vertexBuilder, combinedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }
}