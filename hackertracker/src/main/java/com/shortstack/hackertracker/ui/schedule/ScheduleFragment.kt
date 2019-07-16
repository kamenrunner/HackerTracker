package com.shortstack.hackertracker.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.android.material.tabs.TabLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.models.local.Conference
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.schedule.list.OverlapStickyItemDecoration
import com.shortstack.hackertracker.ui.schedule.list.ScheduleAdapter
import com.shortstack.hackertracker.utils.TickTimer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.view_empty.view.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleFragment : Fragment() {

    private val adapter: ScheduleAdapter = ScheduleAdapter()

    private val timer: TickTimer by inject()

    private var disposable: Disposable? = null

    private var shouldScroll = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false) as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        shouldScroll = true
        list.adapter = adapter

        val decoration = OverlapStickyItemDecoration(adapter)
        list.addItemDecoration(decoration)


        val scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        scheduleViewModel.conference.observe(this, Observer {
            addDayTabs(it)
        })

        scheduleViewModel.schedule.observe(this, Observer {
            hideViews()

            if (it != null) {
                adapter.state = it.status

                when (it.status) {
                    Status.SUCCESS -> {
                        val list = adapter.setSchedule(it.data)
                        if (adapter.isEmpty()) {
                            showEmptyView()
                        }

                        scrollToCurrentPosition(list)
                    }
                    Status.ERROR -> {
                        showErrorView(it.message)
                    }
                    Status.LOADING -> {
                        adapter.clearAndNotify()
                        showProgress()
                    }
                    Status.NOT_INITIALIZED -> {
                        showEmptyView()
                    }
                }
            }
        })

        tab_layout.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(tab: TabLayout.Tab) {
                val date = Date(tab.tag as Long)
                scrollToDate(date)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val date = Date(tab.tag as Long)
                scrollToDate(date)
            }
        })
    }

    private fun addDayTabs(conference: Conference) {
        val days = getDaysBetweenDates(conference.startDate, conference.endDate) + conference.endDate

        tab_layout.removeAllTabs()

        val format = SimpleDateFormat("MMM d")

        for (day in days) {
            val tab = tab_layout.newTab()
            tab.text = format.format(day)
            tab.tag = day.time

            tab_layout.addTab(tab)
        }
    }

    private fun scrollToCurrentPosition(data: ArrayList<Any>) {
        val manager = list.layoutManager ?: return
        val first = data.filterIsInstance<Event>().firstOrNull { !it.hasFinished } ?: return

        if (shouldScroll) {
            shouldScroll = false
//            val index = getScrollIndex(data, first)
//            manager.scrollToPosition(index)
        }
    }

    private fun getScrollIndex(data: ArrayList<Any>, first: Event): Int {
        val event = data.indexOf(first)
        val index = data.indexOf(data.subList(0, event).filterIsInstance<Time>().last())
        if (index > 1) {
            if (data[index - 1] is Day) {
                return index - 1
            }
            return index
        }
        return event
    }

    private fun scrollToDate(date: Date) {
        val index = adapter.getDatePosition(date)
        if (index != -1) {
            val scroller = object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
            }
            scroller.targetPosition = index
            list.layoutManager?.startSmoothScroll(scroller)
        }
    }

    override fun onResume() {
        super.onResume()

        disposable = timer.observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // adapter.notifyTimeChanged()
                    if (adapter.isEmpty()) {
                        showEmptyView()
                    } else {
                        hideViews()
                    }
                }
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
        super.onPause()
    }

    private fun showProgress() {
        loading_progress.visibility = View.VISIBLE
    }

    private fun hideViews() {
        empty.visibility = View.GONE
        loading_progress.visibility = View.GONE
    }

    private fun showEmptyView() {
        empty.visibility = View.VISIBLE
    }

    private fun showErrorView(message: String?) {
        empty.title.text = message
        empty.visibility = View.VISIBLE
    }

    fun getDaysBetweenDates(startdate: Date, enddate: Date): List<Date> {
        val dates = ArrayList<Date>()
        val calendar = GregorianCalendar()
        calendar.time = startdate

        while (calendar.time.before(enddate)) {
            val result = calendar.time
            dates.add(result)
            calendar.add(Calendar.DATE, 1)
        }
        return dates
    }

    companion object {

        fun newInstance() = ScheduleFragment()

    }
}