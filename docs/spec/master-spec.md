<command-message>plan</command-message>
<command-name>/plan</command-name>
<command-args># KROX LAUNCHER — ULTIMATE MASTER PROMPT + PRD + SYSTEM ARCHITECTURE BLUEPRINT + SCREEN-BY-SCREEN UI/UX SPECIFICATION + DESIGN SYSTEM + RESPONSIVE LAYOUT PLAN + IMPLEMENTATION ROADMAP + MIGRATION STRATEGY + REGRESSION STRATEGY + QA MATRIX + DEFINITION OF DONE

## UI/UX REDESIGN ONLY · PRESERVE ALL FUNCTIONALITY · PRESERVE LAUNCH ENGINE · PREMIUM KROX VISUAL IDENTITY · FINAL FROZEN EDITION

---

## COVER PAGE

| Field | Value |
|---|---|
| **Document Class** | Master Prompt + PRD + System Architecture Blueprint + Screen-by-Screen UI/UX Specification + Design System + Responsive Layout Plan + Implementation Plan + Migration Plan + Regression Strategy + QA Matrix + Definition of Done |
| **Target** | **KROX Launcher** — an EXISTING launcher derived from **ZalithLauncher2Plus** |
| **Scope** | **UI/UX redesign and visual polish ONLY** |
| **Explicitly Excluded** | New features · New functionality · Player preview · 3D skin rendering · Cosmetics preview · New authentication · New launch engine · New backend · New downloads · New diagnostics · New repair · New profile systems · New gameplay features |
| **Preserved** | Every existing launcher feature, launch engine, account system, Java management, Fabric handling, file manager, logs, statistics, changelog, settings, About, navigation logic, configuration, launch arguments, libraries, natives, game directories |
| **Platform** | Android · mobile-first (inherited from ZalithLauncher2Plus lineage) · desktop-compatible |
| **Visual Identity** | KROX premium dark theme (see Part 3) |
| **Delivery** | Incremental compile-verify-continue · never claim success without verification |
| **Status** | **FROZEN — Execute exactly as written** |

**One-line summary:**
> Take the existing ZalithLauncher2Plus-derived KROX Launcher, keep every functional behavior, every navigation destination, every launch-engine path, and every existing screen — and completely redesign its visual system, layout, typography, iconography, spacing, animations, responsiveness, and overall polish into a premium KROX-branded launcher that looks brand new but behaves exactly like the existing one.

---

## PART 0 — EXECUTIVE SUMMARY

The KROX Launcher is an existing launcher built on the ZalithLauncher2Plus codebase. It currently works. It launches Minecraft, manages accounts, manages Java, supports Fabric, exposes logs, statistics, changelogs, a file manager, settings, and an About page.

The task is **not** to add anything. The task is **not** to replace anything. The task is **not** to rewrite the launch engine. The task is **not** to introduce player previews, cosmetics previews, or any cosmetic/visual feature of the game itself.

The task is to **completely redesign the visual system** of the existing launcher — its layout, its navigation, its typography, its icons, its spacing, its panels, its buttons, its hover states, its animations, its transitions, its responsiveness — so that the resulting launcher looks like a premium KROX product while remaining functionally identical to the existing launcher.

**The final result must satisfy one sentence:**

> Same functionality. Same launcher. Same purpose. Completely new premium visual experience.

---

## PART 1 — ABSOLUTE SCOPE RULE

This is the most important part of the document. Every other part is subordinate to it.

### 1.1 Allowed changes

The following changes are **within scope**:

- UI layout (composition, positioning, alignment)
- Spacing (padding, margin, gutter, rhythm)
- Typography (font family, weight, size, tracking, hierarchy)
- Icons (system, weight, style, dimensions)
- Colors (palette application, semantic coloring)
- Panels (backgrounds, borders, radius, shadows)
- Cards (containers, styling, states)
- Navigation appearance (sidebar, tabs, breadcrumbs)
- Page composition (grouping, hierarchy, visual flow)
- Button styling (primary, secondary, danger, ghost)
- Hover states
- Selected states
- Focus states
- Disabled states
- Loading states (presentation only)
- Empty states (presentation only)
- Error states (presentation only)
- Animations (visual transitions)
- Transitions (page change, panel expand, hover)
- Responsive layout (rearranging existing content)
- Visual hierarchy (size, weight, spacing, color)
- Visual consistency (across all screens)
- Polish (micro-interactions, refinement)

### 1.2 Forbidden additions

The following are **strictly out of scope** and must never be added:

- Player preview (3D character, skin viewer, cape viewer, rotation)
- Cosmetics preview or cosmetics system
- New account functionality (new auth flow, new account types)
- New authentication system
- New profile system
- New launcher features
- New download system
- New repair system
- New diagnostics engine
- New performance engine
- New Minecraft management functionality
- New Java functionality
- New file-management functionality (beyond what exists)
- New statistics functionality
- New gameplay functionality
- New launcher APIs
- New backend
- New online service
- New features merely because they exist in another launcher
- Any feature not already present in the current KROX Launcher

### 1.3 The one-line rule

**If the current launcher does not already have it, the redesign must not add it.**

**If the current launcher already has it, the redesign must not remove it.**

**If the current launcher already has it, the redesign may only change how it looks, not what it does.**

---

## PART 2 — DOCTRINE (READ BEFORE WRITING ANY CODE)

### 2.1 The fundamental rule

**This is a UI/UX redesign and visual polish project. It is not a feature project. It is not a rebuild. It is not a migration.**

The launcher is the same launcher. Only its appearance changes.

### 2.2 The preservation contract

Every existing launcher behavior must be preserved:

- Every launch completes exactly as before
- Every account works exactly as before
- Every Java runtime selection works exactly as before
- Every Minecraft version loads exactly as before
- Every Fabric path resolves exactly as before
- Every log is captured exactly as before
- Every statistic is calculated exactly as before
- Every changelog entry is displayed exactly as before
- Every file-manager operation works exactly as before
- Every setting toggles exactly as before
- Every About-page entry is displayed exactly as before
- Every navigation destination resolves exactly as before

### 2.3 The engine-touch rule

The launch engine — the code that actually spawns Minecraft — is **not** part of this redesign.

Do not rewrite:
- Java launch system
- Classpath construction
- JVM arguments
- Game arguments
- Native library handling
- Fabric launch logic
- Authentication logic
- Process creation
- Game-directory logic
- Library resolution

**Exception:** if a pre-existing bug in that exact subsystem must be fixed for the UI to be usable, fix only that specific bug, with a dedicated ADR, minimal scope, and a regression test.

### 2.4 The user-recognition rule

A user who already uses the current KROX Launcher must immediately recognize the redesigned launcher. The screens must have the same **names**, the same **destinations**, the same **conceptual content**, the same **functional behavior** — just presented with a completely new visual language.

---

## PART 3 — KROX VISUAL IDENTITY

### 3.1 Palette

| Token | Value | Use |
|---|---|---|
| Background | `#08080A` | App background |
| Surface | `#121216` | Panels, cards |
| Elevated | `#24242C` | Elevated containers, dropdowns |
| Accent | `#DC2626` | Primary action, active state, focus ring |
| Success | `#10B981` | Positive status, Play button accent |
| Warning | `#EAB308` | Caution status |
| Danger | `#EF4444` | Destructive action, error state |
| Disabled | `#374151` | Disabled controls |
| Text Primary | `#F9FAFB` | Main text |
| Text Secondary | `#9CA3AF` | Secondary text |
| Text Muted | `#6B7280` | Metadata |
| Border Hairline | `rgba(255,255,255,0.06)` | Default border |
| Border Accent | `rgba(220,38,38,0.6)` | Selected border |
| Overlay | `rgba(0,0,0,0.55)` | Modal scrim, dialog backdrop |

### 3.2 Design characteristics

The KROX visual language is:

- **Premium** — refined materials, considered spacing, restrained motion
- **Dark** — deep backgrounds, high contrast, no washed-out grays
- **Clean** — minimal ornamentation, generous whitespace
- **Minimal** — no gratuitous decoration
- **Technical** — precise alignment, consistent rhythm
- **Cinematic** — subtle depth, considered lighting on surfaces
- **Refined** — no visual noise, no clutter
- **Modern** — contemporary spacing and typography conventions

### 3.3 What to avoid

Do **not** produce:

- AI-dashboard appearance (generic card grid with random gradients)
- Excessive gradients (subtle is fine, layered is not)
- Unnecessary neon
- Giant empty cards
- Overuse of glassmorphism
- Random visual effects (particles, glows, sparkles)
- Excessive shadows (one shadow level, not stacked)
- Clutter (too many simultaneous visual elements)
- Overly saturated colors
- Tiny unreadable typography
- Inconsistent radii across the app

### 3.4 Glass / surface treatment

Preferred surface treatment:

- Dark surface (`#121216`)
- Subtle transparency when stacked (`rgba(18,18,22,0.85)`)
- Thin hairline border (`rgba(255,255,255,0.06)`)
- Optional subtle blur only when a panel overlays content
- Never heavy glass over the entire UI

---

## PART 4 — AUDIT-FIRST MANDATE

### 4.1 The rule

Before making any UI change, the redesign agent must produce a complete audit of the current KROX Launcher.

### 4.2 Required audit outputs

- `docs/audit/SCREENS.md` — every current screen, its purpose, its content
- `docs/audit/NAVIGATION.md` — current navigation model, destinations, links
- `docs/audit/COMPONENTS.md` — every reusable UI component, its current API
- `docs/audit/LAYOUTS.md` — current page layouts, dimensions, breakpoints
- `docs/audit/SIDEBAR.md` — current sidebar / top-bar model
- `docs/audit/HOME.md` — current Home composition
- `docs/audit/ACCOUNTS.md` — current Accounts page
- `docs/audit/SETTINGS.md` — every setting and its control
- `docs/audit/FILE_MANAGER.md` — current file-manager toolbar, rows, paths
- `docs/audit/LOGS.md` — current logs view
- `docs/audit/STATISTICS.md` — current statistics view and data sources
- `docs/audit/CHANGELOG.md` — current changelog view
- `docs/audit/ABOUT.md` — current About page
- `docs/audit/THEME.md` — current color, typography, icon usage
- `docs/audit/RESPONSIVE.md` — current responsive behavior
- `docs/audit/ANIMATIONS.md` — current animation usage
- `docs/audit/RESOURCES.md` — fonts, icons, textures, drawables
- `docs/audit/DEPENDENCIES.md` — UI libraries already in use
- `docs/audit/RISKS.md` — any risk of breaking functionality during redesign

### 4.3 Classification

Every subsystem is classified:

| Class | Meaning | Action |
|---|---|---|
| **KEEP** | Existing functionality and navigation | Do not touch |
| **REDESIGN** | Visual/layout systems | Redesign |
| **FIX** | UI bug preventing existing functionality | Fix minimally |

### 4.4 The rule against scope creep

**The audit is not a reason to rewrite the launcher.** It is a map. It exists so the redesign can proceed without breaking anything.

---

## PART 5 — INFORMATION ARCHITECTURE

### 5.1 Navigation destinations (unchanged)

The launcher exposes these existing destinations. **Do not add any.**

- Home
- Statistics
- Changelog
- Logs
- File Manager
- Accounts
- Settings
- About

Only destinations that already exist in the current launcher may appear. If the current launcher uses different names for some of these, the redesign keeps those names.

### 5.2 Hierarchy

```
KROX Launcher
├── Home              ← primary launch screen
├── Statistics        ← usage stats
├── Changelog         ← version history
├── Logs              ← runtime logs
├── File Manager      ← instance folders
├── Accounts          ← account management
├── Settings          ← launcher configuration
└── About             ← version, credits
```

### 5.3 Rule

The hierarchy is **preserved**. Only the visual representation changes.

---

## PART 6 — MAIN NAVIGATION REDESIGN

### 6.1 Left vertical sidebar

The primary navigation is a clean left vertical sidebar. It presents the existing destinations.

### 6.2 Sidebar specification

- **Width:** ~220 dp expanded, ~72 dp collapsed (if the current launcher supports collapse — if not, do not add collapse)
- **Background:** `#0C0C0F` (slightly darker than surface)
- **Border-right:** hairline `rgba(255,255,255,0.06)`
- **Padding:** 12 dp vertical, 12 dp horizontal
- **Item height:** 44 dp
- **Item spacing:** 4 dp
- **Item radius:** 8 dp

### 6.3 Sidebar item anatomy

Each item contains:

- Icon (20 dp, consistent stroke weight)
- Label (13 sp, medium weight, single line)
- Padding: 12 dp horizontal
- Gap between icon and label: 12 dp

### 6.4 Sidebar item states

| State | Background | Text | Icon | Left bar |
|---|---|---|---|---|
| Default | Transparent | `#9CA3AF` | `#9CA3AF` | none |
| Hover | `rgba(255,255,255,0.04)` | `#F9FAFB` | `#F9FAFB` | none |
| Selected | `rgba(220,38,38,0.10)` | `#F9FAFB` | `#DC2626` | 2 dp accent left bar |
| Focus | `rgba(220,38,38,0.15)` | `#F9FAFB` | `#DC2626` | none |
| Disabled | Transparent | `#6B7280` | `#6B7280` | none |

### 6.5 Transitions

- Background: 120 ms ease-out
- Text color: 120 ms ease-out
- Icon color: 120 ms ease-out
- Left bar: 140 ms ease-out

### 6.6 Section grouping (only if current launcher groups)

If the current launcher groups navigation items into sections (e.g., "Main", "Advanced"), the redesign may present those groups with small section labels. Section labels are 10 sp uppercase tracked, `#6B7280`, spacing 16 dp above the first item.

**Do not invent sections.** Only group items that are already conceptually grouped in the current launcher.

---

## PART 7 — DESIGN SYSTEM

### 7.1 Spacing scale

Use a fixed scale everywhere:

| Token | Value |
|---|---|
| `space-0` | 0 |
| `space-1` | 4 |
| `space-2` | 8 |
| `space-3` | 12 |
| `space-4` | 16 |
| `space-5` | 20 |
| `space-6` | 24 |
| `space-7` | 32 |
| `space-8` | 40 |
| `space-9` | 48 |
| `space-10` | 64 |

**Rule:** every margin, padding, and gap value is one of these tokens. No arbitrary values.

### 7.2 Radius scale

| Token | Value | Use |
|---|---|---|
| `radius-xs` | 4 | Chips, badges |
| `radius-sm` | 6 | Buttons, inputs |
| `radius-md` | 8 | Cards, sidebar items |
| `radius-lg` | 12 | Panels, modals |
| `radius-xl` | 16 | Large containers |
| `radius-full` | 999 | Pills, avatars |

### 7.3 Elevation

Only two elevation levels:

| Level | Shadow |
|---|---|
| Base | none |
| Raised | `0 1px 2px rgba(0,0,0,0.4), 0 4px 12px rgba(0,0,0,0.2)` |

**Do not stack shadows.** Do not invent additional levels.

### 7.4 Typography scale

| Token | Size | Weight | Line height | Use |
|---|---|---|---|---|
| `type-display` | 28 sp | 600 | 36 | Page hero title |
| `type-h1` | 22 sp | 600 | 30 | Page title |
| `type-h2` | 16 sp | 600 | 24 | Section title |
| `type-h3` | 14 sp | 600 | 20 | Subsection |
| `type-body` | 13 sp | 400 | 20 | Primary text |
| `type-body-strong` | 13 sp | 600 | 20 | Emphasized body |
| `type-caption` | 12 sp | 400 | 16 | Secondary text |
| `type-meta` | 11 sp | 500 | 14 | Metadata |
| `type-overline` | 10 sp | 600 | 14 | Section labels (uppercase, tracked) |
| `type-mono` | 12 sp | 400 | 18 | Log lines, paths |

**One family everywhere** (see Part 7.5). Fallback chain required.

### 7.5 Font family

- **Primary:** a modern geometric sans (Inter, Manrope, or Söhne-equivalent)
- **Monospace:** a modern monospace (JetBrains Mono, IBM Plex Mono, or equivalent) — only for logs, paths, and technical data
- **Fallback:** system UI fonts, then platform default

Never mix more than two families.

### 7.6 Motion tokens

| Token | Duration | Easing |
|---|---|---|
| `motion-instant` | 80 ms | linear |
| `motion-fast` | 120 ms | ease-out |
| `motion-base` | 180 ms | ease-out |
| `motion-slow` | 240 ms | ease-out |
| `motion-page` | 220 ms | cubic-bezier(0.2, 0, 0, 1) |

**Rules:**
- No animation longer than 240 ms
- No bounce, no elastic, no spring
- All durations reduce to 0 ms when reduced-motion is active

### 7.7 Border tokens

- **Hairline:** `1 px` at `rgba(255,255,255,0.06)`
- **Divider:** `1 px` at `rgba(255,255,255,0.04)`
- **Accent border:** `1 px` at `rgba(220,38,38,0.6)`

### 7.8 Icon system

- **Size:** 16 dp (inline), 20 dp (navigation, buttons), 24 dp (page header)
- **Stroke:** 1.5 dp uniform, or a matched solid system
- **Style:** consistent across all pages (all outline OR all solid, never mixed)
- **Color:** inherits text color except when explicitly accent
- **Library:** single icon family, not multiple

---

## PART 8 — COMPONENT LIBRARY

Every screen is built from these components. No bespoke controls.

### 8.1 Button

- Variants: `primary` (accent), `secondary` (elevated surface), `ghost` (transparent), `danger` (red)
- Sizes: `sm` (28 dp), `md` (36 dp), `lg` (44 dp), `xl` (56 dp, for Play)
- States: default, hover, pressed, focused, disabled, loading
- Icon support: leading icon, trailing icon, icon-only
- Radius: `radius-sm`

### 8.2 Sidebar Item

- See Part 6

### 8.3 Tab (if existing)

- Uses same visual tokens as sidebar item but horizontal
- Underline accent for selected

### 8.4 Page Header

- Overline (optional category)
- H1 title
- Optional subtitle
- Optional trailing actions

### 8.5 Card

- Surface `#121216`
- Border hairline
- Radius `radius-md`
- Padding `space-4`
- Optional raised elevation

### 8.6 Setting Row

- Left: label + optional description
- Right: control (toggle, slider, selector, text input, button)
- Height: adaptive, min 48 dp
- Divider between rows

### 8.7 Toggle

- 36 × 20 dp track, 16 dp knob
- Off: `#374151` track
- On: `#10B981` track
- Motion: 140 ms

### 8.8 Slider

- Track 4 dp, thumb 16 dp
- Accent fill for active portion
- Optional value label

### 8.9 Selector / Dropdown

- Closed: input-like surface
- Open: elevated menu `#24242C`, hairline border, radius `radius-md`, elevation raised
- Item height 32 dp

### 8.10 Search Field

- Icon leading, placeholder, clear affordance
- Radius `radius-sm`
- Focus ring accent

### 8.11 Dialog

- Scrim `rgba(0,0,0,0.55)`
- Panel surface `#121216`, radius `radius-lg`, padding `space-6`
- Title, body, actions
- Max width ~480 dp

### 8.12 Toast / Notification (only if existing)

- Surface `#24242C`
- Accent left bar by type (info / success / warning / danger)
- Auto-dismiss 4 s

### 8.13 File Row (File Manager)

- Icon (folder / file type)
- Name (bold if folder)
- Metadata (size, date — only if already present)
- Selected / hover state
- Double-tap or tap to open (existing behavior)

### 8.14 Account Card

- Avatar (if current launcher has one)
- Username
- Account type / status
- Selected indicator (accent border or check)
- Action affordance (existing only)

### 8.15 Empty State

- Centered icon
- Title
- Short description
- Optional action button (only if an existing action is relevant)

### 8.16 Loading State

- Centered spinner or skeleton
- Label
- Never a bare blank area

### 8.17 Error State

- Centered error icon
- Title
- Explanation
- Retry action (only if the underlying system supports retry)

---

## PART 9 — SCREEN-BY-SCREEN SPECIFICATION

### 9.1 Home

**Purpose:** the primary launch screen. Contains the Play button, account summary, version selector, and status information — all existing.

**Layout:**

```
┌─────────────────────────────────────────────────────────────────────┐
│  [Sidebar]  │                    Page: Home                          │
│             │                                                       │
│             │  ┌─────────────────────────────────────────────┐      │
│             │  │  Account summary card                       │      │
│             │  │  [avatar] Username   · Selected            │      │
│             │  └─────────────────────────────────────────────┘      │
│             │                                                       │
│             │  ┌─────────────────────────────────────────────┐      │
│             │  │  Version selector                            │      │
│             │  │  Minecraft 1.21.11 · Fabric 0.19.5          │      │
│             │  └─────────────────────────────────────────────┘      │
│             │                                                       │
│             │                    [   PLAY   ]                       │
│             │                                                       │
│             │  ┌─────────────────────────────────────────────┐      │
│             │  │  Status / launch log preview                 │      │
│             │  └─────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────────────┘
```

**Rules:**

- The Play button is the visual anchor
- No new information is added
- Existing information is regrouped for better hierarchy
- Play button remains wired to the existing launch path

### 9.2 Play Button

**Do not replace the launch behavior.**

Visual spec:

- Size: `xl` (56 dp min height, ~320 dp width, or full-width of its container)
- Typography: `type-h2`, tracked uppercase
- Icon: existing icon if the current launcher uses one
- Radius: `radius-md`
- Background: `#DC2626`
- Text: `#FFFFFF`
- Hover: background lightens ~8%
- Pressed: background darkens ~8%, scale 0.98
- Disabled: `#374151` background, `#6B7280` text
- Launching: spinner replaces icon, label changes to existing launching string
- Motion: 120 ms background, 80 ms scale

### 9.3 Statistics

**Purpose:** existing usage statistics. Only redesign presentation.

**Layout:**

- Page header: overline "Statistics", H1 title, optional subtitle
- Section: summary chips or cards (only if already present)
- Section: charts or data (only if already present)
- Section: tables or lists (only if already present)

**Rules:**

- Do not fabricate new statistics
- Do not introduce new analytics
- Do not change the underlying data pipeline

### 9.4 Changelog

**Purpose:** existing version history.

**Layout:**

- Page header: overline "Changelog", H1 title
- List: one card per version
  - Version number (H2)
  - Date (meta)
  - Sections (Added / Changed / Fixed) with bullets
- Scroll area

**Rules:**

- Do not create a new update system
- Do not add new content
- Do not change how versions are stored

### 9.5 Logs

**Purpose:** existing runtime logs.

**Layout:**

- Page header: overline "Logs", H1 title
- Toolbar (only if existing): search, filter, clear, export
- Log list: monospace rows with severity color
- Severity coloring:
  - INFO — `#9CA3AF`
  - WARN — `#EAB308`
  - ERROR — `#EF4444`
  - DEBUG — `#6B7280`
- Scroll area
- Empty state if no logs
- Search highlight accent

**Rules:**

- Do not build a new diagnostic engine
- Keep the existing log source
- Keep the existing filter/search behavior

### 9.6 File Manager

**Purpose:** existing file-manager for instance folders.

**Paths preserved (only if currently supported):**

- `/mods`
- `/saves`
- `/resourcepacks`
- `/shaderpacks`
- `/logs`

**Layout:**

- Page header: overline "File Manager", H1 title
- Breadcrumb row: existing path chain
- Toolbar: Back · Forward · Refresh · Search · Sort (existing actions only)
- File list:
  - Folder rows
  - File rows with icon, name, metadata
  - Selected state
- Bottom: optional status bar (only if existing)

**Rules:**

- Do not add new filesystem features
- Do not change how files are listed
- Do not change how files are opened

### 9.7 Accounts

**Purpose:** existing account management.

**Layout:**

- Page header: overline "Accounts", H1 title
- Account list:
  - Account cards (avatar, username, type, status)
  - Selected indicator
- Existing actions only:
  - Add / Import / Export (if present)
  - Delete / Logout (if present)
  - Select active account
- Empty state if no accounts

**Rules:**

- Do not add a new authentication system
- Do not change existing auth behavior
- Do not add player preview

### 9.8 Settings

**Purpose:** existing launcher settings.

**Layout:**

- Page header: overline "Settings", H1 title
- Section list (only groups already present):
  - General
  - Appearance (if present)
  - Java (if present)
  - Minecraft (if present)
  - Fabric (if present)
  - File paths (if present)
  - Advanced (if present)
- Setting rows: label + optional description + control
- Dividers between rows

**Rules:**

- Every visible setting must correspond to an existing setting
- Do not invent new settings
- Do not change setting semantics
- Do not change config file format

### 9.9 About

**Purpose:** existing About page.

**Layout:**

- Page header: overline "About", H1 title
- KROX emblem + wordmark
- Version, build, date
- Credits section (existing content only)
- Links (existing links only)
- License (existing text only)

**Rules:**

- Do not add unrelated content
- Do not change version strings
- Do not add social links not already present

---

## PART 10 — COMPONENT USAGE RULES

### 10.1 Every screen uses the design system

No screen is allowed to introduce its own button, card, input, or panel style.

### 10.2 Page composition rules

Every page follows:

```
Page Header (overline, H1, optional subtitle, optional actions)
↓ space-6
Section 1 (title + content)
↓ space-6
Section 2
↓ space-6
...
```

Every section follows:

```
Section Title (H2)
↓ space-3
Section Content
```

### 10.3 Content width

On desktop, the main content area is either:

- **Centered, max-width ~960 dp**, with generous side margins, OR
- **Full-width**, with the same padding (`space-6`) as the page header

Choose one and apply consistently. Do not mix.

### 10.4 Padding and margin rhythm

- Page outer padding: `space-6`
- Section internal padding: `space-4`
- Card internal padding: `space-4`
- Row internal padding: `space-3` vertical, `space-4` horizontal
- Gap between rows: `space-1` or a divider

---

## PART 11 — RESPONSIVE LAYOUT PLAN

### 11.1 Breakpoints

| Breakpoint | Width | Behavior |
|---|---|---|
| Compact | < 600 dp | Sidebar collapses to icon-only bottom nav (only if the current launcher supports mobile layout; otherwise compact sidebar) |
| Medium | 600–900 dp | Sidebar collapsed to icons, main content adjusts |
| Expanded | 900–1200 dp | Sidebar expanded, content max-width ~960 dp |
| Large | > 1200 dp | Sidebar expanded, content still max-width ~960 dp centered |

**Do not invent a new navigation paradigm.** Use only what the current launcher already supports conceptually. If the current launcher already has a mobile layout, match it. If not, do not introduce one — instead adapt the existing layout.

### 11.2 Rules

- Never overlap text and buttons
- Never overflow the container
- Never clip content
- Never break the sidebar into an unfamiliar shape
- Never hide existing navigation on smaller screens (use scroll or collapse, not removal)

### 11.3 Adaptive content

- Lists stack vertically on narrow screens
- Grids collapse columns (2 → 1) on narrow screens
- Cards become full-width on narrow screens
- Toolbars wrap or scroll horizontally, never disappear

### 11.4 Text scaling

Support system font scaling. Do not cap the user's scaling below the system default.

---

## PART 12 — ANIMATION SYSTEM

### 12.1 What animates

Only:

- Page transitions (fade + subtle slide)
- Sidebar hover / selected state
- Button hover / press / focus
- Toggle
- Slider thumb drag
- Dropdown open / close
- Dialog entrance / exit
- Toast entrance / exit
- Panel expand / collapse (if applicable)
- File row hover
- Card hover

### 12.2 What does not animate

- No parallax
- No particle effects
- No shimmer on content
- No continuous background animation
- No bounce / overshoot / elastic
- No long intro sequences

### 12.3 Reduced motion

If the current launcher already has a settings mechanism that can carry a reduced-motion toggle without adding a new setting, use it. **Otherwise do not add a reduced-motion setting.** Instead, respect the platform's accessibility settings where available.

### 12.4 Motion rules

- Durations: only from the motion tokens in Part 7.6
- Easings: only `linear`, `ease-out`, `cubic-bezier(0.2, 0, 0, 1)`
- No animation longer than 240 ms
- No animation on scroll
- No animation that delays user input

---

## PART 13 — ERROR-FIX SCOPE

### 13.1 Allowed fixes

Only UI bugs that prevent existing functionality from being used:

- Dead existing button
- Broken page navigation
- Incorrect layout (overlap, overflow)
- Text clipping
- Responsive sizing failure
- Missing state rendering (blank where content should be)
- Existing UI state bug

### 13.2 Forbidden fixes

Do not use UI redesign as an excuse to:

- Rewrite the launch engine
- Rewrite authentication
- Rewrite the file manager's filesystem operations
- Rewrite logging
- Rewrite statistics calculation
- Rewrite the config system
- Rewrite any non-UI subsystem

### 13.3 Fix protocol

Every UI fix must:

- Be minimal
- Have a dedicated ADR in `docs/adr/`
- Have a regression test
- Not touch any non-UI subsystem

---

## PART 14 — ARCHITECTURE RULES

### 14.1 Separation

Redesigned UI must be separated from:

- Launch command generation
- Java runtime engine
- Authentication
- Fabric handling
- Native loading
- Library management

UI changes must not require modifying any of these — unless the current UI is directly coupled, in which case a **minimal** refactor is permitted with an ADR.

### 14.2 No new dependencies

Do not add new UI dependencies unless:

- The current launcher already lacks a capability required for the redesign
- The dependency is minimal
- The dependency has an ADR
- The dependency does not change functional behavior

### 14.3 No architecture rewrite

Do not replace the launcher's UI framework, navigation library, or state-management approach. Redesign within the existing architecture.

### 14.4 Build safety

Every change is:

1. Incrementally made
2. Compiled
3. Tested
4. Verified
5. Committed

**Never claim a successful build without actually verifying it.** Never assume a change compiles. Never assume a screen renders correctly — verify it.

---

## PART 15 — IMPLEMENTATION PHASES

### PHASE 0 — AUDIT

**Deliverables:** all `docs/audit/*.md` files (see Part 4.2).
**Gate:** all audit docs exist; classification (KEEP / REDESIGN / FIX) complete.

### PHASE 1 — DESIGN SYSTEM

**Deliverables:**
- `docs/spec/tokens.md` — colors, spacing, radius, typography, motion
- `docs/spec/components.md` — component library spec
- `docs/spec/icons.md` — icon system spec
- Design-system code: colors, typography, spacing, radii, motion, elevation primitives

**Gate:** design system compiles; visual harness (dev-only screen) shows every component.

### PHASE 2 — GLOBAL NAVIGATION

**Deliverables:**
- Redesigned sidebar
- Redesigned page shell (page header, layout container)
- Navigation state wiring

**Gate:** every existing destination opens; sidebar states correct.

### PHASE 3 — HOME

**Deliverables:**
- Redesigned Home layout
- Redesigned Play button
- Redesigned account summary card
- Redesigned version selector
- Redesigned status panel

**Gate:** Play button launches exactly as before; existing Home behavior preserved.

### PHASE 4 — ALL EXISTING PAGES

**Deliverables:**
- Statistics redesign
- Changelog redesign
- Logs redesign
- File Manager redesign
- Accounts redesign
- Settings redesign
- About redesign

**Gate:** every page opens; every existing feature works; no new features added.

### PHASE 5 — RESPONSIVE

**Deliverables:**
- Compact, medium, expanded, large layouts

**Gate:** no overlap, no overflow, no clipping at any breakpoint.

### PHASE 6 — POLISH

**Deliverables:**
- Micro-interactions
- Typography tuning
- Icon alignment
- Spacing audit
- Transition tuning
- Hover / focus / disabled state audit

**Gate:** visual consistency across all screens.

### PHASE 7 — QA

**Deliverables:**
- Full QA pass (see Part 16)
- Regression report
- Final visual audit

**Gate:** all QA items pass; no functional regressions.

---

## PART 16 — QA MATRIX

| ID | Test | Pass criteria |
|---|---|---|
| QA-01 | Navigation | Every existing navigation item opens the same destination as before |
| QA-02 | Home | Existing Home behavior unchanged |
| QA-03 | Play | Play launches Minecraft exactly as before |
| QA-04 | Accounts | Existing account operations unchanged |
| QA-05 | Settings | Existing settings still work |
| QA-06 | File Manager | Existing filesystem behavior unchanged |
| QA-07 | Logs | Existing logs remain accessible |
| QA-08 | Statistics | Existing statistics remain accurate |
| QA-09 | Changelog | Existing changelog content remains intact |
| QA-10 | About | Existing About information remains available |
| QA-11 | Responsive | No text / button overlap at any breakpoint |
| QA-12 | Theme | Visual consistency across all screens |
| QA-13 | Regression | No launch-engine regression caused by UI changes |
| QA-14 | No new features | No feature was added that was not present before |
| QA-15 | No player preview | No 3D skin / cape / cosmetic preview was added |
| QA-16 | No new auth | No new authentication flow was added |
| QA-17 | No engine rewrite | Launch engine files show no unrelated changes |
| QA-18 | No backend | No new backend or online service was added |
| QA-19 | Motion | No animation exceeds 240 ms |
| QA-20 | Reduced motion | Platform reduced-motion respected where available |
| QA-21 | Contrast | Text meets minimum contrast ratio against its background |
| QA-22 | Build | Project compiles with no UI-related errors |
| QA-23 | Config | No config schema change was introduced |
| QA-24 | Logs format | No log format change was introduced |
| QA-25 | Statistics source | No statistics source change was introduced |
| QA-26 | File operations | No file-operation behavior change was introduced |
| QA-27 | Account persistence | Selected account persists across restarts as before |
| QA-28 | Java runtime | Existing Java selection works as before |
| QA-29 | Fabric path | Existing Fabric launch works as before |
| QA-30 | Platform variants | Launcher runs on target platforms as before |

---

## PART 17 — DEFINITION OF DONE

The redesign is complete when:

- ✅ UI completely redesigned
- ✅ Current navigation preserved
- ✅ Existing functionality preserved
- ✅ Home redesigned
- ✅ Sidebar redesigned
- ✅ Settings redesigned
- ✅ Accounts redesigned
- ✅ File Manager redesigned
- ✅ Logs redesigned
- ✅ Statistics redesigned
- ✅ Changelog redesigned
- ✅ About redesigned
- ✅ Typography upgraded
- ✅ Icons upgraded
- ✅ Spacing upgraded
- ✅ Animations upgraded
- ✅ Responsive layout improved
- ✅ Design system documented and applied everywhere
- ✅ Component library implemented and used everywhere
- ✅ No new features
- ✅ No player preview
- ✅ No 3D skin rendering
- ✅ No cosmetics preview
- ✅ No new authentication
- ✅ No new launch engine
- ✅ No new backend
- ✅ No unnecessary architecture rewrite
- ✅ No regressions in existing functionality
- ✅ All 30 QA items pass
- ✅ Build verified manually
- ✅ No "success" claim without real verification

---

## PART 18 — MIGRATION STRATEGY

### 18.1 No user-visible migration

Because the redesign changes no functional behavior, no user-visible migration is required. Existing settings, accounts, versions, files, and logs remain exactly where they are.

### 18.2 Config compatibility

The redesign must not change the config schema. If any UI-state field is added (e.g., a stored sidebar collapse state, **only if the current launcher already supported collapse**), the addition must be backward-compatible and version-tolerant.

### 18.3 Save compatibility

No save-file format is touched.

### 18.4 Log compatibility

No log format is touched.

### 18.5 Statistics compatibility

No statistics calculation is touched.

### 18.6 Rollback

If a redesign issue breaks a functional path, revert the specific change, do not patch around it. Log the revert in `docs/REGRESSIONS.md`.

---

## PART 19 — REGRESSION STRATEGY

### 19.1 Before starting any phase

- Verify the current build works
- Verify the current launcher launches
- Verify every existing destination opens
- Capture baseline screenshots

### 19.2 After every phase

- Re-verify every destination opens
- Re-verify Play works
- Re-verify account selection works
- Re-verify settings work
- Re-verify file manager works
- Re-verify logs open
- Re-verify statistics render
- Re-verify changelog renders
- Re-verify About renders

### 19.3 If a regression is detected

1. Identify the change that caused it
2. Revert that change
3. Re-verify
4. Log in `docs/REGRESSIONS.md`
5. Continue from the last known-good state

### 19.4 Regression-free by design

The redesign achieves regression-free status by:

- Not touching launch engine code
- Not touching authentication
- Not touching file operations
- Not touching logging
- Not touching statistics calculation
- Not touching config schema
- Only touching visual and layout layers

---

## PART 20 — FINAL DEEPSEEK INSTRUCTION

> Generate the exhaustive KROX Launcher UI/UX Redesign Master Prompt + PRD + Architecture Specification + Screen-by-Screen Layout Specification + Design System + Responsive Layout Plan + Implementation Plan + Migration Plan + Regression Strategy + QA Matrix.
>
> The project is an existing ZalithLauncher2Plus-derived KROX Launcher. **Do not rebuild the launcher. Do not add functionality.** Preserve every existing launcher feature and the existing launch engine. The only objective is to completely redesign and polish the user interface and layout into a premium KROX-branded launcher.
>
> Redesign the existing Home, navigation/sidebar, Statistics, Changelog, Logs, File Manager, Accounts, Settings, and About screens. Improve typography, spacing, iconography, panels, buttons, card hierarchy, hover states, selected states, animations, transitions, responsiveness, and visual consistency.
>
> Keep existing functional behavior exactly the same. The Play button must still perform the existing launch operation. Existing accounts, Java management, Minecraft versions, Fabric, logs, file management, settings, and other existing functionality must remain untouched unless a UI bug directly requires a minimal correction.
>
> **Do NOT add player preview, 3D skin rendering, cosmetics preview, new account functionality, new launcher features, new diagnostics, new download systems, new repair systems, new backend services, or any other feature not already present.**
>
> This is purely a premium UI/UX redesign and visual-polish project.
>
> The final launcher should look completely redesigned while still behaving exactly like the existing launcher.
>
> **Exact philosophy:**
> *Same functionality. Same launcher. Same purpose. Completely new premium visual experience.*

---

## APPENDIX A — SCREEN INVENTORY (REFERENCE)

| Screen | Purpose | Redesign scope | Feature changes allowed |
|---|---|---|---|
| Home | Launch + status | Full visual redesign | None |
| Statistics | Usage data | Full visual redesign | None |
| Changelog | Version history | Full visual redesign | None |
| Logs | Runtime logs | Full visual redesign | None |
| File Manager | Instance folders | Full visual redesign | None |
| Accounts | Account management | Full visual redesign | None |
| Settings | Launcher config | Full visual redesign | None |
| About | Version + credits | Full visual redesign | None |

---

## APPENDIX B — COMPONENT INVENTORY

| Component | Exists already | Redesign | Add |
|---|---|---|---|
| Button | Yes | Yes | No |
| Sidebar item | Yes | Yes | No |
| Tab | If present | Yes | No |
| Setting row | Yes | Yes | No |
| Toggle | Yes | Yes | No |
| Slider | If present | Yes | No |
| Selector / Dropdown | If present | Yes | No |
| Search field | If present | Yes | No |
| Card | If present | Yes | No |
| Dialog | If present | Yes | No |
| Notification | If present | Yes | No |
| File row | Yes | Yes | No |
| Account card | Yes | Yes | No |
| Page header | Yes | Yes | No |
| Empty / Loading / Error state | If present | Yes | No |

**If a component does not exist in the current launcher, it must not be added.**

---

## APPENDIX C — COLOR TOKENS (FINAL)

```
--bg:              #08080A
--surface:         #121216
--surface-raised:  #24242C
--accent:          #DC2626
--accent-hover:    #E73030
--accent-pressed:  #C11F1F
--success:         #10B981
--warning:         #EAB308
--danger:          #EF4444
--disabled:        #374151
--text-primary:    #F9FAFB
--text-secondary:  #9CA3AF
--text-muted:      #6B7280
--border-hairline: rgba(255,255,255,0.06)
--border-divider:  rgba(255,255,255,0.04)
--border-accent:   rgba(220,38,38,0.60)
--overlay:         rgba(0,0,0,0.55)
```

---

## APPENDIX D — SPACING / RADIUS / MOTION TOKENS (FINAL)

```
space-0:  0
space-1:  4
space-2:  8
space-3:  12
space-4:  16
space-5:  20
space-6:  24
space-7:  32
space-8:  40
space-9:  48
space-10: 64

radius-xs:   4
radius-sm:   6
radius-md:   8
radius-lg:   12
radius-xl:   16
radius-full: 999

motion-instant: 80ms linear
motion-fast:    120ms ease-out
motion-base:    180ms ease-out
motion-slow:    240ms ease-out
motion-page:    220ms cubic-bezier(0.2, 0, 0, 1)
```

---

## APPENDIX E — TYPOGRAPHY TOKENS (FINAL)

```
type-display:     28sp / 600 / 36
type-h1:          22sp / 600 / 30
type-h2:          16sp / 600 / 24
type-h3:          14sp / 600 / 20
type-body:        13sp / 400 / 20
type-body-strong: 13sp / 600 / 20
type-caption:     12sp / 400 / 16
type-meta:        11sp / 500 / 14
type-overline:    10sp / 600 / 14 (uppercase, tracked)
type-mono:        12sp / 400 / 18
```

---

**END OF THE KROX LAUNCHER UI/UX REDESIGN MASTER SPECIFICATION**

Treat **PART 1 (Scope Rule)**, **PART 2 (Doctrine)**, **PART 3 (Visual Identity)**, **PART 4 (Audit-First)**, **PART 7 (Design System)**, **PART 8 (Component Library)**, **PART 9 (Screen-by-Screen)**, **PART 16 (QA Matrix)**, and **PART 17 (Definition of Done)** as **hard constraints**.

The redesign agent's first action is to audit the existing launcher. Its second action is to build the design system. Its third action is to redesign navigation. Its remaining actions are to redesign Home, then every existing page, then responsive, then polish, then QA.

It never adds features. It never adds player preview. It never adds 3D rendering. It never adds cosmetics preview. It never rewrites the launch engine. It never touches authentication. It never changes config schema. It never changes log format. It never changes statistics calculations. It never changes file operations. It verifies every build. It verifies every screen. It ships a launcher that looks completely new but behaves exactly like the existing one.

**Same functionality. Same launcher. Same purpose. Completely new premium visual experience.**</command-args>