# 15 Annoyances

A Fabric mod for Minecraft **1.21.4** that forces **one of 15 randomized annoyances** onto your world — or onto every player individually. Nothing is under your control.

When a world loads for the first time, one annoyance is randomly picked and revealed to every player via a slot-machine title animation. The annoyance stays until changed via command.

## The 15 Annoyances

| # | Annoyance | Subtitle | What it does |
|---|-----------|----------|--------------|
| 1 | Outrovert | they like you i guess | All mobs within 20 blocks rush toward you at boosted speed. They bypass water drag. Hostile mobs won't attack — they're in love. |
| 2 | Wind Surge | That was not the wind. | Periodic wind gusts launch you in random directions. Some spin you around. |
| 3 | Chaos | BEHOLD, THE CHAOS!! | Every few seconds, a random event fires: lightning, explosions, mob bursts, potion roulette, loot rain, fire, teleports, primed creepers. |
| 4 | Introverts | social anxiety simulator | All mobs within 14 blocks flee from you at high speed. A scary scream plays when they startle. |
| 5 | Earthquake | the ground has a stutter | Periodic earthquakes shake you around with random velocity and rumbling sounds. |
| 6 | Gravity Flip | toasts land on butter side now. | Gravity periodically inverts. Portal particles warn you 2 seconds before a flip. Sneak to hold on, water cancels the lift. |
| 7 | Vision Glitch | you might have schizophrenia. | Your vision cycles through 5 broken modes every 6 seconds: glitch slices, color inversion, mirror, upside-down, and behind-your-head. |
| 8 | Teleport Frenzy | you teleport, i guess. | Your actions teleport you: attacking warps you away from targets, damage teleports you randomly, using items sends you across the map, jumps launch you skyward. Dimension-aware (nether-safe, end-floats). |
| 9 | Floor Is Lava | do not afk. | Standing still for 1.75 seconds ignites the block under you and converts it to lava. Water within 4 blocks evaporates. |
| 10 | Drunk | are drunk you lot a | Constant nausea and screen wobble. |
| 11 | Block Temper | your blocks have a temper. | Every block you break has a 30-60% chance to explode (TNT-level). Mining is stressful. |
| 12 | Caffeinated | you're too addicted to coffee. | You move at 6x speed, jump 4x higher, and get periodic speed/jump/night-vision bursts. Sleep is blocked. |
| 13 | Mob Rain | someone seems to rain from above | Mobs rain from the sky within 25 blocks of you every few seconds. |
| 14 | Identity Crisis | js choose an identity bro | You become invisible and a mirror mob follows you — everyone sees you as that mob. Cycles through ~90 mob types with matching effects, sounds, and abilities. Your own mob is hidden in first person. |
| 15 | Those Dang Mobs | they got a BAD temper. | All passive mobs gain AggressiveChaseGoal and target the nearest player at boosted speed. They bypass water drag. |

## Modes

- **Shared** (default) — one random annoyance for the whole server. Everyone suffers together.
- **Random** — every player gets their own random annoyance. Different people, different pain.

## Commands

| Command | Permission | Effect |
|---------|-----------|--------|
| `/annoyance` | anyone | Show the active annoyance. |
| `/annoyance mode shared` | op | Switch to shared mode (one annoyance for all). |
| `/annoyance mode random` | op | Switch to random mode (per-player annoyances). |
| `/annoyance set <name>` | op | Force a specific annoyance (e.g. `/annoyance set blocktemper`). |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) 0.16.x for Minecraft 1.21.4.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) 0.119.x and place it in your `mods/` folder.
3. Drop `fifteenannoyances-1.0.0.jar` into `mods/`.
4. Launch the game.

A pre-compiled jar is included in the `compiledmod/` folder.

## Custom Sounds

The mod includes a custom scary scream sound (`fifteenannoyances:scary_scream`) that plays when Introverts mobs startle and flee. Minecraft only supports Ogg Vorbis audio — mp3 files will silently fail.

## Building from Source

```bash
./gradlew build
```

The jar lands in `build/libs/`. For development:

```bash
./gradlew runClient
```

## Requirements

- Minecraft **1.21.4**
- [Fabric Loader](https://fabricmc.net/use/) 0.16.x
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.119.x
- Java **21**

## License

This project is licensed under the [Apache License, Version 2.0](LICENSE).
