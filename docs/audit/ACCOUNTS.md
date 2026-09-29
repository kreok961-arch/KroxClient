# Accounts Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

`ui/screens/content/AccountManageScreen.kt`, **1159 lines**, is the largest
settings-adjacent screen. It is **KEEP** in structure and **REDESIGN** in
presentation. §19.4 forbids touching authentication — this file is a rendering
surface over an existing auth system, and nothing here may change what an
account *does*.

## Declarations

| Symbol | Line | Visibility | Role |
|---|---|---|---|
| `AccountManageScreen(` | 191 | public | Screen root, `BaseScreen`-wrapped |
| `AccountManageContent(` | 270 | private | Body: list + operation overlays |
| `AccountCard(` | 573 | private | One account row |
| `getAccountTypeName(context, account)` | 702 | private | Type label resolver |
| `LoginMenuOperation(` | 707 | private | Login-type menu overlay |
| `MicrosoftLoginOperation(` | 760 | private | **Auth** — Microsoft flow |
| `LocalLoginOperation(` | 799 | private | **Auth** — offline/local flow |
| `OtherLoginOperation(` | 879 | private | **Auth** — third-party flow |
| `ServerTypeOperation(` | 953 | private | Server-type selection overlay |
| `AccountSkinOperation(` | 1028 | private | Skin/cape **management** overlay |
| `AccountOperation(` | 1104 | private | Delete/rename/edit overlay |
| `AccountManageContentPreview()` | 1136 | private | `@Preview` only |

## §9.7 Accounts — the three prohibitions

§9.7 is the spec's Accounts section. It is unusually restrictive, and the three
prohibitions are load-bearing:

### 1. No new authentication

`MicrosoftLoginOperation` (`:760`), `LocalLoginOperation` (`:799`), and
`OtherLoginOperation` (`:879`) render **existing** OAuth/local flows. §1.2 forbids
"new auth, new accounts, new profile system." The redesign may restyle these
overlays — surface, divider, button, spacing — and may not add, remove, reorder,
or re-wire a single auth path.

### 2. No 3D skin/cape rendering

`AccountSkinOperation` (`:1028`) drives `AccountSkinDialogState` and
`accountCapes`. The redesign **must not** add a 3D model viewer, a rotating
preview, or any real-time skin render. The cape gallery is a browser destination
(`navigateToCapeGallery`, `checkIfInWebScreen`) and stays one.

### 3. `AccountManageContentPreview()` is not a renderer

`AccountManageContentPreview()` at `:1136` is annotated
`@Preview(showBackground = true, widthDp = 800, heightDp = 480)`. It is a
**Compose layout composable for the account card's content area** — a
`CompositionLocalProvider` / `MaterialExpressiveTheme` / `Surface` wrapper around
`AccountManageContent` with no-op `AccountActions`. It exists to render a preview
in Android Studio, nothing more.

**It must not be turned into a 3D skin/cape renderer.** The name is a trap: it
reads like a visual renderer and is not one. Any Phase 5 work that "improves" this
composable by adding a live model view would violate §9.7 prohibition 2 and §1.2.

## States

| State | Host | Verdict |
|---|---|---|
| List (accounts present) | `AccountManageContent` list branch, each row = `AccountCard` (`:573`) | **KEEP** structure |
| Empty (no accounts) | the `else` branch of the content list — was a `Box(fillMaxSize)` centring a single `ScalingLabel`; **now a centred `Column` with icon + title + description, bounded by `widthIn`** | **REDESIGN — done in Phase 6.** Full §8.15 treatment applied to the existing surface. |
| Loading | No dedicated host. The account list is driven by `profileUiState`; loading is implied by absence. | **Not present in code** — no new loading surface is added (§1.3) |
| Error | Routed through `formatError` / `submitError` into the shared error channel, not a local state | **KEEP** shared channel |

### The empty state — was the redesign's real opportunity here

The branch was four lines: a bare `ScalingLabel` with no icon, no supporting
copy, and no action. It was the most visible §8.15 gap in the app.

**Phase 6 applied the treatment** (`docs/REGRESSIONS.md` R-36). The branch is
now a `Box(fillMaxSize, contentAlignment = Center)` wrapping a centred `Column`
with a 48 dp `ic_person_outlined` at 0.35 alpha, the existing
`account_no_account` string as a `titleSmall` title, and a new
`account_no_account_desc` as `bodySmall` at 0.6 alpha — the same three-part
shape the file manager's already-compliant empty-folder branch uses.

**R-38 then bounded the description column** with
`Modifier.widthIn(max = 320.dp)`, so on a narrow width the copy wraps instead of
running edge to edge — see `docs/audit/RESPONSIVE.md`. The `8.dp` gap between
the three parts stayed a literal: this file has zero `KroxSpacing` occurrences
and no `ui.theme` import, so introducing one token here would make it the only
such reference in the file.

**No action button was added, and none should be.** §8.15 permits an action
"only if an existing action is relevant", and a relevant one already exists: the
"Add Account" `ScalingActionButton` in the left panel, dispatching
`UpdateMicrosoftLoginOp(MicrosoftLoginOperation.Tip)` or
`UpdateLoginMenuOp(LoginMenuOperation.Login)`. Duplicating it inside the empty
state would put the same login path on screen twice. §9.7 prohibition 1 and §1.2
would also be at risk if a *new* account path were introduced instead.

## Token adoption

`grep -c KroxSpacing AccountManageScreen.kt` → **0**. The file uses literal `dp`
throughout, consistent with `docs/spec/tokens.md` §3 (no mass rewrite; rewritten
code adopts the scale). Phase 5 edits to this file adopt `KroxSpacing`; untouched
regions keep their literals.

## §19.4 position

| Guarantee | Status |
|---|---|
| Not touching authentication | **Held** — `MicrosoftLoginOperation` / `LocalLoginOperation` / `OtherLoginOperation` bodies are untouched; edits are visual-state only (divider token, `DisabledAlpha` at `ui/screens/content/elements/CommonElements.kt:113`). |
| Not touching config schema | **Held** — no `AllSettings` entry added, removed, renamed, or re-typed. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | All 12 declarations' **structure and behaviour**; the 4 operation overlays' flow; `AccountCard`; the shared error channel. |
| **REDESIGN** | All presentation — card surfaces, spacing, dividers, the empty state, overlay chrome, disabled/selected states. **The empty state is done** (Phase 6, R-36, bounded R-38); the rest remains. |
| **FIX** | **None outstanding.** The §8.15 empty state was the only substantive gap and it was closed in Phase 6 (R-36, bounded R-38). |

## Notes

- `getAccountTypeName` (`:702`) is a `@Composable` that resolves a display string.
  It is a label resolver, not a control — REDESIGN covers the label's typography,
  not the resolver.
- The `@Preview` composable at `:1136` ships in release builds. Removing it is a
  legitimate cleanup (it is dev-only surface), but it is **not** part of the
  redesign and should not ride along with a visual commit.
- All edits to this file are unverified by a compiler until R-01 clears — see the
  verification-status section in `docs/REGRESSIONS.md`.
