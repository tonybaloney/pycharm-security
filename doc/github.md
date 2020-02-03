# GitHub Action

The PyCharm Security plugin is available as a CI/CD workflow for GitHub Actions on the [GitHub Marketplace](https://github.com/marketplace/actions/pycharm-python-security-scanner).

## Example

This plugin will inspect the GitHub Workplace for Python code and report on vulnerabilities

```yaml
on: [push]

jobs:
  security_checks:
    runs-on: ubuntu-latest
    name: Execute the pycharm-security action
    steps:
      - uses: actions/checkout@v1
      - name: Run PyCharm Security
        uses: tonybaloney/pycharm-security@1.5.0
        with:
          path: .
```

This would give a log of issues inside the report:

![](_static/usage-github.png)

## List of vulnerabilities and inspections

See [check index](checks/index) for a list of currently supported inspections.

## Additional configuration

If you wish to only scan a subdirectory within your code checkout, add the `path` argument with the relative path from the root.

For example, to scan the `src` subdirectory:

```yaml
      - name: Run PyCharm Security
        uses: tonybaloney/pycharm-security@1.5.0
        with:
          path: src/
```
