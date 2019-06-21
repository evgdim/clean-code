package com.github.evgdim.restest;

import io.vavr.control.Either;
import io.vavr.control.Try;

import java.math.BigDecimal;
import java.util.Objects;

public class TrySample {
    public static void main(String[] args) {

        Try<BigDecimal> error = devide(1, 0);

        Try<String> tryDivide = devide(1, 5)
                .onSuccess(TrySample::log)         //separation of concerns
                .onFailure(TrySample::sendMail)
                .map(String::valueOf)               //pure functions
                .map(String::toUpperCase);
        System.out.println(tryDivide); //Success(0.2)

//        tryDivide.get();        // don't do that
//        tryDivide.getCause();   // don't do that
//
//        tryDivide.toEither();

        Try<Integer> tryOfInteger = tryDivide.flatMap(TrySample::someFunnctionThatReturnTry);

    }

    private static Try<Integer> someFunnctionThatReturnTry(String str) {
        return Try.of(() -> str.length());
    }

    private static void log(BigDecimal bigDecimal) {
    }

    private static void sendMail(Throwable bigDecimal) {
        throw new RuntimeException();
    }

    private static Try<BigDecimal> devide(int divident, int divisor) {
        Objects.requireNonNull(divident);
        Objects.requireNonNull(divisor);
        BigDecimal dividentDecimal = BigDecimal.valueOf(divident);
        BigDecimal divisorDecimal = BigDecimal.valueOf(divisor);
        return Try.of(() -> dividentDecimal.divide(divisorDecimal));
    }
}
