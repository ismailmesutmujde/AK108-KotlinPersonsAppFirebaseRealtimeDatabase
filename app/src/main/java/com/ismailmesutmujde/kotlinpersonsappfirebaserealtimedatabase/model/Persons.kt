package com.ismailmesutmujde.kotlinpersonsappfirebaserealtimedatabase.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Persons (var person_id : String? = "",
                    var person_name : String? = "",
                    var person_phone : String? = "") {
}