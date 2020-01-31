# Usage

## Using this plugin within the editor

After installing this plugin, whenever a check detects some code that might cause a security vulnerability, it will be highlighted in Yellow.

If you hover over the highlighted line, or select **Show Context Actions**, PyCharm will tell you the check identifier, and a brief description.

![Usage within editor screenshot](_static/usage-in-editor.png)

All checks are documented on the site. Using the code, you can look up details in [List of Checks](checks/index).

Some checks have automatic **Quick Fixes** available, listed on [List of Fixes](fixes/index).

Where a quick fix is available, it can be applied using the quick fix shortcut key in PyCharm, or by clicking the action in the Context Window.

## Scanning an entire project

To scan an entire project, navigate to the **Inspect Code** from the **Code** menu

<img src="_static/usage-inspect-code-for-project.png" width="50%"/>

To scan a directory, right click on it from the Project panel, and selecting **Inspect Code** from the menu:

<img src="_static/usage-inspect-code-from-context.png" width="50%"/>

This will pop up with the Inspections Profile Window. 

Click the `...` ellipsis to edit the inspection profiles.

From here, deselect all inspections, then select the **Python Security** group:

<img src="_static/usage-inspection-profile.png" width="50%"/>

You can customize the severity for this particular run. Save the inspection profile with the name "**Security**" (or similar), by clicking on the cog and choosing **Copy to Project**.

<img src="_static/usage-save-profile.png" width="50%"/>

    You can also use **Copy to IDE** to reuse this profile for any project.

Once completed, the **Inspection Results** pane at the bottom will show any discovered issues:

![](_static/usage-inspection-pane.png)

If Quick Fixes are available they can be applied here to a single occurrence, file or the whole project. Use the Quick Fix (Lightbulb) icon on the right hand side.

## Disabling checks

Any of the checks can be disabled directly from PyCharm, just as you would with PyCharm's builtin inspection rules.

### For a statement

Checks can be disabled for a given statement by adding a `noinspection` comment above the line with the check identifier.

This is available as a shortcut from the context window as **Suppress for statement**.

![](_static/usage-suppress-for-statement.png)

The editor will add a comment above your code to disable that check

![](_static/usage-disable-line.png)

To re-enable inspection, remove the commented line

### For a project or globally

Any checks can be disabled for an entire project, or globally for your editor.

From the context panel for the check you want to disable, select **Disable Inspection**

![](_static/usage-disable-inspection.png)

This can be re-enabled from the Inspections configuration panel in __Preferences -> Editor -> Inspections__.

## Customizing the severity levels

In PyCharm, navigate to __Preferences -> Editor -> Inspections__ and find the __Python Security__ Group:

The severity levels for any check can be configured in this pane.

<img src="_static/usage-edit-levels.png" width="50%"/>

Some checks are grouped together because they have very similar styles, e.g. the Django middleware checks (DJG200, DJG201) are bundled.

There is a shortcut to this window from the context menu:

![](_static/usage-edit-setting.png)
