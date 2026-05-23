---
name: SwapIt
colors:
  surface: '#e8fff0'
  surface-dim: '#b8e4cc'
  surface-bright: '#e8fff0'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#d1fee5'
  surface-container: '#ccf8df'
  surface-container-high: '#c6f2da'
  surface-container-highest: '#c1ecd4'
  on-surface: '#002114'
  on-surface-variant: '#404943'
  inverse-surface: '#0e3727'
  inverse-on-surface: '#cffbe2'
  outline: '#707973'
  outline-variant: '#bfc9c1'
  surface-tint: '#2c694e'
  primary: '#0f5238'
  on-primary: '#ffffff'
  primary-container: '#2d6a4f'
  on-primary-container: '#a8e7c5'
  inverse-primary: '#95d4b3'
  secondary: '#2b694d'
  on-secondary: '#ffffff'
  secondary-container: '#b0f1cc'
  on-secondary-container: '#327053'
  tertiary: '#364d3c'
  on-tertiary: '#ffffff'
  tertiary-container: '#4d6553'
  on-tertiary-container: '#c6e1ca'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#b1f0ce'
  primary-fixed-dim: '#95d4b3'
  on-primary-fixed: '#002114'
  on-primary-fixed-variant: '#0e5138'
  secondary-fixed: '#b0f1cc'
  secondary-fixed-dim: '#94d4b1'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#0c5136'
  tertiary-fixed: '#cee9d3'
  tertiary-fixed-dim: '#b3cdb7'
  on-tertiary-fixed: '#092012'
  on-tertiary-fixed-variant: '#354c3b'
  background: '#e8fff0'
  on-background: '#002114'
  surface-variant: '#c1ecd4'
typography:
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  button:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 20px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 8px
  sm: 16px
  md: 24px
  lg: 32px
  xl: 48px
  margin-mobile: 20px
  gutter-mobile: 12px
---

## Brand & Style

The design system is anchored in the concept of "Circularity with Confidence." It targets an eco-conscious, community-driven demographic that values sustainability without sacrificing a premium digital experience. The brand personality is neighborly yet professional, removing the friction and "garage sale" clutter often associated with peer-to-peer exchanges.

The visual direction follows **Modern Minimalism** with a **Soft UI** influence. This means generous whitespace, a focus on high-fidelity product imagery as the primary "color," and a clean, structured layout that prioritizes ease of use. The emotional response should be one of relief and optimism—users should feel that trading items is as sophisticated and safe as buying them new.

## Colors

The palette is a monochromatic exploration of nature-inspired greens, chosen to reinforce the eco-friendly "second life" mission of the product.

- **Primary (#2D6A4F):** A deep, trustworthy forest green used for primary actions, active navigation states, and brand identifiers.
- **Secondary (#95D5B2):** A soft sage green used for secondary buttons, success states, and subtle background highlights.
- **Tertiary (#D8F3DC):** A pale mint used for large surface areas, card backgrounds, and decorative elements to keep the interface feeling airy.
- **Neutral (#1B4332):** An ultra-dark "evergreen" used for typography instead of pure black to maintain a softer, more organic feel.
- **System Grays:** Use a cool-toned gray scale for borders (e.g., #E9ECEF) and disabled states.

## Typography

This design system utilizes **Plus Jakarta Sans** for its modern, rounded apertures that evoke a friendly and approachable character. 

- **Headlines:** Set with tight tracking (-0.02em) and bold weights to create a strong visual anchor for product titles.
- **Body Text:** Uses a slightly more generous line height (1.5x) to ensure readability during long browsing sessions.
- **Labels:** Uppercase with increased letter spacing for small metadata (e.g., distance, item condition) to provide a distinct stylistic contrast to body copy.
- **Scale:** Maintain a strict 4px baseline grid for all typography to ensure vertical rhythm across the mobile viewport.

## Layout & Spacing

The layout is optimized for a mobile-first, single-column experience that transitions into a multi-column grid for discovery feeds.

- **Grid:** Use a 4-column grid for mobile with 20px outside margins.
- **Spacing Rhythm:** All spacing should be multiples of the 4px base unit. Use 16px (sm) for internal card padding and 24px (md) for vertical spacing between distinct content sections.
- **Safe Areas:** Ensure the bottom navigation bar accounts for device-specific home indicators, with a minimum height of 84px to accommodate icons and labels comfortably.
- **Alignment:** Center-align primary CTA buttons at the bottom of the screen (sticky) to ensure they are within the "thumb zone" for easy interaction.

## Elevation & Depth

This design system uses **Tonal Layering** combined with **Ambient Shadows** to create a sense of organized depth.

- **Surface Levels:** The main background is pure white (#FFFFFF). Cards containing product information should use a subtle 1px border (#E9ECEF) or a very soft, diffused shadow (0px 4px 20px rgba(27, 67, 50, 0.05)) to appear slightly elevated.
- **Active States:** When an item is selected or a button is pressed, it should "sink" slightly (reduce shadow) to provide tactile feedback.
- **Overlays:** Modals and bottom sheets should use a 40% opacity blur on the background to maintain context while focusing the user's attention on the swap negotiation.

## Shapes

The shape language is consistently "Soft-Rounded." 

- **Primary Elements:** Buttons and input fields use a 0.5rem (8px) radius to feel approachable but sturdy.
- **Containers:** Product cards and bottom sheets use a 1rem (16px) radius for the top corners, creating a "cradle" effect for content.
- **Product Photos:** Always use a 12px corner radius on images to prevent them from feeling sharp or aggressive within the soft UI.
- **Iconography:** Use "Linear-Rounded" icons with a 2px stroke weight to match the weight of the Plus Jakarta Sans typeface.

## Components

- **Buttons:** Primary buttons are solid Forest Green with white text. Secondary buttons are "Ghost" style with a sage green border. The "Swap" action should be a high-contrast floating action button (FAB) or a fixed-bottom button.
- **Product Cards:** Must feature a 1:1 aspect ratio image, a bold title, and a "Condition Badge" (e.g., "Like New") in the top right corner using the Tertiary green color.
- **Chips:** Used for category filtering (e.g., "Electronics," "Home"). Use a Tertiary green background for unselected states and Primary green with white text for selected states.
- **Bottom Navigation:** A fixed bar featuring four icons: Discover, Swaps (Activity), Add Listing (+), and Profile. The "Add" icon should be visually emphasized (e.g., a circle background).
- **Input Fields:** Use a light gray background (#F8F9FA) instead of a border to minimize visual noise, with a clear focus state that adds a 1px Forest Green border.
- **Trust Badges:** Small, subtle icons next to user names to indicate "Verified Swapper" or "Eco-Contributor" status.