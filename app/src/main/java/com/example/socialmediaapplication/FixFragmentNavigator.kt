package com.example.socialmediaapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("fixFragment")
class FixFragmentNavigator(
    context: Context,
    fragmentManager: FragmentManager, containerId: Int
) : FragmentNavigator(context, fragmentManager, containerId) {

    private val mContext: Context = context
    private val mFragmentManager: FragmentManager = fragmentManager
    private val mContainerId: Int = containerId
    private val TAG = "FixFragmentNavigator"

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (mFragmentManager.isStateSaved) {
            Log.i(
                TAG, "Ignoring navigate() call: FragmentManager has already"
                        + " saved its state"
            )
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = mContext.packageName + className
        }
        // 自定义FragmentNavigator为了实现重用fragment
//        val frag = instantiateFragment(
//            mContext, mFragmentManager,
//            className, args
//        )
//        frag.arguments = args
        val ft = mFragmentManager.beginTransaction()

        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }
        // 获取正在显示的fragment
        val fragment = mFragmentManager.primaryNavigationFragment
        // 如果正在显示的fragment不为空，就隐藏它，将下一个页面展示出来
        if (fragment != null) {
            ft.hide(fragment)
        }
        // 通过tag查找fragment
        var frag: Fragment?
        val tag : String = destination.id.toString()
        frag = mFragmentManager.findFragmentByTag(tag)
        // 如果没有实例化过，则创建
        if (frag == null) {
            frag = instantiateFragment(mContext, mFragmentManager, className, args)
            frag.arguments = args
            ft.add(mContainerId, frag, tag)
        } else {
            // 如果实例化过，则显示出来
            ft.show(frag)
        }

//        ft.replace(mContainerId, frag)
        ft.setPrimaryNavigationFragment(frag)

        @IdRes val destId = destination.id
        // mBackStack是回退堆栈。是private的，所以通过反射获取
//        val initialNavigation = mBackStack.isEmpty()
        var mBackStack: java.util.ArrayDeque<Int>?
        val field = FragmentNavigator::class.java.getDeclaredField("mBackStack")
        field.isAccessible = true
        mBackStack = field.get(this) as java.util.ArrayDeque<Int>
        val initialNavigation = mBackStack.isEmpty()
        // TODO Build first class singleTop behavior for fragments
        // TODO Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop() && mBackStack.last() == destId)

        val isAdded: Boolean
        isAdded = if (initialNavigation) {
            true
        } else if (isSingleTopReplacement) {
            // Single Top means we only want one instance on the back stack
            if (mBackStack.size > 1) {
                // If the Fragment to be replaced is on the FragmentManager's
                // back stack, a simple replace() isn't enough so we
                // remove it from the back stack and put our replacement
                // on the back stack in its place
                mFragmentManager.popBackStack(
                    generateBackStackName(mBackStack.size, mBackStack.last()),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
            }
            false
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
            true
        }
        if (navigatorExtras is Extras) {
            for (sharedElement in navigatorExtras.sharedElements) {
                ft.addSharedElement(sharedElement.key, sharedElement.value)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        // The commit succeeded, update our view of the world
        // The commit succeeded, update our view of the world
        return if (isAdded) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }


    private fun generateBackStackName(backStackIndex: Int, destId: Int): String {
        return "$backStackIndex-$destId"
    }
}