package com.example.cryptos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cryptos.databinding.ActivityCoinDetailBinding
import com.squareup.picasso.Picasso

private lateinit var viewModel: CoinViewModel

@SuppressLint("StaticFieldLeak")
private lateinit var coinDetailBinding: ActivityCoinDetailBinding

class CoinDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_detail)
        
       coinDetailBinding = ActivityCoinDetailBinding.inflate(layoutInflater)
        val view = coinDetailBinding.root
        setContentView(view)


        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]

        if (!intent.hasExtra(EXTRA_FROM_SYMBOL)) {
            finish()
            return
        }
        val fromSymbol = intent.getStringExtra(EXTRA_FROM_SYMBOL)

        if (fromSymbol != null) {
            viewModel.getDetailInfo(fromSymbol).observe(this, Observer {
                with(coinDetailBinding) {
                    textViewCoinName.text = it.fromSymbol
                    textViewCoinPrice.text= it.getFormattedPrice(6)
                    textViewMinPriceDay.text=it.lowDay.toString()
                    textViewMaxPriceDay.text=it.highDay.toString()
                    textViewLastTransaction.text = it.lastMarket
                    textViewLastUpdate.text = it.getFormattedDate()

                    Picasso.get().load(it.getFullImageUrl()).into(imageViewLogoCoin)
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