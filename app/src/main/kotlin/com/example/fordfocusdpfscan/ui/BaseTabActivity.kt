package com.example.fordfocusdpfscan.ui

import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.example.fordfocusdpfscan.R
import kotlin.math.abs

// ═══════════════════════════════════════════════════════════════════════════════
// BaseTabActivity.kt — Base class for the 3 main tab screens.
//
// Adds horizontal swipe-to-navigate between tabs:
//   Swipe LEFT  → next tab  (Monitor → Diagnostica → Storico)
//   Swipe RIGHT → prev tab  (Storico → Diagnostica → Monitor)
//
// Subclasses declare their position via [tabIndex].
// Touch events are observed in dispatchTouchEvent so they work even when
// child ScrollViews or RecyclerViews are consuming vertical scroll.
// ═══════════════════════════════════════════════════════════════════════════════

abstract class BaseTabActivity : AppCompatActivity() {

    /** Position of this tab: 0 = Monitor, 1 = Diagnostica, 2 = Storico. */
    abstract val tabIndex: Int

    private lateinit var gestureDetector: GestureDetectorCompat

    // ── Swipe thresholds ──────────────────────────────────────────────────────
    private val SWIPE_MIN_DISTANCE   = 120   // px — minimum horizontal travel
    private val SWIPE_MIN_VELOCITY   = 200   // px/s — minimum fling speed
    private val SWIPE_MAX_OFF_AXIS   = 0.5f  // diffY/diffX — keeps swipes horizontal

    override fun onPostCreate(savedInstanceState: android.os.Bundle?) {
        super.onPostCreate(savedInstanceState)
        gestureDetector = GestureDetectorCompat(this, TabSwipeListener())
    }

    /**
     * Intercept all touch events BEFORE they reach child views.
     * We only OBSERVE here (return super), never consume — this lets ScrollViews
     * and other touch consumers work normally while still detecting tab swipes.
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    // ── Swipe listener ────────────────────────────────────────────────────────

    private inner class TabSwipeListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent) = true  // required to receive subsequent events

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val startX = e1?.x ?: return false
            val startY = e1.y

            val diffX = e2.x - startX
            val diffY = e2.y - startY

            // Must be primarily horizontal and fast enough
            if (abs(diffX) < SWIPE_MIN_DISTANCE) return false
            if (abs(velocityX) < SWIPE_MIN_VELOCITY) return false
            if (abs(diffY) / abs(diffX) > SWIPE_MAX_OFF_AXIS) return false

            return if (diffX < 0) {
                // ← Swipe left: go to NEXT tab
                navigateToTab(tabIndex + 1, goingForward = true)
            } else {
                // → Swipe right: go to PREVIOUS tab
                navigateToTab(tabIndex - 1, goingForward = false)
            }
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private fun navigateToTab(index: Int, goingForward: Boolean): Boolean {
        val targetClass: Class<*> = when (index) {
            0 -> MainActivity::class.java
            1 -> DiagnosticaActivity::class.java
            2 -> HistoryActivity::class.java
            else -> return false   // already at first or last tab — ignore
        }
        startActivity(Intent(this, targetClass))
        if (goingForward) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        finish()
        return true
    }
}
