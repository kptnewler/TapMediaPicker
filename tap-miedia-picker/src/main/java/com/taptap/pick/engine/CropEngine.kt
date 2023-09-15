package com.taptap.pick.engine

import androidx.fragment.app.Fragment
import com.taptap.pick.data.bean.LocalMedia

interface CropEngine {
    fun onCrop(fragment: Fragment, dataSource: MutableList<LocalMedia>, requestCode: Int)
}