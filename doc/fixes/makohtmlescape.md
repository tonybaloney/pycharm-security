# Mako HTML escape fixer

## Suggested By

* [MK100](../checks/MK100.md)

## Logic

Alters the Mako `Template` constructor to include a keyword argument `default_filters=['h']` which applies HTML escaping to all inputs.
