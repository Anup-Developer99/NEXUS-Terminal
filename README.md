# NEXUS Terminal

An open-source Android terminal project built from the ground up.

> **Status:** Early development scaffold. This repository contains an Android starter app and a native C++ placeholder. It is not yet a complete terminal emulator and does not currently execute commands.

## Goals
- Terminal-style Android interface and multiple sessions
- Controlled command execution and process lifecycle
- Optional Linux userspace integration
- User-controlled file access
- Developer tools where Android permits them
- Themes, keyboard shortcuts, and accessibility
- Native C/C++ components

## Technology
- Kotlin and Android SDK
- XML layouts
- C++ and CMake for future native components
- Gradle Kotlin DSL

## Build
1. Install Android Studio and a compatible Android SDK.
2. Open this repository as an Android project.
3. Allow Gradle sync to download dependencies.
4. Select the `app` run configuration and build.

Gradle wrapper properties are included. If wrapper scripts are absent from your clone, generate them with an installed Gradle distribution using `gradle wrapper`.

## Current functionality
The starter screen accepts text and displays it in a terminal-like view. The command engine is not implemented yet.

## Roadmap
- [x] Initial Android project structure
- [x] Kotlin activity and starter terminal screen
- [x] Native C++ / CMake placeholder
- [ ] Terminal sessions and process management
- [ ] Command execution and output streaming
- [ ] ANSI escape-sequence rendering
- [ ] Session history and settings
- [ ] Android storage integration
- [ ] Optional Linux userspace
- [ ] Automated tests and release builds

## Security
The app must respect Android's application sandbox, avoid silent privileged operations, and request access through supported Android APIs. See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## License
MIT. See [LICENSE](LICENSE).
