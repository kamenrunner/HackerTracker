package com.shortstack.hackertracker.ui.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.information.faq.FAQFragment
import com.shortstack.hackertracker.ui.information.info.InfoFragment
import com.shortstack.hackertracker.ui.information.speakers.SpeakersFragment
import com.shortstack.hackertracker.ui.information.vendors.VendorsFragment
import kotlinx.android.synthetic.main.fragment_information.*

class InformationFragment : Fragment() {

    companion object {

//        private const val INFO = 0
        private const val FAQ = 0
        private const val SPEAKERS = 1
        private const val VENDORS = 2


        fun newInstance(): InformationFragment {
            return InformationFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_information, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setNavigationOnClickListener {
            (context as MainActivity).openNavDrawer()
        }


        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment = when (tab.position) {
//                    INFO -> InfoFragment.newInstance()
                    SPEAKERS -> SpeakersFragment.newInstance()
                    FAQ -> FAQFragment.newInstance()
                    VENDORS -> VendorsFragment.newInstance()
                    else -> throw IndexOutOfBoundsException("Position out of bounds: ${tab.position}")
                }


                childFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, "")
                        .addToBackStack(null)
                        .commit()

            }
        })



//        tabs.addTab(tabs.newTab().setText("Info"))
        tabs.addTab(tabs.newTab().setText("FAQ"))
        tabs.addTab(tabs.newTab().setText("Speakers"))
        tabs.addTab(tabs.newTab().setText("Vendors"))
    }
}