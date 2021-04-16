package com.example.rxdemo1;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.reactivestreams.Subscription;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String greeting = "greeting hello RxJava";
    private String greeting2 = "greeting hello RxJava2";
    private Observable<String> myObservable;
    private Observer<String> myObserver;

    private TextView textView;

    @SuppressLint("CheckResult")
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
                Log.e(TAG, "Observable onSubscribe: ");
            }

            //第二个就是调用这个函数
            @Override
            public void onNext(@NonNull String s) {
                Log.e(TAG, "Observable onNext: s:" + s);
                textView.setText(s);
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT);

            }

            //有出错时，就是调用这个函数
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "Observable onError: ");
            }

            //第最后就是调用这个函数
            @Override
            public void onComplete() {
                Log.e(TAG, "Observable onComplete: ");
            }
        };

        //最后一定要用上下面这一句，才会调用上面myObserver，就是Observable (可观察者，即被观察者)被 Observer (观察者) 订阅了
        myObservable.subscribe(myObserver);

        textView.setOnClickListener(v -> {
            Log.e(TAG, "ok");
            v.setTag(0);
        }
        );


        Observable.just("Observable.just test")
                .subscribe(
                        s -> Log.e(TAG, s)
                );


        //创建观察者
        FlowableSubscriber<String> subscriber = new FlowableSubscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                //订阅时候的操作
                s.request(Long.MAX_VALUE);//请求多少事件，这里表示不限制
            }
            @Override
            public void onNext(String s) {
                //onNext事件处理
                Log.e("FlowableSubscriber onNext", s);
            }
            @Override
            public void onError(Throwable t) {
                //onError事件处理
            }
            @Override
            public void onComplete() {
                //onComplete事件处理
            }
        };


        //被观察者
        Flowable<String> flowable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                //订阅观察者时的操作
                e.onNext("Flowable test1");
                e.onNext("Flowable test2");
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);

        flowable.subscribe(subscriber);



        flowable.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                //相当于onNext事件处理
                Log.e("Consumer accept", s);
            }
        });
    }
}