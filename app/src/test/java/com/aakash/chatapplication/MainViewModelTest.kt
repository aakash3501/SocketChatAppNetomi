package com.aakash.chatapplication

import com.aakash.chatapplication.domain.OnConnected
import com.aakash.chatapplication.domain.OnMessageReceived
import com.aakash.chatapplication.domain.intent.ConnectToChat
import com.aakash.chatapplication.domain.model.MessageModel
import com.aakash.chatapplication.domain.model.MessageType
import com.aakash.chatapplication.domain.repository.PieSocketRepository
import com.aakash.chatapplication.domain.state.ChatScreenState
import com.aakash.chatapplication.utils.ConnectionStatus
import com.aakash.chatapplication.utils.ConnectivityHelper
import com.aakash.chatapplication.utils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxUnitFun = true, relaxed = true)
    lateinit var pieSocketRepository: PieSocketRepository

    @MockK(relaxUnitFun = true, relaxed = true)
    lateinit var connectivityHelper: ConnectivityHelper

    lateinit var subject: MainViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        subject = MainViewModel(pieSocketRepository, connectivityHelper)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `connect to socket and receive message successfully`() = runTest {
        val testResults = mutableListOf<ChatScreenState>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            subject.state.toList(testResults)
        }

        coEvery { pieSocketRepository.connectToSocket() } returns flowOf(
            OnConnected,
            OnMessageReceived("Hello test")
        )

        subject.handleIntent(ConnectToChat)

        assertEquals(2, testResults.size)
        assertEquals(ChatScreenState(), testResults[0])
        assertEquals(
            ChatScreenState(
                connectionStatus = ConnectionStatus.CONNECTED, chatList = mutableListOf(
                    MessageModel(message = "Hello test", type = MessageType.RECEIVE)
                )
            ), testResults[1]
        )
        coVerify { pieSocketRepository.connectToSocket() }
    }

}