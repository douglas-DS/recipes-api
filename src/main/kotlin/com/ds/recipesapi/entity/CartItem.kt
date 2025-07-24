package com.ds.recipesapi.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Check
import org.hibernate.proxy.HibernateProxy

@Entity
@Table(name="cart_items")
@Check(constraints = "product_id IS NOT NULL OR recipe_id IS NOT NULL")
data class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    val cart: Cart,

    @ManyToOne
    @JoinColumn(name = "product_id")
    val product: Product? = null,

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    val recipe: Recipe? = null,
) {
    init {
        require(product != null || recipe != null) {
            "Either product or recipe must be specified"
        }
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as CartItem

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return "${this::class.simpleName}(id=$id, cart=$cart, product=$product, recipe=$recipe)"
    }
}
