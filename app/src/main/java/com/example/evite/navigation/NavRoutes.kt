package com.example.evite.navigation

sealed class NavRoutes(val route: String) {

    object Home : NavRoutes("home")
    object EventsList : NavRoutes("events_list")
    object CreateEvent : NavRoutes("create_event")
    object EventDetails : NavRoutes("event_details/{eventId}") {
        fun createRoute(eventId: Int) = "event_details/$eventId"
    }
    object Profile : NavRoutes("profile")
    object EditProfile : NavRoutes("edit_profile")

    // Auth screens (no bottom bar)
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object ForgotPassword : NavRoutes("forgot_password")
    object ResetPassword : NavRoutes("reset_password")
}
