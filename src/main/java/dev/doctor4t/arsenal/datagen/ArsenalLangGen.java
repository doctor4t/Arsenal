package dev.doctor4t.arsenal.datagen;

import dev.doctor4t.arsenal.index.ArsenalEntities;
import dev.doctor4t.arsenal.index.ArsenalItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ArsenalLangGen extends FabricLanguageProvider {

    protected ArsenalLangGen(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        builder.add(ArsenalItems.ANCHORBLADE, "Anchorblade");
        builder.add(ArsenalEntities.ANCHORBLADE, "Anchorblade");
        builder.add(ArsenalItems.SCYTHE, "Scythe");
        builder.add(ArsenalEntities.BLOOD_SCYTHE, "Blood Scythe");
        builder.add(ArsenalItems.WEAPON_RACK, "Weapon Rack");
        builder.add(ArsenalEntities.WEAPON_RACK, "Weapon Rack");
        builder.add("tooltip.supporter_only", "Cosmetics are reserved to Ko-Fi and YouTube members only.\nIf you want access to them (and other cool perks), consider supporting!");
        builder.add("tooltip.arsenal.anchorblade_lux", "Orginally designed by Diansu and forged by RAT to be gifted\nto Lux in a distant universe, this weapon has seen many worlds.\nAfter this Lux saw her own die, she travelled to another one\nto hand it to this new world's Lux. Since then, it has been\npassed on for generations, almost as a tradition,\none coral fan growing for every Lux who owned it.");
        builder.add("tooltip.arsenal.hidden", "Press [Sneak] to show lore");
        builder.add("enchantment.arsenal.spewing", "Spewing");
        builder.add("enchantment.arsenal.spewing.desc", "Using the Scythe will spew out a blood blade damaging you and transferring half the duration of your potion effects to any entity hit.");
        builder.add("enchantment.arsenal.reeling", "Reeling");
        builder.add("enchantment.arsenal.reeling.desc", "Using the Anchorblade will reel you in like a grapple or reel in the hit entity.");
        builder.add("arsenal.subtitles.item.scythe.hit", "Scythe hits");
        builder.add("arsenal.subtitles.item.scythe.spewing", "Scythe spews");
        builder.add("arsenal.subtitles.entity.blood_scythe.hit", "Blood Scythe lands");
        builder.add("arsenal.subtitles.item.anchorblade.hit", "Anchorblade hits");
        builder.add("arsenal.subtitles.item.anchorblade.throw", "Anchorblade throw");
        builder.add("arsenal.subtitles.entity.anchorblade.land", "Anchorblade lands");
        builder.add("category.arsenal", "Arsenal");
        builder.add("key.arsenal.select_weapon", "Select Weapon Slot");
        builder.add("key.arsenal.swap_weapon", "Swap with Weapon Slot");
        builder.add("death.attack.spewing", "%1$s bled out");
        builder.add("death.attack.spewing.player", "%1$s bled out whilst fighting %2$s");
        builder.add("death.attack.blood_scythe", "%1$s suffered bad blood");
        builder.add("death.attack.blood_scythe.player", "%1$s suffered bad blood whilst fighting %2$s");
        builder.add("death.attack.anchor", "%1$s got anchored");
        builder.add("death.attack.anchor.player", "%1$s got anchored by %2$s");
    }
}
