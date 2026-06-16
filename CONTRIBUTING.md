# Contributing

Thanks for your interest in contributing to SkyBlock! This document explains how
to build the project and the conventions we follow.

## Prerequisites

- **JDK 25** or newer
- **Maven 3.9+**

## Building

The project is a Maven multi-module aggregator. Build everything from the
repository root:

```bash
mvn clean install
```

To build a single module:

```bash
mvn -pl <module> -am clean install
```

To run the test suite only:

```bash
mvn test
```

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
