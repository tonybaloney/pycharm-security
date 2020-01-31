# Tempfile `mkstemp` Fixer - Replaces uses of `tempfile.mktemp` with `tempfile.mkstemp`

## Suggested By

* [TMP100](../checks/TMP100.md)

## Logic

Locates instances of `tempfile.mktemp()` and replaces them with `tempfile.mkstemp()`

## Examples

```python
import tempfile
tmp_f = tempfile.mktemp()
```

Will raise [TMP100](../checks/TMP100.md) and suggest the Tempfile `mkstemp` fixer. When executed the code will become:

```python
import tempfile
tmp_f = tempfile.mkstemp()
```

Keyword and ordered arguments are preserved.