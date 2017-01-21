package io.tremendous.request;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public final class RequestServiceServer {
    private static final Logger logger = Logger.getLogger(RequestServiceServer.class.getName());

    /* The port on which the server should run */
    private static int port = 50060;
    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new RequestImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            RequestServiceServer.this.stop();
            System.err.println("*** server shut down");
        }));
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
        final RequestServiceServer server = new RequestServiceServer();
        server.start();
        server.blockUntilShutdown();
    }

    private class RequestImpl extends RequestGrpc.RequestImplBase {
        @Override
        public void createRequest(RequestInformation request, StreamObserver<RequestId> responseObserver) {
            logger.info("Creating new request to receiver: " + request.getReceiver().getName());
            final RequestId requestId = RequestId.newBuilder().setId("genius!").build();
            responseObserver.onNext(requestId);
            responseObserver.onCompleted();
        }

        @Override
        public void getRequest(RequestId request, StreamObserver<RequestInformation> responseObserver) {
            super.getRequest(request, responseObserver);
            throw new UnsupportedOperationException("not implemented");
        }

        /*@Override
        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }*/
    }
}
