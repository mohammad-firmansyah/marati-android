package com.zeroone.marati.utils

import android.util.Log
import info.mqtt.android.service.BuildConfig
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
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
            val qos = 2 // Mention your qos value

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