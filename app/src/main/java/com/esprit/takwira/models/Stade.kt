package com.esprit.takwira.models

import java.io.Serializable

data class Stade(var _id: String ? = null,
                var name: String ? = null,
                var DateTime: String ? = null,
                var location: String ? = null,
                var capacity: Int ? = null,
                var image : String ? = null,
                var phone: String ? = null,
                var price: Float ? = null,

                ): Serializable