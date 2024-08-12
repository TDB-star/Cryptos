package com.example.cryptos.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.cryptos.domain.CoinInfoEntity

object CoinInfoDiffCallback: DiffUtil.ItemCallback<CoinInfoEntity>() {
    override fun areItemsTheSame(oldItem: CoinInfoEntity, newItem: CoinInfoEntity): Boolean {
        return oldItem.fromSymbol == newItem.fromSymbol
    }

    override fun areContentsTheSame(oldItem: CoinInfoEntity, newItem: CoinInfoEntity): Boolean {
        return oldItem == newItem
    }
}