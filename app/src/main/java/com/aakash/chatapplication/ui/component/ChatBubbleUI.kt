package com.aakash.chatapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SentMessageBubble(text: String, queued: Boolean) {
    Spacer(modifier = Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 6.dp, start = 50.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 48f, topEnd = 48f, bottomStart = 48f, bottomEnd = 0f
                    )
                )
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = text, color = Color.DarkGray)
                if (queued)
                    Icon(
                        modifier = Modifier.size(15.dp),
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "",
                        tint = Color.Black
                    )
            }
        }
    }
}

@Composable
fun ReceivedMessageBubble(text: String) {
    Spacer(modifier = Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .padding(start = 6.dp, end = 50.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 48f, topEnd = 48f, bottomStart = 0f, bottomEnd = 48f
                )
            )
            .background(Color.Yellow)
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, color = Color.DarkGray)
    }
}