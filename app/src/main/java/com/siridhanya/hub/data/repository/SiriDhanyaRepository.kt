package com.siridhanya.hub.data.repository

import com.siridhanya.hub.data.dao.*
import com.siridhanya.hub.data.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SiriDhanyaRepository @Inject constructor(
    private val mandiDao:  MandiPriceDao,
    private val recipeDao: RecipeDao,
    private val healthDao: HealthBenefitDao,
    private val fpoDao:    FpoContactDao
) {
    // ── SEED ──────────────────────────────────────────────────────────────
    suspend fun seedIfEmpty() {
        if (mandiDao.getCount()  == 0) mandiDao.insertAll(SeedData.mandiPrices)
        if (recipeDao.getCount() == 0) recipeDao.insertAll(SeedData.recipes)
        if (healthDao.getCount() == 0) healthDao.insertAll(SeedData.healthBenefits)
        if (fpoDao.getCount()    == 0) fpoDao.insertAll(SeedData.fpoContacts)
    }

    // ── MANDI ─────────────────────────────────────────────────────────────
    fun getAllPrices(): Flow<List<MandiPrice>>          = mandiDao.getAllPrices()
    fun getPricesForCity(city: String)                 = mandiDao.getPricesForCity(city)
    fun getPricesForMillet(type: String)               = mandiDao.getPricesForMillet(type)
    fun getAllCities(): Flow<List<String>>              = mandiDao.getAllCities()
    suspend fun updatePrices(prices: List<MandiPrice>) = mandiDao.insertAll(prices)

    // ── RECIPES ───────────────────────────────────────────────────────────
    fun getAllRecipes(): Flow<List<Recipe>>             = recipeDao.getAllRecipes()
    fun getRecipesByMillet(type: String)               = recipeDao.getRecipesByMillet(type)
    fun getFavoriteRecipes(): Flow<List<Recipe>>       = recipeDao.getFavoriteRecipes()
    fun searchRecipes(q: String)                       = recipeDao.searchRecipes(q)
    suspend fun toggleFavorite(recipe: Recipe) {
        recipeDao.setFavorite(recipe.id, !recipe.isFavorite)
    }

    // ── HEALTH ────────────────────────────────────────────────────────────
    fun getAllHealthBenefits(): Flow<List<HealthBenefit>> = healthDao.getAll()
    fun getHealthForMillet(type: String)               = healthDao.getForMillet(type)
    fun getHealthForCondition(c: String)               = healthDao.getForCondition(c)

    // ── FPO ───────────────────────────────────────────────────────────────
    fun getAllFpo(): Flow<List<FpoContact>>             = fpoDao.getAll()
    fun getFpoByDistrict(d: String)                    = fpoDao.getByDistrict(d)
}
