package ca.hobin.bugdemo

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
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
            Button(onClick = { createLockdownRestrictions()
            }) {
                Text("Enable Lockdown")
            }
            Button(onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://www.google.com")
                        setPackage("com.android.chrome")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    // Verify Chrome is installed
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                        // Fallback to default browser
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) {
                Text("Open Chrome")
            }
        }
    }

    fun createHomeLauncherIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        return intentFilter
    }

    fun createLockdownRestrictions() {
        val lockTaskFeatures = DevicePolicyManager.LOCK_TASK_FEATURE_HOME or
            DevicePolicyManager.LOCK_TASK_FEATURE_GLOBAL_ACTIONS or
            DevicePolicyManager.LOCK_TASK_FEATURE_KEYGUARD
        devicePolicyManager.setLockTaskPackages(adminComponent,
            arrayOf("ca.hobin.bugdemo", "com.android.chrome")
        )
        devicePolicyManager.clearPackagePersistentPreferredActivities(adminComponent, "com.google.android.apps.nexuslauncher")
        val componentName = ComponentName(this, MainActivity::class.java)
        devicePolicyManager.addPersistentPreferredActivity(adminComponent, createHomeLauncherIntentFilter(), componentName)
        val nexus = arrayOf("com.google.android.apps.nexuslauncher")
        devicePolicyManager.setPackagesSuspended(adminComponent, nexus, true)
        devicePolicyManager.setLockTaskFeatures(adminComponent, lockTaskFeatures)
        val activity = this@MainActivity
        activity.startLockTask()
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


