package com.babapanda.rxoperators

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.babapanda.rxoperators.fragments.MainFragment
import com.babapanda.rxoperators.utility.RxBus

class MainActivity : AppCompatActivity() {

    private var rxBus: RxBus? = null

    override fun onBackPressed() {
        super.onBackPressed()
        removeWorkerFragments()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, MainFragment(), this.toString())
                .commit()
        }
    }

    fun getRxBusSingleton(): RxBus? {
        if (rxBus == null) {
            rxBus = RxBus()
        }
        return rxBus
    }

    private fun removeWorkerFragments() {
//        val frag = supportFragmentManager.findFragmentByTag(RotationPersist1WorkerFragment.javaClass.getName());
//
//        if (frag != null) {
//            getSupportFragmentManager().beginTransaction().remove(frag).commit();
//        }
//
//        frag =
//            getSupportFragmentManager()
//                .findFragmentByTag(RotationPersist2WorkerFragment.class.getName());
//
//        if (frag != null) {
//            getSupportFragmentManager().beginTransaction().remove(frag).commit();
//        }
    }
}