# 15 Annoyances

A Fabric mod for Minecraft **1.21.4** that forces **one of 15 randomized annoyances** onto your world — or onto every player individually. Nothing is under your control.

## The 15 Annoyances

| Annoyance | Description |
|---|---|
| Helium Air | Air is now helium. Hope you like floating. |
| Wind Surge | Tornado season, every day. |
| Swapped Textures | Nothing looks right anymore. |
| Multifall | Every fall bounces you right back up. |
| Upside Down | Everything is flipped. Or is it? |
| Earthquake | The ground has a mind of its own. |
| Gravity Flip | Down is up. Up is down. Good luck. |
| Sounds?? | What even is audio. |
| Teleport Frenzy | You never quite stay where you are. |
| Floor Is Lava | Standing still is a fire hazard. |
| Drunk | Constant nausea and screen wobble. |
| Honeymoon | Nights are sticky. |
| Caffeinated | Sleep is impossible. Coffee is life. |
| Mob Rain | The sky is falling. Literally. |
| Those Dang Mobs | All mobs are completely aggressive. |

## Modes

- **Shared** — one random annoyance for the whole server.
- **Random** — every player gets their own random annoyance each time one is picked.

## Commands

| Command | Effect |
|---|---|
| `/annoyance` | Show the active annoyance. |
| `/annoyance mode shared` | Enable one annoyance for everyone (op). |
| `/annoyance mode random` | Give each player their own annoyance (op). |
| `/annoyance set <name>` | Force a specific annoyance, e.g. `/annoyance set windsurge`. |

## Requirements

- Minecraft **1.21.4**
- [Fabric Loader](https://fabricmc.net/use/) 0.16.x
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.119.x

## Building

```bash
./gradlew build
```

The jar lands in `build/libs/`. For development:

```bash
./gradlew runClient
```

## License

This project is licensed under the [Apache License, Version 2.0](LICENSE).
