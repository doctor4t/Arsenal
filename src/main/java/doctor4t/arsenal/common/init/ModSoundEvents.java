package doctor4t.arsenal.common.init;

import doctor4t.arsenal.common.Arsenal;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModSoundEvents {
	Map<SoundEvent, Identifier> SOUND_EVENTS = new LinkedHashMap<>();

	SoundEvent ITEM_SCYTHE_HIT = createSoundEvent("item.scythe.hit");
	SoundEvent ITEM_SCYTHE_SPEWING = createSoundEvent("item.clown_scythe.spewing");
	SoundEvent ENTITY_BLOOD_SCYTHE_HIT = createSoundEvent("entity.blood_scythe.hit");
	SoundEvent ITEM_ANCHORBLADE_HIT = createSoundEvent("item.anchorblade.hit");
	SoundEvent ITEM_ANCHORBLADE_THROW = createSoundEvent("item.anchorblade.throw");
	SoundEvent ENTITY_ANCHORBLADE_LAND = createSoundEvent("entity.anchorblade.land");


	static void initialize() {
		SOUND_EVENTS.keySet().forEach(soundEvent -> Registry.register(Registry.SOUND_EVENT, SOUND_EVENTS.get(soundEvent), soundEvent));
	}

	private static SoundEvent createSoundEvent(String path) {
		SoundEvent soundEvent = new SoundEvent(new Identifier(Arsenal.MOD_ID, path));
		SOUND_EVENTS.put(soundEvent, new Identifier(Arsenal.MOD_ID, path));
		return soundEvent;
	}
}
