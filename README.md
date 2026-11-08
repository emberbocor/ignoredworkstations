# Ignored Workstations

Keep villagers off your workstations.

A barrel in your storage room or a composter on your farm is a job site as far as villagers are concerned, and the nearest unemployed villager will walk over and claim it. Ignored Workstations lets you wax a workstation with a honeycomb. Villagers then leave it alone, and the block keeps working for you exactly as before. Scrape the wax off with an axe and it is an ordinary job site again.

The mod runs on the server only. Players join with a plain vanilla client.

Available for NeoForge, Forge and Fabric.

## Features

- **Mark with a honeycomb.** Right click a workstation with a honeycomb. Villagers will not take it as a job site from then on.
- **Undo with an axe.** Right click a marked workstation with an axe and villagers can use it again.
- **Claims let go at once.** A villager that holds the block when you mark it loses that job site right away.
- **The block still works.** Barrels store, lecterns hold books, smokers cook, cauldrons fill, hoppers and comparators work as usual.
- **Saved with the world.** Marks survive server restarts and chunk reloads.
- **No config, nothing to install on clients.**

## How it works

- **Workstations.** The job site blocks of the vanilla professions: blast furnace, smoker, cartography table, brewing stand, composter, barrel, fletching table, cauldron (empty or with water, lava or powder snow), lectern, stonecutter, loom, smithing table and grindstone.
- **Marking.** Right click a workstation that is not marked while holding a honeycomb; you do not need to sneak. One honeycomb is used (none in Creative mode), the block shows the wax particles and sound of waxing copper, and "Villagers will ignore this block" appears above your hotbar. The block does not open or react on that click.
- **Honeycomb on a marked block.** Nothing special happens: the block opens or reacts as usual and the honeycomb is not used.
- **Removing a mark.** Right click a marked workstation with an axe, without sneaking. The axe loses 1 durability (none in Creative mode), the block shows the wax-off particles and sound, and "Villagers can use this block again" appears above your hotbar. An axe on a workstation that is not marked does what it always does.
- **Hands.** The main hand goes first, as in vanilla. With an empty main hand and a honeycomb in the off hand, a barrel opens, because the empty hand already used the click.
- **Villagers.** A villager never picks a marked block as a job site. If one held it when you marked it, the claim ends at once and vanilla takes over: a villager that has never traded loses its profession and looks for another workstation, one that has traded keeps its profession. A villager in an unloaded chunk at that moment forgets the block when it next comes near it.
- **Blocks that change.** A mark stays while the block at that position remains a workstation with the same job site. Composter levels, cauldron contents, a lectern getting a book, a barrel opening and a furnace lighting up all keep the mark.
- **Blocks that leave.** A mark is gone when the block leaves its position or is replaced, however that happens: broken, blown up, pushed by a piston, or changed with `/setblock` or `/fill`. A block placed there afterwards is not marked.
- **What stays vanilla:** beds, bells, raids, gossip, trading, breeding and iron golems.

## Requirements

| | NeoForge | Forge | Fabric |
|---|---|---|---|
| Minecraft | 1.21.1 | 1.21.1 | 1.21 or 1.21.1 |
| Loader | NeoForge 21.1.1 or newer | Forge 52.0.0 or newer | Fabric Loader 0.15.11 or newer |
| Jar | `ignoredworkstations-neoforge-1.21.1-<version>.jar` | `ignoredworkstations-forge-1.21.1-<version>.jar` | `ignoredworkstations-fabric-1.21-1.21.1-<version>.jar` |

On Minecraft 1.21.1, NeoForge and Forge are separate loaders that do not run each other's mods, so each has its own jar. Pick the jar that matches your server's loader. Fabric API is not needed.

### Client and server

Install the mod on the server only, on every loader. It adds no items, blocks, entities, commands, network messages or registries, so the server has nothing a client needs to know about:

- **NeoForge:** vanilla clients and NeoForge clients without the mod can join.
- **Forge:** vanilla clients and Forge clients without the mod can join. The mod is marked as server-only, so a Forge client without it still shows the server as compatible in the server list.
- **Fabric:** vanilla clients and Fabric clients without the mod can join.

Installing it on a client as well does no harm. Marks are handled on the server, and the messages are plain text.

In singleplayer, install it in your own game; the built-in server runs it.

## Removing the mod

The world opens normally and every workstation is an ordinary job site again, because the mod does not change the blocks or remove the game's own job site records. The mod's data stays in the world folder (`data/ignoredworkstations.dat` of each dimension); vanilla ignores it, and it does no harm. If you install the mod again, marks on blocks that have not changed work again. Marks are stored by position and the mod cannot see changes made while it is removed, so if a workstation was broken and the same type of block was placed at that spot in the meantime, the new block counts as marked. Use an axe to remove the mark.

## Compatibility

- **Job sites added by other mods** are not supported, including job sites of professions from other mods.
- **Mods that change right click interactions** may conflict. A protection mod that cancels the right click stops the marking too. A mod that handles honeycomb or axe clicks on these blocks in its own way may get there first, or be skipped.
- **Mods that give villagers their own way of finding a job site** may bypass the mark.

## Building

```
./gradlew build
```

The jars are written to `neoforge/build/libs/`, `forge/build/libs/` and `fabric/build/libs/`.

## License

MIT.
