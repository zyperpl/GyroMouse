package pl.zyper.gyroscopemouse

import android.os.AsyncTask
import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ServerScanner(private val callback: ServerScanner.Callback) : AsyncTask<Void, String, String?>() {

    companion object {
        const val PORT = 1366
        private const val SCAN_TIMEOUT = 2000

        private const val SCAN_MAGIC = "pl.zyper.gyroscopemouse.scan_servers_01"

        private const val REPEATS = 2

        private const val MAX_ITERATIONS = 10
    }

    override fun doInBackground(vararg params : Void): String? {

        val buffer = SCAN_MAGIC

        Log.d("SERVER SCANNER", "Sending: $buffer")
        var socket = DatagramSocket()
        try {
            socket.broadcast = true
            // to minimize risk of dropped packets
            for (i in 1..REPEATS) {
                socket.send(DatagramPacket(buffer.toByteArray(), 0, buffer.length, InetAddress.getByName("255.255.255.255"), PORT))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        socket.close()

        Log.d("SERVER SCANNER", "Listening on $PORT")
        socket = DatagramSocket(PORT)

        var iterations = MAX_ITERATIONS
        while (iterations > 0) {

            socket.soTimeout = SCAN_TIMEOUT

            val array = ByteArray(512)

            val packet = DatagramPacket(array, array.size)
            try {
                socket.receive(packet)
                val data = String(array, 0, packet.length)
                if (data == SCAN_MAGIC) {
                    onProgressUpdate(packet.address.hostAddress)
                    Thread.sleep(500)
                }
            }
            catch(e:Exception) {
                if (socket.isConnected) {
                    socket.disconnect()
                    Log.d("SERVER SCANNER", "Disconnected!")
                }
                Log.d("SERVER SCANNER",  e.message)
                iterations = 0
            }
            iterations--

        }
        Log.d("SERVER SCANNER", "Exiting...")

        socket.close()

        return null
    }

    override fun onProgressUpdate(vararg values: String) {
        super.onProgressUpdate(*values)
        callback.onNewData(values[0])
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        callback.onNewData(result)
    }

    interface Callback {
        fun onNewData(result: String?)
    }
}