package com.herbarium.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Destination {
    val route: String
    val title: String
}

object PlantListDestination : Destination {
    override val route = "plant_list"
    override val title = "Plant List"
}

object PlantDetailDestination : Destination {
    override val route = "plant_details"
    override val title = "Plant Details"
    const val plantId = "plant_id"
    val arguments = listOf(navArgument(name = plantId) {
        type = NavType.StringType
    })
    fun createRouteWithParam(plantId: String) = "$route/${plantId}"
}

object AddPlantDestination : Destination {
    override val route = "add_plant"
    override val title = "Add Plant"
}

object AuthenticationDestination : Destination {
    override val route = "authentication"
    override val title = "Authentication"
}

object SignUpDestination : Destination {
    override val route = "sign_up"
    override val title = "Sign Up"
}

object CameraDestination : Destination {
    override val route = "camera"
    override val title = "Camera"
}