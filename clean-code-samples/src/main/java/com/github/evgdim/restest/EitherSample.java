package com.github.evgdim.restest;

import io.vavr.control.Either;

import java.math.BigDecimal;

public class EitherSample {
    public static void main(String[] args) {
        Either<ArithmeticException, BigDecimal> ok = devide(1, 5);
        Either<ArithmeticException, BigDecimal> error = devide(1, 0);

        Either<ArithmeticException, BigDecimal> mappedOk = ok.map(result -> result.multiply(BigDecimal.TEN));
        System.out.println(mappedOk);

        Either<ArithmeticException, BigDecimal> mappedError = error.map(result -> result.multiply(BigDecimal.TEN));
        System.out.println(mappedError);
    }

    private static Either<ArithmeticException, BigDecimal> devide(int divident, int divisor) {
        BigDecimal dividentDecimal = BigDecimal.valueOf(divident);
        BigDecimal divisorDecimal = BigDecimal.valueOf(divisor);
        return divisor != 0 ?
                Either.right(dividentDecimal.divide(divisorDecimal)) :
                Either.left(new ArithmeticException("Devision by zero"));
    }
}
