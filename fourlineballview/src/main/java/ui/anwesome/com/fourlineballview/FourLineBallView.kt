package ui.anwesome.com.fourlineballview

/**
 * Created by anweshmishra on 12/04/18.
 */

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.MotionEvent

class FourLineBallView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : FLBRenderer = FLBRenderer(this)


    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class FLBState (var prevScale : Float = 0f, var j : Int = 0, var dir : Float = 0f) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f, 0f, 0f,0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[this.j] += 0.1f * this.dir
            if (Math.abs(this.scales[this.j] - this.prevScale) > 1) {
                scales[this.j] = prevScale + dir
                this.j += this.dir.toInt()
                if (this.j == scales.size || this.j == -1) {
                    this.j -= this.dir.toInt()
                    this.dir = 0f
                    this.prevScale = this.scales[this.j]
                    stopcb(this.prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (this.dir == 0f) {
                this.dir = 1 - 2 * this.prevScale
                startcb()
            }
        }
    }

    data class FLBAnimator (var view : View, var animated : Boolean = false) {

        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class FourLineBall (var i : Int = 0, private val state : FLBState = FLBState()) {
        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val h_size : Float = (h/3) * this.state.scales[0]
            val r : Float = Math.min(w,h)/15
            val x_gap : Float = w/6
            var x_start : Float =  -3 * x_gap/2
            var x_orig = x_start
            paint.color = Color.parseColor("#00C853")
            paint.strokeWidth = r/5
            paint.strokeCap = Paint.Cap.ROUND
            canvas.save()
            canvas.translate(w/2, h/2)
            for (i in 0..3) {
                val x_size : Float = (x_start) * this.state.scales[1]
                canvas.drawLine(x_size, -h_size, x_size, h_size, paint)
                x_start += x_gap
                if (i > 0) {
                    x_orig += x_gap * this.state.scales[3 + (i-1)]
                }
            }
            canvas.drawCircle(x_orig, 0f, r * this.state.scales[2], paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }

    data class FLBRenderer (var view : FourLineBallView) {

        val fourLineBall : FourLineBall = FourLineBall(0)

        val animator : FLBAnimator = FLBAnimator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            fourLineBall.draw(canvas, paint)
            animator.animate {
                fourLineBall.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            fourLineBall.startUpdating {
                animator.start()
            }
        }
    }
    companion object {
        fun create(activity : Activity) : FourLineBallView {
            val view : FourLineBallView = FourLineBallView(activity)
            activity.setContentView(view)
            return view
        }
    }
}