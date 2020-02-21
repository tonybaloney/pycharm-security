# Django security

## Django configuration

## SQL injection in Django

## Cross-Site-Scripting (XSS) in Django

Django comes with a cross-site-scripting protection mechanism. It works via the templating systems (django's or jinja2).

Fields from the template's context dictionary will be HTML-escaped automatically.

[See `mark_safe()` documentation](https://docs.djangoproject.com/en/3.0/ref/utils/#module-django.utils.safestring)

### Detection and warning of safe strings

This plugin will detect and warn of occurrences, 
