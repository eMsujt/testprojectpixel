# Contributing to SkyBlock Core

SkyBlock Core is a Minecraft SkyBlock plugin built on top of the Paper 1.8.8 API. The project is structured as a Maven multi-module build; the main game logic lives in the `skyblock-core` module under `com.skyblock.core`.

## Prerequisites

- **Java 17** (JDK 17+)
- **Maven 3.9+**
- **Paper 1.8.8** (provided at runtime; the build pulls it from the local repo or a configured remote)

## Building

Build and test the core module from the repository root:

```bash
mvn -pl skyblock-core -am test
```

Run the full reactor build:

```bash
mvn clean install
```

Run tests only:

```bash
mvn test
```

> Note: the `items` and `plugin` modules have known pre-existing compile issues. Use the `-pl skyblock-core -am` flag to target only the core module when iterating locally.

## Branching strategy

- `main` — stable, always passing CI
- Feature branches: `feature/<short-description>` (e.g. `feature/auction-house-manager`)
- Bug fixes: `fix/<short-description>`
- Open a pull request against `main` when your branch is ready for review.

## Pull request guidelines

- One logical change per PR; keep diffs small and focused.
- All tests must pass (`mvn -pl skyblock-core -am test`) before opening a PR.
- Match the surrounding code style — naming, formatting, comment density.
- Write a clear PR description: what changed, why, and how to verify it.
- Do not reformat unrelated code or introduce new dependencies without discussion.

## Reporting issues

Open a GitHub issue with:
1. Steps to reproduce
2. Expected vs. actual behavior
3. Relevant log output and the module/class involved
