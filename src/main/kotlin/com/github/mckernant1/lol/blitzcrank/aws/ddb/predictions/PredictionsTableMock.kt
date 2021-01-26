package com.github.mckernant1.lol.blitzcrank.aws.ddb.predictions

import com.github.mckernant1.lol.blitzcrank.model.Prediction

class PredictionsTableMock : PredictionsAccess {
    override fun getUsersPredictions(userId: String): List<Prediction> = emptyList()

    override fun deletePrediction(userId: String, matchId: String) = Unit

    override fun addItem(prediction: Prediction) = Unit
}
