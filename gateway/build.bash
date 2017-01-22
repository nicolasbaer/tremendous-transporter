#!/bin/bash

cd request

protoc -I/usr/local/include -I. \
    -I$GOPATH/src \
    -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
    -I../request/src/main/proto \
    --go_out=Mgoogle/api/annotations.proto=github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis/google/api,plugins=grpc:. \
    ../request/src/main/proto/request.proto


protoc -I/usr/local/include -I. \
    -I$GOPATH/src \
    -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
    -I../request/src/main/proto \
    --grpc-gateway_out=logtostderr=true:. \
    ../request/src/main/proto/request.proto


protoc -I/usr/local/include -I. \
    -I$GOPATH/src \
    -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
    -I../request/src/main/proto \
    --swagger_out=logtostderr=true:. \
    ../request/src/main/proto/request.proto

cd ..