# Glaze Client (Fabric, Minecraft 1.21.11)

UNTESTED: written without the ability to compile. Expect to fix build errors on first build.

## Use
- Right Shift opens the GUI: searchable module list, category tabs, per-module settings ("..."),
  profiles (Save/Load), themes, and a Keybinds button (vanilla controls screen, "Glaze Client" category).
- Commands: /wp add <name>, /wp remove <name>, /wp list
- Profiles: config/glaze/profiles/*.json. A profile named "donutsmp" auto-loads on servers whose address contains "donutsmp".
- Data: config/glaze/waypoints.json, config/glaze/exploration.json

## Build without installing anything
Upload this folder (including .github) to a GitHub repo, open Actions, download "glaze-jar".
Use glaze-0.1.0.jar (not -sources) in .minecraft/mods with Fabric API.
