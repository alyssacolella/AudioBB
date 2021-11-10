package edu.temple.audiobb

import org.json.JSONObject
import java.io.Serializable

// Simple data class
data class Book(val id: Int, val title: String, val author: String, val coverURL: String) : Serializable
