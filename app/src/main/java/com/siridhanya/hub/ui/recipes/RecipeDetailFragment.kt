package com.siridhanya.hub.ui.recipes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.siridhanya.hub.data.entities.MilletType
import com.siridhanya.hub.data.entities.Recipe
import com.siridhanya.hub.databinding.FragmentRecipeDetailBinding
import com.siridhanya.hub.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {
    private var _b: FragmentRecipeDetailBinding? = null
    private val b get() = _b!!
    private val vm: RecipeViewModel by viewModels()
    private var recipe: Recipe? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentRecipeDetailBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipe = if (android.os.Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelable("recipe", Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("recipe")
        }
        recipe?.let { r ->
            val milletEnum = try { MilletType.valueOf(r.milletType) } catch(e: Exception) { null }
            b.tvRecipeName.text    = r.name
            b.tvKannadaName.text   = r.kannadaName
            b.tvDescription.text   = r.description
            b.tvMilletBadge.text   = "${milletEnum?.emoji ?: "🌾"} ${milletEnum?.displayName ?: r.milletType}"
            b.tvPrepTime.text      = "Prep: ${r.prepTimeMin} min"
            b.tvCookTime.text      = "Cook: ${r.cookTimeMin} min"
            b.tvServings.text      = "Serves: ${r.servings}"
            b.tvCalories.text      = "${r.calories} calories"
            b.tvDifficulty.text    = r.difficulty

            // Ingredients with bullet points
            b.tvIngredients.text = r.ingredients.split("\n").joinToString("\n") { "• $it" }

            // Steps numbered
            b.tvSteps.text = r.steps.split("\n").joinToString("\n\n")

            b.btnFavorite.text = if (r.isFavorite) "❤️ Saved" else "🤍 Save Recipe"
            b.btnFavorite.setOnClickListener {
                vm.toggleFavorite(r)
                b.btnFavorite.text = if (r.isFavorite) "🤍 Save Recipe" else "❤️ Saved"
            }
        }
        b.btnBack.setOnClickListener { findNavController().popBackStack() }
    }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
