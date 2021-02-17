package com.twoinc.twopay

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener


class reloadGateway : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val TAG = "FragmentActivity"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reload_gateway)

        val intent = intent
        val reloadVal = intent.getStringExtra("Value")

        val amount = "RM $reloadVal"
        Log.d(TAG, "Amount : $amount")
        val amountHolder = findViewById<TextView>(R.id.textView2)
        amountHolder.setText(amount)

        val tabLayout = findViewById<View>(R.id.tabLayout) as TabLayout
        val viewPager = findViewById<View>(R.id.viewPager) as ViewPager2

        tabLayout.addTab(tabLayout.newTab().setText("Home"))
        tabLayout.addTab(tabLayout.newTab().setText("Sport"))
        tabLayout.addTab(tabLayout.newTab().setText("Movie"))
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)

//        val adapter = MyAdapter(this, supportFragmentManager, tabLayout.getTabCount())
//        viewPager.setAdapter(adapter)
//
//        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
//
//        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                viewPager.setCurrentItem(tab.position)
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })
    }
}

