package sixcloud.vlt.android.demo.v1

import android.support.v7.app.AppCompatActivity

/**
 * Created by cs03 on 3/5/18.
 */
open class BaseAppCompatActivity : AppCompatActivity() {
  override fun onDestroy() {
    CleanLeakUtils.fixInputMethodManagerLeak(this)
    CleanLeakUtils.fixHuaWeiMemoryLeak(this)
    CleanLeakUtils.fixTextLineCacheLeak()
    super.onDestroy()
  }
}