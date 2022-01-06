package com.jera.justtalk.ui.utils

fun String.isFormattedEmail(): Boolean {
    return (android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches())
}
