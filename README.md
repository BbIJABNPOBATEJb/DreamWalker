# DreamWalker

**DreamWalker** is a Minecraft Forge mod (1.7.10) that extends chat functionality with aliases, custom messages, and
lightweight status effects. It provides powerful tools for both players and administrators to streamline interactions,
commands, and role-playing features.

---

## 🔧 Features

### ◼ Aliases

- Define reusable command sets.
- Trigger using `/alias` or `;<alias>`.
- Supports global and player-specific aliases.
- Aliases have:
    - `<alias>` ID
    - `<title>` display name
    - `<description>` (shown via `;<alias> ?`)
    - Commands separated by `;`

### ◼ Alias Execution

- Use `;<alias>` to run associated commands.
- Use `;<alias> ?` or `/alias ? <alias>` to see description.

### ◼ Admin Alias Management

- `/alias help` — view all admin commands.
- `/alias player <name> list` — show all player's aliases.
- `/alias player <name> add <alias> <title_underscored> <desc_underscored> <commands_separated_by_;>` — create/edit.
- `/alias player <name> remove <alias>` — delete alias.
- `/alias global list/add/remove` — manage global aliases.

### ◼ Effects (Textual)

- `/effects` — show player's active effects.
- `/effects <name>` — view description of an effect.
- `/effects <player> "<name>" "<description>"` — add effect as admin.
- `/effects list <player>` — view effects of any player.
- `/effects remove <player> <name|all>` — remove effect(s).

Effects are per-player and purely descriptive (not potion-based).

---

## ❓ Commands Overview

| Command                               | Description                              |
|---------------------------------------|------------------------------------------|
| `/alias ?`                            | Show available aliases (global & player) |
| `/alias help`                         | Show admin instructions                  |
| `/alias player <name> list`           | List player's aliases                    |
| `/alias player <name> add ...`        | Add an alias                             |
| `/alias player <name> remove ...`     | Remove an alias                          |
| `/alias global list/add/remove`       | Manage global aliases                    |
|                                       |                                          |
| `/effects`                            | Show your own effects                    |
| `/effects help`                       | Show help for /effects command           |
| `/effects <player> list`              | Admin: View player's effects             |
| `/effects <player> set <name> <desc>` | Admin: Add new effect to player          |
| `/effects <player> remove <name>`     | Admin: Remove specific effect            |
| `/effects <player> remove all`        | Admin: Remove all effects                |

---

## 🗃 Data Storage

- Aliases are stored in JSON files per-player:  
  `config/dreamwalker/players/<player>.json`
- Global aliases:  
  `config/dreamwalker/global_alias_config.json`
- Effects are stored in memory (or in code) per session — optionally extend to persistent format.

---

## 🛠 Requirements

- Minecraft 1.7.10
- ForgeGradle 1.2 (compatible with Java 8)
- Java 8 (recommended and required)
- Mixin mod `justMixins-0.7.11-1.7.10.jar`.

---

## 🔗 Integration Ideas

- Combine with `PermissionsEx` to control alias permissions.
- Use with `Skript` or custom command frameworks.
- Hook into `PlayerChatEvent` for richer context-based alias usage.

---

## 🧪 Demo Alias Entry Example

```json
"alias": "luck",
"displayName": "Lucky",
"description": "+5 -2",
"runCommands": [
  {
    "command": "/say Luck",
    "delay": 10
  },
  {
    "command": "/say {args}",
    "delay": 0
  }
]