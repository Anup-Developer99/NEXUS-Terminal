# NEXUS Terminal Architecture

## Current alpha architecture

- **Android UI (Kotlin):** terminal output, command input, and session controls.
- **TerminalSession:** one `/system/bin/sh` process per session, with stdin/stdout handling.
- **Session isolation:** sessions are separate shell processes. They share the app's Android UID and permission boundary; they are not security-isolated from one another.
- **Native layer (C++):** placeholder foundation only. It is not currently part of command execution.
- **Linux environment:** not included in this alpha.

## Command execution

Each session starts Android's `/system/bin/sh` through `ProcessBuilder`. Standard error is merged into standard output. Input is sent to the shell's stdin. The process runs as the application UID, with the app's permissions and filesystem access.

This is a line-oriented shell process, not a PTY. Interactive terminal programs, terminal resize, job control, and complete Ctrl+C behavior require a PTY implementation and terminal emulator.

## Linux userspace distinction

Android uses the Linux kernel, but its userspace differs from a typical GNU/Linux distribution. A complete optional Linux environment would need:
- A supported ARM/ARM64 userspace root filesystem.
- A compatible process translation layer such as proot, with its licensing and maintenance obligations reviewed.
- Safe download, integrity verification, extraction, storage, and upgrade flows.
- A userspace launcher that stays inside app-private storage.
- Clear handling of architecture, storage, network, and Android-version compatibility.

Those components are not bundled or claimed as implemented in 0.2.0-alpha.

## Security boundaries

- Run commands with the app's normal Android UID.
- Do not silently invoke `su` or claim root access.
- Keep process input/output and cancellation under app control.
- Keep downloaded environments in app-private storage unless the user explicitly exports files.
- Verify downloaded archives before extraction and prevent path traversal during extraction.
- Explain clearly when a command or feature is unavailable.

## Known limitations

- Uses `/system/bin/sh`, not a PTY.
- No ANSI/VT screen parser.
- No full-screen terminal application support.
- No Linux distribution, proot, apt, or pkg.
- No root access or unrestricted host filesystem access.
- Sessions are closed when the activity is destroyed.
