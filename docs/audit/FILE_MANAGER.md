# File Manager Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

`ui/screens/content/BuiltInFileManager.kt`, **1715 lines**, is the largest
screen in the app and the host of the live sidebar
(`docs/audit/SIDEBAR.md` §2). It is **KEEP** in structure and **REDESIGN** in
presentation. §19.4 forbids touching file operations — this file is a
rendering surface over an existing file API, and nothing here may change what
a copy, move, rename, or delete does.

## Declarations

| Symbol | Line | Visibility | Role |
|---|---|---|---|
| `SIDEBAR_WIDTH` | 116 | private const | `180.dp` — sidebar container width |
| `SidebarSlideEnterSpec` | 118 | private val | §7.6-compliant slide-in |
| `SidebarSlideExitSpec` | 120 | private val | §7.6-compliant slide-out |
| `SectionExpandSpec` | 122 | private val | Collapsible section expand |
| `SectionShrinkSpec` | 124 | private val | Collapsible section shrink |
| `SectionFadeInSpec` | 126 | private val | Collapsible section fade-in |
| `SectionFadeOutSpec` | 128 | private val | Collapsible section fade-out |
| `EDITABLE_EXTENSIONS` | 131 | private val | Text-file extension allowlist |
| `isEditableTextFile(file)` | 136 | private fun | Extension guard for the editor |
| `BuiltInFileManagerScreen(` | 143 | **public** | Screen root — the file's only public declaration |
| `CollapsibleSidebarSection(` | 1181 | private | Collapsible section container |
| `SidebarNavItem(` | 1240 | private | Single nav row |
| `SidebarStorageFooter(rootDirectory)` | 1300 | private | Storage summary pinned to sidebar bottom |
| `FileItemLayout(` | 1402 | private | One file/folder row |
| `PropertiesDialog(files, onDismiss)` | 1531 | private | §8.11 multi-select properties |
| `PropertyRow(label, value)` | 1582 | private | Label/value pair inside the dialog |
| `formatBytesShort(bytes)` | 1601 | private fun | Human-readable size |
| `formatFileDate(millis)` | 1608 | private fun | Human-readable mtime |
| `buildClipboardLabel(files, isCut)` | 1611 | private fun | Clipboard action label |
| `FileManagerShortcutBar(` | 1630 | private | Quick-action row |
| `ShortcutChip(` | 1663 | private | One quick action |
| `getFileIcon(file)` | 1687 | private fun | Icon resolver — directory vs. file vs. extension |

`BuiltInFileManagerScreen(` at `:143` is the **only** public declaration in the
file. Everything else is private and unreachable from outside; a redesign
cannot accidentally alter another screen's behaviour by editing them.

## Motion — six hand-rolled specs

Lines `:118-128` define six `FiniteAnimationSpec<Float>` values. They are the
**only** such constants in the app: every other screen uses `KroxMotion` or
`kroxTween` from `utils/animation/AnimationUtils.kt`.

| Spec | Used by |
|---|---|
| `SidebarSlideEnterSpec` | `CollapsibleSidebarSection` expand |
| `SidebarSlideExitSpec` | `CollapsibleSidebarSection` collapse |
| `SectionExpandSpec` | Section height expand |
| `SectionShrinkSpec` | Section height shrink |
| `SectionFadeInSpec` | Section alpha in |
| `SectionFadeOutSpec` | Section alpha out |

All six are §7.6-compliant — **no** bounce, elastic, or spring. `KEEP` them
byte-identical. Any *new* sidebar animation introduced during Phase 5 must
reuse one of these six or a `KroxMotion` token, not add a seventh constant
(see `docs/audit/ANIMATIONS.md`).

## Tokens already adopted

Unlike most of the app, this file is already tokenized on the two rules that
matter most:

| Token | Where | Note |
|---|---|---|
| §7.7 divider `rgba(255,255,255,0.04)` | Sidebar + list dividers | Matches `SettingsScreen.kt:156-161` |
| §7.6 track `Color.White.copy(alpha = 0.12f)` | Progress / selection track | Logged as R-21 |

`grep -c KroxSpacing BuiltInFileManager.kt` → **0**. The file uses literal `dp`
for spacing, which §3 of `docs/spec/tokens.md` explicitly permits for
untouched code. Phase 5 edits adopt `KroxSpacing`; untouched regions keep their
literals.

## §9.6 File Manager compliance

| Spec requirement | Code reality | Verdict |
|---|---|---|
| Browse, select, and act on files | `FileItemLayout` (`:1402`) + `FileManagerShortcutBar` (`:1630`) | **Met** |
| Sidebar navigation | `CollapsibleSidebarSection` (`:1181`) + 3 siblings | **Met** |
| Properties inspection | `PropertiesDialog` (`:1531`) | **Met** |
| Storage visibility | `SidebarStorageFooter` (`:1300`) | **Met** |
| Empty state | Empty-folder branch of the list | **Already compliant** — see below |

### The empty-folder state is already §8.15-compliant — corrected

**This entry previously recorded a gap. It was wrong.** Re-reading
`BuiltInFileManager.kt:848-871` shows the empty-folder branch already
implements the full §8.15 treatment, inside a centred
`Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center)`:

- a 48 dp `ic_folder_outlined` `Icon` at `onSurfaceVariant.copy(alpha = 0.35f)`
- a `titleSmall` title that switches between `file_manager_no_files` and
  `file_manager_no_matching_files` depending on `searchQuery`
- a `bodySmall` description at `onSurfaceVariant.copy(alpha = 0.6f)` that
  switches between `file_manager_import_files_hint` and
  `file_manager_try_different_search` on the same condition
- a `CircularProgressIndicator(36.dp)` for `isLoadingFiles` immediately above it,
  satisfying §8.16

The branch is **search-aware**, which is a stronger treatment than §8.15 asks
for. No edit was made — recording a compliant surface as a defect would be the
same error as fixing it. This block is the reference implementation that the
account and game-statistics empty states were rewritten to match.

It still must not gain a "create folder" or "upload" button, which would be new
functionality (§1.2).

## `SIDEBAR_WIDTH = 180.dp` — off-scale, deliberately

`KroxSpacing` tops out at `max = 64.dp`, and no spacing token covers 180. The
sidebar width is a **container dimension**, not a spacing step, so it sits
outside the scale by design. §11.2 forbids hiding existing navigation, so the
sidebar may never collapse out of the layout at any breakpoint.

Responsive Phase 5 work may adjust this **value** (that is presentation) but
must never remove the sidebar from the composition.

## §19.4 position

| Guarantee | Status |
|---|---|
| Not touching file operations | **Held** — `isEditableTextFile` (`:136`), `getFileIcon` (`:1687`), and every callback passed to `PropertiesDialog` are untouched. Edits are surface, padding, divider, and selection-state only. |
| Not touching config schema | **Held** — no `AllSettings` entry added, removed, renamed, or re-typed. |
| Not adding a file feature | **Held** — §1.3. The empty-folder branch was verified and left untouched; no presentation was added to it, and no new destination or action was introduced. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | `BuiltInFileManagerScreen` composition, all 21 private declarations' structure and behaviour, the six motion specs at `:118-128`, the file-operation callbacks, `SIDEBAR_WIDTH` as a non-collapsible container. |
| **REDESIGN** | All presentation: file-row surfaces, spacing, dividers, selection state, sidebar section chrome, `PropertiesDialog` dialog surface (§8.11). |
| **FIX** | None. The previously-recorded empty-folder defect was re-verified and did not exist; see above. |

## Notes

- `getFileIcon` (`:1687`) is a **resolver**, not a control. REDESIGN may change
  the icon set it returns; it may not change the extension→icon mapping's
  meaning, because that mapping is what tells users what a file is.
- `formatBytesShort` (`:1601`) and `formatFileDate` (`:1608`) are formatting
  helpers. Presentation-neutral — leave them alone.
- `EDITABLE_EXTENSIONS` (`:131`) drives which files the editor opens. Adding an
  extension is a **feature** under §1.3, not a redesign. `KEEP` as-is.
- `PropertiesDialog` is a §8.11 dialog. §8.11 governs its surface, scrim
  (`rgba(0,0,0,0.55)`), and dismissal — not its contents.
- All edits to this file are unverified by a compiler until R-01 clears — see
  the verification-status section in `docs/REGRESSIONS.md`.
