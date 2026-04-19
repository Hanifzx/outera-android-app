# Design System: Outera Camping Rental UI

## Overview
**Creative North Star:** "The Modern Scout"
This design system represents a fusion of rugged, field-tested reliability and the sophisticated precision of a premium lifestyle magazine. It rejects standard grid boundaries in favor of **Atmospheric Layering**, utilizing intentional asymmetry, expansive whitespace, and a high-contrast typographic scale.

## Typography
**Font Family:** Inter

The typography hierarchy uses a "Top-Heavy" approach, creating an authoritative editorial voice:
*   **Headline / Display:** Bold weights, tight letter-spacing (-0.02em)
*   **Body:** 1rem (`body-lg`) for clean legibility
*   **Labels:** All-caps, increased tracking (+0.05em) for a technical "spec-sheet" feel

## Color Palette
The color strategy is rooted in a "High-Contrast Naturalist" palette, featuring a dominant white base punctuated by a deep, authoritative Army Green.

### Primary Colors
*   **Primary:** `#343c0a`
*   **Primary Container:** `#4b5320` (Main Call-to-Action Color)
*   **On Primary:** `#ffffff`

### Secondary Colors
*   **Secondary:** `#5d6048`
*   **Secondary Container:** `#e2e5c5`

### Surface & Background
*   **Surface / Background:** `#f9f9f9` (Global base)
*   **Surface Container Low:** `#f3f3f3` (Section modules)
*   **Surface Container:** `#eeeeee`
*   **Surface Container Lowest:** `#ffffff` (Cards / Interactive elements)
*   **On Surface:** `#1a1c1c` (Primary text color - never use pure black `#000000`)

### Outline & Borders
*   **Outline:** `#77786b`
*   **Outline Variant:** `#c8c7b8`

## Design Principles

### 1. The "No-Line" Rule
1px solid borders are prohibited for sectioning. Boundaries must be defined solely through background color shifts or tonal transitions (e.g., `#f9f9f9` to `#f3f3f3`).

### 2. Tonal Layering (Elevation)
Do not use traditional harsh black drop shadows. Use a `surface-container-lowest` (#ffffff) card on a `surface-container-low` (#f3f3f3) background for natural lift. For ambient shadows, use an extra-diffused shadow tinted with the `on-surface` color.

### 3. Glass & Gradient
Main CTAs and hero headers should utilize a subtle directional gradient (transitioning from `#343c0a` to `#4b5320`). Use backdrop-blur for floating navigation over images.

### 4. Components
*   **Buttons:** 8dp rounded corners.
*   **Cards:** 16dp internal padding, no divider lines, 12dp whitespace between image and title.
*   **Inputs:** Flat, filled inputs (`#e8e8e8`) with a 2px inner-glow/underline on focus (`#343c0a`).
