package com.growd25.vkclient.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

fun Fragment.addFragment(@IdRes containerId: Int, fragment: Fragment, tag: String? = null) {
    childFragmentManager.transact {
        add(containerId, fragment, tag)
    }
}

inline fun Fragment.attachDetachFragmentByTag(
    @IdRes containerId: Int,
    tag: String,
    provideFragment: () -> Fragment
) {
    childFragmentManager.attachDetachFragmentByTag(
        containerId = containerId,
        tag = tag,
        provideFragment = provideFragment
    )
}
