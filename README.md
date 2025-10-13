# version-catalog-update-ruler-plugin

version-catalog-update-ruler-plugin is a Gradle plugin that extends
[littlerobots/version-catalog-update-plugin](https://github.com/littlerobots/version-catalog-update-plugin)
with advanced, rule-based controls for your dependency updates. It gives you fine-grained control over which versions
are suggested, preventing unwanted or breaking changes from being applied automatically.

## ✨ Features

- **Stable Version Enforcement:**
    - Update only to stable versions. You can customize the definition of an "unstable" version via a configurable
      regular expression.
- **Advanced Version Pinning:**
    - Prevent automatic updates to the next **major** or **minor** version independently, giving you granular control to
      avoid breaking changes.
- **Artifact-Only Version Check:**
    - Provides an option to consider only the version directly associated with an artifact as an update candidate.

## 🚀 Getting Started

To use this plugin, apply it to your project's build script.

### 1. Apply the plugin

Add the plugin to your `build.gradle.kts` file.

```kotlin
// build.gradle.kts
plugins {
    id("io.github.eno314.version-catalog-update-ruler") version "<latest_version>"
}
```

### 2. Configure the rules

Configure the update rules in your `build.gradle.kts` using the `versionCatalogUpdateRuler` extension.

```kotlin
// build.gradle.kts
versionCatalogUpdateRuler {
    // Enforce stable updates only.
    onlyStable.set(true)

    // (Optional) Override the default regex for unstable versions.
    // The default already covers most common cases (alpha, beta, rc, etc.).
    unStableVersionRegex.set(Regex(".*(SNAPSHOT|PREVIEW).*", RegexOption.IGNORE_CASE))

    // Prevent updates to the next major version.
    pinMajorVersion.set(true)

    // Also prevent updates to the next minor version.
    pinMinorVersion.set(true)

    // Only consider the artifact's own version for updates.
    onlyArtifactVersion.set(true)
}
```

Now, when you run the `versionCatalogUpdate` task from the base plugin, these rules will be applied to filter the
available dependency updates.

## ⚙️ Configuration

The following properties are available in the `versionCatalogUpdateRuler` block:

| Property               | Type      | Default                                            | Description                                                                                                            |
|:-----------------------|:----------|:---------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------|
| `onlyStable`           | `Boolean` | `false`                                            | If `true`, the plugin will only suggest stable versions, filtering out any versions that match `unStableVersionRegex`. |
| `unStableVersionRegex` | `Regex`   | `".*(alpha\|beta\|rc\|preview\|snapshot\|test).*"` | A regular expression used to identify unstable version strings. This is used by the `onlyStable` property.             |
| `onlyArtifactVersion`  | `Boolean` | `false`                                            | If `true`, the plugin will only consider the version directly associated with an artifact as an update candidate.      |
| `pinMajorVersion`      | `Boolean` | `false`                                            | If `true`, the plugin will prevent updates where the major version number increases (e.g., `1.5.0` -> `2.0.0`).        |
| `pinMinorVersion`      | `Boolean` | `false`                                            | If `true`, the plugin will prevent updates where the minor version number increases (e.g., `1.2.5` -> `1.3.0`).        |

## 🤝 Contributing

Contributions are welcome! If you have a suggestion or find a bug, please feel free to open an issue or submit a pull
request.

## 🙏 Acknowledgments

This plugin is built upon and extends the
excellent [littlerobots/version-catalog-update-plugin](https://github.com/littlerobots/version-catalog-update-plugin). A
huge thanks to its maintainers for providing such a solid foundation.

## 📜 License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
