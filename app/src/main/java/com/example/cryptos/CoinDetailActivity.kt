package com.example.cryptos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cryptos.databinding.ActivityCoinDetailBinding
import com.squareup.picasso.Picasso


class CoinDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: CoinViewModel

    private val binding by lazy {
        ActivityCoinDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]

        if (!intent.hasExtra(EXTRA_FROM_SYMBOL)) {
            finish()
            return
        }
        val fromSymbol = intent.getStringExtra(EXTRA_FROM_SYMBOL)

        if (fromSymbol != null) {
            viewModel.getDetailInfo(fromSymbol).observe(this, Observer {
                with(binding) {
                    textViewCoinName.text = it.fromSymbol
                    textViewCoinPrice.text= it.price.toString()
                    textViewMinPriceDay.text=it.lowDay.toString()
                    textViewMaxPriceDay.text=it.highDay.toString()
                    textViewLastTransaction.text = it.lastMarket
                    textViewLastUpdate.text = it.lastUpdate

                    Picasso.get().load(it.imageUrl).into(imageViewLogoCoin)
                }
            })
        }
    }

    companion object {
        private const val EXTRA_FROM_SYMBOL: String = "fsymb"

        fun newIntent(context: Context, fromSymbol: String): Intent {
            val intent = Intent(context, CoinDetailActivity::class.java)
            intent.putExtra(EXTRA_FROM_SYMBOL, fromSymbol)
            return intent
        }
    }
}