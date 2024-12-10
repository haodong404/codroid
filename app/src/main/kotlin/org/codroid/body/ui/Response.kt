package org.codroid.body.ui

class Response<T> (state: Int) {

    constructor(state: Int, result: T) : this(state) {
        this.result = result
    }

    constructor(state: Int, message: String) : this(state) {
        this.errorMessage = message
    }

    private var stateCode: Int = state
    var result: T? = null
    var errorMessage: String? = null

    companion object {
        const val SUCCEED = 1;
        const val FAILED = 2;
    }

    fun isSucceed(): Boolean {
        return stateCode == SUCCEED
    }
}