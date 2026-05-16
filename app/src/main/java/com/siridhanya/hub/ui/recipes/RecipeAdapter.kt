package com.siridhanya.hub.ui.recipes

import android.view.*
import androidx.recyclerview.widget.*
import com.siridhanya.hub.data.entities.MilletType
import com.siridhanya.hub.data.entities.Recipe
import com.siridhanya.hub.databinding.ItemRecipeCardBinding

class RecipeAdapter(
    private val onRecipeClick: (Recipe) -> Unit,
    private val onFavoriteClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.VH>(DIFF) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(a: Recipe, b: Recipe) = a.id == b.id
            override fun areContentsTheSame(a: Recipe, b: Recipe) = a == b
        }
    }
    inner class VH(private val b: ItemRecipeCardBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(r: Recipe) {
            val milletEnum = try { MilletType.valueOf(r.milletType) } catch (e: Exception) { null }
            b.tvRecipeName.text    = r.name
            b.tvKannadaName.text   = r.kannadaName
            b.tvMilletTag.text     = "${milletEnum?.emoji ?: "🌾"} ${milletEnum?.displayName ?: r.milletType}"
            b.tvPrepTime.text      = "⏱ ${r.prepTimeMin + r.cookTimeMin} min"
            b.tvDifficulty.text    = r.difficulty
            b.tvCalories.text      = "${r.calories} cal"
            b.btnFavorite.text     = if (r.isFavorite) "❤️" else "🤍"
            b.btnFavorite.setOnClickListener { onFavoriteClick(r) }
            b.root.setOnClickListener { onRecipeClick(r) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemRecipeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(getItem(pos))
}
