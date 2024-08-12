package com.example.cryptos.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.cryptos.data.database.AppDatabase
import com.example.cryptos.data.mapper.CoinMapper
import com.example.cryptos.domain.CoinInfoEntity
import com.example.cryptos.domain.CoinRepository
import com.example.cryptos.workers.RefreshDataWorker

class CoinRepositoryImpl(
    private val application: Application
): CoinRepository {

    private val coinInfoDao = AppDatabase.getInstance(application).coinPriceInfoDao()
    private val mapper = CoinMapper()

    override fun getCoinInfoList(): LiveData<List<CoinInfoEntity>> {
        val coinInfoList = coinInfoDao.getPriceList()
        return coinInfoList.map {
            it.map {
                mapper.mapDbModelToEntity(it)
            }
        }
    }

    override fun getCoinInfo(fromSymbol: String): LiveData<CoinInfoEntity> {
        val coinInfo = coinInfoDao.getCoinPriceInfoAbout(fromSymbol)
        return coinInfo.map {
            mapper.mapDbModelToEntity(it)
        }
    }

    override fun loadData() {
        val workManager = WorkManager.getInstance(application)
        workManager.enqueueUniqueWork(
            RefreshDataWorker.NAME,
            ExistingWorkPolicy.REPLACE,
            RefreshDataWorker.makeRequest()
        )
    }
}