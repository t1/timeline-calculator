# Timeline Calculator

I'm just trying to keep my Kotlin skills sharp.

This is a little https://quarkus.io/ app that provides a web app showing the remaining time for a project.
Simply enter a project duration (e.g. as copied from the remaining time of a JIRA ticket like `12w 3d 4h 50m`)
and the app will show you a calendar with the remaining working days.

If you want to define holidays or more than one team member and their vacations,
you can add a `project.yaml` file to the root of the project; something like this:

```yaml
name: My Project
team:
  Alice:
    vacations:
      - 2024-12-23..2025-01-03
  Bob:
    vacations:
      - 2024-12-20..2025-01-03
holidays:
  2024-12-25: Christmas Day
  2024-12-26: Boxing Day
  2025-01-01: New Year's Day
```
