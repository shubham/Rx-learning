package com.babapanda.rxoperators.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.babapanda.rxoperators.BaseFragment
import com.babapanda.rxoperators.R
import com.babapanda.rxoperators.databinding.FragmentBufferBinding
import io.reactivex.Flowable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import java.util.*
import java.util.concurrent.Callable

class UsingFragment : BaseFragment() {

    private lateinit var logsList: MutableList<String>
    private lateinit var logAdapter: LogAdapter

    private lateinit var binding: FragmentBufferBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBufferBinding.inflate(inflater, container, false)

        binding.textDescription.setText(R.string.msg_demo_using)

        setupLogger()
        binding.btnStartOperation.setOnClickListener { executeUsingOperation() }
        return binding.root
    }

    private fun executeUsingOperation() {
        val resourceSupplier = Callable<RTData> { RTData() }
        val sourceSupplier = Function<RTData, Publisher<Int>> { rtData ->
            Flowable.just(true)
                .map {
                    rtData.doSomething()
                    Random().nextInt(50)
                }
        }
        val disposer = Consumer<RTData> { rtData ->
            rtData.clear()
        }
// flowable , maybe , completeable, single, observable
        Flowable.using(resourceSupplier, sourceSupplier, disposer)
            .lift {  }
            .subscribe({ i ->
                logger("got a value $i - (look at the logs)")
            })
    }

    inner class RTData {
        init {
            logger("initializing RTData instance")
        }

        fun doSomething() {
            logger("do something with RTData instance")
        }

        fun clear() {
            logger("cleaning up the resources (happens before a manual 'dispose'")
        }
    }

    private fun logger(logMsg: String) {
        logsList.add(0, logMsg)

        // You can only do below stuff on main thread.
        Handler(Looper.getMainLooper()).post {
            logAdapter.clear()
            logAdapter.addAll(logsList)
        }
    }

    private fun setupLogger() {
        logsList = ArrayList<String>()
        logAdapter = LogAdapter(requireActivity(), ArrayList<String>())
        binding.listThreadingLog.adapter = logAdapter
    }

    private class LogAdapter(context: Context, logs: List<String>) :
        ArrayAdapter<String>(context, R.layout.item_log, R.id.item_log, logs)
}