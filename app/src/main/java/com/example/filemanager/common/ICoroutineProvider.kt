package com.alphaverse.grocerymart.common

import kotlinx.coroutines.CoroutineDispatcher

interface ICoroutineProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}