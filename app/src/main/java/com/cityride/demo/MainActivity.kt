package com.cityride.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.cityride.demo.ui.CityRideNavHost
import com.cityride.demo.ui.theme.CityRideTheme
import com.cityride.demo.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels {
        AppViewModel.factory((application as CityRideApp).container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CityRideTheme { CityRideNavHost(viewModel) }
        }
    }
}
