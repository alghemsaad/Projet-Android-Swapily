package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.Swap
import com.swapily.app.data.model.Message
import com.swapily.app.data.repository.SwapRepository
import com.swapily.app.data.repository.AuthRepository
import com.swapily.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SwapViewModel : ViewModel() {
    private val repository = SwapRepository()
    private val authRepository = AuthRepository()
    private val productRepository = ProductRepository()

    private val _swaps = MutableStateFlow<List<Swap>>(emptyList())
    val swaps = _swaps.asStateFlow()

    // Accepted/completed swaps for history/archive
    private val _swapHistory = MutableStateFlow<List<Swap>>(emptyList())
    val swapHistory = _swapHistory.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private var fetchSwapsJob: Job? = null
    private var fetchMessagesJob: Job? = null
    private var fetchHistoryJob: Job? = null

    init {
        fetchSwaps()
        fetchSwapHistory()
    }

    fun fetchSwaps() {
        val userId = authRepository.getCurrentUserUid() ?: return
        fetchSwapsJob?.cancel()
        fetchSwapsJob = viewModelScope.launch {
            _loading.value = true
            repository.getAllUserSwaps(userId).collect {
                _swaps.value = it
                _loading.value = false
            }
        }
    }

    /**
     * Subscribes to real-time accepted swaps for history/archive.
     */
    fun fetchSwapHistory() {
        val userId = authRepository.getCurrentUserUid() ?: return
        fetchHistoryJob?.cancel()
        fetchHistoryJob = viewModelScope.launch {
            repository.getSwapHistory(userId).collect {
                _swapHistory.value = it
            }
        }
    }

    fun fetchMessages(swapId: String) {
        fetchMessagesJob?.cancel()
        fetchMessagesJob = viewModelScope.launch {
            repository.getSwapMessages(swapId).collect {
                _messages.value = it
            }
        }
    }

    fun clearData() {
        fetchSwapsJob?.cancel()
        fetchMessagesJob?.cancel()
        fetchHistoryJob?.cancel()
        _swaps.value = emptyList()
        _messages.value = emptyList()
        _swapHistory.value = emptyList()
    }

    fun sendMessage(swapId: String, text: String) {
        val userId = authRepository.getCurrentUserUid() ?: return
        viewModelScope.launch {
            val message = Message(
                swapId = swapId,
                senderId = userId,
                text = text
            )
            repository.sendMessage(swapId, message)
        }
    }

    fun proposeSwap(
        receiverId: String,
        receiverName: String,
        receiverImage: String,
        senderProductId: String,
        senderProductTitle: String,
        senderProductImage: String,
        receiverProductId: String,
        receiverProductTitle: String,
        receiverProductImage: String,
        senderName: String,
        senderImage: String
    ) {
        val userId = authRepository.getCurrentUserUid() ?: return
        
        viewModelScope.launch {
            val swap = Swap(
                senderId = userId,
                senderName = senderName, 
                senderImage = senderImage,
                receiverId = receiverId,
                receiverName = receiverName,
                receiverImage = receiverImage,
                senderProductId = senderProductId,
                senderProductTitle = senderProductTitle,
                senderProductImage = senderProductImage,
                receiverProductId = receiverProductId,
                receiverProductTitle = receiverProductTitle,
                receiverProductImage = receiverProductImage,
                status = "PENDING",
                lastSenderId = userId, // Important: Mark sender
                read = false           // Unread for the receiver
            )
            repository.proposeSwap(swap)
        }
    }

    fun updateSwapStatus(swapId: String, status: String) {
        viewModelScope.launch {
            if (status == "ACCEPTED") {
                // Use batch write for atomic swap acceptance
                val swap = _swaps.value.find { it.id == swapId } ?: return@launch
                val result = repository.acceptSwapRequest(
                    swapId = swap.id,
                    senderProductId = swap.senderProductId,
                    receiverProductId = swap.receiverProductId
                )
                if (result.isSuccess) {
                    // Refresh swaps and products to reflect changes
                    fetchSwaps()
                    fetchSwapHistory()
                }
            } else {
                repository.updateSwapStatus(swapId, status)
            }
        }
    }

    fun markAsRead(swapId: String) {
        viewModelScope.launch {
            repository.markSwapAsRead(swapId)
        }
    }
}
