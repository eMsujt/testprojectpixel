# Contributing

Thanks for your interest in contributing to SkyBlock! This document explains how
to build the project and the conventions we follow.

## Prerequisites

- **JDK 25** or newer
- **Maven 3.9+**

## Building locally

The project is a Maven multi-module aggregator. Build everything from the
repository root:

```bash
mvn package
```

Skip tests for a faster local build:

```bash
mvn package -DskipTests
```

To build a single module:

```bash
mvn -pl skyblock-core -am package
```

To run the test suite only:

```bash
mvn test
```

## Running on a test server

1. Build the plugin JAR: `mvn package -DskipTests -pl skyblock-core -am`
2. Copy `skyblock-core/target/skyblock-core-*.jar` into your Paper/Spigot server's `plugins/` directory.
3. Start (or restart) the server: `./start.sh` (or however your test server is launched).
4. Reload without a full restart: `/reload confirm` — though a full restart is preferred to catch class-loading issues.

The server must be running **Paper 1.21+** (or a compatible Spigot fork). Keep a fresh world in your test environment so island/profile data does not interfere with production.

## Coding conventions

- **Java style** — 4-space indentation, braces on the same line (`1TBS`). Match whatever the surrounding file uses; do not reformat unrelated code.
- **Naming** — classes `UpperCamelCase`, methods/fields `lowerCamelCase`, constants `UPPER_SNAKE_CASE`.
- **Chat messages** — use `ChatUtil.send()` / `ChatUtil.colorize()` (in `com.skyblock.core.util`); never call `player.sendMessage()` with a raw string.
- **Menus** — extend `AbstractSkyBlockMenu`; do not duplicate the `player` field or re-implement `open()`.
- **Managers** — one manager per system under `com.skyblock.core.manager`; wire it into `SkyBlockCore` (field + constructor).
- **ItemStacks** — build items with `ItemBuilder`; avoid raw `new ItemStack` + `ItemMeta` blocks.
- **Tests** — mock Bukkit objects with Mockito (`mock(ItemStack.class)`); never depend on a live server in unit tests.

## Project layout

Each gameplay system lives in its own Maven module (see the module list in the
root `pom.xml`). The canonical managers live under
`com.skyblock.core.manager`; see the **Core Managers** section of the
[README](README.md) before adding new functionality.

## Pull requests

- Keep changes focused — one logical change per pull request.
- Match the surrounding code style; do not reformat unrelated code.
- Make sure `mvn clean install` passes before opening a PR.
- Write a clear description of what changed and why.

## Reporting issues

Open a GitHub issue with steps to reproduce, expected behavior, and actual
behavior. Include the module and relevant log output where possible.
