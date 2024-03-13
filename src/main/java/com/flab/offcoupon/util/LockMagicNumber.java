package com.flab.offcoupon.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LockMagicNumber {

    public static final long LOCK_WAIT_MILLI_SECOND = 3000L;

    public static final long LOCK_LEASE_MILLI_SECOND = 3000L;
}
