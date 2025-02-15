package com.yydx.lockscreencmd

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yydx.lockscreencmd.ui.theme.LockScreenCMDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LockScreenCMDTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ServiceToggleScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ServiceToggleScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isServiceEnabled by remember { mutableStateOf(false) }
    var shellCommand by remember { mutableStateOf("echo 'Lock screen activated'") }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 文本输入框，用于输入锁屏时执行的 shell 命令
        OutlinedTextField(
            value = shellCommand,
            onValueChange = { shellCommand = it },
            label = { Text("Shell Command") }
        )
        // 显示开关说明
        Text(
            text = "Shell commands to be executed when locking the screen",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        Switch(
            checked = isServiceEnabled,
            onCheckedChange = { checked ->
                isServiceEnabled = checked
                val serviceIntent = Intent(context, LockScreenService::class.java)
                // 将输入的 shell 命令作为 Extra 传递给服务
                serviceIntent.putExtra("extra_shell_command", shellCommand)
                if (checked) {
                    // Android 8.0 及以上建议使用 startForegroundService
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                } else {
                    context.stopService(serviceIntent)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceToggleScreenPreview() {
    LockScreenCMDTheme {
        ServiceToggleScreen()
    }
}
