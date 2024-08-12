package com.example.cryptos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptos.adapters.CoinInfoAdapter
import com.example.cryptos.data.network.model.CoinInfoDto

class CoinPriceListActivity : AppCompatActivity() {

    private lateinit var viewModel: CoinViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_price_list)

        recyclerView = findViewById(R.id.recyclerViewCoinPriceList)

        val adapter = CoinInfoAdapter(this)
        adapter.onCoinClickListener = object : CoinInfoAdapter.OnCoinClickListener {
            override fun onCoinClick(coinPriceInfo: CoinInfoDto) {
                val intent = CoinDetailActivity.newIntent(
                    this@CoinPriceListActivity,
                    coinPriceInfo.fromSymbol
                )
                startActivity(intent)
            }
        }

        recyclerView.adapter = adapter
        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]

        viewModel.priceList.observe(this, Observer {
            adapter.coinInfoList = it
        })
    }

}