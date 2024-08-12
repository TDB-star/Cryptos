package com.example.cryptos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptos.R
import com.example.cryptos.data.network.model.CoinInfoDto
import com.squareup.picasso.Picasso

class CoinInfoAdapter(private val context: Context): RecyclerView.Adapter<CoinInfoAdapter.CoinInfoViewHolder>() {

    var coinInfoList: List<CoinInfoDto> = listOf()
        set(value) {
          field = value
          notifyDataSetChanged()
        }

    var onCoinClickListener: OnCoinClickListener? = null

    override fun getItemCount() = coinInfoList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinInfoViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_coin_info,
                parent, false
            )
        return CoinInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoinInfoViewHolder, position: Int) {
       val coin = coinInfoList[position]
        val symbolsTemplate = context.resources.getString(R.string.symbols_template)
        val lastUpdateTemplate = context.resources.getString(R.string.last_update_template)
        holder.tvSymbols.text = String.format(symbolsTemplate, coin.fromSymbol, coin.toSymbol)
        holder.textViewCoinPrice.text = coin.getFormattedPrice(6)
        holder.textViewLastUpdate.text = String.format(lastUpdateTemplate, coin.getFormattedDate())

        Picasso.get().load(coin.getFullImageUrl()).into(holder.imageViewCoinLogo)
        holder.itemView.setOnClickListener {
            onCoinClickListener?.onCoinClick(coin)
        }
    }

    inner class CoinInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewCoinLogo: ImageView = itemView.findViewById(R.id.imageViewCoinLogo)
        val tvSymbols: TextView = itemView.findViewById(R.id.tvSymbols)
        val textViewCoinPrice: TextView = itemView.findViewById(R.id.textViewCoinPrice)
        val textViewLastUpdate: TextView = itemView.findViewById(R.id.textViewLastUpdate)
    }

    interface OnCoinClickListener {
        fun onCoinClick(coinPriceInfo: CoinInfoDto)
    }
}