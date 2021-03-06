---
id: permissions
title: Permissions
---

MythicDrops has many permissions in order to allow you to customize your server to your liking. Below is a
comprehensive list of all of the permissions available within MythicDrops.

Permissions default to only being available to OPs unless otherwise marked with `default: 'true'`.

Wildcard permissions (`.*`) have a list of children permission that they also grant. If a child permission is marked
with false, that permission explicitly denied by the wildcard.

## Permissions List

```yaml
mythicdrops.identify:
  description: Allows a player to identify items.
  default: "true"
mythicdrops.socket:
  description: Allows a player to use socket gems.
  default: "true"
mythicdrops.repair:
  description: Allows a player to repair items.
  default: "true"
mythicdrops.command.spawn.custom:
  description: Allows player to use "/mythicdrops spawn custom" command.
mythicdrops.command.spawn.gem:
  description: Allows player to use "/mythicdrops spawn gem" command.
mythicdrops.command.spawn.tier:
  description: Allows player to use "/mythicdrops spawn tier" command.
mythicdrops.command.spawn.tome:
  description: Allows player to use "/mythicdrops spawn tome" command.
mythicdrops.command.spawn.unidentified:
  description: Allows player to use "/mythicdrops spawn unidentified" command.
mythicdrops.command.spawn.*:
  description: Allows player to use all "/mythicdrops spawn" commands.
  children:
    mythicdrops.command.spawn.custom: true
    mythicdrops.command.spawn.gem: true
    mythicdrops.command.spawn.tier: true
    mythicdrops.command.spawn.tome: true
    mythicdrops.command.spawn.unidentified: true
mythicdrops.command.give.custom:
  description: Allows player to use "/mythicdrops give custom" command.
mythicdrops.command.give.gem:
  description: Allows player to use "/mythicdrops give gem" command.
mythicdrops.command.give.tier:
  description: Allows player to use "/mythicdrops give tier" command.
mythicdrops.command.give.tome:
  description: Allows player to use "/mythicdrops give tome" command.
mythicdrops.command.give.unidentified:
  description: Allows player to use "/mythicdrops give unidentified" command.
mythicdrops.command.give.*:
  description: Allows player to use all "/mythicdrops give" commands.
  children:
    mythicdrops.command.give.custom: true
    mythicdrops.command.give.gem: true
    mythicdrops.command.give.tier: true
    mythicdrops.command.give.tome: true
    mythicdrops.command.give.unidentified: true
mythicdrops.command.load:
  description: Allows player to reload configuration files.
mythicdrops.command.unidentified:
  description: Allows player to use "/mythicdrops unidentified" command.
mythicdrops.command.identitytome:
  description: Allows player to use "/mythicdrops tome" command.
mythicdrops.command.customcreate:
  description: Allows player to use "/mythicdrops customcreate" command.
mythicdrops.command.customitems:
  description: Allows player to use "/mythicdrops customitems" command.
mythicdrops.command.socketgems:
  description: Allows player to use "/mythicdrops socketgems" command.
mythicdrops.command.debug:
  description: Allows player to use "/mythicdrops debug" command.
mythicdrops.command.errors:
  description: Allows player to use "/mythicdrops errors" command.
mythicdrops.command.tiers:
  description: Allows player to use "/mythicdrops tiers" command.
mythicdrops.command.modify.name:
  description: Allows player to use "/mythicdrops modify name" command.
mythicdrops.command.modify.lore.add:
  description: Allows player to use "/mythicdrops modify lore add" command.
mythicdrops.command.modify.lore.remove:
  description: Allows player to use "/mythicdrops modify lore remove" command.
mythicdrops.command.modify.lore.insert:
  description: Allows player to use "/mythicdrops modify lore insert" command.
mythicdrops.command.modify.lore.set:
  description: Allows player to use "/mythicdrops modify lore set" command.
mythicdrops.command.modify.lore.*:
  description: Allows player to use "/mythicdrops modify lore" commands.
  children:
    mythicdrops.command.modify.lore.add: true
    mythicdrops.command.modify.lore.remove: true
    mythicdrops.command.modify.lore.insert: true
    mythicdrops.command.modify.lore.set: true
mythicdrops.command.modify.enchantment.add:
  description: Allows player to use "/mythicdrops modify enchantment add" command.
mythicdrops.command.modify.enchantment.remove:
  description: Allows player to use "/mythicdrops modify enchantment remove" command.
mythicdrops.command.modify.enchantment.*:
  description: Allows player to use "/mythicdrops modify enchantment" commands.
  children:
    mythicdrops.command.modify.enchantment.add: true
    mythicdrops.command.modify.enchantment.remove: true
mythicdrops.command.modify.*:
  description: Allows player to use "/mythicdrops modify" commands.
  children:
    mythicdrops.command.modify.name: true
    mythicdrops.command.modify.lore.*: true
    mythicdrops.command.modify.enchantment.*: true
mythicdrops.command.combiners.list:
  description: Allows player to use "/mythicdrops combiners list" command.
mythicdrops.command.combiners.add:
  description: Allows player to use "/mythicdrops combiners add" command.
mythicdrops.command.combiners.remove:
  description: Allows player to use "/mythicdrops combiners remove" command.
mythicdrops.command.combiners.*:
  description: Allows player to use all "/mythicdrops combiners" commands.
  children:
    mythicdrops.command.combiners.list: true
    mythicdrops.command.combiners.add: true
    mythicdrops.command.combiners.remove: true
mythicdrops.command.*:
  description: Allows player to use all commands.
  children:
    mythicdrops.command.spawn: true
    mythicdrops.command.spawn.wildcard: true
    mythicdrops.command.give: true
    mythicdrops.command.give.wildcard: true
    mythicdrops.command.load: true
    mythicdrops.command.unidentified: true
    mythicdrops.command.identitytome: true
    mythicdrops.command.customcreate: true
    mythicdrops.command.customitems: true
    mythicdrops.command.socketgems: true
    mythicdrops.command.tiers: true
    mythicdrops.command.modify.*: true
    mythicdrops.command.combiners.*: true
mythicdrops.*:
  description: Allows player to do all MythicDrops tasks.
  children:
    mythicdrops.identify: true
    mythicdrops.socket: true
    mythicdrops.repair: true
    mythicdrops.command.*: true
```
