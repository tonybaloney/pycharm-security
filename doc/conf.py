# Configuration file for the Sphinx documentation builder.
#
# This file only contains a selection of the most common options. For a full
# list see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

# -- Project information -----------------------------------------------------

project = 'PyCharm Python Security plugin'
copyright = '2020, Anthony Shaw'
author = 'Anthony Shaw'


# -- General configuration ---------------------------------------------------

extensions = [
    "sphinx.ext.autodoc",
    "sphinx.ext.doctest",
    "sphinx.ext.todo",
    "sphinx.ext.coverage",
    "sphinx.ext.viewcode",
    "sphinx.ext.githubpages",
    "sphinx_markdown_tables"
]

# Add any paths that contain templates here, relative to this directory.
templates_path = ['_templates']

# List of patterns, relative to source directory, that match files and
# directories to ignore when looking for source files.
# This pattern also affects html_static_path and html_extra_path.
exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']

source_suffix = [".rst", ".md"]

source_parsers = {
    '.md': 'recommonmark.parser.CommonMarkParser',
}
# -- Options for HTML output -------------------------------------------------

# The theme to use for HTML and HTML Help pages.  See the documentation for
# a list of builtin themes.
#
html_theme = 'alabaster'
html_theme_options = {
    'logo': 'logo.png',
    'logo_name': True,
    'logo_text_align': "center",
    'github_user': 'tonybaloney',
    'github_repo': 'pycharm-security',
    'github_banner': True,
    'github_button': True,
    'fixed_sidebar': True,
    'sidebar_width': '330px',
    'page_width': '70%',
    'extra_nav_links': {
        'JetBrains Marketplace': "https://plugins.jetbrains.com/plugin/13609-python-security",
        "GitHub Marketplace": "https://github.com/marketplace/actions/pycharm-python-security-scanner",
        "Docker Hub": "https://hub.docker.com/r/anthonypjshaw/pycharm-security"
    },
    'show_powered_by': False
}
html_show_copyright = False
html_show_sphinx = False
# Add any paths that contain custom static files (such as style sheets) here,
# relative to this directory. They are copied after the builtin static files,
# so a file named "default.css" will overwrite the builtin "default.css".
html_static_path = ['_static']
html_sidebars = {'**': ['about.html', 'navigation.html', 'searchbox.html'], }
master_doc = 'index'