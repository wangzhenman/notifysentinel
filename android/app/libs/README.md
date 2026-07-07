Place the official Xiaomi Mi Push Android client JAR here before wiring the app-side SDK.

Expected contents:
- A Mi Push client SDK JAR downloaded from the official Xiaomi Mi Push download page.

Why this folder exists:
- The Android Gradle config already loads any JAR placed in this folder.
- Official public Maven coordinates for the Android client SDK were not verified during integration work, so the repo is prepared for the documented JAR-based setup.