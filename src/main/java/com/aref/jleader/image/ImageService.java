package com.aref.jleader.image;

import containerd.services.images.v1.ImagesGrpc;
import containerd.services.images.v1.ImagesOuterClass;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class ImageService {

    private final ImagesGrpc.ImagesBlockingStub stub;

    public ImageService(Channel channel) {
        this.stub = ImagesGrpc.newBlockingStub(channel);
    }

    public List<Image> list() {

        ImagesOuterClass.ListImagesRequest imagesRequest = ImagesOuterClass.ListImagesRequest
                .newBuilder()
                .build();

        ImagesOuterClass.ListImagesResponse response = stub.list(imagesRequest);
        return response.getImagesList()
                .stream()
                .map(Image::fromProtobuf)
                .toList();
    }

    public Image get(String imageName) {
        ImagesOuterClass.GetImageRequest getImageRequest = ImagesOuterClass.GetImageRequest
                .newBuilder()
                .setName(imageName)
                .build();

        ImagesOuterClass.GetImageResponse response = stub.get(getImageRequest);
        return Image.fromProtobuf(response.getImage());
    }

    public String delete(String imageName) {
        ImagesOuterClass.DeleteImageRequest deleteImageRequest = ImagesOuterClass.DeleteImageRequest
                .newBuilder()
                .setName(imageName)
                .build();
        try {
            stub.delete(deleteImageRequest);
        }catch (StatusRuntimeException e) {
            return e.getStatus().getDescription();
        }

        return "Image %s successfully deleted!".formatted(imageName);
    }

    public Image create(String imageName) {

        ImagesOuterClass.Image image = ImagesOuterClass.Image
                .newBuilder()
                .setName(imageName)
                .build();

        ImagesOuterClass.CreateImageRequest createImageRequest = ImagesOuterClass.CreateImageRequest
                .newBuilder()
                .setImage(image)
                .build();

        ImagesOuterClass.CreateImageResponse createImageResponse = stub.create(createImageRequest);
        return Image.fromProtobuf(createImageResponse.getImage());
    }
}
