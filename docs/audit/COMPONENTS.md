# Components Audit — KROX Launcher

Generated from codebase analysis on 2026-09-27.

## Overview
The KROX Launcher utilizes a custom component library in `ui.components` that wraps Material3 and provides branded, consistent UI elements.

## Core Component Categories

| Category | Components | Purpose |
|---|---|---|
| **Containers** | `BackgroundCard`, `_FakeShadow` | Standardized elevated surfaces with KROX branding. |
| **Inputs** | `Buttons`, `CheckBoxs`, `InputFields`, `Sliders`, `Switch`, `Radio` | Branded interactive controls. |
| **Navigation** | `_SimpleRail` | Sidebar/rail navigation structure. |
| **Feedback** | `Dialogs`, `_Notification`, `_Warning`, `Shimmer` | User feedback and state indicators. |
| **Typography** | `Texts` | Reusable text components with defined styles. |
| **Specialized** | `ColorPicker`, `VideoPlayer`, `RecordingPlayerOverlay`, `_Markdown`, `_PlayerSkin` | Complex functional components. |

## Key Implementation Patterns
- **Branded M3:** Most components are wrappers or custom implementations of Material3 components to enforce the KROX premium dark theme.
- **State Management:** Many components use standard Compose `State` or `MutableState` for reactivity.
- **Consistency:** `BackgroundCard` appears to be the primary container for grouping related information, enforcing uniform padding, border radius, and surface color.

## Audit Observations
- The component library is extensive and well-structured, facilitating the redesign without needing to create many new primitives.
- Redesign effort should focus on updating `BackgroundCard`, `Buttons`, and `InputFields` styles to match the new visual identity.
