package ca.benwu.uwinfosessions.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import ca.benwu.uwinfosessions.models.InfoSession

/**
 * Created by Ben Wu on 2016-09-13.
 */
class SessionAdapter(private val context: Context, private val sessions: InfoSession): PagerAdapter() {

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}