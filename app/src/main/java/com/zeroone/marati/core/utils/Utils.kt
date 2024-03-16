package com.zeroone.marati.core.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.zeroone.marati.edit.EditViewModel
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlin.random.Random


class Utils {

    companion object {

        fun generateRandomString(length: Int): String {
            val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            return (1..length)
                .map { Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }

        fun getLocalFormat(dateUtc: String): String? {
            val parser = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            );
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(dateUtc)

            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            formatter.timeZone = TimeZone.getDefault()
            return date?.let { formatter.format(it) }
        }

        fun showDialogConfirmation(
            context: Context,
            title:String,
            message: String,
            onPositiveClick: () -> Unit,
            onNegativeClick: () -> Unit
            ){
            val alertDialogBuilder = AlertDialog.Builder(context)

            alertDialogBuilder.setTitle(title)
            alertDialogBuilder.setMessage(message)
            alertDialogBuilder.setPositiveButton("Yes"){ _ , _ ->
                onPositiveClick.invoke()
            }

            alertDialogBuilder.setNegativeButton("No"){_,_ ->
                onNegativeClick.invoke()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
        fun connect(mqttAndroidClient: MqttAndroidClient, topics:List<String>) {

            try {

                val token = mqttAndroidClient.connect()
                token.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken)                        {
                        Log.i("Connection", "success ")
                        //connectionStatus = true
                        // Give your callback on connection established here

                        subscribe(topics,mqttAndroidClient)
                    }
                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        //connectionStatus = false
                        Log.i("Connection", "failure")
                        // Give your callback on connection failure here
                        exception.printStackTrace()
                    }
                }
            } catch (e: MqttException) {
                // Give your callback on connection failure here
                e.printStackTrace()
            }
        }

        fun subscribe(topics: List<String>,mqttAndroidClient:MqttAndroidClient) {
            val qos = 0 // Mention your qos value

                for (topic in topics){
                    try {
                        mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
                            override fun onSuccess(asyncActionToken: IMqttToken) {
                                // Give your callback on Subscription here
                                Log.i("Subscription $topic", "success")
                            }
                            override fun onFailure(
                                asyncActionToken: IMqttToken,
                                exception: Throwable
                            ) {
                                Log.i("Subscription $topic","failure")
                            }
                        })
                    } catch (e: MqttException) {
                        // Give your subscription failure callback here
                    }

                }

        }

        fun receiveMessages(mqttAndroidClient: MqttAndroidClient) {
            mqttAndroidClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable) {
                    //connectionStatus = false
                    // Give your callback on failure here
                }
                override fun messageArrived(topic: String, message: MqttMessage) {
                    try {
                        val data = String(message.payload, charset("UTF-8"))
                        // data is the desired received message
                        // Give your callback on message received here
                        Log.i("Message",data)
                    } catch (e: Exception) {
                        // Give your callback on error here
                    }
                }
                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    // Acknowledgement on delivery complete
                }
            })
        }

        fun receiveMessagesByTopic(mqttAndroidClient: MqttAndroidClient,selectedTopic:String,vm:ViewModel) {
            mqttAndroidClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable) {
                    //connectionStatus = false
                    // Give your callback on failure here
                }
                override fun messageArrived(topic: String, message: MqttMessage) {
                    try {
                        if(topic == selectedTopic){
                            val data = String(message.payload, charset("UTF-8"))
                            // data is the desired received message
                            // Give your callback on message received here
                            val editViewModel = vm as EditViewModel
//                            editViewModel.set(data)

                            Log.i("Message",data)
                        }
                    } catch (e: Exception) {
                        // Give your callback on error here
                    }
                }
                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    // Acknowledgement on delivery complete
                }
            })
        }

        fun getUUID() = UUID.randomUUID().toString()

        fun disconnect(mqttAndroidClient: MqttAndroidClient) {
            try {
                val disconToken = mqttAndroidClient.disconnect()
                disconToken.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        //connectionStatus = false
                        // Give Callback on disconnection here
                    }
                    override fun onFailure(
                        asyncActionToken: IMqttToken,
                        exception: Throwable
                    ) {
                        // Give Callback on error here
                    }
                }
            } catch (e: MqttException) {
                // Give Callback on error here
            }
        }

        fun publish(mqttAndroidClient: MqttAndroidClient,topic: String, data: String) {
            val encodedPayload : ByteArray
            try {
                encodedPayload = data.toByteArray(charset("UTF-8"))
                val message = MqttMessage(encodedPayload)
                message.qos = 0
                message.isRetained = false
                mqttAndroidClient.publish(topic, message)
            } catch (e: Exception) {
                Log.e("Publish",e.message.toString())
            } catch (e: MqttException) {
                Log.e("Publish",e.message.toString())
                // Give Callback on error here
            }
        }

    }
}