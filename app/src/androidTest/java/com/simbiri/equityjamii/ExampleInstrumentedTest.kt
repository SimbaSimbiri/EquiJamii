package com.simbiri.equityjamii

import android.content.ContextParams
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.api.Context

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.runner.manipulation.Ordering

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.simbiri.equityjamii", appContext.packageName)

    }
}