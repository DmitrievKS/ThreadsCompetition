package com.kirdmt.threadscompetition;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MainActivity extends AppCompatActivity {

    public volatile boolean thereIsWinner;

    private static final String COUNT_TAG = "CountTag";
    private static final String WRITE_TAG = "WriteTag";
    private static final String READ_TAG = "ReadTag";

    private static MyThread myThreadA;
    private static MyThread myThreadB;

    private CyclicBarrier cyclicBarrier;

    //private Button readButton, startButton;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text_view);

    }

    public void readButtonOnClick(View view) {
        readFile();

    }

    public void clearButtonOnClick(View view) {
        clearFile();
        textView.setText("");
    }

    public void startButtonOnClick(View view) {
        clearFile();
        startCompetition();
    }

    private void refreshTextView(String str) {
        textView.append(str + "\n");
    }

    private void startCompetition() {

        thereIsWinner = false;

        cyclicBarrier = new CyclicBarrier(2);

        myThreadA = new MyThread("Thread A", cyclicBarrier);
        myThreadB = new MyThread("Thread B", cyclicBarrier);

        myThreadA.start();
        myThreadB.start();

    }

    void clearFile() {

        String FILENAME = "threadCompetitionFile";

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    void readFile() {

        try {
            final String FILENAME = "threadCompetitionFile";

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            String str = "";

            while ((str = br.readLine()) != null) {
                //Log.d(READ_TAG, str);
                refreshTextView(str);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class MyThread extends Thread {

        CyclicBarrier innerCbar;
        private String threadName;
        private String aboutThread;
        final static String FILENAME = "threadCompetitionFile";

        MyThread(String name, CyclicBarrier cb) {
            threadName = name;
            innerCbar = cb;
        }


        @Override
        public void run() {

            try {
                innerCbar.await(2000L, TimeUnit.MILLISECONDS);
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }


            for (int i = 1; i < 101; i++) {

                          /*  try {
                    sleep(100); //delete this line later
                } catch (InterruptedException e) {
                }*/

                aboutThread = "Thread name is: " + threadName + " iteration number is: " + String.valueOf(i);
                //Log.d(COUNT_TAG, "Thread name is: " + threadName + " iteration number is: " + String.valueOf(i));
                writeFile(aboutThread);

            }

            if (!thereIsWinner) {

                thereIsWinner = true;
                aboutThread = "Thread name is: " + threadName + " Im winner!";
                writeFile(aboutThread);

            } else {
                aboutThread = "Thread name is: " + threadName + " Im loose this round :(";
                writeFile(aboutThread);
            }

        }

        void writeFile(String str) {

            try {

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        openFileOutput(FILENAME, MODE_APPEND)));

                bw.write(str + "\n");
                bw.close();
                Log.d(WRITE_TAG, str);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
