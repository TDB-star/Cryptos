package com.example.cryptos.domain

data class CoinInfoEntity(
    val fromSymbol: String,
    val toSymbol: String?,
    val price: Double?,
    val lastUpdate: Long?,
    val highDay: Double?,
    val lowDay: Double?,
    val lastMarket: String?,
    val imageUrl: String?,
)