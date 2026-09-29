# AxolotlClient Index

## Metadata
- SHA-256: 02d81fcbf33866d169e8a5efd02792b692292c52f937f4f7ac39a03df5550366
- Mod ID: axolotlclient
- Version: 3.2.1+1.21.11
- License: LGPL-3.0-or-later
- MC version: 1.21.11
- Fabric Loader required: >=0.18.0
- Fabric API required: *

## Entrypoints
- client: ["io.github.axolotlclient.AxolotlClient"]
- modmenu: ["io.github.axolotlclient.config.modmenu.ModMenuCompat"]
- worldhost: ["io.github.axolotlclient.api.worldhost.AxolotlClientWorldHostPlugin"]

## Mixins declared
- axolotlclient.mixins.json
- axolotlclient-bridge.mixins.json
- axolotlclient.e4mc.mixins.json

## Public API surface
- Key Classes:
    - io.github.axolotlclient.AxolotlClient — Main entrypoint
    - io.github.axolotlclient.config.modmenu.ModMenuCompat — Integration point

## Configuration
- Data files read: Config and common/bridge data files

## Integration points
- Client initialization: Hook into `io.github.axolotlclient.AxolotlClient`
