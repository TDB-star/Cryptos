package com.example.cryptos

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.cryptos.data.network.api.ApiFactory
import com.example.cryptos.data.database.AppDatabase
import com.example.cryptos.data.network.model.CoinInfoDto
import com.example.cryptos.data.network.model.CoinInfoJsonContainerDto
import com.google.gson.Gson
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CoinViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application) // экземпляр БД
    private val compositeDisposable = CompositeDisposable()

    val priceList = db.coinPriceInfoDao().getPriceList() // объект liveData

    fun getDetailInfo(fSym: String) : LiveData<CoinInfoDto> {
        return db.coinPriceInfoDao().getCoinPriceInfoAbout(fSym)
    }

    init {
        loadData()
    }

    private fun loadData() {
        val disposable = ApiFactory.apiService.getTopCoinsInfo(limit = 25)
            .map { it.names?.map { it.coinNameDto?.name }?.joinToString(",") ?: "ERROR LOADING DATA" }
            .flatMap { ApiFactory.apiService.getFullPriceList(fSyms = it) }
            .map { getPriceListFromRowData(it) }
            .delaySubscription(60, TimeUnit.SECONDS)
            .repeat()
            .retry()
            .subscribeOn(Schedulers.io())
            .subscribe({
                db.coinPriceInfoDao().insertPriceList(it)
                Log.d("TEST_LOADING_DATA", "Success: $it")
            }, {
                Log.d("ERROR_LOADING_DATA", "Failure: ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    private fun getPriceListFromRowData(
        coinInfoJsonContainerDto: CoinInfoJsonContainerDto
    ): List<CoinInfoDto> {
        val result = ArrayList<CoinInfoDto>()
        val jsonObject = coinInfoJsonContainerDto.json ?: return result
        val coinKeySet = jsonObject.keySet()
        for (coinKey in coinKeySet) {
            val currencyJson = jsonObject.getAsJsonObject(coinKey)
            val currencyKeySet = currencyJson.keySet()
            for (currencyKey in currencyKeySet) {
                val priceInfo = Gson().fromJson(
                    currencyJson.getAsJsonObject(currencyKey),
                    CoinInfoDto::class.java
                )
                result.add(priceInfo)
            }
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}