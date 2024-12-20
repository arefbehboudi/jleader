package com.aref.jleader.grpc;

import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollDomainSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress;

import java.io.File;

public class GrpcChannel {

    public Channel getChannel(String namespace) {

        EpollEventLoopGroup eventExecutors = new EpollEventLoopGroup();

        ManagedChannel channel = NettyChannelBuilder
                .forAddress(new DomainSocketAddress(new File("/run/k3s/containerd/containerd.sock")))
                .eventLoopGroup(eventExecutors)
                .channelType(EpollDomainSocketChannel.class)
                .usePlaintext()
                .build();


        // Wrap the channel with the interceptor
        return ClientInterceptors.intercept(channel, getClientInterceptor(namespace));
    }

    private ClientInterceptor getClientInterceptor(String namespace) {
        return new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
                    MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
                return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
                    @Override
                    public void start(Listener<RespT> responseListener, Metadata headers) {
                        // Add the namespace metadata
                        Metadata.Key<String> namespaceKey = Metadata.Key.of("containerd-namespace", Metadata.ASCII_STRING_MARSHALLER);
                        headers.put(namespaceKey, namespace);
                        super.start(responseListener, headers);
                    }
                };
            }
        };
    }
}
