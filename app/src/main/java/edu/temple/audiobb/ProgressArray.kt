package edu.temple.audiobb

import android.util.SparseArray
import java.io.Serializable

data class ProgressArray(val times: SparseArray<Int>): Serializable{
    companion object {
        private const val serialVersionUID: Long = -5133191997535836845L
    }
}
