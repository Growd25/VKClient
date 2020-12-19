package com.growd25.vkclient.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply(action).commit()
}

inline fun FragmentManager.attachDetachFragmentByTag(
    @IdRes containerId: Int,
    tag: String,
    provideFragment: () -> Fragment
) {
    val currentFragment = findFragmentById(containerId)
    if (currentFragment?.tag != tag) {
        transact {
            currentFragment?.let(::detach)
            val fragmentForAttach = findFragmentByTag(tag)
            if (fragmentForAttach != null) {
                attach(fragmentForAttach)
            } else {
                add(containerId, provideFragment(), tag)
            }
        }
    }
}
