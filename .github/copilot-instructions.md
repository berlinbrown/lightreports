# GitHub Copilot / AI Agent Instructions for LightReports 🔧

## Quick summary
- Purpose: Clojure + Java application for generating PDF/Image/SVG reports and a lightweight log/HTML editor GUI. Main active module: `light_reports/light_reports`. Legacy GUI: `light_edit_deprecated` (deprecated).
- Build system: **Apache Ant** (no Leiningen/Gradle). Third-party jars live in `lib/`.
- Runtime: launched via shell scripts that start a Java process running `clojure.main` or `clojure.lang.Repl` with a top-level `.clj` UI file.

---

## Key entry points & scripts ✅
- Build JAR for light reports:
  - cd into `light_reports/light_reports` and run: `ant jar` (produces `${build.dir}/octane_textviewer.jar` or `octane_start.jar` depending on target)
- Run the GUI (from module root):
  - `./light_reports.sh` — launches `clojure.main src/octane/toolkit/octane_main_window.clj`
- Older editor (deprecated):
  - `./light.sh` in `light_edit_deprecated` — launches `clojure.lang.Repl` with `src/clojure/light/toolkit/light_main_window.clj`
- Common runtime JVM flags used in scripts: `-Xms128m -Xmx224m` and `-D<app>.install.dir="<INSTALL_DIR>"` (scripts set `INSTALL_DIR` to `pwd` or a configured path).

---

## Project layout (what to open first) 📁
- `light_reports/light_reports/src/octane/toolkit/` — primary Clojure namespaces for UI and app logic (e.g., `octane_main_window.clj`, `octane_templates.clj`, `octane_testing.clj`).
- `light_reports/light_reports/src/java/src/` — Java helpers (PDF helpers, SWT utilities) used from Clojure via `:import`.
- `light_reports/light_reports/lib/` — bundled third-party jars (SWT, newpdf/core-renderer, iText, tagsoup, clojure.jar, etc.). Platform-specific SWT under `lib/swt/<os>`.
- `light_reports/light_reports/conf/` — runtime properties (`octane_core_sys.properties`, `octane_user.properties`) read at startup.
- `pdf/samples` and `pdf/styles` — sample templates and CSS used by PDF generation.

---

## Common patterns & conventions (important for code changes) 🧭
- No modern Clojure tooling: code relies on raw `.clj` files and the Java classpath. Use the provided shell scripts or Ant targets to run/rebuild.
- Main UI code lives under `octane.toolkit.*`. Look there for UI flows, templates, and menu actions.
- PDF rendering uses native Java libs (xhtmlrenderer/newpdf/iText). Look for usage in `src/java` and places that call Java helper classes from Clojure.
- Config is read from `conf/` relative to `INSTALL_DIR`. Many scripts set `INSTALL_DIR` to `pwd` so running scripts from module root is typical during development.
- Tests are custom (see `octane_testing.clj` and template-based JUnit snippets); there is no standardized test runner. Expect some test helpers to be embedded in the Clojure code.

---

## Debugging & iterative development tips 💡
- Prefer interactive development via REPL launched by the existing scripts. They already set up the classpath and properties.
- When editing Java code, build with the Java `build.xml` targets and ensure `build/classes` is on the classpath used by the running script.
- To reproduce runtime issues, ensure the `lib/` folder contains platform-correct SWT and PDF libs.

---

## Integration & external dependencies 🔗
- SWT (UI) → platform-specific jars in `lib/swt/` (ensure the correct OS subfolder is used).
- PDF rendering → `lib/newpdf/` and other jar files such as `core-renderer.jar`, `iText-2.0.8.jar`, `tagsoup`.
- Some third-party code is bundled under `src/java/thirdparty` (xhtmlrenderer wrapper projects).

---

## Gotchas / historical notes ⚠️
- This is older code (designed for Java 1.5 and Clojure ~1.1). Expect deprecated APIs and older dependencies.
- Windows/Cygwin branches exist in launch scripts—prefer running the native OS script for your platform.
- `light_edit_deprecated` is intentionally left in the repo for reference; prefer working in `light_reports/light_reports`.

---

If you'd like, I can (pick one):
1. Add a few targeted CLI examples (exact `java -cp` command for quick dev runs) ✅
2. Add a short 