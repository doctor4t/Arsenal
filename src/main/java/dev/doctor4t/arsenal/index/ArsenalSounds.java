package dev.doctor4t.arsenal.index;

import dev.doctor4t.arsenal.Arsenal;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ArsenalSounds {
    Map<SoundEvent, Identifier> SOUND_EVENTS = new LinkedHashMap<>();

    SoundEvent ITEM_SCYTHE_HIT = createSoundEvent("item.scythe.hit");
    SoundEvent ITEM_SCYTHE_SPEWING = createSoundEvent("item.scythe.spewing");
    SoundEvent ENTITY_BLOOD_SCYTHE_HIT = createSoundEvent("entity.blood_scythe.hit");
    SoundEvent ITEM_ANCHORBLADE_HIT = createSoundEvent("item.anchorblade.hit");
    SoundEvent ITEM_ANCHORBLADE_THROW = createSoundEvent("item.anchorblade.throw");
    SoundEvent ENTITY_ANCHORBLADE_LAND = createSoundEvent("entity.anchorblade.land");

    static void initialize() {
        SOUND_EVENTS.keySet().forEach(soundEvent -> Registry.register(Registries.SOUND_EVENT, SOUND_EVENTS.get(soundEvent), soundEvent));
    }

    private static SoundEvent createSoundEvent(String path) {
        SoundEvent soundEvent = SoundEvent.of(new Identifier(Arsenal.MOD_ID, path));
        SOUND_EVENTS.put(soundEvent, new Identifier(Arsenal.MOD_ID, path));
        return soundEvent;
    }
}
