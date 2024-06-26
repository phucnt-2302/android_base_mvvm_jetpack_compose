package com.truongdc.android.base.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truongdc.android.base.ui.components.state.UiStateDelegate
import com.truongdc.android.base.data.remote.error.ErrorResponse
import com.truongdc.android.base.data.base.DataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    fun <T> UiStateDelegate<*, *>.launchTaskSync(
        isLoading: Boolean = false,
        onRequest: suspend CoroutineScope.() -> DataResult<T>,
        onSuccess: (T) -> Unit = {},
        onError: (Exception) -> Unit = {},
        onCompletion: () -> Unit = {},
    ) = viewModelScope.launch {
        if (isLoading) showLoading()
        when (val asynchronousTasks = onRequest(this)) {
            is DataResult.Success -> onSuccess(asynchronousTasks.data)
            is DataResult.Error -> {
                val throwable = asynchronousTasks.exception
                onError(throwable)
                ErrorResponse.convertToRetrofitException(throwable).run {
                    val errorResponse = getErrorResponse()
                    if (errorResponse != null) {
                        onSendErrorResponse(errorResponse)
                    } else {
                        onSendError(throwable)
                    }
                }
            }

            is DataResult.Loading -> {}
        }
    }.also { job ->
        job.invokeOnCompletion {
            onCompletion()
            if (isLoading) hideLoading()
        }
    }
}
