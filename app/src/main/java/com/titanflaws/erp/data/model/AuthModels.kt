package com.titanflaws.erp.data.model

/**
 * Phone verification request data model
 */
data class PhoneVerificationRequest(
    val phoneNumber: String,
    val deviceId: String? = null
)

/**
 * OTP verification request data model
 */
data class OtpVerificationRequest(
    val phoneNumber: String,
    val otp: String,
    val deviceId: String? = null,
    val fcmToken: String? = null
)

/**
 * Social auth request data model
 */
data class SocialAuthRequest(
    val token: String,
    val provider: String, // google, facebook, apple, microsoft
    val deviceId: String? = null,
    val fcmToken: String? = null
) 