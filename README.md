Also see:
http://code.google.com/p/lighttexteditor/

<img src="https://github.com/berlinbrown/lightreports/raw/master/media/screenshot_1_main.png" />

There aren't many open, easy to use tools for generating PDF, Image, or SVG report documents that use a black box approach. Light Reports combines a set of simple tools for converting XML or HTML documents into PDF documents.

    * Simple light-weight text editor.
    * Standalone GUI
    * Cross-platform, tested on Windows, Linux and OSX.
    * Generate reports in batch mode
    * Read input template files, generate to document format 

The htmltopdf parser, xhtmlrenderer has support for most of the features of CSS 2.1 including table pagination, positioned headers and footers, page number, page sizing. 


    * Simple light-weight text editor.
    * Cross-platform, tested on Windows, Linux and OSX.
    * Simple File Manager for accessing important files quickly.
    * Search tools using Linux or Win32 applications (unxutils).
    * Create PDF Documents with PDF create tools.
    * Built on Java 1.5 and the Clojure programming language. 

This tool also is useful for editing and viewing log4j log files.

---

## Developer Guide 🔧

**Quick summary**
- This repository contains an older **Clojure + Java + Scala** application for generating PDF/Image/SVG reports and a small log editor GUI.
- Active module: `light_reports/light_reports`. Legacy (reference-only): `light_edit_deprecated`.

**Project layout (what to open first)**
- `light_reports/light_reports/src/octane/toolkit/` — primary Clojure namespaces (UI, templates, testing helpers).
- `light_reports/light_reports/src/java/src/` — Java helpers used by the Clojure code (PDF helpers, SWT utilities).
- `light_reports/light_reports/lib/` — bundled third-party jars (SWT, newpdf/iText/tagsoup). **SWT is platform-specific** under `lib/swt/<os>`.

**Build & run (developer commands)**
- Build the main jar (uses Ant):
  - cd into `light_reports/light_reports` and run: `ant jar` (requires Ant installed).
- Run the GUI (from module root):
  - `./light_reports.sh` — launches `clojure.main src/octane/toolkit/octane_main_window.clj` (macOS/Linux).
- Legacy GUI (reference):
  - `./light_edit_deprecated/light.sh` — launches `clojure.lang.Repl` with `src/clojure/light/toolkit/light_main_window.clj`.

**Environment & tips**
- JVM flags used in scripts: `-Xms128m -Xmx224m` and `-D<app>.install.dir="<INSTALL_DIR>"` (scripts typically set `INSTALL_DIR` to `pwd`).
- Ensure the matching platform SWT jar exists under `lib/swt/` before launching the GUI.
- Tests are custom and embedded—see `octane_testing.clj` and JUnit templates under `src/java/src/com/octane/.../test`.

**Formatting & style**
- Java formatting: `google-java-format`. I added `.editorconfig` and `tools/format-java.sh` to run the formatter:
  - macOS install: `brew install google-java-format`
  - Format: `tools/format-java.sh`
- We intentionally skip formatting of vendored third-party sources under `lib/` and `thirdparty/`.

**Notes & gotchas**
- This project targets older Java/Clojure versions; expect deprecated APIs and older dependencies. Prefer safe, non-invasive changes (formatting, small bugfixes) over broad modernization.
- For CI: ensure Ant is installed and that platform-specific SWT jars are available.

Usage:

Place the octane directory such that HOME is placed at :
  
  C:\usr\local\projects\light_logs
  
  And click on the light_logs.bat batch script.
 
