# Build
Upload `www`, `android-overrides`, `package.json`, `capacitor.config.json`, `patch_manifest.py`, `BUILD-WORKFLOW.yml` and docs to a private repo; add `.github/workflows/build-apk.yml` (copy of BUILD-WORKFLOW.yml). Actions builds `app-debug.apk` (artifact My-Workouts-V3-APK). App ID `com.myworkout.v3` differs from V2. No secrets are stored in the repo.

## Updating the native Android code on GitHub
1. Open your repository on github.com and click the folder `android-overrides`.
2. Click Add file, then Upload files. Drag in `GifFolderPlugin.java` from the new zip (same file name replaces the old one).
3. Click Commit changes.
4. Also upload the new `www` folder contents the same way (open `www`, Add file, Upload files, drag `index.html`), plus `package.json` and `BUILD-WORKFLOW.yml` at the top level. Update `.github/workflows/build-apk.yml` by opening it, clicking the pencil, and pasting the new workflow text.
5. The Actions tab starts a build. Wait for the green tick, download My-Workouts-V3-APK and install it over the old V3.
6. On the phone: open Workout Library, tap Choose a folder, pick your folder, then switch on 'Allow access to manage all files' when asked and tap Sync all folders now.
