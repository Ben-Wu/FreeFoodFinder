package ca.benwu.freefoodfinder.adapters

/**
 * Created by Ben Wu on 2016-09-14.
 */
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.benwu.freefoodfinder.R
import ca.benwu.freefoodfinder.models.InfoSession

import java.util.ArrayList

class SessionAdapter(val sessions: List<InfoSession>) : PagerAdapter() {

    @JvmField val MAX_ELEVATION_FACTOR = 8

    private val mViews: MutableList<CardView?>
    var baseElevation: Float = 0.toFloat()
        private set

    init {
        mViews = ArrayList<CardView?>()

        for (i in 0..sessions.size) {
            mViews.add(null)
        }
    }

    fun getCardViewAt(position: Int): CardView? {
        return mViews[position]
    }

    override fun getCount(): Int {
        return sessions.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.item_session, container, false)
        container.addView(view)
        val cardView = view.findViewById(R.id.cardView) as CardView

        if (baseElevation == 0f) {
            baseElevation = cardView.getCardElevation()
        }

        (view.findViewById(R.id.employerName) as TextView).text = sessions[position].employer
        (view.findViewById(R.id.description) as TextView).text = sessions[position].description
        (view.findViewById(R.id.location) as TextView).text = sessions[position].building.code + " " + sessions[position].building.room
        (view.findViewById(R.id.startTime) as TextView).text = sessions[position].startTime

        cardView.setMaxCardElevation(baseElevation * MAX_ELEVATION_FACTOR)
        mViews[position] = cardView
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
        mViews[position] = null
    }

}