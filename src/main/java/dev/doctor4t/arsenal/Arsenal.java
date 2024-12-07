package dev.doctor4t.arsenal;

import dev.doctor4t.arsenal.cca.BackWeaponComponent;
import dev.doctor4t.arsenal.index.*;
import dev.upcraft.datasync.api.util.Entitlements;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.UUID;

public class Arsenal implements ModInitializer {
    public static final String MOD_ID = "arsenal";

    // Packet Identifiers
    public static final Identifier SERVERBOUND_HOLD_WEAPON_PACKET = id("hold_weapon");
    public static final Identifier SERVERBOUND_SWAP_WEAPON_PACKET = id("swap_weapon");
    public static final Identifier SERVERBOUND_SWAP_INVENTORY_PACKET = id("swap_inventory");

    public static final Identifier WEAPON_SKINS_DATA_ID = id("weapon_skins");

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ArsenalEntities.initialize();
        ArsenalItems.initialize();
        ArsenalEnchantments.initialize();
        ArsenalSounds.initialize();
        ArsenalParticles.initialize();

        ServerPlayNetworking.registerGlobalReceiver(SERVERBOUND_HOLD_WEAPON_PACKET, (server, player, handler, buf, responseSender) -> {
            boolean hold = buf.readBoolean();
            BackWeaponComponent.setHoldingBackWeapon(player, hold);
        });

        ServerPlayNetworking.registerGlobalReceiver(SERVERBOUND_SWAP_WEAPON_PACKET, (server, player, handler, buf, responseSender) -> {
            if (!player.isSpectator()) {
                boolean toggled = BackWeaponComponent.isHoldingBackWeapon(player);
                BackWeaponComponent.setHoldingBackWeapon(player, false);
                ItemStack itemStack = BackWeaponComponent.getBackWeapon(player);
                boolean success = BackWeaponComponent.setBackWeapon(player, player.getStackInHand(Hand.MAIN_HAND));
                if (success) {
                    player.setStackInHand(Hand.MAIN_HAND, itemStack);
                }
                player.clearActiveItem();
                BackWeaponComponent.setHoldingBackWeapon(player, toggled);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(SERVERBOUND_SWAP_INVENTORY_PACKET, (server, player, handler, buf, responseSender) -> {
            int slotId = buf.readInt();
            if (!player.isSpectator()) {
                if (!player.currentScreenHandler.isValid(slotId)) {
                    return;
                }
                Slot slot = player.currentScreenHandler.getSlot(slotId);
                ItemStack itemStack = BackWeaponComponent.getBackWeapon(player);
                boolean success = BackWeaponComponent.setBackWeapon(player, slot.getStack());
                if (success) {
                    slot.setStack(itemStack);
                }
            }
        });
    }

    public static boolean isSupporter(UUID uuid) {
        Optional<Entitlements> entitlements = Entitlements.token().get(uuid);
        return entitlements.map(value -> value.keys().stream().anyMatch(identifier -> identifier.equals(WEAPON_SKINS_DATA_ID))).orElse(false);
    }
}
