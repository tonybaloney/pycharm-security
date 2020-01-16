# PyCharm Python Security plugin

[![GitHub Actions](https://github.com/tonybaloney/pycharm-security/workflows/CI/badge.svg)](https://github.com/tonybaloney/pycharm-security/actions)
[![Downloads](https://img.shields.io/jetbrains/plugin/v/13609-python-security.svg)](https://plugins.jetbrains.com/plugin/13609-python-security)
[![Version](https://img.shields.io/jetbrains/plugin/d/13609-python-security.svg)](https://plugins.jetbrains.com/plugin/13609-python-security)
[![codecov](https://codecov.io/gh/tonybaloney/pycharm-security/branch/master/graph/badge.svg)](https://codecov.io/gh/tonybaloney/pycharm-security)

![](src/main/resources/META-INF/pluginIcon.svg)

A plugin to run security checks for common flaws in Python code and suggest quick fixes.

Available on the [Jetbrains plugin marketplace](https://plugins.jetbrains.com/plugin/13609-python-security)

![](src/main/resources/META-INF/screenshot.png)

## SafetyDB

This plugin will check the installed packages in your Python projects against the SafetyDB and raise a warning for any vulnerabilities.

![](src/main/resources/META-INF/safetydb-screenshot.png)

## Current checks

See [Supported Checks](doc/checks.md) for a current list.

## Release History

See [Release History](HISTORY.md) for the release history.

## License

This project is [MIT Licensed](LICENSE).

## Credits

Credit to the PyUp.io team for the SafetyDB. This project uses SafetyDB to scan packages.