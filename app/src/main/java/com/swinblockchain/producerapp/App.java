package com.swinblockchain.producerapp;

import android.app.Application;
import android.content.Context;
/*
  Swinburne Capstone Project - ICT90004
  Aidan Beale & John Humphrys
  https://github.com/SwinburneBlockchain
*/
  
/**
 * Created by John on 3/10/2017.
 */

public class App extends Application{


        private static Application sApplication;

        public static Application getApplication() {
            return sApplication;
        }

        public static Context getContext() {
            return getApplication().getApplicationContext();
        }

        @Override
        public void onCreate() {
            super.onCreate();
            sApplication = this;

    }
}
