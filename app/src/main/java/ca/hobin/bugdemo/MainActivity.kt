package ca.hobin.bugdemo

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.UserManager.DISALLOW_CONFIG_WIFI
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ca.hobin.bugdemo.ui.theme.BugDemoTheme

class MainActivity : ComponentActivity() {
    private val adminComponent by lazy { ComponentName(this, DeviceAdmin::class.java) }
    private val devicePolicyManager by lazy { getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainUi()
        }
    }

    @Composable
    fun WifiConfigurationButtons(modifier: Modifier = Modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { devicePolicyManager.addUserRestriction(adminComponent, DISALLOW_CONFIG_WIFI) }) {
                Text("Set DISALLOW_CONFIG_WIFI")
            }
            Button(onClick = { devicePolicyManager.clearUserRestriction(adminComponent, DISALLOW_CONFIG_WIFI) }) {
                Text("Unset DISALLOW_CONFIG_WIFI")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainUi() {
        BugDemoTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                WifiConfigurationButtons(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}


