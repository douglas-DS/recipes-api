package com.ds.recipesapi.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.CascadeType
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
@Table(name = "carts")
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(nullable = false, columnDefinition = "BIGINT")
    var totalInCents: Long,

    // Intentional join with "cart_items" to retrieve all items at once.
    // Could be retrieved from within another endpoint (e.g.: GET /carts/:id/items) for better performance and resource saving.
    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    var items: MutableList<CartItem> = mutableListOf()
) {
    fun addRecipe(recipe: Recipe) {
        val cartItem = CartItem(cart = this, recipe = recipe)
        items.add(cartItem)
        totalInCents += recipe.getIngredientsTotal()
    }

    fun removeItem(item: CartItem) {
        items.remove(item)
        totalInCents -= item.product?.priceInCents ?: item.recipe?.getIngredientsTotal() ?: 0
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Cart

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String = "${this::class.simpleName}(id=$id, totalInCents=$totalInCents)"
}
