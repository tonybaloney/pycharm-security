# Supported Fixes

* [Safe Load Fixer](fixes/safeloadfixer) Replaces uses of `yaml.load` with `yaml.safe_load`
* [Tempfile Fixer](fixes/tempfilefixer.md) Replaces uses of `tempfile.mktemp` with `tempfile.mkstemp`
* [Compare Digest Fixer](fixes/comaredigestfixer.md) Replaces direct password/hash comparisons with `compare_digest()`

