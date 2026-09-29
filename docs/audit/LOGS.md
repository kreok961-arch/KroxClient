# Logs Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

The §9.5 Logs feature is **split across two surfaces** and, critically, **has no
list surface at all**. What exists is a viewer and a share menu:

| Surface | File | Lines | Role |
|---|---|---|---|
| Log viewer | `ui/screens/content/LogViewScreen.kt` | 104 | Renders one log file, read-only |
| Crash-log share menu | `ui/screens/main/crashlogs/LogShareMenu.kt` | 145 | View / share menu over a crash log |

There is **no log index, no log list, and no log filter screen** anywhere in
the codebase. `docs/audit/HOME.md` records the consequence: the `LastLogCard`
that used to live on Home was removed during the selective `RightMenu` restore
(R-02), and with it the only navigational host for Logs. `LogView` is still
reachable, but only through an explicit `navigateToLogView(logPath)` call with
a known path — see the call sites below.

## `LogViewScreen.kt` — declarations

| Symbol | Line | Visibility | Role |
|---|---|---|---|
| `navigateToLogView(logPath)` | 53 | **public** | Nav extension → `NormalNavKey.LogView(logPath)`, class equality |
| `LogViewScreen(` | 61 | **public** | Screen root, `BaseScreen`-wrapped |

Only two public declarations; both are navigation-surface, not feature-surface.

## Composition

`LogViewScreen` (`:61`) → `BaseScreen` (`:82`) → `AnimatedVisibility` (`:86`)
→ `SoraEditor` (`:96`).

The screen is a **thin wrapper over the shared code editor**. It supplies a
read-only editor state, a log syntax highlighter, and the IDE theme; the actual
text rendering is `SoraEditor` in `ui/code_editor/`.

| Element | Line | Note |
|---|---|---|
| `isDark` | 65 | `isLauncherInDarkTheme()` |
| `editorState` | 67 | `mutableStateOf<EditorState>(EditorState.Loading)` |
| `LaunchedEffect(key)` | 69 | Reads the file on `Dispatchers.IO` |
| `runCatching { File(key.logPath).readText() }` | 72-77 | Failure path logs via `Logger.warning("ViewLog", …)` and surfaces `e.message` |
| `BaseScreen` | 82 | Supplies the swap-in/swap-out visibility animation |
| `fadeIn` / `fadeOut` | 88-89 | `kroxTween(KroxMotion.FAST)` — §12.4-compliant |
| `scheme` | 91-93 | `SchemeIDEADark()` / `SchemeIDEALight()` |
| `language` | 94 | `LogLanguage()` — log syntax highlighting |
| `SoraEditor(isReadOnly = true)` | 96-102 | `onSaveClick = {}` — no-op |

## §9.5 compliance

| Spec requirement | Code reality | Verdict |
|---|---|---|
| View a log | `SoraEditor` at `:96` | **Met** |
| Read log contents | `readText()` at `:73` on `Dispatchers.IO` | **Met** |
| Syntax highlighting | `LogLanguage()` at `:94` | **Met** |
| Transition on navigate | `BaseScreen` + `kroxTween(KroxMotion.FAST)` | **Met** |
| Log list / history | **Not present in code** | **Pre-existing gap** — D-06 in `docs/REGRESSIONS.md` |

### The log read is a §19.4 no-touch

`File(key.logPath).readText()` at `:73` is a **file operation**. §19.4 forbids
touching file operations, and §1.3 forbids adding a log-list feature. The
error path at `:74-77` is equally load-bearing: on failure it logs a warning
**and** renders the exception message into the editor body, so the user sees
why the log is empty instead of a blank screen. That behaviour is `KEEP`
byte-identical.

## Navigation — every route into Logs

| Call site | Trigger |
|---|---|
| `MainActivity.kt:296` | `EventViewModel.Event.OpenLog` — an event carrying a known path |
| `MainActivity.kt:444` | `LogShareMenu.onView` — the user picks "view" in the crash-log menu |
| `MainScreen.kt:718` | Nav-graph host: `entry<NormalNavKey.LogView> { LogViewScreen(key, backStackViewModel) }` |

`MainScreen.kt:718` is the only place the destination is *rendered*; the two
`MainActivity` sites are the only places it is *entered*. There is no third
entry point, and there is no Home tile, menu row, or settings link that reaches
it. This is the R-02 consequence, and it is **pre-existing** — the redesign
does not restore a home tile (that would be re-adding a removed feature, and
§1.3 says do not remove what exists while §1.2 forbids new navigation).

## `LogShareMenu.kt` — the crash-log surface

145 lines, one public declaration: `LogShareMenu(` at `:49`.

| Callback | Consumer | Action |
|---|---|---|
| `onView` | `MainActivity.kt:443` | `navigateToLogView(logFile.absolutePath)`, then closes the menu |
| `onShare` | `MainActivity.kt:449` | `shareFile(this@MainActivity, logFile)` — Android share sheet |
| `onChange` | `MainActivity.kt:437-441` | Closes on `LogShareMenuOperation.None` |

**Do not touch `onShare`.** It hands a file to the platform share sheet, which
is a system-integration path, not a UI path. Restyling the menu that *contains*
it is presentation; changing what it passes to the sheet is not.

`LogShareMenuOperation` is an enum with `ShowMenu` / `None` (and whatever the
menu's own states are). It is a menu state machine — `KEEP`.

## Token adoption

`grep -c KroxSpacing LogViewScreen.kt` → **0**. The file uses literal `dp`
(nowhere — it has no padding of its own; all layout belongs to `SoraEditor`).
Motion **is** tokenized: both `fadeIn` and `fadeOut` use
`kroxTween(KroxMotion.FAST)` at `:88-89`. This is one of the few files in the
app already migrated to the KROX motion scale (`docs/audit/ANIMATIONS.md`).

## §8.16 loading state — present

`EditorState.Loading` at `:67` is an explicit loading state, and
`EditorState.Success(Content(content))` at `:79` is its terminal state. The
loading presentation is owned by `SoraEditor`, not by this screen.

This is the **only** screen in the app with a real loading state modelled
explicitly. Most others imply loading by absence (see
`docs/audit/ACCOUNTS.md` States table). `KEEP` — do not "simplify" it away.

## §19.4 position

| Guarantee | Status |
|---|---|
| Not touching log capture | **Held** — `bridge/LogMultiplexer.kt`, `utils/logging/Logger.kt`, `utils/logging/LogMessage.kt`, `game/launch/LogName.kt`, and the whole `ui/screens/game/elements/log_parser/` tree are out of scope. |
| Not touching file operations | **Held** — `readText()` at `:73` and the `shareFile(...)` call at `MainActivity.kt:449` are untouched. |
| Not adding a log feature | **Held** — §1.3. No log list, no filter, no search, no export is added. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | `LogViewScreen` structure, the `LaunchedEffect` read, the error path, `EditorState.Loading`, `SoraEditor(isReadOnly = true)`, the `fadeIn`/`fadeOut` motion tokens, `navigateToLogView`, `LogShareMenu` and all three of its callbacks, the nav-graph entry at `MainScreen.kt:718`. |
| **REDESIGN** | Presentation only: the editor's surrounding surface and padding, the share menu's §8.11 dialog chrome (scrim `rgba(0,0,0,0.55)`, border hairline `rgba(255,255,255,0.06)`), and the menu's row spacing. |
| **FIX** | None. The absent log list is a pre-existing gap, not a defect in what exists — restoring it would be a new feature under §1.2. |

## Notes

- **Do not restyle `SoraEditor` itself.** It is shared with the code editor and
  control-editor features. Changing its syntax theme to make Logs look a certain
  way would repaint those too. Theme selection happens at `:91-93` and is
  already correct — dark when the launcher is dark, light when it is light.
- `LogLanguage()` at `:94` is `remember`ed once, outside the dark-theme key.
  That is intentional — the highlighter is theme-independent. Do not move it
  inside the `remember(isDark)` block.
- The read at `:73` uses `runCatching` and `getOrElse` rather than try/catch.
  Both are fine; the point is that the failure **is surfaced to the user**, not
  swallowed. Any "cleanup" that replaces this with a silent return would be a
  regression under §8.15/§8.16.
- All edits to these files are unverified by a compiler until R-01 clears — see
  the verification-status section in `docs/REGRESSIONS.md`.
