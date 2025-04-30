package com.aakash.chatapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aakash.chatapplication.MainViewModel
import com.aakash.chatapplication.R
import com.aakash.chatapplication.domain.intent.ConnectToChat
import com.aakash.chatapplication.domain.intent.SendMessage
import com.aakash.chatapplication.domain.model.MessageModel
import com.aakash.chatapplication.domain.model.MessageType
import com.aakash.chatapplication.ui.component.ConnectionStatusUI
import com.aakash.chatapplication.ui.component.ReceivedMessageBubble
import com.aakash.chatapplication.ui.component.SendMessageButton
import com.aakash.chatapplication.ui.component.SentMessageBubble
import com.aakash.chatapplication.ui.theme.Spacing

@Composable
fun ChatScreenUI(modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = hiltViewModel()
    val state = viewModel.state.collectAsState()

    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (chatField, sendButton, chatList, textConnectionStatus) = createRefs()

        var chatBoxValue by remember { mutableStateOf(TextFieldValue("")) }

        SendMessageButton(Modifier.constrainAs(sendButton) {
            bottom.linkTo(chatField.bottom)
            top.linkTo(chatField.top)
            end.linkTo(parent.end, margin = Spacing.xxSmall)
        }) {
            viewModel.handleIntent(SendMessage(chatBoxValue.text))
            chatBoxValue = TextFieldValue("")
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(chatField) {
                    start.linkTo(parent.start, margin = Spacing.xxSmall)
                    bottom.linkTo(parent.bottom, margin = Spacing.xxSmall)
                    end.linkTo(sendButton.start, margin = Spacing.xxSmall)
                    width = Dimension.fillToConstraints
                }, value = chatBoxValue, onValueChange = { newText ->
                chatBoxValue = newText
            }, shape = RoundedCornerShape(Spacing.xMedium), placeholder = {
                Text(text = stringResource(R.string.type_message))
            })

        ConnectionStatusUI(
            modifier = Modifier
                .constrainAs(textConnectionStatus) {
                    top.linkTo(parent.top)
                }, state.value.connectionStatus
        )

        ChatList(
            modifier = Modifier
                .constrainAs(chatList) {
                    bottom.linkTo(chatField.top, margin = Spacing.xxSmall)
                    top.linkTo(textConnectionStatus.bottom)
                    height = Dimension.fillToConstraints
                },
            state.value.chatList
        )

    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {}
            Lifecycle.State.RESUMED -> {
                viewModel.handleIntent(ConnectToChat)
            }
        }
    }
}


@Composable
fun ChatList(modifier: Modifier, chatList: MutableList<MessageModel>) {
    val chatListState = rememberLazyListState()
    LaunchedEffect(chatList.size) {
        if (chatList.isNotEmpty())
            chatListState.animateScrollToItem(chatList.size - 1)
    }
    LazyColumn(
        modifier.then(
            Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(bottom = Spacing.xxSmall)
        ), verticalArrangement = Arrangement.Bottom,
        state = chatListState
    ) {
        items(chatList.size) { index ->
            when (chatList[index].type) {
                MessageType.SENT -> {
                    SentMessageBubble(chatList[index].message, chatList[index].isQueued)
                }

                MessageType.RECEIVE -> {
                    ReceivedMessageBubble(chatList[index].message)
                }
            }
        }
    }
}

