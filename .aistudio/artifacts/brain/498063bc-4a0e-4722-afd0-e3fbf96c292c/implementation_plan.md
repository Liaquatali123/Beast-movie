# Implementation Plan - Pure API Data & Flexible Playback Validation

## Proposed Changes

### 1. Data Validation Service (`PlaybackValidationService.kt`)
- Modify `validateDuration` to relax strict 10-minute blocking as requested by user ("Allow short clips if requested").
- Keep keyword filtering for obvious error logging but allow short videos/trailers if the user explicitly chooses to play them.

### 2. Repository & Data Layer (`MovieRepository.kt` & `MediaModels.kt`)
- Audit all repositories and data sources for any hardcoded fallback media items or static mock lists.
- Ensure all screens (Home, Explore, Search, Details, MyList) display exclusively data returned by the live API endpoints (`/wefeed-h5api-bff/home`, `/wefeed-h5api-bff/subject/search`, `/wefeed-h5api-bff/subject/filter`, etc.).

### 3. Video Player Screen (`VideoPlayerScreen.kt`)
- Ensure smooth playback initialization with authorized `/subject/play` streams without arbitrary duration errors.
- Display status and progress cleanly.

## Verification Plan
- Run unit tests: `gradle :app:testDebugUnitTest`
- Compile applet: `compile_applet`
