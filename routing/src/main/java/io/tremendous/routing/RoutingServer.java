package io.tremendous.routing;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.examples.helloworld.HelloWorldServer;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author robert.erdin@gmail.com
 *         created on 21/01/17.
 */
public class RoutingServer {
    private static final Logger logger = Logger.getLogger(RoutingServer.class.getName());

    /* The port on which the server should run */
    private int port = 50051;
    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new DistanceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                RoutingServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final RoutingServer server = new RoutingServer();
        server.start();
        server.blockUntilShutdown();
    }

    private class DistanceImpl extends DistanceGrpc.DistanceImplBase {

        @Override
        public void getDistance(DistanceRequest distanceRequest, StreamObserver<DistanceReply> responseObserver) {

            responseObserver.onNext(DistanceReply.newBuilder().setDistance(123.456f).build());
            responseObserver.onCompleted();
        }

    }
}
