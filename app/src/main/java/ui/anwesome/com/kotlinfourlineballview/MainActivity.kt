package ui.anwesome.com.kotlinfourlineballview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.fourlineballview.FourLineBallView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FourLineBallView.create(this)
    }
}
