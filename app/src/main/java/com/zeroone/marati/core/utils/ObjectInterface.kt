package com.zeroone.marati.core.utils

import android.graphics.Canvas

interface ObjectInterface {
    val id : String;
    val status : Boolean;
    val type : String;
    val topicObject : String;
    val contentObject : String;
    val rules : String;
    val model_id : String;

    fun getObjX() : Float
    fun getObjY() : Float
    fun setTouchOffsetX(x:Float) : ObjectInterface
    fun setTouchOffsetY(y:Float) : ObjectInterface

    fun getTouchOffsetX() : Float
    fun getTouchOffsetY() : Float

    fun setObjX(x : Float) : Float
    fun setObjY(y: Float) : Float
    fun width() : Float
    fun setWidth(r : Float) : Float
    fun height() : Float
    fun setHeight(r : Float) : Float
    fun drawCustom(canvas: Canvas,content:String)
    fun getObjId():String
    fun insideObject(x:Float,y:Float):Boolean
    fun pushData()
    fun getData()

    fun setContent(content: String)
    fun setTopic(topic: String)

}