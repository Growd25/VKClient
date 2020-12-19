package com.growd25.vkclient.utils

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.growd25.vkclient.R

fun AppCompatActivity.addFragment(@IdRes containerId: Int, fragment: Fragment) {
    supportFragmentManager.transact {
        setCustomAnimations(R.anim.slide_in_left, 0)
        add(containerId, fragment)
    }
}

fun AppCompatActivity.replaceFragment(
    @IdRes containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true,
    tag:String? = null
) {
    supportFragmentManager.transact {
        if (addToBackStack) {
            addToBackStack(null)
        }
        setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
        replace(containerId, fragment,tag)
    }
}
