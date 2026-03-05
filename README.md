# TestSolz - Kotlin Multiplatform (KMP)

Employee/HR Attendance Management System converted from SwiftUI to Kotlin Multiplatform.

## Project Structure

```
TestSolzKMP/
├── composeApp/                    # Shared Compose Multiplatform code
│   └── src/
│       ├── commonMain/            # Shared code for all platforms
│       │   └── kotlin/
│       │       └── com/testsolz/
│       │           ├── app/       # App entry point
│       │           ├── core/      # Core functionality (Auth, Navigation, Storage)
│       │           ├── designsystem/  # Theme, Colors, Typography
│       │           ├── domain/    # Models, Protocols
│       │           ├── features/  # Admin & Employee features
│       │           ├── services/  # Network, Attendance, User services
│       │           ├── shared/    # Shared components
│       │           └── utilities/ # Constants, Helpers, Logging
│       ├── androidMain/           # Android-specific code
│       └── iosMain/               # iOS-specific code
├── androidApp/                    # Android application
├── iosApp/                        # iOS application (Xcode project)
└── build.gradle.kts

## Features

### Employee Portal
- **Home**: Check-in/out, Tasks management, Projects
- **Attendance History**: View personal attendance records
- **Requests**: Create leave/late arrival requests

### Admin Portal
- **Dashboard**: Overview metrics and analytics
- **Employee List**: Monitor all employee attendance
- **Request Management**: Approve/reject employee requests

## Technology Stack
- **Kotlin Multiplatform**: Shared business logic
- **Compose Multiplatform**: UI for Android & iOS
- **MVVM Architecture**: ViewModels and State management
- **Coroutines**: Async operations

## Design System
- Brand Color: Cyan/Turquoise (#00D9D9)
- Clean, professional, enterprise-grade design
- Responsive layouts for all screen sizes
