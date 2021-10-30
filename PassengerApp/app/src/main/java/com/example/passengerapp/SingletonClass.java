package com.example.passengerapp;

import java.util.Hashtable;

/**
 * The purpose of this class is to store the devices so other classes can easily access them
 */
public class SingletonClass {

    /**
     * Properties that singleton stores:
     */
    Hashtable<String, Object> hashObjects;

    //property created only one time
    private static SingletonClass mSingletonClass;

    //Only this class can instantiate itself
    private SingletonClass(){
        hashObjects = new Hashtable<String, Object>();
    }

    //The other classes use this get method
    public static SingletonClass get(){
        if(mSingletonClass==null)
            mSingletonClass = new SingletonClass();
        return mSingletonClass;
    }

    //Gets that other classes use to get the attributes of this class
    public Hashtable<String, Object> getHashObjects(){
        return this.hashObjects;
    }


}