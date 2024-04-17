package com.flab.offcoupon.exception.product;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductErrorMessage {
    public static final String PRODUCT_NOT_EXIST = "해당 상품이 존재하지 않습니다. id: %s";
}
