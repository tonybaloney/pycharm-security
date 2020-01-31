# Safe Load Fixer - Replaces uses of `yaml.load` with `yaml.safe_load`

## Suggested By

* [YML100](../checks/YML100.md)

## Logic

Locates instances of `yaml.load()` and replaces them with `yaml.safe_load()`

## Examples

```python
import yaml
with open('cfg.yaml') as cfg:
    config = yaml.load(cfg)
```

Will raise [YML100](../checks/YML100.md) and suggest the Safe Load Fixer. When executed the code will become

```python
import yaml
with open('cfg.yaml') as cfg:
    config = yaml.safe_load(cfg)
```

Keyword and ordered arguments are preserved.