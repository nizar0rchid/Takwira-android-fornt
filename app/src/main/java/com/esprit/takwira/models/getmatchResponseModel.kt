package com.esprit.takwira.models

data class MatchResponseModel(var _id: String ? = null,
                 var teamA: List<String?> ? = null,
                 var teamB: List<String?> ? = null,
                 var stadeId: String ? = null,
                 var teamCapacity: Int ? = null

)