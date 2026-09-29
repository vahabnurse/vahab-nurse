package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.MainScreen
import com.example.ui.screens.PatientDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PatientViewModel

class MainActivity : ComponentActivity() {

    private val patientViewModel: PatientViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Persian RTL layout support
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(viewModel = patientViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: PatientViewModel) {
    val selectedPatientId by viewModel.selectedPatientId.collectAsStateWithLifecycle()

    if (selectedPatientId != null) {
        BackHandler {
            viewModel.selectPatient(null)
        }
        PatientDetailScreen(
            patientId = selectedPatientId!!,
            viewModel = viewModel,
            onBackClick = { viewModel.selectPatient(null) }
        )
    } else {
        MainScreen(
            viewModel = viewModel,
            onNavigateToPatientDetail = { patientId ->
                viewModel.selectPatient(patientId)
            }
        )
    }
}
