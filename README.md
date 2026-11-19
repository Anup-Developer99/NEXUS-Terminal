# NEXUS Terminal

An open-source Android terminal project built from the ground up.

> **Current version:** 0.2.0-alpha  
> **Status:** Early alpha. Android shell command execution and multiple shell sessions are implemented in the app source. A separate Linux userspace distribution is not yet included.

## Features in 0.2.0-alpha

- Runs commands using Android's `/system/bin/sh`.
- Streams standard output and standard error into the terminal view.
- Supports up to eight independent shell sessions.
- Create, switch between, and close sessions.
- Each session keeps its own shell process and working directory.
- Runs under the app's normal Android UID and sandbox.
- Limits retained terminal output to avoid unbounded memory growth.

Try basic commands such as `pwd`, `ls`, `id`, `uname -a`, `echo hello`, and `mkdir`.

## Important: Android shell is not a Linux distribution

This alpha uses Android's built-in shell. Android uses the Linux kernel, but that does **not** mean this app currently includes a conventional GNU/Linux userspace. It does not yet include Alpine/Debian root filesystems, `proot`, `apt`, or `pkg`. Commands and filesystem access are restricted by Android's app sandbox. Root access is not provided.

Do not paste commands from unknown sources. Shell commands run with the app's own permissions and can modify files available to the app.

## Build

1. Install Android Studio, JDK 17, and Android SDK Platform 35.
2. Open this repository in Android Studio.
3. Sync Gradle.
4. Build and run the `app` configuration on an Android 8.0+ device.

The GitHub Actions workflow builds the debug APK and uploads it as a workflow artifact.

## Technology

- Kotlin and Android SDK
- Gradle Kotlin DSL
- C++ / CMake foundation (not yet connected to the terminal process engine)

## Roadmap

- [x] Initial Android project structure
- [x] Android shell process execution
- [x] Output and error stream display
- [x] Multiple independent terminal sessions
- [x] Session create, switch, and close controls
- [ ] PTY-backed interactive terminal (Ctrl+C, full-screen apps, resize)
- [ ] ANSI/VT escape-sequence terminal renderer
- [ ] Linux userspace bootstrap and lifecycle management
- [ ] Package manager integration for a supported userspace
- [ ] Persistent session history and settings
- [ ] Automated device tests and signed release pipeline

## Security boundaries

NEXUS does not silently request root or bypass Android's sandbox. Linux userspace integration must remain inside app-private storage and clearly disclose its limitations.

## License

MIT. See [LICENSE](LICENSE).
