package com.example.socialmediaapplication.utils

import android.content.ComponentName
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavigatorProvider
import com.example.socialmediaapplication.FixFragmentNavigator
import com.example.socialmediaapplication.model.Destination

class NavGraphBuilder {
    companion object{
        fun build(navController: NavController, fragmentActivity: FragmentActivity, @IdRes containerId: Int){
            val provider : NavigatorProvider = navController.navigatorProvider
//            val fragmentNavigator = provider.getNavigator(FragmentNavigator::class.java)
            // 使用自定义的FragmentNavigator
            val fragmentNavigator = FixFragmentNavigator(
                fragmentActivity,
                fragmentActivity.supportFragmentManager,
                containerId
            )
            val activityNavigator = provider.getNavigator(ActivityNavigator::class.java)

            val destConfig  : HashMap<String,Destination> = AppConfig.getDestConfig()
            // 将自定义的FragmentNavigator添加到导航管理器中
            provider.addNavigator(fragmentNavigator)
            destConfig.values.forEach(){
                if (it.isFragment){
                    val destination = fragmentNavigator.createDestination()
                    destination.setClassName(it.className)
                    destination.id = it.id
                    destination.addDeepLink(it.pageUrl)
                    navController.graph.addDestination(destination)
                }else{
                    val destination = activityNavigator.createDestination()
                    destination.id = it.id
                    destination.addDeepLink(it.pageUrl)
                    destination.setComponentName(ComponentName(AppGlobals.getApplication()!!.packageName,it.className))
                    navController.graph.addDestination(destination)
                }
                if (it.asStarter){
                    navController.graph.setStartDestination(it.id)
                }
            }
        }
    }
}