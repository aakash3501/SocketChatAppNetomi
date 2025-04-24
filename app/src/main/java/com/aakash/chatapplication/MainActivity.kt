package com.aakash.chatapplication

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.aakash.chatapplication.model.MessageType
import com.aakash.chatapplication.ui.component.ReceivedMessageBubble
import com.aakash.chatapplication.ui.component.SentMessageBubble
import com.aakash.chatapplication.ui.theme.ChatApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatScreenUI(
                        modifier = Modifier.padding(innerPadding),
                        viewModel
                    )
                }
            }
        }
        registerForNetworkChange()
    }

    override fun onResume() {
        super.onResume()
        viewModel.connectToPieSocket()
    }

    private fun registerForNetworkChange(){
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                viewModel.connectToPieSocket()
            }

            // Network capabilities have changed for the network
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                Log.d("network------", "changed")
//                val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("network------", "lost")
            }
        }


        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
}


@Composable
fun ChatScreenUI(modifier: Modifier = Modifier, viewModel: MainViewModel) {

    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (chatField, sendButton, chatList, textConnectionStatus) = createRefs()
        var chatBoxValue by remember { mutableStateOf(TextFieldValue("")) }
        IconButton(modifier = Modifier.constrainAs(sendButton) {
            bottom.linkTo(chatField.bottom)
            top.linkTo(chatField.top)
            end.linkTo(parent.end, margin = 8.dp)
        }, onClick = {
            viewModel.sendMessage(chatBoxValue.text)
            chatBoxValue = TextFieldValue("")
        }) {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = Color.Green)
                    .padding(8.dp),
                imageVector = Icons.AutoMirrored.Outlined.Send,
                contentDescription = "",
                tint = Color.White
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(chatField) {
                    start.linkTo(parent.start, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                    end.linkTo(sendButton.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                }, value = chatBoxValue, onValueChange = { newText ->
                chatBoxValue = newText
            }, shape = RoundedCornerShape(25.dp), placeholder = {
                Text(text = "Type something")
            })

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (viewModel.isConnected) Color.Green else Color.Red)
                .padding(4.dp)
                .constrainAs(textConnectionStatus) {
                    top.linkTo(parent.top)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (viewModel.isConnected) "Connected" else "Disconnected",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )
        }

        val chatListState = rememberLazyListState()
        LaunchedEffect(viewModel.conversation.size) {
            if (viewModel.conversation.isNotEmpty())
                chatListState.animateScrollToItem(viewModel.conversation.size - 1)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(bottom = 8.dp)
                .constrainAs(chatList) {
                    bottom.linkTo(chatField.top, margin = 8.dp)
                    top.linkTo(textConnectionStatus.bottom)
                    height = Dimension.fillToConstraints
                }, verticalArrangement = Arrangement.Bottom,
            state = chatListState
        ) {
            items(viewModel.conversation) { item ->
                when (item.type) {
                    MessageType.SENT -> {
                        SentMessageBubble(item.message,item.isQueued)
                    }

                    MessageType.RECEIVE -> {
                        ReceivedMessageBubble(item.message)
                    }
                }
            }
        }

    }
}
