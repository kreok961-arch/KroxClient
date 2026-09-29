# Resources Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

`ZalithLauncher/src/main/res/` holds **269 files** across 22 directories. It is
almost entirely **already-converted Material Symbols** — the KROX icon
migration is substantially done, which contradicts the common assumption that
Phase 5 must replace the icon set.

| Directory | Files | Notes |
|---|---|---|
| `drawable/` | 198 | 125 vector XML + 73 raster |
| `raw/` | 17 | 15 license texts + 2 audio cues |
| `values-*/` | 17 | 16 locales + `values-night` |
| `mipmap-*/` | 12 | 5 densities + `anydpi-v26` adaptive |
| `values/` | 6 | `strings.xml` (1624 strings) + 5 others |
| `xml/` | 4 | Network security, backup rules, provider paths |
| `layout/` | 1 | `player_texture_view.xml` — the game surface, not UI |
| `ruvector.db` | 1 | 1.5 MB — a shipped SQLite DB |

## The icon set is already migrated

`res/drawable/` file-type breakdown:

| Type | Count | Assessment |
|---|---|---|
| **Vector XML** | **125** | Material Symbols — already on-spec |
| PNG | 70 | Screenshots, banners, app art |
| WebP | 2 | |
| JPG | 1 | |

Naming: **129** files are prefixed `ic_` (Material Symbols, e.g.
`ic_schedule_outlined`, `ic_dashboard_outlined`, `ic_assignment_filled`,
`ic_sports_esports_filled` — the four `StatCard` icons in
`PlayTimeStatsScreen.kt:93/101/116/125`). **68** are prefixed `img_` — launcher
artwork, plugin icons, avatars, skin renders.

**Phase 5 does not need an icon migration.** §14.2 forbids new dependencies, and
`androidx.compose.material:material-icons-core` ships no bundled drawables in
recent versions; the 125 vectors are the correct approach already taken. What
Phase 5 *does* do is standardize **tint and size** — the four `StatCard` icons
all read `32.dp` + `tint = primary`, which is the on-spec pattern; other screens
inconsistently do not.

**Do not add a font or an icon font.** `res/font/` **does not exist**. Adding one
would violate §14.2 and contradict the locked `KroxFontFamily = FontFamily.Default`
decision (`docs/audit/THEME.md`).

## `values/strings.xml` — 1624 strings

The largest single resource file, and the app's entire localization surface.

| Directory | Locale |
|---|---|
| `values/` | English (default) — 1624 strings |
| `values-ar` | Arabic |
| `values-es` | Spanish |
| `values-fil` | Filipino |
| `values-in` | Indonesian |
| `values-it` | Italian |
| `values-ja` | Japanese |
| `values-ko` | Korean |
| `values-pt` / `values-pt-rBR` | Portuguese / Brazilian |
| `values-ru` | Russian |
| `values-th` | Thai |
| `values-tr` | Turkish |
| `values-ug` | Uyghur |
| `values-vi` | Vietnamese |
| `values-zh-rCN` / `values-zh-rTW` | Simplified / Traditional Chinese |
| `values-night` | Dark-mode resource overrides |

**16 locales, 17 `values-*` directories** (one is `values-night`).

### The Phase 5 string rule

| Allowed | Forbidden |
|---|---|
| Adding a string **key** for a §8.15 empty-state's supporting copy | Hard-coding a literal in a composable |
| Reusing an existing key that already says the right thing | Renaming or deleting a key |
| — | Changing an existing string's **English** value |

The last row is the load-bearing one. §2.2's preservation contract means a user
on `values-ru` must see the same Russian text after the redesign. Editing the
English source of an existing key would leave the 15 translations describing a
screen that no longer exists — a visible regression in the one place §1.3 is
most easily violated by accident.

**New §8.15 copy therefore needs 16 translations.** That is real work, not a
formality, and it is the reason the empty-state treatment is additive: it adds
keys without editing any existing one.

`values-night` is a dark-mode resource override, not a locale — it changes
which values load at night, and must not be collapsed into `values/`.

## `values/` — the other five files

| File | Role |
|---|---|
| `strings.xml` | 1624 strings — the localization surface |
| `themes.xml` | Android XML theme (splash window, system bars) |
| `attrs.xml` | Custom view attributes — the game surface |
| `ic_launcher_background.xml` | Adaptive-icon background |
| `chinese_festivals.xml` | Festival date logic (see `ui/theme/feativals/`) |
| `terracotta.xml` | Terracotta rules configuration |

**`themes.xml` and `ic_launcher_background.xml` are the launcher icon itself.**
§1.3 forbids removing what exists; the KROX app icon must survive the redesign
byte-identical. Restyling it is out of scope.

`attrs.xml` belongs to the game surface (`player_texture_view.xml`), which §2.3
puts off-limits.

## `raw/` — 15 licenses + 2 audio cues

| File | Consumer |
|---|---|
| `angle_license.txt` | `AboutInfoScreen.openLicense(raw)` |
| `apache_license_2.txt` | |
| `bhook_license.txt` | |
| `fcl_license.txt` | |
| `gl4es_license.txt` | |
| `hmcl_license.txt` | |
| `lgpl_3_license.txt` | |
| `lwjgl_license.txt` | |
| `mesa_license.txt` | |
| `mmkv_license.txt` | |
| `ng_gl4es_license.txt` | |
| `opennbt_license.txt` | |
| `skinview3d_license.txt` | |
| `sora_editor_license.txt` | |
| `xz_java_license.txt` | |
| `recorder_start.mp3` / `recorder_end.mp3` | **Game recording** — §2.3 off-limits |

**Every license text is a §2.2 preservation item.** `openLicense(raw)` takes a
`raw` resource id; the About page's license list must keep offering all 15
after the redesign. Losing one is an attribution regression, not a visual
change.

The two audio cues belong to the recorder and are **untouched** — §2.3.

## `mipmap-*/` — the app icon

5 density buckets (`mdpi`…`xxxhdpi`) plus `mipmap-anydpi-v26`, 12 files total.
**KEEP byte-identical.** See above.

## `layout/` — one file, and it is not UI

`player_texture_view.xml` is the only XML layout, and it belongs to the
`TextureView` the OpenGL game renders into. §2.3 puts it off-limits.

**Consequence worth stating plainly: the entire UI is Compose.** There is no XML
layout to keep in sync, no `View`/`ViewModel` XML pair to update, and no
`findViewById` to touch. Phase 5 has no migration path outside `.kt` files —
which is why the compile gate (R-01) is the only real verification.

## `xml/` — 4 platform files

| File | Role |
|---|---|
| `network_security_config.xml` | `cleartextTrafficPermitted="true"` |
| `backup_rules.xml` | Android Auto Backup |
| `data_extraction_rules.xml` | | Android 12+ backup/transfer |
| `provider_paths.xml` | `FileProvider` paths for `shareFile(...)` |

**`network_security_config.xml` permits cleartext HTTP.** This is a real,
shipped configuration. It is **not** the redesign's business — §1.2 forbids new
network behaviour and §19.4 covers everything else. It is recorded here so a
future security pass finds it; a visual redesign must not touch it.

**`provider_paths.xml` is load-bearing for Logs.** `MainActivity.kt:449` calls
`shareFile(this@MainActivity, logFile)`, which goes through a `FileProvider`
reading these paths. Changing them breaks log sharing — a §19.2 regression
("logs open") that would look unrelated to the file that caused it.

## `res/ruvector.db` — 1.5 MB shipped database

A prebuilt SQLite database at the `res/` root. It is **data, not a resource
declaration**, and is loaded at runtime. §19.4 covers it: the redesign does not
migrate, re-seed, or re-query it.

## §19.4 position

| Guarantee | Status |
|---|---|
| Not touching the config schema | **Held** — no `values/*.xml` value added, removed, renamed, or re-typed except new §8.15 string keys. |
| Not touching file operations | **Held** — `provider_paths.xml` untouched; `shareFile` keeps working. |
| Not touching the launch engine | **Held** — `layout/player_texture_view.xml`, `attrs.xml`, `res/ruvector.db`, and the two recorder cues are all out of scope. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | All 125 vector drawables and their names; all 73 raster assets; all 15 license texts; the two recorder cues; all 16 locale directories; the 12 mipmap files; `themes.xml`; `ic_launcher_background.xml`; `terracotta.xml`; `chinese_festivals.xml`; `provider_paths.xml`; `network_security_config.xml`; `res/ruvector.db`; `layout/player_texture_view.xml`. |
| **REDESIGN** | Icon **tint and size** standardization (Phase 5 touches no new drawables); §8.15 empty-state copy as new string keys. |
| **FIX** | None. |

## Notes

- **No new drawable may be added for a §8.15 empty state if an existing
  `ic_` vector fits.** The 125 Material Symbols almost certainly include an
  empty/inbox/folder-off icon. Check before authoring; a new drawable is 16
  untranslated-free but still an asset that must be maintained.
- The **16-locale burden is the real cost of the empty-state work**, not the
  layout. Budget for it. Skipping the translations leaves a screen that is
  half-translated, which is worse than not adding the copy at all.
- `res/drawable/` has **no density-qualified variants** — one flat directory.
  That is correct for vectors, but the 73 raster files are therefore
  single-density. Do not add `drawable-hdpi/` etc. to "fix" it; that is an asset
  pipeline change with no UI payoff.
- `values-night` in the same list as 16 locales is a trap when scripting
  translation work. It is not a 17th language.
- All conclusions here are unverified by a compiler until R-01 clears — see the
  verification-status section in `docs/REGRESSIONS.md`.
