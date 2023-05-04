package com.mvv.bank.orders.rest.conversion;

import java.util.concurrent.Semaphore;

public class SemaphoreTest {

    public static void main(String[] args) throws Exception {

        Semaphore sem = new Semaphore(2);

        System.out.println("Before release");

        sem.release();
        System.out.println("After release 1");

        sem.release();
        System.out.println("After release 2");

        sem.acquire();
        System.out.println("After acquire 1");

        sem.acquire();
        System.out.println("After acquire 2");

        sem.acquire();
        System.out.println("After acquire 3");

        sem.acquire();
        System.out.println("After acquire 4");

        sem.acquire();
        System.out.println("After acquire 5");
    }
}
