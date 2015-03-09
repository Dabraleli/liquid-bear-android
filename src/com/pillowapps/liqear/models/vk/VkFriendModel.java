package com.pillowapps.liqear.models.vk;

import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkUser;
import com.pillowapps.liqear.entities.vk.roots.VkUsersResponseRoot;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.callbacks.VkCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.network.service.VkApiService;

import java.util.List;

public class VkFriendModel {
    private VkApiService vkService = ServiceHelper.getVkService();

    public void getFriends(int count, int offset, final VkSimpleCallback<List<VkUser>> callback) {
        String fields = "first_name,last_name,uid,photo_medium";
        String order = "hints";
        vkService.getFriends(fields, order, offset, count, new VkCallback<VkUsersResponseRoot>() {
            @Override
            public void success(VkUsersResponseRoot data) {
                callback.success(data.getResponse().getUsers());
            }

            @Override
            public void failure(VkError error) {
                callback.failure(error);
            }
        });
    }
}