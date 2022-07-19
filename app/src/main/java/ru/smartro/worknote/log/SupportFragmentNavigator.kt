package ru.smartro.worknote.log

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.*

abstract class SupportFragmentNavigator(fragmentManager: FragmentManager, containerId: Int) : Navigator {
    private val fragmentManager: FragmentManager
    private val containerId: Int
    override fun applyCommand(command: Command) {

        if (command is Forward) {
            val forward = command
            fragmentManager
                .beginTransaction()
                .replace(containerId, createFragment(forward.screenKey, forward.transitionData))
                .addToBackStack(forward.screenKey)
                .commit()
        } else if (command is Back) {
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStackImmediate()
            } else {
                exit()
            }

        } else if (command is Replace) {
            val replace = command
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStackImmediate()
                fragmentManager
                    .beginTransaction()
                    .replace(containerId, createFragment(replace.screenKey, replace.transitionData))
                    .addToBackStack(replace.screenKey)
                    .commit()
            } else {
                fragmentManager
                    .beginTransaction()
                    .replace(containerId, createFragment(replace.screenKey, replace.transitionData))
                    .commit()
            }
        } else if (command is BackTo) {
            val key = command.screenKey
            if (key == null) {
                backToRoot()
            } else {
                var hasScreen = false
                for (i in 0 until fragmentManager.getBackStackEntryCount()) {
                    if (key == fragmentManager.getBackStackEntryAt(i).getName()) {
                        fragmentManager.popBackStackImmediate(key, 0)
                        hasScreen = true
                        break
                    }
                }
                if (!hasScreen) {
                    backToUnexisting()
                }
            }
        } else if (command is SystemMessage) {
            showSystemMessage(command.message)
        }
    }

    private fun backToRoot() {
        for (i in 0 until fragmentManager.getBackStackEntryCount()) {
            fragmentManager.popBackStack()
        }
        fragmentManager.executePendingTransactions()
    }

    /**
     * Creates Fragment matching `screenKey`.
     * @param screenKey screen key
     * @param data initialization data
     * @return instantiated fragment for the passed screen key
     */
    protected abstract fun createFragment(screenKey: String?, data: Any?=null): Fragment

    /**
     * Shows system message.
     * @param message message to show
     */
    protected abstract fun showSystemMessage(message: String?)

    /**
     * Called when we try to back from the root.
     */
    protected abstract fun exit()

    /**
     * Called when we tried to back to some specific screen, but didn't found it.
     */
    protected fun backToUnexisting() {
        backToRoot()
    }

    /**
     * Creates SupportFragmentNavigator.
     * @param fragmentManager support fragment manager
     * @param containerId id of the fragments container layout
     */
    init {
        this.fragmentManager = fragmentManager
        this.containerId = containerId
    }

//    /**
//     * Created by Konstantin Tckhovrebov (aka @terrakok)
//     * on 11.10.16
//     */
//
//    /**
//     * Created by Konstantin Tckhovrebov (aka @terrakok)
//     * on 11.10.16
//     */
//    /**
//     * The low-level navigation interface.
//     * Navigator is the one who actually performs any transition.
//     */
//    interface Navigator {
//        /**
//         * Performs transition described by the navigation command
//         *
//         * @param command the navigation command to apply
//         */
//        fun applyCommand(command: Command?)
//    }

}
