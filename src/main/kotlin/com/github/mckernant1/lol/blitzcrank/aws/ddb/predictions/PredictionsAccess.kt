package com.github.mckernant1.lol.blitzcrank.aws.ddb.predictions

import com.github.mckernant1.lol.blitzcrank.model.Prediction

interface PredictionsAccess {

    fun getUsersPredictions(userId: String): List<Prediction>

    fun deletePrediction(userId: String, matchId: String)

    fun addItem(prediction: Prediction)
}
