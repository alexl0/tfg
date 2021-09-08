package com.example.passengerapp;

/**
 * The purpose of this class is to store the devices so other classes can easily access them
 */
public class SingletonClass {

    /**
     * Properties that singleton stores:
     */
    int prueba;

    //property created only one time
    private static SingletonClass mSingletonClass;

    //Only this class can instantiate itself
    private SingletonClass(){
        prueba = 3;
    }

    //The other classes use this get method
    public static SingletonClass get(){
        if(mSingletonClass==null)
            mSingletonClass = new SingletonClass();
        return mSingletonClass;
    }

    //Gets that other classes use to get the attributes of this class
    public int getPrueba(){
        return this.prueba;
    }

}
