package ca.benwu.uwinfosessions.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.benwu.uwinfosessions.R
import ca.benwu.uwinfosessions.models.InfoSession

/**
 * Created by Ben Wu on 2016-09-13.
 */
class DateAdapter(private val context: Context, private val sessions: List<List<InfoSession>>): PagerAdapter() {

    val layoutInflater = LayoutInflater.from(context)

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view!!.equals(`object`)
    }

    override fun getCount(): Int {
        return sessions.size
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val view: View

        view = layoutInflater.inflate(R.layout.item_date, container, false)

        val employerName: TextView = view.findViewById(R.id.date) as TextView

        employerName.text = sessions[position][0].date

        //val verticalInfiniteCycleViewPager = view.findViewById(R.id.vicvp) as VerticalInfiniteCycleViewPager
        //verticalInfiniteCycleViewPager.adapter = VerticalPagerAdapter(mContext, TWO_WAY_LIBRARIES[position])
        //verticalInfiniteCycleViewPager.currentItem = 0

        container!!.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}