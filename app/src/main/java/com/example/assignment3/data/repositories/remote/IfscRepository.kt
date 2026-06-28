package com.example.assignment3.data.repositories.remote

import android.util.Log
import com.example.assignment3.data.model.IfscDetails
import com.example.assignment3.network.IfscApi
import javax.inject.Inject

class IfscRepository @Inject constructor(private val api: IfscApi){

    private val ifscList = listOf(
        "HDFC0CAGSBK",
        "SBIN0000001",
        "ICIC0000001",
        "PUNB0244200",
        "UTIB0000001"
    )

    suspend fun getIfscDetails(): List<IfscDetails>{
        return try{
            val ifscEntities = mutableListOf<IfscDetails>()

            for(ifsc in ifscList){
                val response = api.getIfscDetails(ifsc)

                ifscEntities.add(
                    IfscDetails(
                        IFSC = ifsc,
                        BANK = response.BANK,
                        BRANCH = response.BRANCH,
                        CITY = response.CITY,
                        STATE = response.STATE,
                        MICR = response.MICR ?: "N/A",
                    )
                )
            }
            ifscEntities
        } catch (e: Exception){
            Log.d("IFSC-API-ERROR",e.message.toString())
            emptyList<IfscDetails>()
        }

    }


}