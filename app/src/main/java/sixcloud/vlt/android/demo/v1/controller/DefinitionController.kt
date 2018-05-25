package sixcloud.vlt.android.demo.v1.controller

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.AttrRes
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.dueeeke.videoplayer.player.IjkVideoView
import com.dueeeke.videoplayer.util.L
import com.dueeeke.videoplayer.util.WindowUtil
import sixcloud.vlt.android.demo.v1.R
import sixcloud.vlt.android.demo.v1.videoview.DefinitionMediaPlayerControl

/**
 * Created by bhavdip on 5/24/18.
 */

class DefinitionController(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) : StandardVideoController(context, attrs, defStyleAttr) {
    protected var multiRate: TextView? = null
    //    private PopupMenu mPopupMenu;
    private var mPopupWindow: PopupWindow? = null
    private var mRateStr: MutableList<String>? = null
    private var mRateItems: MutableList<TextView>? = null
    private var mPopLayout: LinearLayout? = null

    private var currentIndex: Int = 0

    private val rateOnClickListener = OnClickListener { v ->
        val index = v.tag as Int
        if (currentIndex == index) return@OnClickListener
        mRateItems!![currentIndex].setTextColor(Color.BLACK)
        mRateItems!![index].setTextColor(ContextCompat.getColor(getContext(), R.color.theme_color))
        multiRate!!.text = mRateStr!![index]
        (mediaPlayer as DefinitionMediaPlayerControl).switchDefinition(mRateStr!![index])
        mPopupWindow!!.dismiss()
        hide()
        currentIndex = index
    }

    override fun initView() {
        super.initView()
        multiRate = controllerView.findViewById(R.id.tv_multi_rate)
        multiRate!!.setOnClickListener(this)
        mPopupWindow = PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mPopLayout = LayoutInflater.from(context).inflate(R.layout.layout_rate_pop, this, false) as LinearLayout
        mPopupWindow!!.contentView = mPopLayout
        mPopupWindow!!.setBackgroundDrawable(ColorDrawable(-0x1))
        mPopupWindow!!.isOutsideTouchable = true
        mPopupWindow!!.isClippingEnabled = false
    }

    override fun setPlayerState(playerState: Int) {
        super.setPlayerState(playerState)
        when (playerState) {
            IjkVideoView.PLAYER_NORMAL -> multiRate!!.visibility = GONE
            IjkVideoView.PLAYER_FULL_SCREEN -> multiRate!!.visibility = VISIBLE
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        val i = v.id
        if (i == R.id.tv_multi_rate) {
            showRateMenu()
        }
    }

    override fun hide() {
        super.hide()
        if (mPopupWindow!!.isShowing) {
            mPopupWindow!!.dismiss()
        }
    }

    private fun showRateMenu() {
        mPopLayout!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        mPopupWindow!!.showAsDropDown(multiRate, -((mPopLayout!!.measuredWidth - multiRate!!.measuredWidth) / 2),
                -(mPopLayout!!.measuredHeight + multiRate!!.measuredHeight + WindowUtil.dp2px(context, 10f)))
    }


    override fun setProgress(): Int {
        if (multiRate != null && TextUtils.isEmpty(multiRate!!.text)) {
            L.d("multiRate")
            val multiRateData = (mediaPlayer as DefinitionMediaPlayerControl).getDefinitionData() ?: return super.setProgress()
            mRateStr = java.util.ArrayList()
            mRateItems = java.util.ArrayList()
            var index = 0
            val iterator = ArrayList(multiRateData.entries).listIterator()
            while (iterator.hasPrevious()) {//反向遍历
                val entry = iterator.previous()
                mRateStr!!.add(entry.key)
                val rateItem = LayoutInflater.from(context).inflate(R.layout.layout_rate_item, null) as TextView
                rateItem.setText(entry.key)
                rateItem.tag = index
                rateItem.setOnClickListener(rateOnClickListener)
                mPopLayout!!.addView(rateItem)
                mRateItems!!.add(rateItem)
                index++
            }
            mRateItems!![index - 1].setTextColor(ContextCompat.getColor(context, R.color.theme_color))
            multiRate!!.text = mRateStr!![index - 1]
            currentIndex = index - 1
        }
        return super.setProgress()
    }
}
