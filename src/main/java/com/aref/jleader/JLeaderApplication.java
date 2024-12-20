package com.aref.jleader;


import com.aref.jleader.cli.TableData;
import com.aref.jleader.cli.TableFormatter;
import com.aref.jleader.grpc.GrpcChannel;
import com.aref.jleader.image.Image;
import com.aref.jleader.image.ImageService;
import io.grpc.Channel;

public class JLeaderApplication {

    public static void main(String[] args) throws InterruptedException {

        GrpcChannel grpcChannel = new GrpcChannel();
        Channel channel = grpcChannel.getChannel("k8s.io");
        ImageService imageService = new ImageService(channel);

        String delete = imageService.delete("docker.io/library/nginx@sha256:f7988fb6c02e0ce69257d9bd9cf37ae20a60f1df7563c3a2a6abe24160306b8d");
        System.out.println(delete);
        imageService.create("docker.io/library/nginx");


        TableData<Image> tableData = new TableData<>(imageService.list());
        TableFormatter<Image> tableFormatter = new TableFormatter<>(tableData);
        tableFormatter.printTable();

    }


}

