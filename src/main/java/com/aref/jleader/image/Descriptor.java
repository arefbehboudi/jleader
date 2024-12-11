package com.aref.jleader.image;

import containerd.types.DescriptorOuterClass;

import java.util.HashMap;
import java.util.Map;

public class Descriptor {

    private String mediaType;
    private String digest;
    private long size;
    private Map<String, String> annotations;

    // Constructor
    public Descriptor(String mediaType, String digest, long size, Map<String, String> annotations) {
        this.mediaType = mediaType;
        this.digest = digest;
        this.size = size;
        this.annotations = annotations;
    }

    // Mapper for Protobuf Descriptor to Java Descriptor
    public static Descriptor fromProtobuf(DescriptorOuterClass.Descriptor protoDescriptor) {
        Map<String, String> annotations = new HashMap<>(protoDescriptor.getAnnotationsMap());

        return new Descriptor(
                protoDescriptor.getMediaType(),
                protoDescriptor.getDigest(),
                protoDescriptor.getSize(),
                annotations
        );
    }
}

