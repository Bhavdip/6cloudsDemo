package sixcloud.vlt.android.demo.v1.videoview

import android.content.Context
import android.util.AttributeSet
import com.dueeeke.videoplayer.player.IjkVideoView

/**
 * Created by bhavdip on 5/24/18.
 */
class DefinitionIjkVideoView : IjkVideoView, DefinitionMediaPlayerControl {

    private var mCurrentDefinition: String? = null

    private var mDefinitionMap:LinkedHashMap<String,String> = LinkedHashMap()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun getDefinitionData(): LinkedHashMap<String, String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return mDefinitionMap
    }

    override fun switchDefinition(definition: String) {
        val url = mDefinitionMap!![definition]
        if (definition == mCurrentDefinition) return
        mCurrentUrl = url
        addDisplay()
        currentPosition
        startPrepare(true)
        mCurrentDefinition = definition
    }

    fun setDefinitionVideos(videos: LinkedHashMap<String, String>) {
        this.mDefinitionMap = videos
        this.mCurrentUrl = getValueFromLinkedMap(videos, 0)
    }

    companion object {

        fun getValueFromLinkedMap(map: LinkedHashMap<String, String>, index: Int): String? {
            return map.keys
                    .filterIndexed { currentIndex, key -> currentIndex == index }
                    .firstOrNull()
                    ?.let { map[it] }
        }
    }

}
