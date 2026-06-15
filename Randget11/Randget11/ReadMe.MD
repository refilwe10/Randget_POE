# Randget - Personal Budget Tracker

Randget is an Android budgeting app built in Kotlin that helps users log expenses,
set monthly spending goals, track their spending visually, and stay motivated
through gamification.

## Purpose

The app lets a user:

- Register and log in.
- Log expenses with a description, category, amount, date/time, and optional photo.
- View and filter logged expenses between two dates.
- Set a minimum and maximum spending goal for each month.
- View a dashboard summarising total spend vs budget for a chosen month.
- View a graph of spending per category over a chosen period, with min/max
  goal lines overlaid.
- View a visual breakdown of how well they are tracking against their
  monthly goals (cumulative spending line vs goal lines, plus a progress bar).
- Earn gamification badges for good budgeting habits.

## Architecture & Design

- **Language**: Kotlin
- **UI**: Android Views (XML layouts) with `AppCompatActivity` screens.
- **Persistence**: Room database (`AppDatabase`) with entities for
  `Category`, `Expense`, `Budget`, `Badge`, and `RecurringExpense`.
- **Navigation**: `MainActivity` initialises the database and seeds default
  categories on first run, then routes to `LoginActivity`. From the
  `DashboardActivity`, the user can navigate to every other feature screen.
- **Charts**: [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) is
  used for the category spending bar chart and the goal-progress line chart.
- **Logging**: `android.util.Log` is used throughout activities and helper
  objects (e.g. `BadgeManager`, `CsvExporter`, `CategorySeeder`) to trace
  database operations, navigation, and feature logic - useful for debugging
  and for demonstrating understanding of the code's flow.

## Screens

| Screen | Activity | Description |
|---|---|---|
| Login | `LoginActivity` | Simple credential check, navigates to Dashboard. |
| Register | `RegisterActivity` | Basic registration form (not yet persisted). |
| Dashboard | `DashboardActivity` | Central hub: shows monthly total/budget status and links to every feature. |
| Add Expense | `AddExpenseActivity` | Add an expense with category, amount, date/time and optional photo. |
| View Expenses | `ViewExpensesActivity` | List expenses between two dates, with CSV export. |
| Set Budget | `SetBudgetActivity` | Set min/max spending goals for a given month. |
| Category Graph | `CategoryReportActivity` | Bar chart of spend per category over a chosen period, with min/max goal lines. |
| Goal Progress | `GoalProgressActivity` | Line chart of cumulative spending vs min/max goals, plus a progress bar summary. |
| Badges | `BadgesActivity` | List of gamification badges earned. |
| Recurring Expenses | `RecurringExpensesActivity` | Manage recurring monthly expenses and generate this month's entries. |

## Required Features

1. **Spending graph per category** (`CategoryReportActivity`) - a bar chart
   showing the total spent per category over a user-selectable date range,
   with horizontal lines marking the month's minimum and maximum spending
   goals.
2. **Goal progress visual** (`GoalProgressActivity`) - a line chart of
   cumulative spending across the month compared to the min/max goal lines,
   plus a progress bar and text summary of how the user is tracking.
3. **Gamification** (`BadgesActivity`, `BadgeManager`) - badges are awarded
   automatically (when the dashboard is loaded) for:
   - **First Step** - logging your first ever expense.
   - **Consistent Logger** - logging expenses on 20+ days in a month.
   - **Budget Boss** - staying within the max budget for a month.
   - **Saver Star** - spending below the minimum goal for a month.

## Custom Features (own features, as required)

### 1. Recurring Expenses (`RecurringExpensesActivity`, `RecurringExpense`, `RecurringExpenseDao`)

Many expenses repeat every month (rent, subscriptions, insurance). This
feature lets the user define a recurring expense once (description, amount,
category, day of month) and then generate this month's matching expense
entries for all active recurring expenses with a single button press,
removing the need for repetitive manual entry.

**What to look out for**: Open "Recurring Expenses" from the Dashboard, add
an item (e.g. "Rent", R3000, day 1), then tap "Generate This Month's
Expenses". A new expense entry is created and will appear in "View Expenses"
and count towards the monthly totals/charts/badges.

## Database & Logging

- `DatabaseProvider` exposes a single shared `AppDatabase` instance (Room,
  `fallbackToDestructiveMigration()`), used by all activities to avoid
  redundant database instances.
- `CategorySeeder` seeds a default set of expense/income categories on first
  launch only (checked in `MainActivity`).
- Key operations (saving expenses/budgets, generating recurring expenses,
  exporting CSVs, awarding badges, navigation) are logged with `Log.d` using
  per-class tags for easy filtering in Logcat.

## Testing

Automated tests live in `app/src/androidTest/java/com/example/randget11/AppDatabaseTest.kt`
and cover the app's core functionality using an in-memory Room database:

- Inserting and retrieving categories.
- Calculating total spend for a month.
- Grouping total spend per category over a date range (used by the graph).
- Saving and retrieving monthly budget goals.
- Badge de-duplication (a badge is only ever awarded once).
- Generating an expense from a recurring expense definition.

## GitHub Actions (CI)

`.github/workflows/build.yml` runs on every push/PR to `main` and:

1. Checks out the repository.
2. Sets up JDK 17.
3. Runs unit tests (`./gradlew test`).
4. Builds a debug APK (`./gradlew assembleDebug`).
5. Uploads the APK as a build artifact.

This was based on the guidance from:
- https://github.com/marketplace/actions/automated-build-android-app-with-github-action
- https://github.com/IMAD5112/Github-actions/blob/main/.github/workflows/build.yml

## Demonstration Video



## Known Limitations / Future Work

- Login/Registration is not yet persisted (no Firebase/Room user table).
- `RegisterActivity` does not save the new user account.
- The goal-progress and category graphs assume dates in `YYYY-MM-DD` format
  and budgets keyed by `YYYY-MM`.
