package com.example.cryptos.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.cryptos.data.database.AppDatabase
import com.example.cryptos.data.mapper.CoinMapper
import com.example.cryptos.data.network.api.ApiFactory
import com.example.cryptos.domain.CoinInfoEntity
import com.example.cryptos.domain.CoinRepository
import kotlinx.coroutines.delay

class CoinRepositoryImpl(
    private val application: Application
): CoinRepository {

    private val coinInfoDao = AppDatabase.getInstance(application).coinPriceInfoDao()
    private val mapper = CoinMapper()
    private val apiService = ApiFactory.apiService

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

    override suspend fun loadData() {
        while (true) { // загрузка данных каждые 10 сек
            val topCoins = apiService.getTopCoinsInfo(limit = 25) // получаем топ самых популярных валют
            val fromSymbols = mapper.mapNamesListToString(topCoins) // преобразуем валюты в строку чз запятую
            val jsonContainer = apiService.getFullPriceList(fSyms = fromSymbols)  // получаем контейнер с данными
            val coinInfoDtoList = mapper.mapJsonContainerToListDto(jsonContainer) // преобразуем контейнер в dto список

            // преобразовываем модель dto в модель БД
            val dbModelList = coinInfoDtoList.map {
                mapper.mapDtoToDbModel(it)
            }
            // кладем данные в БД
            coinInfoDao.insertPriceList(dbModelList)
            delay(10000) //
        }
    }
}