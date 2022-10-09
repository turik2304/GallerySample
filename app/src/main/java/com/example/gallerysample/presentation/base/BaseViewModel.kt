package com.example.gallerysample.presentation.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Store which manages business data and state.
 */
abstract class BaseViewModel<A : BaseAction, S : BaseState, E : BaseSideEffect> : ViewModel() {

    protected abstract val initialState: S

    protected val actions: MutableSharedFlow<A> = MutableSharedFlow()

    protected val states: MutableSharedFlow<S> = MutableSharedFlow()

    protected val sideEffects: MutableSharedFlow<E> = MutableSharedFlow()

    /**
     * Returns the current state. It is equal to the last value returned by the store's reducer.
     */
    val observableStates: StateFlow<S> by lazy {
        states.onEach { Log.d("ViewModel", "Received state: $it") }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = initialState
        )
    }

    /**
     * Returns the current side effect.
     */
    val observableSideEffects: Flow<E>
        get() = sideEffects.onEach { Log.d("ViewModel", "Received effect: $it") }

    /**
     * Dispatches an action. This is the only way to trigger a state change.
     */
    fun dispatch(action: A) {
        Log.d("ViewModel", "Received action: $action")
        viewModelScope.launch { actions.emit(action) }
    }

}