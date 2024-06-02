package com.flab.offcoupon.model;

import static com.flab.offcoupon.exception.common.NonPositiveValueException.*;

import java.math.BigDecimal;

import com.flab.offcoupon.exception.common.NonPositiveValueException;

import lombok.Getter;

/**
 *  음수를 허용하지 않은 BigDecimal 값에 대해 음수 체크하는 클래스 입니다.
 */
@Getter
public final class PositiveBigDecimal {
	private final BigDecimal value;

	public PositiveBigDecimal(BigDecimal value) {
		if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
			throw new NonPositiveValueException(MUST_NOT_BE_NEGATIVE);
		}
		this.value = value;
	}
}
