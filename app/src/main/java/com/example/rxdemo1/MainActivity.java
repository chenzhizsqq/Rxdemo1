package com.example.rxdemo1;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String greeting = "greeting hello RxJava";
    private String greeting2 = "greeting hello RxJava2";
    private Observable<String> myObservable;
    private Observer<String> myObserver;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tvGreeting);

        //Observable部分,被观察者部分。就是说Observable (可观察者，即被观察者)指向哪个变量
        myObservable = Observable.just(greeting, greeting2) // 在onNext时，第一个运行的是greeting, 第二个运行是greeting2
                .subscribeOn(Schedulers.io())               // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()); // 指定 Subscriber 的回调发生在主线程

        //Subscriber部分，观察者部分。写了这句后，就会自动的出onSubscribe，onNext，onError，onComplete的函数
        myObserver = new Observer<String>() {

            //刚运行时，第一个就是调用这个函数
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.e(TAG, "onSubscribe: ");
            }

            //第二个就是调用这个函数
            @Override
            public void onNext(@NonNull String s) {
                Log.e(TAG, "onNext: s:" + s);
                textView.setText(s);

            }

            //有出错时，就是调用这个函数
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: ");
            }

            //第最后就是调用这个函数
            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
            }
        };

        //最后一定要用上下面这一句，才会调用上面myObserver，就是Observable (可观察者，即被观察者)被 Observer (观察者) 订阅了
        myObservable.subscribe(myObserver);
    }
}