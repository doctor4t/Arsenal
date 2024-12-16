------------------------------------------------------
Arsenal 0.1.5 - 1.20.1
------------------------------------------------------
- Fixed Vindicators from raids never spawning with a Scythe
- Fixed inability to reel back in the anchor when anything is in your off-hand

------------------------------------------------------
Arsenal 0.1.4 - 1.20.1
------------------------------------------------------
- Changed the Anchorblade knockback logic
  - Now adds velocity instead of setting it
  - Makes pogo-jumping harder yet still possible
  - Makes using two Anchorblades in a short time (e.g. when dual wielding them) actually knock you further
- Reduced the Anchorblade knockback strength
  - Shockwave strength divided by 2
  - Direct hit strength divided by 3
  - This is to make the Anchorblade a little less obnoxious and strong in PvP, but if you liked its previous kick, don't fret, just keep an eye out
- Fixed Tridents duplicating when thrown from the back slot
- Fixed the Blood Scythe bypassing invulnerability ticks when used with Enchancement
- Fixed the back slot display widget being darker than the off hand display widget

------------------------------------------------------
Arsenal 0.1.3 - 1.20.1
------------------------------------------------------
- Fixed weapon rendering crashing with Iris shaders
- Made the Anchorblade dual-wieldable
  - Using one Anchorblade in each hand no longer instantly recalls both of them, instead throwing the main hand one on the first click and the off hand one on the second click (if the first one is still thrown)
- Added a "Classic Look" integrated resource pack
  - Replaces the default Scythe texture with the original Clown Scythe by Jomk, and the Anchorblade with the original Luxintrus Anchorblade model by Diansu
- Added iron ingots as a repair material for the Scythe and Anchorblade
- Added a Scissors Scythe skin
- Tweaked the Crescent Blade Anchorblade skin's texture

------------------------------------------------------
Arsenal 0.1.2 - 1.20.1
------------------------------------------------------
- Added a Winsweep themed Anchorblade skin (Wanchorblade)
- Added an Arcane Ambessa themed Anchorblade skin (Crescent Blade)
- Reworked the cosmetics system:
  - Skins are no longer stored per individual item stack but instead per item stack name, meaning they are synchronized across worlds.
  - For example, if you have a Scythe named "Bingle" with a Guilded skin, any Scythe named "Bingle" you pick up or display in any world will have the Guilded skin (and change if you select another skin for it).
  - This also means that if you change the skin of a weapon with no name, you will be changing the default skin you are seen with for that weapon, meaning you will not have to visit an anvil / smithing table to get that skin the moment you pick up any unnamed weapon of that type.
  - As a result, if you give a skinned weapon to a non-supporter, the cosmetics will not show for them, but will show for you the moment you get it back.

------------------------------------------------------
Arsenal 0.1.1 - 1.20.1
------------------------------------------------------
- Added item tags to dictate what items can be displayed (for weapon racks) and how they should be displayed (for back slot and weapon racks):
  - "displayable": What items can be displayed in a weapon rack. By default, contains the "tools" Vanilla tag and "big_weapons", "tridents", "shields" and "ranged_weapons" Arsenal tags.
  - "big_weapons": Weapons that have a 32x32 handheld sprite and therefore should be displayed bigger. By default, contains the Scythe and Anchorblade.
  - "tridents": Tridents. By default, contains the Minecraft Trident item.
  - "shields": Shields. By default, contains the Minecraft Shield item.
  - "ranged_weapons": Ranged weapons. By default, contains the Minecraft Bow and Crossbow items.
- Added a new "Folly Tree Branch" Scythe skin for supporters
- Fixed some issues with the back slot and Trinkets
- Supporter cosmetic switching now checks for the anvil tag instead of anvil blocks, fixing switching cosmetics not working on damaged and modded anvils
- Fixed Drowned holding an Anchorblade not being guaranteed to drop it
- Toned down the saturation of the orange swing of the L'Ancre Anchorblade skin

------------------------------------------------------
Arsenal 0.1.0 - 1.20.1
------------------------------------------------------
Initial release (beta)