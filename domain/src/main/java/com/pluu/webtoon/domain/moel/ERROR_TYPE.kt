package com.pluu.webtoon.domain.moel

/**
 * 에러 타입
 * Created by pluu on 2017-04-18.
 */
@Suppress("ClassName")
enum class ERROR_TYPE {
    ADULT_CERTIFY, // 성인 인증
    COIN_NEED, // 코인 필요
    NOT_SUPPORT, // 지원 불가 웹툰 타입
    DEFAULT_ERROR // 기본 에러
}
