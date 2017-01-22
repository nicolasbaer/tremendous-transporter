package main

import (
	"flag"
	"net/http"

	"github.com/golang/glog"
	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	"golang.org/x/net/context"
	"google.golang.org/grpc"

	req "./request"
)

var (
	requestEndpoint = flag.String("request endpoint", "localhost:50060", "endpoint request service")
)

func run() error {
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	mux := runtime.NewServeMux()
	opts := []grpc.DialOption{grpc.WithInsecure()}
	err := req.RegisterRequestHandlerFromEndpoint(ctx, mux, *requestEndpoint, opts)
	if err != nil {
		return err
	}

	err = http.ListenAndServe(":8080", mux)
	return err
}

func main() {
	flag.Parse()
	defer glog.Flush()

	if err := run(); err != nil {
		glog.Fatal(err)
	}
}
