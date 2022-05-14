package com.esprit.takwira.models

data class User(var _id: String ? = null,
                var firstName: String ? = null,
                var lastName: String ? = null,
                var phone: String ? = null,
                var location: String ? = null,
                var email: String ? = null,
                var profilePic : String ? = null,
                var password: String ? = null,
                var age: Int ? = null,
                var role: String ? = null,

                )