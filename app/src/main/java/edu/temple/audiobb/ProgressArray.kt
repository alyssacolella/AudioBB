package edu.temple.audiobb

import android.util.SparseArray
import androidx.lifecycle.ViewModel
import java.io.Serializable

data class ProgressArray(var times: SparseArray<Int>): Serializable{
}

//class ProgressArray(): ViewModel(), Serializable {
//    private val times: SparseArray<Int> by lazy {
//        SparseArray()
//    }
//}
