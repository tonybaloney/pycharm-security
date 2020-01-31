# Shell Escape Fixer - Replaces direct password/hash comparisons with `compare_digest()`

## Suggested By

* [PR100](../checks/PR100.md)

## Logic

Wraps arguments to `subprocess.call`, `subprocess.run`, or `subprocess.Popen` with `shlex.quote()`.

## Examples

Insecure arguments (i.e. returned from function/method calls, or variables) should be escaped:

```python
import subprocess
ret = subprocess.run(['ps', opt], shell=True)
```


Will raise [PR100](../checks/PR100.md) and suggest the Shell Escape. When executed the code will become:

```python
import subprocess
from shlex import quote as shlex_quote
ret = subprocess.run(['ps', shlex_quote(opt)], shell=True)
```

This applies to single or list arguments. One or multiple arguments meeting the criteria will be escaped in a single fix.

NB: `shlex_quote` is used to avoid polluting/colliding with other functions named `quote`