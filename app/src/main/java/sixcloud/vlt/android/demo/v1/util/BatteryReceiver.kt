package sixcloud.vlt.android.demo.v1.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import sixcloud.vlt.android.demo.v1.R

/**
 * Created by bhavdip on 5/24/18.
 */
class BatteryReceiver(val pow: ImageView) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val extras = intent!!.extras ?: return
        val current = extras.getInt("level")// 获得当前电量
        val total = extras.getInt("scale")// 获得总电量
        val percent = current * 100 / total
        when {
            percent < 15 -> pow.setImageResource(R.drawable.ic_action_battery_10)
            percent < 25 -> pow.setImageResource(R.drawable.ic_action_battery_20)
            percent < 35 -> pow.setImageResource(R.drawable.ic_action_battery_30)
            percent < 45 -> pow.setImageResource(R.drawable.ic_action_battery_40)
            percent < 55 -> pow.setImageResource(R.drawable.ic_action_battery_50)
            percent < 65 -> pow.setImageResource(R.drawable.ic_action_battery_60)
            percent < 75 -> pow.setImageResource(R.drawable.ic_action_battery_70)
            percent < 85 -> pow.setImageResource(R.drawable.ic_action_battery_80)
            percent < 95 -> pow.setImageResource(R.drawable.ic_action_battery_90)
            else -> pow.setImageResource(R.drawable.ic_action_battery)
        }
    }

}