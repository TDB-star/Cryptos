package com.example.cryptos.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.cryptos.R
import com.example.cryptos.databinding.ItemCoinInfoBinding
import com.example.cryptos.domain.CoinInfoEntity
import com.squareup.picasso.Picasso

class CoinInfoAdapter(
    private val context: Context
): ListAdapter<CoinInfoEntity, CoinInfoViewHolder>(CoinInfoDiffCallback) {

    var onCoinClickListener: OnCoinClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinInfoViewHolder {
        val binding = ItemCoinInfoBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return CoinInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinInfoViewHolder, position: Int) {
       val coin = getItem(position)
        with(holder.binding) {
            val symbolsTemplate = context.resources.getString(R.string.symbols_template)
            val lastUpdateTemplate = context.resources.getString(R.string.last_update_template)
            tvSymbols.text = String.format(symbolsTemplate, coin.fromSymbol, coin.toSymbol)
            textViewCoinPrice.text = coin.price.toString()
            textViewLastUpdate.text = String.format(lastUpdateTemplate, coin.lastUpdate)

            Picasso.get().load(coin.imageUrl).into(imageViewCoinLogo)
            root.setOnClickListener {  // корневой элемент itemImageView
                onCoinClickListener?.onCoinClick(coin)
            }
        }

    }

    interface OnCoinClickListener {
        fun onCoinClick(coinPriceInfo: CoinInfoEntity)
    }
}