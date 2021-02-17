package com.twoinc.twopay

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter

@Suppress("DEPRECATION")
class MyAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                CardFragment()
            }
            1 -> {
                BankFragment()
            }
            else -> EmptyFragment()
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

}
