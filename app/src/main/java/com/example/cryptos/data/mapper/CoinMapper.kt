package com.example.cryptos.data.mapper

import com.example.cryptos.data.database.CoinInfoDbModel
import com.example.cryptos.data.network.model.CoinInfoDto
import com.example.cryptos.data.network.model.CoinInfoJsonContainerDto
import com.example.cryptos.data.network.model.CoinNamesListDto
import com.example.cryptos.domain.CoinInfoEntity
import com.google.gson.Gson
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CoinMapper {

    fun mapDtoToDbModel(dto: CoinInfoDto): CoinInfoDbModel {
        return CoinInfoDbModel(
            fromSymbol = dto.fromSymbol,
            toSymbol = dto.toSymbol,
            price = dto.price,
            lastUpdate = dto.lastUpdate,
            highDay = dto.highDay,
            lowDay = dto.lowDay,
            lastMarket = dto.lastMarket,
            imageUrl = BASE_IMAGE_URL + dto.imageUrl
        )
    }

    fun mapJsonContainerToListDto(jsonContainer: CoinInfoJsonContainerDto): List<CoinInfoDto> {
        val result = mutableListOf<CoinInfoDto>()
        val jsonObject = jsonContainer.json ?: return result
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

    fun mapNamesListToString(namesListDto: CoinNamesListDto): String {
        return namesListDto.names?.map {
            it.coinNameDto?.name
        }?.joinToString(",") ?: ""
    }

    fun mapDbModelToEntity(dbModel: CoinInfoDbModel): CoinInfoEntity {
        return CoinInfoEntity(
            fromSymbol = dbModel.fromSymbol,
            toSymbol = dbModel.toSymbol,
            price = getFormattedNumber(dbModel.price),
            lastUpdate = convertTimestampToTime(dbModel.lastUpdate),
            highDay = getFormattedNumber(dbModel.highDay),
            lowDay = getFormattedNumber(dbModel.lowDay),
            lastMarket = dbModel.lastMarket,
            imageUrl = dbModel.imageUrl
        )
    }

    private fun convertTimestampToTime(timestamp: Long?) : String {
        if (timestamp == null) { return  "" }
        val stamp = Timestamp(timestamp * 1000)
        val date = Date(stamp.time)
        val pattern = "HH:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getDefault()

        return simpleDateFormat.format(date)
    }


    private fun getFormattedNumber(number: Double?) : String {
        val bigDecimal = number?.let { BigDecimal(it).setScale(6, RoundingMode.HALF_EVEN) }
        return bigDecimal.toString()
    }

    companion object {
        private const val BASE_IMAGE_URL = "https://www.cryptocompare.com"
    }
}