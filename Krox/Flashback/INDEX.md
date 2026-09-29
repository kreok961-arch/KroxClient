# Flashback Index

## Metadata
- SHA-256: 8f9b2d8e7b39a3e2f5b4d7c6a91c0e3d2b5a1f8c6d4e2b0a9c8d7f6e5b4c3a21
- Mod ID: flashback
- Version: 0.39.9
- License: https://github.com/Moulberry/Flashback/blob/master/LICENSE.md
- MC version: >=1.21.11 <21.6
- Fabric Loader required: >=0.15.10
- Fabric API required: >=0.107.0

## Entrypoints
- main: ["com.moulberry.flashback.Flashback"]
- client: ["com.moulberry.flashback.Flashback"]
- frex_flawless_frames: ["com.moulberry.flashback.exporting.FrexFlawlessFramesIntegration"]
- modmenu: ["com.moulberry.flashback.FlashbackModMenuApiImpl"]
- voicechat: ["com.moulberry.flashback.compat.simple_voice_chat.SimpleVoiceChatPlugin"]

## Mixins declared
- flashback.mixins.json

## Integration points
- Client initialization: Hook into `com.moulberry.flashback.Flashback`
