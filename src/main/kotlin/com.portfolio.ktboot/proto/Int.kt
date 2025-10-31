package com.portfolio.ktboot.proto


fun Any.isInt(): Boolean =
        (this as? String)?.toIntOrNull() != null
