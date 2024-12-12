package com.ravikantsharma.mvinotesapp

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseUnitTest(testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) {

    @Rule
    @JvmField
    val coroutineTestRule = CoroutineTestRule(testDispatcher)

    protected fun testBlocking(block: suspend TestScope.() -> Unit) =
        runTest(coroutineTestRule.testDispatcher) {
            block()
        }
}