# Compare Digest Fixer - Replaces direct password/hash comparisons with `compare_digest()`

## Suggested By

* [PW100](../checks/PW100.md)

## Logic

Locates occurrences of a password-named variable, e.g. `password`, `secret` within a binary expression and replaces it with a call to `compare_digest()`.

## Examples

If a comparison (binary expression) using the `==` operator has a variable named `password`, `secret`, etc. it will replace it with a call to `compare_digest()`

```python
if password == "SECRET":
   continue
```

Will raise [PW100](../checks/PW100.md) and suggest the Compare Digest fixer. When executed the code will become:

```python
from hmac import compare_digest
if compare_digest(password, "SECRET"):
   continue
```

The sequence of comparators is preserved. Any additional logic in the left or right expression is preserved.

If the Python version is detected to be 3.7 or above, the `secrets` module will be used instead:

```python
from secrets import compare_digest
if compare_digest(password, "SECRET"):
   continue
```