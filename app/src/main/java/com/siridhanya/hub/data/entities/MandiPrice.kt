package com.siridhanya.hub.data.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "mandi_prices")
data class MandiPrice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "millet_type")   val milletType: String,
    @ColumnInfo(name = "city")          val city: String,
    @ColumnInfo(name = "price_per_kg")  val pricePerKg: Double,
    @ColumnInfo(name = "week_high")     val weekHigh: Double,
    @ColumnInfo(name = "week_low")      val weekLow: Double,
    @ColumnInfo(name = "trend")         val trend: String = "STABLE", // UP, DOWN, STABLE
    @ColumnInfo(name = "last_updated")  val lastUpdated: String,
    @ColumnInfo(name = "market_name")   val marketName: String = ""
) : Parcelable {
    fun trendEmoji() = when(trend) {
        "UP" -> "📈"
        "DOWN" -> "📉"
        else -> "➡️"
    }
    fun trendColor() = when(trend) {
        "UP" -> "#2E7D32"
        "DOWN" -> "#C62828"
        else -> "#E65100"
    }
}
