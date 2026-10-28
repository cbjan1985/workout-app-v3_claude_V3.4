# V3 Progress (checkpoint 7)
Base: V2 project + ChatGPT V3 web app + my changes. V2 files are not modified.
Evidence: JavaScript syntax check + headless Chromium browser tests (desktop). No APK built, nothing run on Android. Section 14 (Health Connect/Fastrack), demonstration videos and section 17 (device testing) are still pending.

## Implemented in code
- Branding, Workout Library rename, V3 app ID `com.myworkout.v3`, light UI polish (scrolling tabs, larger touch targets, card styling).
- Workout Library: add/edit/remove with unique IDs, subcategory, instructions, notes, several YouTube URLs (first one is used for playback), duration/rest units (sec/min), search + category filter, copy-to-customise built-ins, start any workout.
- Plan tab: structured builder (category -> workout, sets, reps, per side, duration + unit, rest + unit, notes, optional date), sequence preview with Start, saved daily plan templates (save / apply / delete).
- Calendar: real plan per selected date with planned/completed status.
- Yoga and Cardio: library picker + "Start selected".
- Excel: template, share, import preview/validation, merge/replace, export; unknown exercises are added to the library; weekday rows can be mapped to real calendar dates for N weeks or to plan weeks 1-8; optional routing of Yoga/Cardio rows into those sessions; rest values are applied.
- Timers: per-exercise rest between sets; rep-based exercises show "N reps" instead of a countdown in auto mode. Manual player only shows a timer for timed exercises. Rest between exercises still uses the More-screen setting.
- History: each completed set stores planned sets, reps text and rest; Progress shows planned vs completed %, status filter, date range, weekly bars, per-session notes, .xlsx export, weight and glucose trend charts, health KPIs (only when the tracker is unlocked).
- Health Tracker: encrypted vault, multiple records per day with time, edit/delete, height in cm or ft/in, glucose in mg/dL or mmol/L, minutes since eating, HbA1c, body fat %, muscle mass %, source/device, input range checks, interpretation, trend charts with date filters, configurable inactivity lock (1-30 min or never), manual lock, explicit export.

## Not implemented
- Folder access / workout_gif sync fix, local media preview, YouTube playback integration beyond opening links (section 11, deferred).
- Google Fit / Health Connect / Fastrack (section 14, deferred): Progress shows "Not Connected".
- Biometric unlock for the Health Tracker.
- Demonstration videos.
- Full visual redesign beyond the polish above.
- Automatic migration of V2 data (V3 is a separate app; use V2 Backup -> V3 Restore).

## Checkpoint 6 additions
- Folder access: new native plugin (android-overrides/GifFolderPlugin.java). Finds a folder named workout_gif in any letter case (internal storage, Download, Documents, Pictures, Movies, DCIM), includes sub-folders (2 levels), and adds a native folder picker that keeps read permission and returns the real path so files are read in place (no copying, no app size limit). The Library tab has a "Media folders" card: Choose a folder, Sync all folders, Remove, clear error messages (access off, folder not found, picker cancelled) with retry. The plugin was syntax-checked against stand-in Android classes only; it has NOT been built against the real Android/Capacitor libraries or run on a phone.
- Visual redesign: calm indigo/slate palette taken from the CB logo (light and dark), CB monogram drawn as an SVG, name + "Designed by CB" header.
- Health Tracker: first-use default password 0000 (any other first password is refused), change-password option, 5 wrong tries = 30 s pause. Biometric unlock was replaced by this at the user's request.
- Fastrack: the supplied Health Connect screenshot shows Fastrack Smart is allowed to write active calories, blood pressure, body temperature, distance, exercise, heart rate, heart rate variability and oxygen saturation (list continues below the screenshot; steps and sleep not visible). Google Fit is not allowed access. Integration is NOT built yet.
- Demonstration videos: deferred by the user.

## Checkpoint 7 changes
- Whole app is now plain ASCII text (no typographic symbols), and saved data from older builds is cleaned on load, so text can no longer show as garbled characters. The health password/encryption was NOT the cause of that.
- Checkboxes are small (18 px) and aligned, including "Run only selected items".
- Excel import: only Main Category and Subcategory / Workout Name are required. Blank or invalid optional values are ignored with a warning (not rejected); unknown category becomes Other; rows without a Day use the day chosen in the import screen.
- Health Tracker: password, encryption, lock and auto-lock REMOVED at the user's request. Records are stored as plain text on the device. Earlier protected records can be imported once with the old password (default 0000) or discarded.
- Progress: "Connected health & activity" section removed. Weight, BMI and muscle mass share one trend graph (each line on its own scale). Projected HbA1c (estimate) added; the manual HbA1c field is untouched.
- Projected HbA1c: per reading, (glucose + 46.7) / 28.7; overall estimate = same formula on the average of the fasting, post-meal and random averages over the last 90 days (needs 3 readings). Indicative only.
