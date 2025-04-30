package com.aakash.chatapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.aakash.chatapplication.R
import com.aakash.chatapplication.ui.theme.Spacing
import com.aakash.chatapplication.utils.ConnectionStatus


@Composable
fun ConnectionStatusUI(modifier: Modifier, connectionStatus: ConnectionStatus) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .background(if (connectionStatus == ConnectionStatus.CONNECTED) Color.Green else Color.Red)
                .padding(Spacing.xSmall)
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (connectionStatus == ConnectionStatus.CONNECTED) stringResource(
                R.string.connecting
            ) else stringResource(R.string.disconnected),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(Spacing.xSmall)
        )
    }
}