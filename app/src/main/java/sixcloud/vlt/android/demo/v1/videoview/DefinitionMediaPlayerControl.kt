package sixcloud.vlt.android.demo.v1.videoview

import com.dueeeke.videoplayer.controller.MediaPlayerControl

/**
 * Created by bhavdip on 5/24/18.
 */
interface DefinitionMediaPlayerControl: MediaPlayerControl{
    fun getDefinitionData(): LinkedHashMap<String, String>
    fun switchDefinition(definition : String)
}