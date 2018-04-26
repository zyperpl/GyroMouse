package pl.zyper.gyroscopemouse

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_app.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.concurrent.thread

class AppActivity : AppCompatActivity(), SensorEventListener {

    companion object {
        private const val BUNDLE_IP = "bundleIP"
        private const val SIZEOF_FLOAT = 4
        private const val SIZEOF_CHAR = 2

        private const val DATA_TAG = 'D'
        private const val LIGHT_SAMPLING_PERIOD = 1000*1000*4

        fun start(context: Context, ip: String) {
            val intent = Intent(
                    context,
                    AppActivity::class.java
            )
            intent.putExtra(
                    BUNDLE_IP, ip
            )
            context.startActivity(intent)
        }
    }

    enum class Type {
        LeftClick, RightClick, Gyroscope, Accelerometer, Light, Touch
    }

    private var ip:String? = null
    private val socket = DatagramSocket()

    private lateinit var sensorManager: SensorManager
    private var lastLightSensorTimestamp = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        ip = intent.extras.getString(BUNDLE_IP)

        Toast.makeText(this, getString(R.string.connecting_msg) + " to " + ip, Toast.LENGTH_SHORT).show()

        sensorManager = getSystemService(Service.SENSOR_SERVICE) as SensorManager

        touchView.setOnTouchListener(View.OnTouchListener{ view, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (event.historySize > 0) {
                        sendInputData(Type.Touch, floatArrayOf(
                                event.x - event.getHistoricalX(event.historySize - 1),
                                event.y - event.getHistoricalY(event.historySize - 1)
                        ))
                    }
                }
            }

            true
        })

        LMButton.setOnClickListener { sendInputData(Type.LeftClick,  floatArrayOf(1F)) }
        RMButton.setOnClickListener { sendInputData(Type.RightClick, floatArrayOf(1F)) }
    }

    override fun onResume() {
        super.onResume()

        registerListeners()
    }

    override fun onPause() {
        super.onPause()

        unregisterListeners()
    }

    override fun onSensorChanged(event: SensorEvent) {
        //System.out.println("Event " + event.sensor.stringType + " onSensorChanged occurred " + event.timestamp)

        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE ->
            if (mouseSwitch.isChecked) {
                sendInputData(Type.Gyroscope, event.values)
            }
            Sensor.TYPE_LIGHT ->
            if (brightnessSwitch.isChecked) {
                if (event.timestamp-lastLightSensorTimestamp > LIGHT_SAMPLING_PERIOD*1000L) {
                    sendInputData(Type.Light, event.values)
                    lastLightSensorTimestamp = event.timestamp
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun registerListeners() {
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME
        )

        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                LIGHT_SAMPLING_PERIOD
        )
    }

    private fun unregisterListeners() {
        sensorManager.unregisterListener(this)
        sensorManager.unregisterListener(this)
    }

    private fun sendInputData(type: Type, values: FloatArray?) {
        thread(start=true) {

            val bytes = sensorByteArray(
                    type,
                    values
            )
            socket.send(DatagramPacket(
                    bytes,
                    0,
                    bytes.size,
                    InetAddress.getByName(ip),
                    ServerScanner.PORT
            ))
        }
    }

    private fun sensorByteArray(type:Type, elements: FloatArray?): ByteArray {
        val buffer:ByteBuffer = ByteBuffer.allocate(SIZEOF_FLOAT*(elements!!.size+1)+SIZEOF_CHAR*2)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.putChar(DATA_TAG)
        buffer.putChar(('A'.toInt()+type.ordinal).toChar())
        for (e in elements) buffer.putFloat(e)
        buffer.putFloat(1F)

        return buffer.order(ByteOrder.LITTLE_ENDIAN).array()
    }
}
