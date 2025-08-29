# **DreamWalker**

**DreamWalker** is a Minecraft Forge mod for **Minecraft 1.7.10**, designed to enhance chat interaction by introducing powerful features like **aliases**, **textual effects**, and support for **custom commands with delays**. It’s built for both players and administrators to support immersive gameplay, roleplay, and streamlined command usage.

---

## 🔧 Features

### ◼ Aliases

- Define reusable command sequences with custom metadata.
- Trigger using `/alias` or `;<alias>`.
- Supports **global** and **player-specific** aliases.
- Alias structure includes:
    - `<alias>` — identifier (used for invoking).
    - `<title>` — display name for menus.
    - `<description>` — visible via `;<alias> ?` or `/alias ? <aliasName>`.
    - **Commands with optional delay** in ticks.

### ◼ Alias Execution

- `;<alias>` — execute alias.
- `;<alias> ?` — view alias info.
- `/alias ?` — list all available aliases for the player.

### ◼ Admin Alias Management

- `/alias help` — display admin command guide.
- `/alias player <name> list` — view player-specific aliases.
- `/alias player <name> add <alias> <title_with_underscores> <desc_with_underscores> <command1;delay1;command2;delay2;...>` — add an alias.
- `/alias player <name> remove <alias>` — delete a player's alias.
- `/alias global list|add|remove` — manage global aliases shared by all players.

### ◼ Effects (Textual Info)

- `/effects` — view your current effects.
- `/effects help` — show available effect commands.
- `/effects <player> list` — view another player's effects (admin).
- `/effects <player> set <name_underscored> <description_underscored>` — assign new effect (admin).
- `/effects <player> remove <effect|all|*>` — remove specific or all effects (admin).

> **Note:** Effects are purely visual/informational (not potion effects).

---

## ❓ Commands Overview

| Command                                                  | Description                              |
|----------------------------------------------------------|------------------------------------------|
| `/alias ?`                                               | Show available aliases                   |
| `/alias help`                                            | Show admin help menu                     |
| `/alias player <name> list`                              | List a player's aliases                  |
| `/alias player <name> add <alias> <title> <desc> <cmds>` | Add new alias with command(s) + delay(s) |
| `/alias player <name> remove <alias>`                    | Remove a player's alias                  |
| `/alias global list `                                    | add                                      |remove`                  | Manage global aliases                           |
| `/effects`                                               | View your own textual effects            |
| `/effects help`                                          | View all effect-related commands         |
| `/effects <player> list`                                 | Admin: View a player’s effects           |
| `/effects <player> set <name> <desc>`                    | Admin: Add effect to player              |
| `/effects <player> remove <name>`                        | all                                      |*>`          | Admin: Remove specific or all effects           |

---

## 🕓 Delayed Commands in Aliases

When defining an alias, each command can include a **delay (in ticks)** before execution.  
Syntax:
```
<command1>;<delay1>;<command2>;<delay2>;...
```

Examples:
```bash
/alias player Steve add warn Warning Caution "/say STOP;20;/say {args};0"
```

This executes:
1. `/say STOP` → after 0 ticks.
2. `/say {args}` → 20 ticks (1 second) later.

> **20 ticks = 1 second of delay**

---

## 📦 Data Storage

- **Per-player aliases:**  
  Stored in: `config/dreamwalker/players/<player>.json`

- **Global aliases:**  
  Stored in: `config/dreamwalker/global_alias_config.json`

- **Effects:**  
  Stored **in memory only** for now (volatile). Can be extended to JSON-based persistence.

---

## 🛠 Requirements

- Minecraft **1.7.10**
- ForgeGradle **1.2** (Java 8 compatible)
- Java 8
- Mixin support:  
  `justMixins-0.7.11-1.7.10.jar` (required)

---

## 🔗 Integration Ideas

- Integrate with **PermissionsEx** to restrict access to aliases.
- Use with roleplay plugins or scripted events.
- Hook into `PlayerChatEvent` for context-aware command triggering.

---

## 💡 Demo Alias JSON Example

```json
{
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
}
```

This alias sends `"Luck"` after 10 ticks, then echoes the player input (`{args}`) immediately.