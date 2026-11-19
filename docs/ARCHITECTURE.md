# NEXUS Terminal Architecture

## Initial layers

- **Android UI (Kotlin):** application lifecycle and terminal-style interface.
- **Terminal engine (planned):** session management, process lifecycle, input/output streams, resize handling, and terminal escape-sequence parsing.
- **Native layer (C++):** future performance-sensitive components. The current C++ file is a placeholder.
- **Environment integration (planned):** optional Linux userspace distribution managed inside app-private storage.

## Android security boundaries

Android apps run inside an application sandbox. NEXUS must not claim root access or unrestricted Linux compatibility on ordinary, non-rooted devices.

Any future command runner must:
- Run with the app's own Android UID and permissions.
- Avoid silently invoking privileged operations.
- Keep process input/output and cancellation under app control.
- Request Android storage access through supported APIs.
- Explain clearly when a command or feature is unavailable.

## Current limitations

The starter UI does not execute commands, create terminal sessions, install packages, or provide a Linux distribution. The C++ scaffold is not yet connected to the Android app through JNI or Gradle's CMake integration.
