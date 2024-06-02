package com.flab.offcoupon.model;

import static com.flab.offcoupon.exception.common.NonPositiveValueException.*;

import java.math.BigDecimal;

import com.flab.offcoupon.exception.common.NonPositiveValueException;

import lombok.Getter;

/**
 *  0과 음수 모두 허용하지 않은 BigDecimal 값에 대해 엄격하게 음수 또는 0인지 체크하는 클래스 입니다.
 */
@Getter
public class StrictPositiveBigDecimal {
	private final BigDecimal value;

	public StrictPositiveBigDecimal(BigDecimal value) {
		if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
			throw new NonPositiveValueException(MUST_BE_POSITIVE);
		}
		this.value = value;
	}
}
