package com.herbarium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.herbarium.ui.navigation.AddPlantDestination
import com.herbarium.ui.navigation.AuthenticationDestination
import com.herbarium.ui.navigation.CameraDestination
import com.herbarium.ui.navigation.PlantDetailDestination
import com.herbarium.ui.navigation.PlantListDestination
import com.herbarium.ui.navigation.SignUpDestination
import com.herbarium.ui.screen.AddPlantScreen
import com.herbarium.ui.screen.CameraScreen
import com.herbarium.ui.screen.PlantDetailsScreen
import com.herbarium.ui.screen.PlantListScreen
import com.herbarium.ui.screen.SignInScreen
import com.herbarium.ui.screen.SignUpScreen
import com.herbarium.ui.theme.HerbariumTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var supabaseClient: SupabaseClient

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HerbariumTheme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination
                Scaffold { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = PlantListDestination.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(SignUpDestination.route) {
                            SignUpScreen(
                                navController = navController
                            )
                        }

                        composable(PlantListDestination.route) {
                            PlantListScreen(
                                navController = navController
                            )
                        }

                        composable(AuthenticationDestination.route) {
                            SignInScreen(
                                navController = navController
                            )
                        }

                        composable(AddPlantDestination.route) {
                            AddPlantScreen(
                                navController = navController
                            )
                        }
                        composable(CameraDestination.route) {
                            CameraScreen(
                                navController = navController
                            )
                        }

                        composable(
                            route = "${PlantDetailDestination.route}/{${PlantDetailDestination.plantId}}",
                            arguments = PlantDetailDestination.arguments
                        ) { navBackStackEntry ->
                            val plantId =
                                navBackStackEntry.arguments?.getString(PlantDetailDestination.plantId)
                            PlantDetailsScreen(
                                plantId = plantId,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}