package com.example.cryptos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.example.cryptos.data.database.AppDatabase
import com.example.cryptos.data.mapper.CoinMapper
import com.example.cryptos.data.network.api.ApiFactory
import kotlinx.coroutines.delay

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {

    private val coinInfoDao = AppDatabase.getInstance(context).coinPriceInfoDao()
    private val apiService = ApiFactory.apiService
    private val mapper = CoinMapper()


    override suspend fun doWork(): Result {
        while (true) { // загрузка данных каждые 10 сек
            try {
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
            } catch (e: Exception) { }
            delay(10000) //
        }
    }

    companion object {
        const val NAME = "RefreshDataWorker"

        fun makeRequest() : OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>().build()
        }
    }
}