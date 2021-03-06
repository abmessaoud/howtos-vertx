package org.ws13.howtos.vertx.eventbus.sender;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;

/**
 * @author ctranxuan
 */
public class RetryWithRxJavaVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        System.out.println("RetryWithRxJavaVerticle.start");
        super.start();

        DeliveryOptions deliveryOptions;
        deliveryOptions = new DeliveryOptions().setSendTimeout(1500);

        vertx.eventBus()
                .<String>rxSend("hello.rx.timeout.retry", "Hello", deliveryOptions)
//                .retry(2) // if you don't bother with dealing with the error
                .retry((count, error) -> { // here, we do bother, just to print the count
                    System.out.printf("timeout scenario with rx: retry count %d, received error \"%s\"\n",
                                      count, error.getMessage());
                    return count < 3;
                })
                .map(Message::body)
                .subscribe(m -> System.out.printf("timeout scenario with rx: received \"%s\"\n", m),
                           Throwable::printStackTrace);

        vertx.eventBus()
                .<String>rxSend("hello.rx.failure.retry", "Hello")
//                .retry(2) // if you don't bother with dealing with the error
                .retry((count, error) -> { // here, we do bother, just to print the count
                    System.out.printf("failure scenario with rx: retry count %d, received error \"%s\"\n",
                                      count, error.getMessage());
                    return count < 3;
                })
                .map(Message::body)
                .subscribe(m -> System.out.printf("failure scenario with rx: received \"%s\"\n", m),
                           Throwable::printStackTrace);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("RetryWithRxJavaVerticle.stop");
    }
}
