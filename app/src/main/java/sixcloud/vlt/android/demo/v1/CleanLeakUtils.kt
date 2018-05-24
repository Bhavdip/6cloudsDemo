package sixcloud.vlt.android.demo.v1

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import java.lang.reflect.Array
import java.lang.reflect.Field

/**
 * Created by cs03 on 3/5/18.
 */
class CleanLeakUtils {
  companion object Factory {
    fun fixInputMethodManagerLeak(destContext: Context?) {
      if (destContext == null) {
        return
      }

      val inputMethodManager = destContext.applicationContext.getSystemService(
          Context.INPUT_METHOD_SERVICE) as InputMethodManager ?: return

      val viewArray = arrayOf("mCurRootView", "mServedView", "mNextServedView")
      var filed: Field
      var filedObject: Any?

      for (view in viewArray) {
        try {
          filed = inputMethodManager.javaClass.getDeclaredField(view)
          if (!filed.isAccessible) {
            filed.isAccessible = true
          }
          filedObject = filed.get(inputMethodManager)
          if (filedObject != null && filedObject is View) {
            val fileView = filedObject as View?
            if (fileView!!.context === destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
              filed.set(inputMethodManager, null) // 置空，破坏掉path to gc节点
            } else {
              break// 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
            }
          }
        } catch (t: Throwable) {
          t.printStackTrace()
        }

      }
    }


    /**
     * 修复华为手机内存的泄露
     */
    fun fixHuaWeiMemoryLeak(activity: Activity) {
      //测试
      try {
        val GestureBoostManagerClass = Class.forName("android.gestureboost.GestureBoostManager")
        val sGestureBoostManagerField = GestureBoostManagerClass.getDeclaredField(
            "sGestureBoostManager")
        sGestureBoostManagerField.isAccessible = true
        val gestureBoostManager = sGestureBoostManagerField.get(GestureBoostManagerClass)
        val contextField = GestureBoostManagerClass.getDeclaredField("mContext")
        contextField.isAccessible = true
        if (contextField.get(gestureBoostManager) === activity) {
          contextField.set(gestureBoostManager, null)
        }
      } catch (e: ClassNotFoundException) {
      } catch (e: NoSuchFieldException) {
      } catch (e: IllegalAccessException) {
      } catch (t: Throwable) {
      }

    }

    fun freeWebview(webView: WebView) {
      val parent = webView.parent
      if (parent != null) {
        (parent as ViewGroup).removeView(webView)
      }

      webView.stopLoading()
      // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
      webView.settings.javaScriptEnabled = false
      webView.clearHistory()
      webView.clearView()
      webView.removeAllViews()

      try {
        webView.destroy()
      } catch (ex: Throwable) {

      }

    }

    fun fixTextLineCacheLeak() {
      var textLineCached: Field? = null
      try {
        textLineCached = Class.forName("android.text.TextLine").getDeclaredField("sCached")
        textLineCached!!.isAccessible = true
      } catch (ex: Exception) {
        ex.printStackTrace()
      }

      if (textLineCached == null) return
      var cached: Any? = null
      try {
        // Get reference to the TextLine sCached array.
        cached = textLineCached.get(null)
      } catch (ex: Exception) {
        ex.printStackTrace()
      }

      if (cached != null) {
        // Clear the array.
        var i = 0
        val size = Array.getLength(cached)
        while (i < size) {
          Array.set(cached, i, null)
          i++
        }
      }
    }
  }
}