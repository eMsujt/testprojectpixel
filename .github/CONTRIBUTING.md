# Contributing to SkyBlock Core

SkyBlock Core is a Minecraft SkyBlock plugin built on top of the Paper API. The project is structured as a Maven multi-module build; the main game logic lives in the `skyblock-core` module under `com.skyblock.core`.

## Prerequisites

- **Java 21** (JDK 21+) — matches `<java.version>` in `pom.xml`
- **Maven 3.9+**
- **Paper API** (provided at runtime; pulled from the PaperMC repository configured in `pom.xml`)

## Building

Build and package the core module from the repository root (this is the command CI runs):

```bash
mvn clean package -pl skyblock-core --no-transfer-progress
```

Run the full reactor build:

```bash
mvn clean package
```

Run tests only for the core module:

```bash
mvn -pl skyblock-core -am test
```

## Branching strategy

- `main` — stable, always passing CI
- Feature branches: `feature/<short-description>` (e.g. `feature/auction-house-manager`)
- Bug fixes: `fix/<short-description>`
- Open a pull request against `main` when your branch is ready for review.

## Pull request checklist

- [ ] `mvn clean package` passes locally
- [ ] One logical change per PR; diffs are small and focused
- [ ] Tests added or updated to cover the change
- [ ] `plugin.yml` updated if commands or permissions changed
- [ ] Code matches the surrounding style — naming, formatting, comment density
- [ ] No reformatting of unrelated code and no new dependencies without discussion
- [ ] New functionality reuses the canonical managers under `com.skyblock.core.manager` where applicable

## Reporting issues

Open a GitHub issue with:
1. Steps to reproduce
2. Expected vs. actual behavior
3. Relevant log output and the module/class involved
