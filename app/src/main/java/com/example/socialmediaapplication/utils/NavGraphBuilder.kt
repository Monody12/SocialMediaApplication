package com.example.socialmediaapplication.utils

import android.content.ComponentName
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.FragmentNavigator
import com.example.socialmediaapplication.model.Destination

class NavGraphBuilder {
    companion object{
        fun build(navController: NavController){
            val provider : NavigatorProvider = navController.navigatorProvider
            val fragmentNavigator = provider.getNavigator(FragmentNavigator::class.java)
            val activityNavigator = provider.getNavigator(ActivityNavigator::class.java)

            val destConfig  : HashMap<String,Destination> = AppConfig.getDestConfig()
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