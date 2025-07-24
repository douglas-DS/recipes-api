package com.ds.recipesapi.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy

@Entity
@Table(name = "recipes")
data class Recipe(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(nullable = false, length = 255)
    val name: String,

    // Intentional join with "recipe_ingredients", since we need all ingredients from a recipe at once.
    // Could be retrieved from within another endpoint (e.g.: GET /recipes/:id/ingredients) for better performance and resource saving.
    @OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER)
    @JsonManagedReference
    val ingredients: MutableList<RecipeIngredient> = mutableListOf(),
) {
    fun getIngredientsTotal(): Long = ingredients.sumOf { it.product.priceInCents }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Recipe

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String = "${this::class.simpleName}(id=$id, name=$name)"
}
