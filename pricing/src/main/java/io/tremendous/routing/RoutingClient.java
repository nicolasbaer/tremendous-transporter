package io.tremendous.routing;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author robert.erdin@gmail.com
 *         created on 21/01/17.
 */
public class RoutingClient {
    private static final Logger logger = Logger.getLogger(RoutingClient.class.getName());

    private final ManagedChannel channel;
    private final DistanceGrpc.DistanceBlockingStub blockingStub;

    /** Construct client connecting to HelloWorld server at {@code host:port}. */
    public RoutingClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext(true)
                .build();
        blockingStub = DistanceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** Say hello to server. */
    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        DistanceRequest request = DistanceRequest.newBuilder()
                .setSrcLat(1.0f)
                .setSrcLong(1.0f)
                .setDstLat(1.0f)
                .setDstLong(1.0f)
                .build();
        DistanceReply response;
        try {
            response = blockingStub.getDistance(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Pricing response: " + response.getDistance());
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception {
        RoutingClient client = new RoutingClient("localhost", 50051);
        try {
      /* Access a service running on the local machine on port 50051 */
            String user = "world";
            if (args.length > 0) {
                user = args[0]; /* Use the arg as the name to greet if provided */
            }
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }
}
