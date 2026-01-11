package com.example.rental_management.others;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryConfig {

    private static Cloudinary cloudinary;

    public static Cloudinary getCloudinaryInstance() {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "desybmgev",
                    "api_key", "284898157845739",
                    "api_secret", "lrAB-mWGIntKQyAEajA9cSyt_0o"
            ));
        }
        return cloudinary;
    }
}