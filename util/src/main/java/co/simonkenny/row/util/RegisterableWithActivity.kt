package co.simonkenny.row.util

import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity

abstract class RegisterableWithActivity {

    protected var isRegistered = false

    @CallSuper
    open fun register(activity: AppCompatActivity) {
        isRegistered = true
    }

    @CallSuper
    open fun unregister(activity: AppCompatActivity) {
        isRegistered = false
    }
}