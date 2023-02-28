package com.github.sdp_begreen.begreen.coroutines

import kotlinx.coroutines.*

//https://flexiple.com/android/using-kotlin-coroutine-builders-in-android/

class coroutinesPlayground {
    companion object {
        val a = 1
        val b = 2
        fun tryLaunch() {
            println("Starting main function..")

            //Should not use GlobalScope but CoroutineScope and MainScope as Global scope is used
            // to launch top-level coroutines which are operating on the whole application lifetime
            // and are not cancelled prematurely
            GlobalScope.launch {
                println(doSomething())
            }
            runBlocking {
                delay(4000L) // make sure to keep the JVM alive in order to wait for doSomething() to execute
                println("4 sec passed on the main thread")
            }
        }

        fun tryAsync() {
            println("Gone to calculate sum of a & b")

            GlobalScope.launch {
                val result = async {
                    calculateSum()
                }
                println("Sum of a & b is: ${result.await()}")
            }
            println("Carry on with some other task while the coroutine is waiting for a result...")
            runBlocking {
                delay(3000L) // keeping jvm alive till calculateSum is finished
            }
        }

        fun tryDeferred() {
            runBlocking {
                val firstResult: Deferred<Boolean> = async {
                    isFirstCriteriaMatch()
                }

                val secondResult: Deferred<Boolean> = async {
                    isSecondCriteriaMatch()
                }

                if (firstResult.await() && secondResult.await()) {
                    println("All criteria matched, go ahead!")
                } else {
                    println("One of the criteria unmatched, sorry!")
                }
            }

        }

        suspend fun isFirstCriteriaMatch(): Boolean {
            delay(1000L) // simulate long running task
            return true
        }

        suspend fun isSecondCriteriaMatch(): Boolean {
            delay(1000L) // simulate long running task
            return false
        }

        suspend fun doSomething(): String {
            delay(3000L) // simulate long running task
            return "Did something that was 3 seconds long"
        }
        suspend fun calculateSum(): Int {
            delay(2000L) // simulate long running task
            return a + b
        }
    }
}